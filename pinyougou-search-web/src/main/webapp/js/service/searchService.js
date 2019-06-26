app.service("searchService",function($http){
	
	this.search=function(searchMap){
		
		return $http.post("searchController/search.do",searchMap)
	}
	
})