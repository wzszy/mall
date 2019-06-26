package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
	public Map search(Map searchMap);
	
	void importList(List list);
	
	void deleteByGoodsIds(List goodsIds);
}
