app.service("cartService",function($http){
	
	//查询购物车列表
	this.findCartList=function(){
		return $http.get("cart/findCartList.do");
	}
	
	//添加购物车
	this.addGoodsToCartList=function(itemId,num){
		return $http.get("cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
	}
	
	this.getSum=function(cartList){
		var totalValue = {totalNum:0,totalMoney:0};
		
		for (var i = 0; i <cartList.length; i++) {
			var cart = cartList[i];//购物车对象
			for (var j = 0; j < cart.orderItemList.length; j++) {
				var item = cart.orderItemList[j];//购物车明细对象
				totalValue.totalNum+=item.num;
				totalValue.totalMoney+=item.totalFee;
			}
		}
		return totalValue;
	}
	
	//查询收货地址列表
	this.findAddressList=function(){
		return $http.get("address/findListByLoginUser.do");
	}
	
	//新增收货地址
	this.add=function(address){
		return $http.post("address/addAddress.do",address);
	}
	
	//提交订单
	this.submitOrder=function(order){
		return $http.post("order/add.do",order);
	}
	
	
	
	
})