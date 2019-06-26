package com.pinyougou.user.controller;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.user.service.AddressService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbAddress> findAll(){			
		return addressService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return addressService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param address
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbAddress address){
		try {
			addressService.add(address);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbAddress findOne(Long id){
		return addressService.findOne(id);		
	}
	
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody TbAddress address){
		
		try {
			addressService.delete(address.getId());
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
	public PageResult search(@RequestBody TbAddress address, int page, int rows  ){
		return addressService.findPage(address, page, rows);		
	}
	
	@RequestMapping("findListByLoginUser")
	public List<TbAddress> findListByLoginUser(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		List<TbAddress> list = addressService.findListByUserId(name);
		return list;
	}
	
	/**
	 * 新增收货地址
	 * @param address
	 * @return
	 */
	@RequestMapping("addAddress")
	public Result addAddress(@RequestBody TbAddress address) {
		try {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			address.setCreateDate(new Date());
			address.setUserId(name);
			addressService.add(address);
			return new Result(true, "添加成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}
	
	/**
	 * 修改
	 * @param address
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbAddress address){
		try {
			addressService.update(address);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 设置默认地址
	 * @param address
	 * @return
	 */
	@RequestMapping("setToDeafult")
	public Result setToDeafult(@RequestBody TbAddress address) {
		try {
			addressService.setToDeafult(address);
			return new Result(true, "添加成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}
	
	//查询省份列表
	@RequestMapping("findProvince")
	public List<TbProvinces> findProvince(){
		return addressService.findProvince();
	}
	//查询市级列表
	@RequestMapping("findCity")
	public List<TbCities> findCity(String provinceId){
		return addressService.findCity(provinceId);
	}
	//查询市级列表
	@RequestMapping("findarea")
	public List<TbAreas> findarea(String cityid){
		return addressService.findarea(cityid);
	}
	
	
	
}
