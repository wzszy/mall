
package com.pinyougou.order.service;
import java.util.List;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Orders;

import entity.PageResult;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	
	
	/**
	 * 增加
	*/
	public void add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbOrder order, int pageNum,int pageSize);
	
	/**
	 * 根据用户名从缓存中读取支付日志
	 * @param userId
	 * @return
	 */
	public TbPayLog searchPayLogFromRedis(String userId);
	
	
	public void updateOrderStatus(String out_trade_no,String transaction_id);
	
	
	/**
	 * 根据登录用户查询订单
	 * @return
	 */
	public List<Orders> findListByUserId(String userId,String status);


	void savePayLog(TbOrder order);


	//=====================订单导出=====================
	
	/**
	 * 孔飞  根据订单号查询订单item表
	 * @param orderId
	 * @return
	 */
	public List<TbOrderItem> findOrderItemByOrderId(Long orderId);
	
	
	/**
	 * 商家后台查询订单
	 * @param order
	 * @param sellerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPageBySellerId(TbOrder order,String sellerId, int pageNum,int pageSize);

	/**
	 * 根据订单ID查询订单详情
	 * @param orderId
	 * @return
	 */
	public Orders findOrderDetail(Long orderId);

	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize, String userId, String status);


	/**
	 * 合并订单
	 * @param orderIds
	 * @param username 
	 */
	public void saveMutPaylog(Long[] orderIds, String username);
	
	
	
	


	
}

