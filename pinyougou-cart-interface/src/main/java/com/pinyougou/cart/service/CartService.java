package com.pinyougou.cart.service;
/**
 * 购物车服务
 * @author zy
 *
 */

import java.util.List;

import com.pinyougou.pojogroup.Cart;
//test2
public interface CartService {

	/**
	 * 添加商品到购物车
	 * @return
	 */
	public List<Cart> addGoodsToCartList(List<Cart> list,Long itemId,Integer num);
	
	/**
	 * 从Redis中获取购物车信息
	 * @param username
	 * @return
	 */
	public List<Cart> findCartListFromRedis(String username);
	
	/**
	 * 将购物车信息存入redis
	 * @param username
	 * @param cartList
	 */
	public void saveCartListToRedis(String username, List<Cart> cartList);
	
	/**
	 * 将cookie中保存的购物车信息合并到redis中
	 * @param list1
	 * @param list2
	 * @return
	 */
	public List<Cart> mergeCartList(List<Cart> list1,List<Cart> list2);
}
