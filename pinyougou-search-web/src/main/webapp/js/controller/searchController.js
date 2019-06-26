app.controller("searchController",function($scope,$location,searchService){
	
	$scope.searchMap={"keywords":"","category":"","brand":"","spec":{},"price":"","pageNum":1,"pageSize":20,"sort":"","sortField":""};
	
	
	$scope.search=function(){
		$scope.searchMap.pageNum=parseInt($scope.searchMap.pageNum);
		
		searchService.search($scope.searchMap).success(
			function(rsponse){
				$scope.resultMap=rsponse;
				
				buildPageLabel(rsponse);
			}
		)
	}
	
	buildPageLabel=function(data){
		$scope.pageLabel=[];
		var startPage = 1;//显示开始页
		var lastPage=data.totalPage;//显示截止页
		$scope.firstDot=true;
		$scope.lastDot=true;
		
		if (lastPage > 5) {//总页数大于5
			if ($scope.searchMap.pageNum<=3) {//当前页小于3
				lastPage=5;
				$scope.firstDot=false;
			}else if ($scope.searchMap.pageNum >= lastPage-2){
				startPage = lastPage-4;
				$scope.lastDot=false;
			}else{
				startPage=$scope.searchMap.pageNum-2;
				lastPage=$scope.searchMap.pageNum+2;
				$scope.firstDot=false;
				$scope.lastDot=false;
			}		
		}
		
		for (var i = startPage; i <= lastPage; i++) {
			$scope.pageLabel.push(i);
		}
	}
	
	$scope.addSerachItem=function(key,value){
		if (key=="category"||key=="brand" ||key=="price") {
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		
		$scope.search();
	}
	
	$scope.removeSerachItem=function(key){
		if (key=="category"||key=="brand" ||key=="price") {
			$scope.searchMap[key]="";
		}else{
			delete $scope.searchMap.spec[key];
		}
		
		$scope.search();
	}
	
	$scope.queryForPage=function(pageNum){
		if (pageNum<1 || pageNum>$scope.resultMap.totalPage) {
			return;
		}
		$scope.searchMap.pageNum=pageNum;
		$scope.search();
	}
	
	$scope.isTopPage=function(){
		if ($scope.searchMap.pageNum==1) {
			return true;
		}else{
			return false;
		}
		
	}
	
	$scope.isEndPage=function(){
		if ($scope.searchMap.pageNum==$scope.resultMap.totalPage) {
			return true;
		}else{
			return false;
		}
	}
	
	$scope.sortSearch=function(sort,sortField){
		$scope.searchMap.sort=sort;
		$scope.searchMap.sortField=sortField;
		$scope.search();
		
	}
	
/*	$scope.isBrandKeywords=function(){
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			var brand = $scope.resultMap.brandList[i].text
			if ($scope.searchMap.keywords.indexOf(brand)>=0) {
				$scope.searchMap.brand=brand;
				return;
			}
		}
		
	}*/
	
	$scope.isBrandKeywords=function(){
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			var brand = $scope.resultMap.brandList[i].text
			if ($scope.searchMap.keywords.indexOf(brand)>=0) {
				return false;//包含品牌，列表不显示
			}
		}
		return true;
		
	}
	
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=$location.search()["keywords"];
		$scope.search();
		
	}
	
	
})