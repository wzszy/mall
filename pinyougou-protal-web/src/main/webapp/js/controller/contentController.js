app.controller("contentController",function($scope,contentService){
	
	
	$scope.contentList=[];
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
			function(response){
				$scope.contentList[categoryId]=response;
			}
		)
	}
	
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
	
	$scope.myOrder=function(){
		location.href="http://localhost:9106/home-index.html";
	}
	
})