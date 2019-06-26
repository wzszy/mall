app.controller("seckillGoodsController",function($scope,$location,$interval,seckillGoodsService){
	//查询列表
	$scope.findList=function(){
		seckillGoodsService.findList().success(
			function(response){
				$scope.list=response;
			}
		);
	}
	
	//查询商品
	$scope.findOne=function(){
		//获取传入的参数id
		var id = $location.search()['id'];
		seckillGoodsService.findOne(id).success(
			function(response){
				$scope.entity=response;
				
				//$interval(参数一，参数二，参数三)①执行逻辑②间隔时间(毫秒)③次数
				allsecond = Math.floor( (new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000 );
				time = $interval(function(){
					allsecond = allsecond - 1;
					$scope.timeString = convertTimeString(allsecond);
					if (allsecond<=0) {
						$interval.cancel(time);
					}
				},1000);
			}	
		);
	}
	
	//转换秒数为 天  小时   分  秒 
	convertTimeString=function(allsecond){
		var days = Math.floor( allsecond/(60*60*24) );
		var hours = Math.floor( (allsecond- days*60*60*24)/(60*60) );
		var minutes = Math.floor( (allsecond - days*60*60*24 - hours*60*60)/60 );
		var seconds = Math.floor( allsecond - days*60*60*24 - hours*60*60 - minutes*60 );
		var timeString = "";
		if (days>0) {
			timeString += days +"天";
		}
		if (hours<10) {
			timeString += "0";
		}
		timeString += hours +":";
		if (minutes<10) {
			timeString += "0";
		}
		timeString += minutes +":"		
		if (seconds<10) {
			timeString += "0";
		}
		return timeString + seconds;
	}
	
	
	//秒杀下单
	$scope.submitOrder=function(){
		seckillGoodsService.submitOrder($scope.entity.id).success(
			function(response){
				if (response.success) {
					alert("请在5分钟内完成支付");
					location.href="pay.html";
				}else{
					alert(response.message);
				}
			}
		);
	}
});