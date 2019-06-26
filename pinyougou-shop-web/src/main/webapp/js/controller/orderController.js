 //控制层 
app.controller('orderController' ,function($scope,$controller ,orderService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	//设置支付方式，支付来源数字代表的
	$scope.paymentTypeList=['','在线支付','货到付款'];
	$scope.sourceTypeList=['','app端','pc端','M端','微信端','手机qq端'];
	$scope.statusList=['','未付款','待发货','','已发货','待评价','交易关闭','已评价']
	
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		orderService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		orderService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		orderService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=orderService.update( $scope.entity ); //修改  
		}else{
			serviceObject=orderService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reload();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.del=function(){			
		//获取选中的复选框			
		orderService.del( $scope.selectIds ).success(
				
			function(response){
				if(response.success){
					$scope.reload();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		orderService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	//查询订单详情 
	$scope.findOne=function(id){
		
		orderService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//更新订单发货状态
	$scope.updateStatus = function(orderId){
		orderService.updateStatus(orderId).success(function(response){
			alert(response.message);
			$scope.reload();//刷新列表
		});
	}
	
	//导出excel表单
	$scope.exportForm = function(){
		
		location.href="http://localhost:9102/order/findByOrderIds.do?orderIds="+$scope.selectIds;
		
	}
	

});	
