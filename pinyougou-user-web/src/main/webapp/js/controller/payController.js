app.controller("payController",function($scope,$location,payService){

	$scope.createNative=function(){
		payService.createNative(/*$scope.userId,$scope.orderId*/).success(
			function(response){
				//显示订单号和金额
				$scope.money=(response.total_fee/100).toFixed(2);
				$scope.out_trade_no=response.out_trade_no;
				
				//生成二维码
				var qr = new QRious({
					element:document.getElementById('qrious'),
					size:250,
					value:response.code_url,
					level:'H'
				});
				queryPayStatus();//生成支付二维码后不停查询支付状态
			}
		)
	}
	
	//查询支付状态
	queryPayStatus=function(){
		payService.queryPayStatus($scope.out_trade_no).success(
			function(response){
				if (response.success) {
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if (response.message == '二维码超时') {
						$scope.createNative();
						//alert('二维码超时');
					}else{
						location.href="payfail.html";
					}
				}
			}
		)
	}
	
	//支付成功页面显示金额
	$scope.getMoney=function(){
		return $location.search()['money'];
		
	}
	
	/*//支付金额和订单ID
	$scope.getUserId=function(){
		$scope.orderId = $location.search()['orderId'];
//		$scope.totalFee = $location.search()['totalFee'];
		
		$scope.userId = $location.search()['userId'];
		
	}*/
	
})