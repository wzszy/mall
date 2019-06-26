 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.findEntityType=function(typeId){
		typeTemplateService.findNameById(typeId).success(
			function(response){			
				
				var typeModel = $("{data:response}").select2();
				typeModel.val(response).trigger("change");
			}
		)
	}
	$scope.entity={type:{data:[]}};
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
				
				$scope.findEntityType(response.typeId);
				
				
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象 
		$scope.entity.typeId=$scope.entity.type.id;
		$scope.entity.parentId=$scope.parentId;
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findByParentId($scope.parentId);//刷新列表
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	//新建
	$scope.add=function(){							
		itemCatService.add( $scope.entity).success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findByParentId($scope.parentId);//刷新列表
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.parentId);//刷新列表
				}else{
					alert(response.message);
				}						
			}		
		);				
	}
	
	//根据id删除
	$scope.delById=function(id){
		itemCatService.delById(id).success(
			function(response){
				if(response.success){
					//重新查询 
					$scope.findByParentId($scope.parentId);//刷新列表
				}else{
					alert(response.message);
				}
			}
		)
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.findByParentId=function(parentId){
		$scope.parentId=parentId;
		itemCatService.findByParentId(parentId).success(
			function(response){
				$scope.list=response;
			}
		)
	}
	
	
	//面包屑
	$scope.grade=1;//初始等级为一
	$scope.setGrade=function(value){
		$scope.grade=value;
	}
	$scope.parentId=0;
	$scope.entity_1=null;
	$scope.entity_2=null;
	
	$scope.selectList=function(parent_entity){
		if ($scope.grade==1) {
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if ($scope.grade==2) {
			$scope.entity_1=parent_entity;
			$scope.entity_2=null;
		}
		if ($scope.grade==3) {
			$scope.entity_2=parent_entity;
		}
		$scope.findByParentId(parent_entity.id)
	}
	
	$scope.reloadList=function(){
		$scope.findByParentId($scope.parentId);//刷新列表
	}
	
	//商品类型下拉框数据
	$scope.typeList={data:[]}
	$scope.findTypeIdList=function(){
		typeTemplateService.selectOptionList().success(
			function(response){
				$scope.typeList={data:response}
			}
		)
	}
	
    
});	
