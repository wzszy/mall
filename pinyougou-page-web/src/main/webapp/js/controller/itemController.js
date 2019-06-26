app.controller("itemController",function($scope,$http){
	
	$scope.specificationItems={};//存储用户选择的规格
	
	$scope.addNum=function(n){
		$scope.num += n;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;
		searchSku();
	}
	
	$scope.isSelect=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.sku={};
	
	$scope.loadSku =function(){
		$scope.sku=skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
		
	}
	
	matchObject=function(map1,map2){
		for(var key in map1){
			if(map1[key]!=map2[key]){
				return false;
			}
		}
		
		for(var key in map2){
			if(map2[key]!=map1[key]){
				return false;
			}
		}
		return true;
	}
	
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if (matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:"",price:0}
	}
	
	//添加购物车
	$scope.addToCart=function(){
		alert($scope.sku.id);
		$http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true}).success(
				function(response){
					if (response.success) {
						location.href="http://localhost:9107/cart.html";
					}else {
						alert(response.message);
					}
				}
		)
	}
});
