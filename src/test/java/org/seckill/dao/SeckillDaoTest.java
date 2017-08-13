package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chen on 2017/6/8.
 */
//配置spring和junit整合，junit启动时加载Spring Ioc容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

	@Autowired
	private SeckillDao seckillDao;

	@Test
	public void reduceNumber() throws Exception {
		long seckillId = 1000L;
		Date killTime = new Date();
		int updateCount = seckillDao.reduceNumber(seckillId, killTime);
		System.out.println("updateCount="+updateCount);
	}

	@Test
	public void queryById() throws Exception {
		long seckillId = 1003L;
		Seckill seckill = seckillDao.queryById(seckillId);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}

	@Test
	public void queryAll() throws Exception {
		List<Seckill> list = seckillDao.queryAll(0,5);
		for(Seckill seckill : list){
			System.out.println(seckill);
		}
	}

}