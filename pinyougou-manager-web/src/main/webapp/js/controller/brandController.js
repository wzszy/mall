app.controller("brandController",function($scope,$http,$controller,brandService){
	$controller("baseController",{$scope:$scope});
	$scope.findAll=function(){
		brandService.findAll().success( 
			function(response) {
				$scope.list=response;
			
		});
	}
	
	

	//分页查询所有
	$scope.findPage=function(page,size){
		brandService.findPage().success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}	
		);
	}
	
	//添加
	$scope.save=function(){
		var object = null;
		if($scope.entity.id!=null){
			object = brandService.update($scope.entity);
		}else{
			object = brandService.add($scope.entity);
		}
		object.success(
			function(response) {
				if (response.success) {
					$scope.reload();
				}else {
					alert(response.message);
				}
			}		
		)
	}
	
	//根据id查询
	$scope.findOne=function(id){
		brandService.findOne(id).success(
			function(response){
				$scope.entity=response;
			}		
		)
	}
	
	
	//删除选中
	$scope.delSelect=function(){
		brandService.delSelect($scope.selectIds).success(
			function(response) {
				if (response.success) {
					$scope.reload();
				}else {
					alert(response.message);
				}
			}		
		)
	}
	
	
	/* //分页查询所有
	$scope.brandEntity={};
	$scope.search=function(page,size){
		brandService.search(page,size,$scope.brandEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}	
		);
	} */
	
	//分页查询所有
	$scope.brandEntity={};
	$scope.search=function(page,size){
		$http.post("../brand/search.do?page="+page +"&size="+size,$scope.brandEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}	
		);
	}
	
});
