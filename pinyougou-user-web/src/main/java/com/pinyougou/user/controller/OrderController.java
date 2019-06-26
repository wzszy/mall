package com.pinyougou.user.controller;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojogroup.Orders;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;
import entity.Result;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){			
		return orderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows,String status){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return orderService.findPage(page, rows,username,status);
	}
	
	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		order.setUserId(name);
		order.setSourceType("2");//订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
		try {
			orderService.add(order);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbOrder findOne(Long id){
		return orderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			orderService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
		return orderService.findPage(order, page, rows);		
	}
	
	@RequestMapping("findListByUserId")
	public List<Orders> findListByUserId(String status){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return orderService.findListByUserId(username,status);
	}
	/**
	 * 添加订单到支付日志
	 * @param orderId
	 * @return
	 */
	@RequestMapping("savePaylog")
	public Result savePaylog(Long orderId) {
		try {
			TbOrder order = orderService.findOne(orderId);
			orderService.savePayLog(order );
			return new Result(true, "添加成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}
	
	/**
	 * 添加合并订单到支付日志
	 * @param orderId
	 * @return
	 */
	@RequestMapping("saveMutPaylog")
	public Result saveMutPaylog(@RequestBody Long[] orderIds) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			orderService.saveMutPaylog(orderIds ,username);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 根据订单ID查询订单详情
	 * @param orderId
	 * @return
	 */
	@RequestMapping("findOrderDetail")
	public Orders findOrderDetail(Long orderId) {
		return orderService.findOrderDetail(orderId);
	}
	
	/**
	 * 确认收货
	 * @param order
	 * @return
	 */
	@RequestMapping("modifyOrder")
	public Result modifyOrder(@RequestBody TbOrder order,String status) {
		try {
			if ("5".equals(status)) {
				order.setEndTime(new Date());
			}
			order.setStatus(status);
			orderService.update(order);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
