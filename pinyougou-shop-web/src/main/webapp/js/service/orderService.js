app.service('orderService',function($http){
	
	
	//删除
	this.del=function(ids){
		return $http.get('../order/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../order/search.do?pageNum='+page+"&pageSize="+rows, searchEntity);
	} 
	//查询指定订单详情
	this.findOne = function(id){
		return $http.get('../order/findOne.do?id='+id);
	}
	//更新发货状态
	this.updateStatus = function(id){
		return $http.get('../order/updateStatus.do?id='+id);
	}
});