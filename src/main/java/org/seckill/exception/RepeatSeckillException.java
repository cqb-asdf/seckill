package org.seckill.exception;

/**
 * 重复秒杀异常
 * Created by chen on 2017/6/8.
 */
public class RepeatSeckillException extends SeckillException {

	public RepeatSeckillException(String message) {
		super(message);
	}

	public RepeatSeckillException(String message, Throwable cause) {
		super(message, cause);
	}
}
