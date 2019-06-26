package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
@Service
@Transactional
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper tbBrandMapper;
	@Override
	public List<TbBrand> findAll() {
		
		return tbBrandMapper.selectByExample(null);
	}
	@Override
	public PageResult findPage(Integer pageNum,Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>)tbBrandMapper.selectByExample(null);
		return  new PageResult(page.getTotal(), page.getResult());
	}
	@Override
	public void add(TbBrand brand) {
		tbBrandMapper.insert(brand);
	}
	@Override
	public TbBrand findById(Long id) {
		return tbBrandMapper.selectByPrimaryKey(id);
	}
	@Override
	public void update(TbBrand brand) {
		tbBrandMapper.updateByPrimaryKey(brand);
		
	}
	@Override
	public void delete(Long[] ids) {
		for(Long id : ids) {
			tbBrandMapper.deleteByPrimaryKey(id);
		}
		
	}
	@Override
	public PageResult findPage(TbBrand brand, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
	
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if (brand!=null) {
			if (brand.getName()!=null && brand.getName().length()>0) {
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if (brand.getFirstChar()!=null && brand.getFirstChar().length()>0) {
				criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
		}
		Page<TbBrand> page = (Page<TbBrand>)tbBrandMapper.selectByExample(example);
		return  new PageResult(page.getTotal(), page.getResult());
	}
	@Override
	public List<Map> selectOptionList() {
		return tbBrandMapper.selectOptionList();
	}
	
	

}
