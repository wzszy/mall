package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbOrderItemExample;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbPayLogExample;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.pojogroup.Orders;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;
import utils.IdWorker;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize,String userId,String status) {
		List<Orders> list = new ArrayList<>();
		
		PageHelper.startPage(pageNum, pageSize);		
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		if (!"0".equals(status)) {
			criteria.andStatusEqualTo(status);
		}		
		example.setOrderByClause("create_time");
		Page<TbOrder> orderList = (Page<TbOrder>) orderMapper.selectByExample(example );
		
		if (orderList!=null && orderList.size()>0) {
			for (TbOrder tbOrder : orderList) {
				
				TbOrderItemExample example_t = new TbOrderItemExample();
				com.pinyougou.pojo.TbOrderItemExample.Criteria criteria_t = example_t.createCriteria();
				criteria_t.andOrderIdEqualTo(tbOrder.getOrderId());
				List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(example_t );
				
				list.add(new Orders(tbOrder,orderItemList,orderItemList.size()));
			}
		}
		
		return new PageResult(orderList.getTotal(), list);
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker IdWorker;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	/**
	 * 增加
	 */
	@Override
	@Transactional
	public void add(TbOrder order) {
		//获取购物车信息	
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		
		List<String> orderList = new ArrayList<>();
		double total_money = 0;
		//循环购物车列表添加订单
		for (Cart cart : cartList) {
			TbOrder tbOrder = new TbOrder();
			long orderId = IdWorker.nextId(); 
			tbOrder.setOrderId(orderId);
			tbOrder.setPaymentType(order.getPaymentType());
			tbOrder.setStatus("1");
			tbOrder.setCreateTime(new Date());
			tbOrder.setUpdateTime(new Date());
			tbOrder.setUserId(order.getUserId());
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());
			tbOrder.setReceiverMobile(order.getReceiverMobile());
			tbOrder.setReceiver(order.getReceiver());
			tbOrder.setSourceType(order.getSourceType());
			tbOrder.setSellerId(cart.getSellerId());
			
			double money = 0;
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(IdWorker.nextId());
				orderItem.setOrderId(orderId);
				orderItem.setSellerId(cart.getSellerId());
				money += orderItem.getTotalFee().doubleValue();
				
				orderItemMapper.insert(orderItem);
			}
			tbOrder.setPayment(new BigDecimal(money));
			orderMapper.insert(tbOrder);
			orderList.add(orderId+"");
			total_money += money;
		}
		
		//添加微信支付日志
		if ("1".equals(order.getPaymentType())) {
			TbPayLog payLog = new TbPayLog();
			payLog.setUserId(order.getUserId());
			payLog.setCreateTime(new Date());
			payLog.setOutTradeNo(IdWorker.nextId()+"");
			payLog.setTotalFee((long) (total_money*100));
			payLog.setOrderList(orderList.toString().replace("[", "").replace("]", ""));
			payLog.setTradeState("0");
			payLog.setPayType("1");
			payLogMapper.insert(payLog );
			//添加到缓存
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
		}
		
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	
	
	//查询商家订单
		@Override
		public PageResult findPageBySellerId(TbOrder order, String sellerId, int pageNum, int pageSize) {
			
			PageHelper.startPage(pageNum, pageSize);
			
			order.setSellerId(sellerId);
			TbOrderExample example=new TbOrderExample();
			Criteria criteria = example.createCriteria();
			
			
			if(order!=null){			
					if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
					criteria.andPaymentTypeEqualTo(order.getPaymentType());
				}
				if(order.getPostFee()!=null && order.getPostFee().length()>0){
					criteria.andPostFeeLike("%"+order.getPostFee()+"%");
				}
				if(order.getStatus()!=null && order.getStatus().length()>0){
					criteria.andStatusEqualTo(order.getStatus());
				}
				if(order.getShippingName()!=null && order.getShippingName().length()>0){
					criteria.andShippingNameLike("%"+order.getShippingName()+"%");
				}
				if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
					criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
				}
				if(order.getUserId()!=null && order.getUserId().length()>0){
					criteria.andUserIdLike("%"+order.getUserId()+"%");
				}
				if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
					criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
				}
				if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
					criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
				}
				if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
					criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
				}
				if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
					criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
				}
				if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
					criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
				}
				if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
					criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
				}
				if(order.getReceiver()!=null && order.getReceiver().length()>0){
					criteria.andReceiverLike("%"+order.getReceiver()+"%");
				}
				if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
					criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
				}
				if(order.getSourceType()!=null && order.getSourceType().length()>0){
					criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
				}
				if(order.getSellerId()!=null && order.getSellerId().length()>0){
					criteria.andSellerIdEqualTo(sellerId);
				}
				if (order.getOrderIdStr()!=null && order.getOrderIdStr().length()>0) {
					criteria.andOrderIdEqualTo(Long.valueOf(order.getOrderIdStr()));
				}
		
			}
			
			
			
			Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
			return new PageResult(page.getTotal(), page.getResult());
			
		}

		@Override
		public TbPayLog searchPayLogFromRedis(String userId) {
			TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
			return payLog;
		}

		@Override
		public void updateOrderStatus(String out_trade_no, String transaction_id) {
			System.out.println("out_trade_no: "+out_trade_no);//==============================
			System.out.println("transaction_id: "+transaction_id);//==============================
			//1.修改支付日志的支付状态和交易号码
			TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
			System.out.println("payLog: "+payLog);//==============================
			payLog.setPayTime(new Date());
			payLog.setTradeState("1");//设置支付状态成功
			payLog.setTransactionId(transaction_id);
			
			payLogMapper.updateByPrimaryKey(payLog);
			
			//2.修改订单表的支付状态
			String orderList = payLog.getOrderList();
			String[] orderIds = orderList.split(",");
			for (String orderId : orderIds) {
				TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
				order.setStatus("2");
				order.setPaymentTime(new Date());
				orderMapper.updateByPrimaryKey(order);
				
			}

			//3.清除缓存中的payLog
			redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
		}
		
		
	@Override
	public List<Orders> findListByUserId(String userId,String status) {
		List<Orders> list = new ArrayList<>();
		
		System.out.println("userId: "+userId);//=========================
		
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		
		if (!"0".equals(status)) {
			criteria.andStatusEqualTo(status);
		}		
		example.setOrderByClause("create_time");
		
		List<TbOrder> orderList = orderMapper.selectByExample(example );
		if (orderList!=null && orderList.size()>0) {
			for (TbOrder tbOrder : orderList) {
				
				TbOrderItemExample example_t = new TbOrderItemExample();
				com.pinyougou.pojo.TbOrderItemExample.Criteria criteria_t = example_t.createCriteria();
				criteria_t.andOrderIdEqualTo(tbOrder.getOrderId());
				List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(example_t );
				
//				TbPayLogExample example_p = new TbPayLogExample();
//				com.pinyougou.pojo.TbPayLogExample.Criteria criteria_p = example_p.createCriteria();
//				criteria_p.andOrderListEqualTo(tbOrder.getOrderId()+"");
//				TbPayLog tbPayLog = (TbPayLog) payLogMapper.selectByExample(example_p );
				
				list.add(new Orders(tbOrder,orderItemList,orderItemList.size()));
			}
		}
		
		
		
		
		return list;
	}
	
	@Autowired
	private IdWorker idWorker;
	
	
	
	
	
	/**
	 * 订单详情
	 */
	@Override
	public List<TbOrderItem> findOrderItemByOrderId(Long orderId) {
		
		TbOrderItemExample example = new TbOrderItemExample();
		com.pinyougou.pojo.TbOrderItemExample.Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(orderId);
		List<TbOrderItem> list = orderItemMapper.selectByExample(example );


		return list;
	}

	/**
	 * 根据订单ID查询订单详情
	 */
	@Override
	public Orders findOrderDetail(Long orderId) {
		try {
			TbOrder order = orderMapper.selectByPrimaryKey(orderId);
			TbOrderItemExample example = new TbOrderItemExample();
			com.pinyougou.pojo.TbOrderItemExample.Criteria criteria = example.createCriteria();
			criteria.andOrderIdEqualTo(orderId);
			List<TbOrderItem> list = orderItemMapper.selectByExample(example );
			
			return new Orders(order,list,list.size());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 订单中心微信支付
	 */
	@Override
	public void savePayLog(TbOrder order) {
		
		long totalFee = (long)(order.getPayment().doubleValue()*100);
		String userId = order.getUserId();
		String orderList = order.getOrderId()+"";
		createPayLog( totalFee, userId, orderList);
	}

	
	/**
	 * 合并订单支付
	 */
	@Override
	public void saveMutPaylog(Long[] orderIds,String userId) {
		String orderList = "";
		long totalFee = 0;
		for (int i = 0; i < orderIds.length; i++) {
			TbOrder order = orderMapper.selectByPrimaryKey(orderIds[i]);
			totalFee += (long)(order.getPayment().doubleValue()*100);
			if (i < orderIds.length-1) {
				orderList += orderIds[i]+",";
			}else {
				orderList += orderIds[i];
			}
			
		}
		
		
		createPayLog(totalFee, userId, orderList);
	}
	/**
	 * 创建支付日志
	 * @param totalFee
	 * @param userId
	 * @param orderList
	 */
	private void createPayLog( long totalFee, String userId, String orderList) {
		//创建支付日志
		TbPayLog payLog = new TbPayLog();
		long out_trade_no = idWorker.nextId();
		payLog.setOutTradeNo(out_trade_no+"");
		payLog.setCreateTime(new Date());
		payLog.setTotalFee(totalFee);
		payLog.setUserId(userId);
		payLog.setTradeState("0");
		payLog.setOrderList(orderList);
		payLog.setPayType("1");
		payLogMapper.insert(payLog);
		
		redisTemplate.boundHashOps("payLog").put(userId, payLog);
	}
	
	



	

}
