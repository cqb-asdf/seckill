package org.seckill.enums;

/**
 * 使用枚举表述常量数据字段
 * Created by chen on 2017/6/8.
 */
public enum SeckillStateEnum {
	SUCCESS(1,"秒杀成功"),
	END(0,"秒杀结束"),
	REPEAT_KILL(-1,"重复秒杀"),
	INNER_ERROR(-2,"系统异常"),
	DATA_REWRITE(-3,"篡改异常");


	private int state;

	private String stateInfo;

	SeckillStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public int getState() {
		return state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public static SeckillStateEnum stateOf(int index){
		for(SeckillStateEnum stateEnum : values()){
			if(stateEnum.getState() == index){
				return stateEnum;
			}
		}
		return null;
	}
}
