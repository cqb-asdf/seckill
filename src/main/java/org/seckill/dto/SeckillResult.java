package org.seckill.dto;

/**
 * Created by chen on 2017/6/8.
 */
//所有ajax请求的返回类型，封装json结果
public class SeckillResult<T> {

	private boolean success;

	private T data;

	private String error;

	public SeckillResult(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	public SeckillResult(boolean success, String error) {
		this.success = success;
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "SeckillResult{" +
				"success=" + success +
				", data=" + data +
				", error='" + error + '\'' +
				'}';
	}
}
