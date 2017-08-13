package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen on 2017/6/8.
 */
//Spring提供的注解支持：@Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	@Autowired
	private RedisDao redisDao;

	//对md5加盐处理
	private final String salt = "gdahajjabdaiub84794884u48%%$%^&&^874387hdjci*&*&*gxusihxi";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0,5);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	@Transactional
	public Exposer exportSeckillUrl(long seckillId) {
		// 优化点：缓存优化---注意不要与业务逻辑代码耦合在一起，应该放在dao包中，
		// 即访问数据库或其他存储数据的组件，如redis，相关的操作---数据访问逻辑操作
		//缓存与数据库的一致性建立在超时基础上
		/**
		 * get from cache
		 * if null
		 * 		get database
		 * 		put cache
		 * else
		 * 		return cache / login
		 */
		//1.访问redis，先在cache中找Seckill
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//2.redis中拿到的Seckill为null,说明还没有缓存相应的Seckill，直接到database中查找
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){
				return new Exposer(false,seckillId); //seckillId对应的Seckill对象不存在，也就是seckillId值没在表中有对应记录存在
			} else {
				//3.从database中拿到了Seckill对象，put到redis缓存中
				redisDao.putSeckill(seckill);
			}
		}

		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//获取当前系统时间
		Date nowTime = new Date();
		if(nowTime.getTime() < startTime.getTime()
				|| nowTime.getTime() > endTime.getTime()){
			return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}

		//md5:转化特定字符串的过程，md5是不可逆的
		String md5 = getMD5(seckillId);
		return new Exposer(true,md5,seckillId);
	}

	private String getMD5(long seckillId){
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	/**
	 *  使用注解控制事务方法的优点：
	 *  1.开发团队达成一致约定，明确标注事务方法的编程风格
	 *  2.保证事务方法的执行时间尽可能短，不要穿插其它网络操作RPC/HTTP请求或者剥离到事务方法外部
	 *  3.不是所有的方法都需要事务，如果只有一条修改操作，或只读操作不需要事务控制
	 */
	@Transactional
	@Override
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatSeckillException{
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		//获取当前系统时间
		Date nowTime = new Date();
		//执行秒杀业务逻辑：减库存 + 记录秒杀成功明细
		try{
			//简单优化：先insert 再update，减少mysql数据库rowback lock的时间
			//1：记录秒杀成功明细
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			//确定秒杀的唯一性：seckillId + userPhone
			if (insertCount <= 0) {
				throw new RepeatSeckillException("seckill repeated");
			} else {
				//2：减库存，热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
				if(updateCount <= 0){
					//秒杀已经结束，不会减库存，不用更新秒杀记录：rollback
					throw new SeckillCloseException("seckill is closed");
				} else {
					//秒杀成功:commit
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e){
			throw e;
		} catch (RepeatSeckillException e){
			throw e;
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			//所有Checked Exception 转化为 RuntimeException
			throw new SeckillException("seckill inner error" + e.getMessage());
		}
	}

	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<>();
		map.put("seckillId",seckillId);
		map.put("phone",userPhone);
		map.put("killTime",killTime);
		map.put("result",null);
		//执行存储过程，result被赋值
		try {
			seckillDao.killByProcedure(map);
			//获取result
			int result = MapUtils.getInteger(map,"result",-2);
			if(result == 1){
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
				return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
			} else {
				return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
		}
	}
}
