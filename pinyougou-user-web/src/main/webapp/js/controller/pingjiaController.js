app.controller("pingjiaController",function($scope,$location,orderService,$http){

	//评价
	$scope.getEvaluate=function(){
		var orderId = $location.search()["orderId"];
		alert(orderId)
		orderService.findOrderDetail(orderId).success(
				function(response){
					$scope.entity=response;
				}
		)
	}
	
	$scope.update=function(orderId){
		orderService.update(orderId).success(
		
				function(response){
					if(response.success){
						location.href="home-index.html";
						
					}else{
						alert(response.message)
					}
					
				}
		)
		
	}

});