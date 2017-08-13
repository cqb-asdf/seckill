package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by chen on 2017/6/8.
 */
//配置spring和junit整合，junit启动时加载Spring Ioc容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

	@Autowired
	private SuccessKilledDao successKilledDao;

	@Test
	public void insertSuccessKilled() throws Exception {
		long seckillId = 1003L;
		long userPhone = 13813811111L;
		int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
		System.out.println("insertCount=" + insertCount);
	}

	@Test
	public void queryByIdWithSeckill() throws Exception {
		long seckillId = 1003L;
		long userPhone = 13813811111L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}

}