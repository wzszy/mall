app.controller("cartController",function($scope,cartService){
	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				$scope.totalValue=cartService.getSum(response);
			}
			
		)
	}
	
	
	
	//购物车中加减商品数量
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				
				if (response.success) {
					$scope.findCartList();
				}else{
					alert(response.message);
				}
			}
		)
	}
	
	/*getSum=function(){
		$scope.totalNum=0;
		$scope.totalMoney=0;
		for (var i = 0; i < $scope.cartList.length; i++) {
			var cart = $scope.cartList[i];//购物车对象
			for (var j = 0; j < cart.orderItemList.length; j++) {
				var item = cart.orderItemList[j];//购物车明细对象
				$scope.totalNum+=item.num;
				$scope.totalMoney+=item.totalFee;
			}
		}
		
	}*/
	
	//查询收货地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				for (var i = 0; i < $scope.addressList.length; i++) {
					if ($scope.addressList[i].isDefault=="1") {
						$scope.address=$scope.addressList[i];
						break;
					}
				}
			}	
		)
	}
	
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;
	}
	//判断当前地址是否是选中的地址
	$scope.isSelectedAddress=function(address){
		if ($scope.address==address) {
			return true;
		}else{
			return false;
		}
	}
	

	
	//保存 
	$scope.saveAddress=function(){
		alert($scope.newAddress.id);
		
		var serviceObject;//服务层对象  				
		if($scope.newAddress.id!=0){//如果有ID
			serviceObject=cartService.update($scope.newAddress); //修改  
		}else{
			alert($scope.newAddress.alias);
			serviceObject=cartService.add($scope.newAddress);//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//清空列表数据
					alert(response.message);
					$scope.newAddress={};
					location.reload();
				}else{
					alert(response.message);
				}
			}		
		);		
	}
	
	$scope.order={paymentType:"1"};
	//选择支付类型
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	//提交订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;
		$scope.order.receiverMobile=$scope.address.mobile;
		$scope.order.receiver=$scope.address.contact;
		
		cartService.submitOrder($scope.order).success(
			function(response){
				if(response.success){
					if ($scope.order.paymentType == "1") {//1  微信支付   2 货到付款
						location.href="pay.html";
					}else{
						location.href="paysuccess.html";
					}
				}else{
					location.href="payfail.html";
				}
			}
		)
	}
	
	
})