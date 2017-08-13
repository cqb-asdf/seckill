package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Service 集成测试
 * Created by chen on 2017/6/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
		               "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@Test
	public void getSeckillList() throws Exception {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}",list);
	}

	@Test
	public void getById() throws Exception {
		long seckillId = 1001L;
		Seckill seckill = seckillService.getById(seckillId);
		logger.info("seckill={}",seckill);
	}

	/*
	@Test
	public void exportSeckillUrl() throws Exception {
		long seckillId = 1001L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		logger.info("exposer={}",exposer);
	}

	@Test
	public void executeSeckill() throws Exception {
		long seckillId = 1001L;
		long userPhone = 13813821111L;
		String md5 = "1f6bc9ade19ada9598742174d164cc2a";
		try {
			SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
			logger.info("seckillExecution={}", seckillExecution);
		} catch (RepeatSeckillException e){
			logger.error(e.getMessage());
		} catch (SeckillCloseException e){
			logger.error(e.getMessage());
		}
	}
	*/

	/**
	 *  应该将testExportSeckillUrl和testExecuteSeckill两个Test集成在一个方法中进行
	 *  秒杀测试完整逻辑，注意可重复执行
	 */

	@Test
	public void testSeckillLogic() throws Exception {
		long seckillId = 1000L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			logger.info("exposer={}",exposer);
			long userPhone = 13813811222L;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,userPhone,md5);
				logger.info("seckillExecution={}",seckillExecution);
			} catch (RepeatSeckillException e){
				logger.error(e.getMessage());
			} catch (SeckillCloseException e){
				logger.error(e.getMessage());
			}
		} else {
			//exposed为false，秒杀未开启
			logger.warn("exposer={}",exposer);
		}
	}

	@Test
	public void executeSeckillProcedure(){
		long seckillId = 1004L;
		long phone = 13613813711L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()){
			String md5 = exposer.getMd5();
			SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
			logger.info(seckillExecution.getStateInfo());
		}
	}

}