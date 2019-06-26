 //控制层 
app.controller('userController' ,function($scope,$controller ,loginService,addressService  ,userService){	
	$scope.loginName = "";
	$scope.showName=function(){
		loginService.showName().success(
			function(response){
				$scope.loginName=response.loginName;
				$scope.headPic=response.headPic;
			}
		)
		
	}
	
	
	//注册
	$scope.register=function(){
		
		if ($scope.entity.password != $scope.password) {
			alert("密码不一致，请重新输入密码");
			$scope.entity.password="";
			$scope.password="";
			return;
		}
		

		userService.add($scope.entity,$scope.smscode).success(
			function(response){
				alert(response.message);
			}
		
		);
	}
	
	//发送验证码
	$scope.sendCode=function(){
		if ($scope.entity.phone==null || $scope.entity.phone=="") {
			alert("请填写手机号");
			return;
		}
		userService.sendCode($scope.entity.phone).success(
			function(response){
				alert(response.message);
			}
		);
		
	}
	
	//根据登录用户名查询用户数据
	$scope.findOne=function(){
		userService.findOne().success(
			function(resonse){
				$scope.entity=resonse;
				showBir();
				
			}
		)
	}
	showBir=function(){
		var bir=$scope.entity.birthday;
		var str=bir.split("-");
		var year = str[0];
		var month = str[1];
		var day = str[2].substr(0,2);
		//alert(year);
		$("#select_year2 option[value='"+year+"']").attr("selected",true); 
		$("#select_month2 option[value='"+month+"']").attr("selected",true); 
		$("#select_day2 option[value='"+day+"']").attr("selected",true); 
	}
	
	//修改用户资料
	$scope.update=function(){
		//获取生日
		var year = document.getElementById("select_year2").value;
		var month = document.getElementById("select_month2").value;
		var day = document.getElementById("select_day2").value;	
		var birth = "";
		birth += year+"-";
		if (month<10) {
			birth += "0";
		}
		birth += month+"-";
		
		if (day<10) {
			birth += "0";
		}
		birth += day;
		$scope.entity.birthday = birth;
		
		
		
//		var headPic = document.getElementById("imgShow_WU_FILE_0").src;
//		if (headPic == "img/_/photo_icon.png") {
//			$scope.entity.headPic = "";
//		}else{
//			$scope.entity.headPic = headPic;
//		}
		
		userService.update($scope.entity).success(
			function(resonse){
				if (resonse.success) {
					alert(resonse.message);
				}else{
					alert(resonse.message);
				}
			}	
		)
	}
	
	

	//查询省份列表
	$scope.selectProvince=function(){
		addressService.findProvince().success(
			function(response){
				$scope.provinceList=response;
			}
		)
	}
	//根据省份列表查询市级列表
	$scope.$watch("newAddress.provinceId",function(newValue,oldValue){	
		addressService.findCity(newValue).success(
			function(response){
				$scope.cityList=response;
			}
		)	
	})	
	//根据市级列表查询县级列表
	$scope.$watch("newAddress.cityId",function(newValue,oldValue){		
		addressService.findarea(newValue).success(
			function(response){
				$scope.areaList=response;
			}
		)	
	})	
	
	
	
	
});	
