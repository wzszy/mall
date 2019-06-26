package com.pinyougou.user.service;
import java.util.List;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface AddressService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbAddress> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbAddress address);
	
	
	/**
	 * 修改
	 */
	public void update(TbAddress address);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbAddress findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long id);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbAddress address, int pageNum,int pageSize);
	
	/**
	 * 根据用户名查询地址
	 * @param userId
	 * @return
	 */
	public List<TbAddress> findListByUserId(String userId);
	
	
	/**
	 * 修改默认地址
	 * @param address
	 */
	public void setToDeafult(TbAddress address);



	//查询省/市/区列表
	public List<TbProvinces> findProvince();
	public List<TbCities> findCity(String provinceId);
	public List<TbAreas> findarea(String cityid);
}
