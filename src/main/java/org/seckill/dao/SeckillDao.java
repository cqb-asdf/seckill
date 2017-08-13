package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chen on 2017/6/7.
 */
public interface SeckillDao {

	/**
	 * 秒杀成功后减库存
	 * @param seckillId
	 * @param killTime
	 * @return 如果影响行数>1，表示更新的记录行数
	 */
	int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

	/**
	 * 根据seckillId查询秒杀对象
	 * @param seckillId
	 * @return Seckill 实体对象
	 */
	Seckill queryById(long seckillId);

	/**
	 *
	 * @param offset
	 * @param limt
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limt);

	/**
	 *使用存储过程执行秒杀
	 * @param paramMap
	 */
	void killByProcedure(Map<String,Object> paramMap);
}

