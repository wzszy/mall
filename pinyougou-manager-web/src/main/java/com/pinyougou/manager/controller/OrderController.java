package com.pinyougou.manager.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import entity.PageResult;
import entity.Result;
import utils.LoggerExportUtil;

/**
 * 孔飞 运营商后台订单管理
 * @author Administrator
 * 11111
 * 22222
 * 33333
 * 4444
 */
@RestController
@RequestMapping("/order")
public class OrderController {
	
	
	@Reference
	private OrderService orderService ;
	
	/**
	 * 查询所有
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){
		return orderService.findAll();
	}
	
	
	
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order) {
		try {
			orderService.add(order);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}
	
	@Reference
	private SellerService sellerService ;
	
	/**
	 * 订单详情查询
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Map findOne(Long id) {
		
		Map map = new HashMap<>();
		TbOrder order = orderService.findOne(id);
		//查询订单详情
		List<TbOrderItem> orderItemList = orderService.findOrderItemByOrderId(order.getOrderId());
		//查询商家详细信息
		TbSeller seller = sellerService.findOne(order.getSellerId());
		map.put("order", order);
		map.put("orderItemList", orderItemList);
		map.put("seller", seller);
		return map;
	}
	
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order) {
		try {
			orderService.update(order);
			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}
	
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			orderService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	@RequestMapping("/search")
	public PageResult findBrandByCondition(@RequestBody TbOrder order,int pageNum,int pageSize) {
		
		return orderService.findPage(order, pageNum, pageSize);
		
	}
	
	//导出excel表单
	@RequestMapping("/findByOrderIds")
	public void findByOrderIds(Long[] orderIds,HttpServletResponse response) {

		List<TbOrder> orderList = new ArrayList<TbOrder>();
		// ========================================
		// 获取包含导出内容的对象集合
		if (orderIds == null || orderIds.length==0) {
			orderList = orderService.findAll();
		}

		for (Long id : orderIds) {
			
			TbOrder tbOrder = orderService.findOne(id);
			orderList.add(tbOrder);
		}

		String fileName = "订单信息表"+System.currentTimeMillis() + ".xls"; // 文件名
		
		String sheetName = "订单详细记录";// sheet名

		String[] title = new String[] { "订单编号", "用户账号", "收货人", "手机号", "订单金额", "支付方式", "订单来源", "订单状态" };// 表格标题

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String[][] values = new String[orderList.size()][];
		for (int i = 0; i < orderList.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			String[] paymentTypeList = { "", "微信", "货到付款","支付宝" };
			String[] sourceTypeList = { "", "app端", "pc端", "M端", "微信端", "手机qq端" };
			String[] statusList = { "", "未付款", "已付款", "未发货", "已发货", "交易成功", "交易关闭", "待评价" };
			TbOrder obj = orderList.get(i);
			if(obj.getOrderId()!=null) {
				values[i][0] = obj.getOrderId().toString();
			}else {
				values[i][0]="";
			}
			
			if(obj.getUserId()!=null) {
				values[i][1] = obj.getUserId().toString();
			}else {
				values[i][1]="";
			}
			
			if(obj.getReceiver()!=null) {
				values[i][2] = obj.getReceiver();
			}else {
				values[i][2]="";
			}
			if(obj.getReceiverMobile()!=null) {
				values[i][3] = obj.getReceiverMobile();
			}else {
				values[i][3] ="";
			}
			if(obj.getPayment()!=null) {
				values[i][4] = obj.getPayment().toString();
			}else {
				values[i][4] ="";
			}
			
			if(obj.getPaymentType()!=null) {
				values[i][5] = paymentTypeList[Integer.parseInt(obj.getPaymentType())];
			}else {
				values[i][5] ="";
			}
			
			if(obj.getSourceType()!=null) {
				values[i][6] = sourceTypeList[Integer.parseInt(obj.getSourceType())];
			}else {
				values[i][6] ="";
			}
			
			if(obj.getStatus()!=null) {
				values[i][7] = statusList[Integer.parseInt(obj.getStatus())];
			}else {
				values[i][7] ="";
			}
			
			
			
			
		}

		HSSFWorkbook wb = LoggerExportUtil.getHSSFWorkbook(sheetName, title, values, null);
		

		

        try {
        	

            //给文件设置编码
            fileName = URLEncoder.encode(fileName,"UTF-8");
        	//设置响应头和数据格式
        	response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        	response.setContentType("multipart/form-data");
			wb.write(response.getOutputStream());
			wb.close();
        	
        	 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       

	}
	
	
	
	
}
