package org.seckill.dao.cache;

import junit.runner.BaseTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by chen on 2017/6/11.
 */
//配置spring和junit整合，junit启动时加载Spring Ioc容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest{

	private long seckillId = 1000L;

	@Autowired
	private RedisDao redisDao;

	@Autowired
	private SeckillDao seckillDao;

	//get和put集成测试
	@Test
	public void testSeckill() throws Exception {
		//get and put
		//1.现在cache中获取Seckill
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//若为null，说明还没有相应的Seckill，直接通过访问database获取
			seckill = seckillDao.queryById(seckillId);
			if(seckill != null){
				String result = redisDao.putSeckill(seckill);
				System.out.println(result);
				seckill = redisDao.getSeckill(seckillId);
				System.out.println(seckill);
			}
		}
	}

	/*
	@Test
	public void getSeckill() throws Exception {

	}

	@Test
	public void putSeckill() throws Exception {

	}
	*/
}