app.controller("baseController",function($scope){
	//分页控件配置
	$scope.paginationConf = {
		currentPage: 1,
		totalItems: 10,
		itemsPerPage: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
			$scope.reload();//重新加载
		}
	};

	//刷新列表
	$scope.reload=function(){
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
	}
	
	//获取选中的id集合
	$scope.selectIds = [];//用户勾选的id的集合
	$scope.selected=function($event,id){
		if ($event.target.checked) {//如果复选框被选中
			$scope.selectIds.push(id);//将id添加到集合中
		}else{
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index,1);
		}
	}
	
	//转换json显示格式  [{"id":26,"text":"尺码"}]-->
	$scope.jsonToString=function(jsonString,key){
		var json = JSON.parse(jsonString);
		var value = "";
		for (var i = 0; i < json.length; i++) {
			if (i>0) {
				value += ",";
			}
			value += json[i][key];
		}
		return value;
	}
	
	$scope.searchObjectByKey=function(list,key,keyName){
		for (var i = 0; i < list.length; i++) {
			if (list[i][key]==keyName) {
				return list[i];
			}
		}
		return null;
	}
	
	
	
})
