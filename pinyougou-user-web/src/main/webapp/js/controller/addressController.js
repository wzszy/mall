 //控制层 
app.controller('addressController' ,function($scope,$controller ,loginService  ,addressService){	
	
	//$controller('baseController',{$scope:$scope});//继承
	
	$scope.showName=function(){
		loginService.showName().success(
			function(response){
				$scope.loginName=response.loginName;
				$scope.headPic=response.headPic;
			}
		)
		
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		addressService.findOne(id).success(
			function(response){
				$scope.newAddress = response;					
			}
		);				
	}
	
//	//保存 
//	$scope.save=function(){				
//		var serviceObject;//服务层对象  				
//		if($scope.entity.id!=null){//如果有ID
//			serviceObject=addressService.update( $scope.entity ); //修改  
//		}else{
//			serviceObject=addressService.add( $scope.entity  );//增加 
//		}				
//		serviceObject.success(
//			function(response){
//				if(response.success){
//					//重新查询 
//		        	$scope.reloadList();//重新加载
//				}else{
//					alert(response.message);
//				}
//			}		
//		);				
//	}
	
	//保存 
	$scope.saveAddress=function(){
		
		var serviceObject;//服务层对象  				
		if($scope.newAddress.id!=0){//如果有ID
			serviceObject=addressService.update($scope.newAddress); //修改  
		}else{
			serviceObject=addressService.add($scope.newAddress);//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//清空列表数据
					$scope.newAddress={};
					location.reload();
				}else{
					alert(response.message);
				}
			}		
		);		
	}
	

	//查询收货地址列表
	$scope.findAddressList=function(){
		addressService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				for (var i = 0; i < $scope.addressList.length; i++) {
					if ($scope.addressList[i].isDefault=="1") {
						$scope.addressDefault=$scope.addressList[i];
						break;
					}
				}
			}	
		)
	}
	//设为默认地址
	$scope.setToDeafult=function(address){
		addressService.setToDeafult(address).success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findAddressList();//重新加载
				}else{
					alert(response.message);
				}
			}
		)
	}
	
	//删除
	$scope.dele=function(address){
		if (address.isDefault == "1") {
			alert("默认地址不能删除！");
		}else{
			addressService.dele(address).success(
					function(response){
						if(response.success){
							//重新查询 
							alert(response.message);
				        	$scope.findAddressList();//重新加载
						}else{
							alert(response.message);
						}
					}
				)
		}
		
	}
	
	
	//查询省份列表
	$scope.selectProvince=function(){
		addressService.findProvince().success(
			function(response){
				$scope.provinceList=response;
			}
		)
	}
	//根据省份列表查询市级列表
	$scope.$watch("newAddress.provinceId",function(newValue,oldValue){	
		addressService.findCity(newValue).success(
			function(response){
				$scope.cityList=response;
			}
		)	
	})	
	//根据市级列表查询县级列表
	$scope.$watch("newAddress.cityId",function(newValue,oldValue){		
		addressService.findarea(newValue).success(
			function(response){
				$scope.areaList=response;
			}
		)	
	})	
	
});	
