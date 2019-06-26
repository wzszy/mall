//服务层
app.service('addressService',function($http){
	    	
	//查询实体
	this.findOne=function(id){
		return $http.get('../address/findOne.do?id='+id);
	}
	
	
	//删除
	this.dele=function(address){
		return $http.post('address/delete.do',address);
	}
   	
	//查询收货地址列表
	this.findAddressList=function(){
		return $http.get("address/findListByLoginUser.do");
	}
	
	//新增收货地址
	this.add=function(address){
		return $http.post("address/addAddress.do",address);
	}
	//修改 
	this.update=function(address){
		return  $http.post('address/update.do',address );
	}
	//设为默认地址
	this.setToDeafult=function(address){
		return $http.post("address/setToDeafult.do",address);
	}
	
	//查询省份 列表
	this.findProvince=function(){
		return $http.get("address/findProvince.do");
	}
	//查询市级 列表
	this.findCity=function(provinceId){
		return $http.get("address/findCity.do?provinceId="+provinceId);
	}
	//查询县级 列表
	this.findarea=function(cityid){
		return $http.get("address/findarea.do?cityid="+cityid);
	}
});
