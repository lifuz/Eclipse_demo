package com.lifuz.netty.bean;

import java.io.Serializable;
/**
 * 
 * @author 李富  on 2015年8月7日
 * @mail lifuzz@163.com
 *
 */
public class SubscribeResp implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int code;
	private String desc;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "SubscribeResp [id=" + id + ", code=" + code + ", desc=" + desc
				+ "]";
	}
	
	

}
