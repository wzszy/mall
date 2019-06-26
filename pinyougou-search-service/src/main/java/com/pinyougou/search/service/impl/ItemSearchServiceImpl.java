package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public Map search(Map searchMap) {
		Map map = new HashMap();
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));
		
		//查询列表(页面主体内容)
		map.putAll(searchList(searchMap));
		//查询分类
		List<String> categoryList = searchGroupList(searchMap);
		map.put("categoryList", categoryList);
		
		//查询品牌和规格
		String category = (String) searchMap.get("category");
		if (category.equals("")) {
			if (categoryList.size()>0) {
				Map brandAndSpecMap = searchBrandAndSpec(categoryList.get(0));
				map.putAll(brandAndSpecMap);
			}
		}else {
			map.putAll(searchBrandAndSpec(category));
		}

		return map;
	}
	

	/**
	 * 查询列表
	 * @param searchMap 返回map利于扩展
	 * @return
	 */
	private Map searchList(Map searchMap) {
		Map map = new HashMap();
		
		//设置关键字高亮
		HighlightQuery query = new SimpleHighlightQuery();
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域(可多个)
		highlightOptions.setSimplePrefix("<em style='color:red'>");//设置高亮样式<em style='color:red'> 关键字 </em>
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions );
		
		//1.1查询关键字
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
			
		//1.2查询分类
		if (!"".equals(searchMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria categoryFilter = new Criteria("item_category").is(searchMap.get("category"));
			filterQuery.addCriteria(categoryFilter);
			query.addFilterQuery(filterQuery);
		}
		
		//1.3查询品牌
		if (!"".equals(searchMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria brandFilter = new Criteria("item_brand").is(searchMap.get("brand"));
			filterQuery.addCriteria(brandFilter );
			query.addFilterQuery(filterQuery );
		}
		
		//1.4查询规格
		if (searchMap.get("spec")!=null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			Set<String> keySet = specMap.keySet();
			for (String key : keySet) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
				filterQuery.addCriteria(filterCriteria );
				query.addFilterQuery(filterQuery );
			}
		}
		
		//1.5查询价格区间
		if (!"".equals(searchMap.get("price"))) {
			String[] price = ((String)searchMap.get("price")).split("-");
			if (!price[0].equals("0")) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria brandFilter = new Criteria("item_price").greaterThanEqual(price[0]);
				filterQuery.addCriteria(brandFilter );
				query.addFilterQuery(filterQuery );
			}
			if (!price[1].equals("*")) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria brandFilter = new Criteria("item_price").lessThanEqual(price[1]);
				filterQuery.addCriteria(brandFilter );
				query.addFilterQuery(filterQuery );
			}
		}

		//1.6设置分页
		Integer pageNum = (Integer) searchMap.get("pageNum");
		Integer pageSize = (Integer)searchMap.get("pageSize");
		if (pageNum==null) {
			pageNum=1;
		}
		if (pageSize==null) {
			pageSize=20;
		}
		Integer startIndex = (pageNum - 1)* pageSize;
		query.setOffset(startIndex);
		query.setRows(pageSize);

		//1.7价格排序
		String sortValue = (String) searchMap.get("sort");
		String sortField = (String) searchMap.get("sortField");
		if (!sortValue.equals("")) {
			if (sortValue.equals("ASC")) {
				Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
				query.addSort(sort);
			}
			if (sortValue.equals("DESC")) {
				Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
				query.addSort(sort);
			}
		}
		
		
		
		
		//******************************//
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : entryList) {
			List<Highlight> highlights = highlightEntry.getHighlights();
			/*for (Highlight highlight : highlights) {
				List<String> list = highlight.getSnipplets();
				System.out.println(list);
			}*/
			if (highlights.size()>0&&highlights.get(0).getSnipplets().size()>0) {
				TbItem item = highlightEntry.getEntity();
				item.setTitle(highlights.get(0).getSnipplets().get(0));	
			}
		}
		map.put("rows", page.getContent());
		map.put("totalPage", page.getTotalPages());
		map.put("totalCounts", page.getTotalElements());
		return map;
	}
	/**
	 * 查询分类
	 * @param searchMap
	 * @return
	 */
	private List<String> searchGroupList(Map searchMap) {
		List<String> list = new ArrayList<>();
		//查询所有
		Query query = new SimpleQuery("*:*");
		
		//查询关键字
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query , TbItem.class);
		//获取分组对象
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
		for (GroupEntry<TbItem> groupEntry : entryList) {
			list.add(groupEntry.getGroupValue());
		}
		return list;
	}
	/**
	 * 从缓存中查询品牌和规格列表
	 * @param categoryName
	 * @return
	 */
	private Map searchBrandAndSpec(String categoryName) {
		Map map = new HashMap();
		Long categoryId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
		if (categoryId!=null) {
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(categoryId);
			List specList = (List) redisTemplate.boundHashOps("specList").get(categoryId);
			map.put("brandList", brandList);
			map.put("specList", specList);
		}
		return map;

	}


	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		
	}


	@Override
	public void deleteByGoodsIds(List goodsIds) {
		SolrDataQuery query = new SimpleQuery("*:*");
		Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
		query.addCriteria(criteria);
		solrTemplate.delete(query );
		solrTemplate.commit();
		
	}

}
