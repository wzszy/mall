package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Cart;

import entity.Result;

@RestController
@RequestMapping("cart")
public class CartController {
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Reference(timeout=10000)
	private CartService cartService;
	
	
	@RequestMapping("addGoodsToCartList")
	@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
	public Result addGoodsToCartList(Long itemId ,Integer num) {
		
//		response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");//设置响应头，允许跨域请求
//		response.setHeader("Access-Control-Allow-Credentials","true");//允许跨域cookie操作（客户端也需要设置）
		
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登录用户: "+name);
		
		try {
			//从cookie中读取购物车
			List<Cart> cartList = findCartList();		
			//添加商品到购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			if (name.equals("anonymousUser")) {//如果未登录
				//将购物车存入cookie
				System.out.println("向cookie中存入购物车信息: ");//============================
				String cartListString = JSON.toJSONString(cartList);
				utils.CookieUtil.setCookie(request, response, "cartList", cartListString, 86400, "UTF-8");
			}else {
				//将购物车存入redis
				cartService.saveCartListToRedis(name, cartList);
			}
			return new Result(true, "添加到购物车");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(true, "添加购物车失败");
		}
		
		
	}
	
	/**
	 * 从cookie中读取购物车信息
	 * @return
	 */
	@RequestMapping("findCartList")
	public List<Cart> findCartList(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登录用户: "+name);
		System.out.println("从cookie中提取购物车信息: ");//=============================
		String cartListString = utils.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartListString == null || cartListString.equals("")) {
			cartListString="[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListString,Cart.class);
		
		if (name.equals("anonymousUser")) {//如果未登录
			return cartList_cookie;
		}else {//如果已登录
			List<Cart> cartList_redis = cartService.findCartListFromRedis(name);
			if (cartList_cookie.size()>0) {//第一次登录cookie中存在购物车信息
				//将cookie中保存的购物车信息合并到redis
				List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
				//合并后的购物车保存到redis
				cartService.saveCartListToRedis(name, cartList);
				//清除cookie
				utils.CookieUtil.deleteCookie(request, response, "cartList");
				System.out.println("合并购物车信息");
				return cartList;
				
			}
			//cookie中没有购物车信息，直接返回redis
			return cartList_redis;
					
		}
		
	}
	
	
	
}
