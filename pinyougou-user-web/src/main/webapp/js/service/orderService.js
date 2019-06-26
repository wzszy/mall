//服务层
app.service('orderService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../order/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows,status){
		return $http.get('order/findPage.do?page='+page+'&rows='+rows+'&status='+status);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../order/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../order/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../order/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../order/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../order/search.do?page='+page+"&rows="+rows, searchEntity);
	}    
	
	//查询用户订单
	this.findList=function(status){
		return $http.get('order/findListByUserId.do?status='+status);		
	}
	

	//提交订单到支付日志
	this.savePaylog=function(orderId){
		return $http.get("order/savePaylog.do?orderId="+orderId);
	}
	
	//提交订单到支付日志
	this.saveMutPaylog=function(orderIds){
		return $http.post("order/saveMutPaylog.do",orderIds);
	}
	//查询订单详情
	this.findOrderDetail=function(orderId){
		return $http.get("order/findOrderDetail.do?orderId="+orderId);
	}
	//确认收货
	this.modifyOrder=function(order,status){
		return  $http.post('order/modifyOrder.do?status='+status,order );
	}
	
});
