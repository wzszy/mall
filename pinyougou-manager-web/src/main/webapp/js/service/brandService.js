app.service("brandService",function($http){
	this.findAll=function(){
		return $http.get("../brand/findAll.do");
	} 
	this.findPage=function(page,size){
		return $http.get("../brand/findPage.do?page="+page +"&size="+size);
	}
	this.add=function(entity){
		return $http.post("../brand/add.do",entity);
	}
	this.update=function(entity){
		return $http.post("../brand/update.do",entity);
	}
	this.findOne=function(id){
		return $http.get("../brand/findById.do?id="+id);
	}
	this.delSelect=function(ids){
		return $http.get("../brand/delete.do?ids="+ids)
	}
	this.search=function(page,size,brandEntity){
		return $http.post("../brand/search.do?page="+page +"&size="+size,brandEntity)
	}
	this.selectOptionList=function(){
		return $http.get("../brand/selectOptionList.do")
	}
})