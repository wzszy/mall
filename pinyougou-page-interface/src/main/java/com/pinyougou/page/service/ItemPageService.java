package com.pinyougou.page.service;

public interface ItemPageService {
	boolean genItemHtml(Long goodsId);
	
	boolean deleteItemHtml(Long[] goodsIds);
}
