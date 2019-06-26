package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class SeckillTask {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Scheduled(cron="0/10 * * * * ?")
	public void refreshSeckillGoods() {
		System.out.println("执行了秒杀商品增量更新任务调度"+ new Date());
		List goodIds = new ArrayList<>(redisTemplate.boundHashOps("seckillGoods").keys());
		System.out.println(goodIds);//======================
		
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//通过审核的商品
		criteria.andStockCountGreaterThan(0);//库存大于0
		criteria.andStartTimeLessThanOrEqualTo(new Date());//当前时间处于秒杀时间内
		criteria.andEndTimeGreaterThanOrEqualTo(new Date());
		
		if (goodIds.size()>0) {
			criteria.andIdNotIn(goodIds);
		}
		
		List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
		System.out.println(seckillGoodsList);//=======================
		for (TbSeckillGoods seckillGoods : seckillGoodsList) {
			redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
			//redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
		}
		System.out.println("-----end-----");
	}
	
	@Scheduled(cron="* * * * * ?")
	public void removeSeckillGoods() {
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
		System.out.println("执行清除过期秒杀商品"+ new Date());
		for (TbSeckillGoods seckillGoods : seckillGoodsList) {
			if (seckillGoods.getEndTime().getTime()<new Date().getTime()) {
				//商品过期后同步到数据库并从redis中删除
				seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
				redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
				System.out.println("秒杀商品"+seckillGoods.getId()+"已过期");
			}
		}
		System.out.println("执行清除过期秒杀商品......end");
	}
}
