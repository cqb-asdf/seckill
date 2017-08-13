package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by chen on 2017/6/11.
 */
public class RedisDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private JedisPool jedisPool;

	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	//初始化JedisPool
	public RedisDao(String ip ,int port){
		jedisPool = new JedisPool(ip,port);
	}

	public Seckill getSeckill(long seckillId){
		//缓存redis操作逻辑
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId; //key-value存储，put cachr时的key值，redis的通常保存命名格式
				//redis并没有实现内部序列化及反序列化操作操作，需要我们自己实现
				//所以从cache中get时拿到的是byte[]，要反序列化为实际的Object(本应用中就是Seckill)
				//根据github提供的序列化性能比较，采用开源的序列化工具protostuff来获取更高的序列化性能，采用自定义序列化
				//protostuff序列化的类:一定是pojo
				byte[] bytes = jedis.get(key.getBytes());
				//序列化的高并发问题
				//从缓存中获取到Seckill对象的byte[]，要通过protostuff反序列化得到实际的Seckill对象
				if(bytes != null){
					//根据Seckill的Schema直接创建一个空对象，其中的属性均无值
					Seckill seckill = schema.newMessage();
					//protostuff根据Seckill的schema将byte[]中的数据传入到空对象中，实现反序列化
					ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);//速度，空间都非常高效
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	public String putSeckill(Seckill seckill){
		//put cache
		//将Object(Seckill)序列化为byte[]--->发送给redis
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//超时缓存：秒
				int timeout = 60 * 60; //缓存1小时
				String result = jedis.setex(key.getBytes(),timeout, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
