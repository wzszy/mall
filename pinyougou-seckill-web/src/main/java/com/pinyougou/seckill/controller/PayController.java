package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.Result;
import utils.IdWorker;

@RestController
@RequestMapping("pay")
public class PayController {
	@Reference
	private WeixinPayService weixinPayService;
	
	@Reference
	private SeckillOrderService seckillOrderService;
	
	////生成支付二维码
	@RequestMapping("createNative")
	public Map createNative() {
		//获取当前登录用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		//获取支付日志
		TbSeckillOrder seckillOrder = seckillOrderService.searchSeckillOrderfromRedis(userId);
		//调用微信支付接口，生成支付二维码完成支付
		if (seckillOrder != null) {
			Map map = weixinPayService.createNative(seckillOrder.getId()+"",(long)(seckillOrder.getMoney().doubleValue()*100)+"");
			return map;
		}else {
			return new HashMap<>();
		}
	}
	
	//查询支付状态
	@RequestMapping("queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
		//获取当前登录用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Result result = null;
		int num = 0;
		while(true) {
			Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
			if (map == null) {
				result = new Result(false, "支付失败");
				break;
			}
			
			if (map.get("trade_state").equals("SUCCESS")) {
				result = new Result(true, "支付成功");
				//修改订单状态
				seckillOrderService.saveOrderfromRedistoDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
				
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			num++;
			if (num>100) {
				result = new Result(false, "二维码超时");
				
				Map<String,String> resultMap = weixinPayService.closePay(out_trade_no);
				if (resultMap != null && "FAIL".equals(resultMap.get("return_code"))) {
					if ("ORDERPAID".equals(resultMap.get("err_code"))) {//关闭前订单已支付
						result = new Result(true, "支付成功");
						//保存订单
						seckillOrderService.saveOrderfromRedistoDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
					}
				}
				//删除订单
				if (result.isSuccess() == false) {
					seckillOrderService.deleteOrderfromRedis(userId,  Long.valueOf(out_trade_no));
				}
				
				break;
			}
		}
		return result;
	}
}
