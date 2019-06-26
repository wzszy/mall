var app = angular.module("pinyougou",[]);


//html过滤器，相当于一个全局方法
app.filter("trustHtml",["$sce",function($sce){
	return function(data){ //data:需要显示html的代码内容
		return $sce.trustAsHtml(data);
	}
}])