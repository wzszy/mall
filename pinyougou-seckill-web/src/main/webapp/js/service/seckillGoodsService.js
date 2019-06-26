app.service("seckillGoodsService",function($http){
	//查询商品列表
	this.findList=function(){
		return $http.get("seckillGoods/findList.do");
	}
	
	//查询商品
	this.findOne=function(id){
		return $http.get("seckillGoods/findOneFromRedis.do?id="+id);
	}
	
	//秒杀下单
	this.submitOrder=function(seckillId){
		return $http.get("seckillOrder/submitOrder.do?seckillId="+seckillId);
	}
})