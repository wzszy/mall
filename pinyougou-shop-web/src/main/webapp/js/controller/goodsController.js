 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){	
		var id = $location.search()["id"]
		if (id==null) {
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				console.info(response);
				editor.html(response.goodsDesc.introduction);//富文本信息
				$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);//图片显示
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				console.info($scope.entity.goodsDesc.specificationItems);
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
				for (var i = 0; i < $scope.entity.itemList.length; i++) {
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		$scope.entity.goodsDesc.introduction = editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//情况列表数据
					/*alert(response.message);
					$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
					editor.html("");*/
					location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	/*//新增商品 
	$scope.add=function(){	
		$scope.entity.goodsDesc.introduction = editor.html();
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					//情况列表数据
					alert(response.message);
					$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
					editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}*/
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//文件上传
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
			function(response){
				if (response.success) {
					//$scope.image_entity=response;
					$scope.image_entity.url=response.message;
				}else{
					alert(response.message);
				}
			}
		)
	}
	//将上传的图片添加到列表展示
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//从列表中删除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	
	//查询一级列表
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(
			function(response){
				$scope.itemCat1List=response;
			}
		)
	}
	//根据一级列表查询二级列表
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){		
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat2List=response;
				$scope.itemCat3List=[];
				//$scope.entity.goods.typeTemplateId="";	
				//$scope.template=[];
				//$scope.specList=[];
			}
		)	
	})	
	//根据二级列表查询三级列表
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat3List=response;
				//$scope.entity.goods.typeTemplateId="";	
				//$scope.template=[];
				//$scope.specList=[];
			}
		)
	})
	//根据三级列表改变获取模板ID
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
			function(response){
				$scope.entity.goods.typeTemplateId=response.typeId;
			}
		)
	})
	
	//根据模板ID获取品牌下拉列表/扩展属性/规格
	$scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){
		if (newValue) {
			typeTemplateService.findOne(newValue).success(
				function(response){
					//品牌下拉列表
					$scope.template=response;
					$scope.template.brandIds=JSON.parse($scope.template.brandIds);
					//扩展属性
					if ($location.search()["id"] == null) {
						$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.template.customAttributeItems);
					}
				}
			)
			//规格选项 
			typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.specList=response;
					/*[{"id":27,
						"text":"网络",
						"options":[{"id":98,"optionName":"移动3G","orders":1,"specId":27},{}....]  },{}....]*/
				}
			)
		}
		
	})
	
	//规格选项
	$scope.updateSpecAttribute=function($event,name,value){	
			//[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
			//{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
		if (object!=null) {
			if ($event.target.checked) {//判断复选框是否选中
				object.attributeValue.push(value);
			}else{
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				if (object.attributeValue.length==0) {
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
	}
	
	//
	/*$scope.createItemList=function(){
		var items = $scope.entity.goodsDesc.specificationItems;
		$scope.itemList = [{spec:{},price:0,num:0,status:'',isDafult:''}];
		//遍历items集合
		for (var i = 0; i < items.length; i++) {
			//items[i] = {"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
			var newList = [];
			for (var j = 0; j < $scope.itemList.length; j++) {
				//$scope.itemList[j] = {spec:{},price:0,num:0,status:'',isDafult:''}
				var oldRow = $scope.itemList[j];
				for (var k = 0; k < items[i].attributeValue.length; k++) {
					//items[i].attributeValue[k] = "移动3G"
					var newRow = JSON.parse(JSON.stringify(oldRow));
					newRow.spec[items[i].attributeName]=items[i].attributeValue[k];
					newList.push(newRow);
				}
			}
			$scope.itemList = newList;
		}	
	} */
	$scope.createItemList=function(){
		var items = $scope.entity.goodsDesc.specificationItems;
		$scope.entity.itemList = [{spec:{},price:0,num:0,status:'',isDafult:''}];
		for (var i = 0; i < items.length; i++) {
			$scope.entity.itemList = $scope.addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
		}
	}
	
	$scope.addColumn=function(list,name,value){
		var newList = [];
		for (var i = 0; i < list.length; i++) {
			var oldRow = list[i];
			//{spec:{},price:0,num:0,status:'',isDafult:''}
			for (var j = 0; j < value.length; j++) {
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[name]=value[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	
	$scope.status=["未审核","审核通过","审核未通过","已关闭"];
	
	$scope.itemCat = [];
	$scope.findItemCat=function(){
		itemCatService.findAll().success(
			function(response){
				for (var i = 0; i < response.length; i++) {
					$scope.itemCat[response[i].id] = response[i].name;
				}
			}
		)
	}
	
	$scope.$watch("searchEntity.auditStatus",function(newValue,oldValue){
		$scope.reload();
	})
	
	$scope.checkAttributeValue=function(specName,optionName){
		var items = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(items,"attributeName",specName);	
		if (object==null) {
			return false;
		}
		if (object.attributeValue.indexOf(optionName)<0) {
			return false;
		}
		return true;
			
	}
	//产品上下架
	$scope.updateMarketable=function(id,status){
		goodsService.updateMarketable(id,status).success(
			function(response){
				if (response.success) {
					$scope.reload();	
				}
				//alert(response.message);
			}
		)
	}
	
});	


















