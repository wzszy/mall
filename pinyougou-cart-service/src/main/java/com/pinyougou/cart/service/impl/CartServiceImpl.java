package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 添加商品到购物车
	 * @return
	 */
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num) {
		
		//1.根据skuId查询商品明细sku对象
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item == null) {
			throw new RuntimeException("商品不存在");
		}
		if (!item.getStatus().equals("1")) {
			throw new RuntimeException("商品状态不合法");
		}
		//2.根据sku对象得到商家id
		String sellerId = item.getSellerId();
		
		//3.根据商家id在购物城列表中查询购物车对象
		Cart cart = searchCart(cartList, sellerId);
		
		if (cart == null) {
			//4.购物车中不存在该商家的购物车
			//4.1.创建一个新的购物车对象
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			
			List<TbOrderItem> orderItemList = new ArrayList();
			TbOrderItem orderItem = createOrderItem(num, item);
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList);
			//4.2.将新的购物车对象添加到购物车列表中
			cartList.add(cart);
			
		}else {
			//5.购物车中存在该商家的购物车
			//判断商家购物车的明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItem(cart.getOrderItemList(),itemId);
			if (orderItem == null) {
				//5.1.不存在，创建新的购物车明细列表，并添加到购物车的明细列表中
				orderItem = createOrderItem(num, item);
				cart.getOrderItemList().add(orderItem);
			}else {
				//5.2.存在，在原有的数量上添加数量，并且更新金额
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
				if (orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				if (cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			}
			
		}

		return cartList;
	}

	/**
	 * 创建购物车明细对象
	 * @param num
	 * @param item
	 * @return
	 */
	private TbOrderItem createOrderItem(Integer num, TbItem item) {
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}
	
	/**
	 * 3.根据商家id在购物城列表中查询购物车对象
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	public Cart searchCart(List<Cart> cartList,String sellerId) {
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
		
	}
	
	/**
	 * 根据skuid在购物车明细列表中查询购物车明细对象
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	public TbOrderItem searchOrderItem(List<TbOrderItem> orderItemList,Long itemId) {
		for (TbOrderItem tbOrderItem : orderItemList) {
			if (tbOrderItem.getItemId().longValue() == itemId.longValue()) {
				return tbOrderItem;
			}
		}
		return null;
		
	}

	/**
	 * 从Redis中获取购物车信息
	 * @param username
	 * @return
	 */
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中提取购物车信息: "+username);
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if (cartList == null) {
			cartList = new ArrayList<>();
		}
		return cartList;
	}

	
	/**
	 * 将购物车信息存入redis
	 * @param username
	 * @param cartList
	 */
	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis中存入购物车信息: "+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
		
	}

	/**
	 * 将cookie中保存的购物车信息合并到redis中
	 * @param list1
	 * @param list2
	 * @return
	 */
	@Override
	public List<Cart> mergeCartList(List<Cart> list1, List<Cart> list2) {
		for (Cart cart : list2) {
			for (TbOrderItem item : cart.getOrderItemList()) {
				list1=addGoodsToCartList(list1, item.getItemId(), item.getNum());
			}
		}
		return list1;
	}
	
	

}
