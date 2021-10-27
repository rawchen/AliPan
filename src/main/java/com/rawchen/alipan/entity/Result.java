package com.rawchen.alipan.entity;

/**
 * 封装结果
 *
 * @author RawChen
 * @since 2021-10-06 20:50
 */
public class Result {
	private Integer code;
	private String msg;
	private Object data;

	private Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
		this.data = null;
	}

	private Result(Integer code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static Result ok(String msg, Object data) {
		return new Result(200, msg, data);
	}

	public static Result ok(String msg) {
		return new Result(200, msg);
	}

	public static Result ok() {
		return new Result(200, "success");
	}

	public static Result ok(Object data) {
		return new Result(200, "success", data);
	}

	public static Result fail(String msg) {
		return new Result(400, msg);
	}

	public static Result fail() {
		return new Result(400, "fail");
	}

	public static Result error(String msg) {
		return new Result(500, msg);
	}

	public static Result error() {
		return new Result(500, "error");
	}

	public static Result create(Integer code, String msg, Object data) {
		return new Result(code, msg, data);
	}

	public static Result create(Integer code, String msg) {
		return new Result(code, msg);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Result{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}