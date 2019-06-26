package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;

public class Orders implements Serializable{
	private TbOrder tbOrder;
	private TbPayLog tbPayLog;
	private List<TbOrderItem> tbOrderItems;
	private Integer orderNum;
	
	public Orders() {
		super();
	}
	public Orders(TbOrder tbOrder, List<TbOrderItem> tbOrderItems,Integer orderNum) {
		super();
		this.tbOrder = tbOrder;
		this.tbOrderItems = tbOrderItems;
		this.orderNum = orderNum;
	}
	public Orders(TbOrder tbOrder, List<TbOrderItem> tbOrderItems,Integer orderNum,TbPayLog tbPayLog) {
		super();
		this.tbOrder = tbOrder;
		this.tbOrderItems = tbOrderItems;
		this.orderNum = orderNum;
		this.tbPayLog = tbPayLog;
	}
	
	public TbPayLog getTbPayLog() {
		return tbPayLog;
	}
	public void setTbPayLog(TbPayLog tbPayLog) {
		this.tbPayLog = tbPayLog;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	
	public List<TbOrderItem> getTbOrderItems() {
		return tbOrderItems;
	}
	public void setTbOrderItems(List<TbOrderItem> tbOrderItems) {
		this.tbOrderItems = tbOrderItems;
	}
	public TbOrder getTbOrder() {
		return tbOrder;
	}
	public void setTbOrder(TbOrder tbOrder) {
		this.tbOrder = tbOrder;
	}
	
	
}
