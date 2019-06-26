app.controller("indexController",function($scope,$location,$controller,$interval,loginService,orderService,$http){
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.showName=function(){
		loginService.showName().success(
			function(response){
				$scope.loginName=response.loginName;
				$scope.headPic=response.headPic;
			}
		)
		
	}
	
//	//根据登录用户名查询用户数据
//	$scope.findOne=function(){
//		userService.findOne().success(
//			function(resonse){
//				$scope.entity=resonse;
//			}
//		)
//	}
	
	//查询用户订单 
	$scope.findList=function(status){
		orderService.findList(status).success(
			function(response){
				$scope.orderList=response;
				
			}			
		);
	}    
	
	
	$scope.totalPage = [];
	//分页查询用户订单 
	$scope.findListByPage=function(page,status){
		if (page<1) {
			return;
		}		
		if (page != 1 && page>$scope.totalPage) {
			//page = $scope.totalPage;
			return;
		}
		orderService.findPage(page,10,status).success(	
			function(response){
				$scope.orderList=response.rows;	
				$scope.totalCounts=response.total;//总记录数
				$scope.totalPage=Math.ceil($scope.totalCounts/10)
				$scope.pageNums = new Array($scope.totalPage);
				for (var i = 0; i < $scope.pageNums.length; i++) {
					$scope.pageNums[i]=i+1;
				}
				$scope.currentPage = page;
				
				$scope.jumpPage="";
				window.scrollTo(0,0);
			}	
		);
	}  
	
	
	$scope.timeString={};
	$scope.allsecond={};
	$scope.getTimeString=function(order){
		$scope.allsecond[order.consignTime] =1296000 - Math.floor( (new Date().getTime() - new Date(order.consignTime).getTime()  )/1000 );
		time = $interval(function(){
			$scope.allsecond[order.consignTime] = parseInt($scope.allsecond[order.consignTime]) - 1;
			
			$scope.timeString[order.consignTime] = convertTimeString($scope.allsecond[order.consignTime],order);
			if ($scope.allsecond[order.consignTime]<=0) {
				$interval.cancel(time);
			}
		},1000);
	}
	

	//转换秒数为 天  小时   分  秒 
	convertTimeString=function(allsecond,order){
		allsecond = parseInt(allsecond);
		
		var days = Math.floor( allsecond/(60*60*24) );
		var hours = Math.floor( (allsecond- days*60*60*24)/(60*60) );
		var minutes = Math.floor( (allsecond - days*60*60*24 - hours*60*60)/60 );
		var seconds = Math.floor( allsecond - days*60*60*24 - hours*60*60 - minutes*60 );
		var timeString = "";
		if (days>0) {
			timeString += days +"天" ;//15天
			if (hours>0) {
				timeString += hours +"小时";//15天3小时
			}
		}else{
			if (hours>0) {
				timeString += hours +"小时";//3小时
				if (minutes>0) {
					timeString += minutes +"分";//3小时15分钟
				}
			}else{
				if (minutes>0) {
					timeString += minutes +"分";//15分钟
				}else{
					if (seconds>0) {
						timeString += seconds +"秒";//59秒
					}else{
						$scope.modifyOrder(order,5);
					}
				}
			}
		}
		return timeString;
	}
	//判断是否为当前页
	$scope.isCurrentPage=function(page){
		if (page == $scope.currentPage) {
			return true;
		}else{
			return false;
		}
	}
	
	//默认地址在最上方
	$scope.isFirst= function(index){
		if (index == 0) {
			//alert("true");
			return true;
		}else{
			return false;
		}
	}
	
	
	$scope.pay=function(orders){
		$http.get("http://localhost:9107/getOrderInfo.html",orders)
	}
	
	//提交订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;
		$scope.order.receiverMobile=$scope.address.mobile;
		$scope.order.receiver=$scope.address.contact;
		
		orderService.submitOrder($scope.order).success(
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

	//添加支付日志
	$scope.savePaylog=function(orderId){
		orderService.savePaylog(orderId).success(
			function(response){
				if (response.success) {
					location.href="pay.html";
				}else{
					location.href="payfail.html";
				}
			}
		)
	}
	//合并支付日志
	$scope.saveMutPaylog=function(){
		orderService.saveMutPaylog($scope.selectIds).success(
			function(response){
				if (response.success) {
					location.href="pay.html";
				}else{
					location.href="payfail.html";
				}
			}
		)
	}
	
	
	//确认收货/取消订单
	$scope.modifyOrder=function(order,status){
		if (status == 6) {
			var flag = confirm("是否确定取消订单？");
			if(flag == false){
				return;
			}
		}
		orderService.modifyOrder(order,status).success(
			function(response){
				if (response.success) {
					location.reload();
				}else{
					
				}
			}
		);
	}
	
	
	
	//交易状态
	$scope.payStatus=["","未付款","已付款","未发货","已发货","交易成功","已取消","待评价"];
	$scope.payType=["","微信支付","货到付款"];
	//查询订单详情
	$scope.findOrderDetail=function(){
		var orderId = $location.search()["orderId"];
		//alert("orderId："+orderId);
		orderService.findOrderDetail(orderId).success(
			function(response){
				$scope.entity = response;
				$scope.totalMoney=0;
				for (var i = 0; i < $scope.entity.tbOrderItems.length; i++) {
					$scope.totalMoney += $scope.entity.tbOrderItems[i]["price"]*$scope.entity.tbOrderItems[i]["num"];
				}
				//创建时间
				if ($scope.entity.tbOrder.createTime != null) {
					$scope.createDate = $scope.entity.tbOrder.createTime.split(" ")[0];
					$scope.createTime = $scope.entity.tbOrder.createTime.split(" ")[1];
				}
				//付款时间
				if ($scope.entity.tbOrder.paymentTime != null) {
					$scope.paymentDate = $scope.entity.tbOrder.paymentTime.split(" ")[0];
					$scope.paymentTime = $scope.entity.tbOrder.paymentTime.split(" ")[1];
				}else{
					$scope.entity.tbOrder.paymentType="0";
				}
				//发货时间
				if ($scope.entity.tbOrder.consignTime != null) {
					$scope.consignDate = $scope.entity.tbOrder.consignTime.split(" ")[0];
					$scope.consignTime = $scope.entity.tbOrder.consignTime.split(" ")[1];
				}
				//交易完成时间
				if ($scope.entity.tbOrder.endTime != null) {
					$scope.endDate = $scope.entity.tbOrder.endTime.split(" ")[0];
					$scope.endTime = $scope.entity.tbOrder.endTime.split(" ")[1];
				}
				//交易关闭时间
				if ($scope.entity.tbOrder.closeTime != null) {
					$scope.closeDate = $scope.entity.tbOrder.closeTime.split(" ")[0];
					$scope.closeTime = $scope.entity.tbOrder.closeTime.split(" ")[1];
				}
				
				
			}
		);
		
	}
	
	
	
	
	
	
	
});