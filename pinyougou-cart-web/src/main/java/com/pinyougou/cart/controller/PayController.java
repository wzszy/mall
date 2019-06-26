package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;
import utils.IdWorker;

@RestController
@RequestMapping("pay")
public class PayController {
	@Reference
	private WeixinPayService weixinPayService;
	
	@Reference
	private OrderService orderService;
	
	////生成支付二维码
	@RequestMapping("createNative")
	public Map createNative() {
		//获取当前登录用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		//获取支付日志
		TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
		//调用微信支付接口，生成支付二维码完成支付
		if (payLog != null) {
			Map map = weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
			return map;
		}else {
			return new HashMap<>();
		}
	}
	
	//查询支付状态
	@RequestMapping("queryPayStatus")
	public Result queryPayStatus(String out_trade_no) {
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
				orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
				
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
				break;
			}
		}
		return result;
	}
}
