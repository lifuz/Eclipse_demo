package com.prd.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 文件上传的action类
 *
 * @作者： 李富
 * @邮箱：lifuzz@163.com
 * @时间：2016年2月24日
 */
public class UploadAction extends ActionSupport {

	private static final long serialVersionUID = -7389578813708898036L;
	
	private File ppt;
	private String pptContentType;
	private String pptFileName;
	public File getPpt() {
		return ppt;
	}
	public void setPpt(File ppt) {
		this.ppt = ppt;
	}
	public String getPptContentType() {
		return pptContentType;
	}
	public void setPptContentType(String pptContentType) {
		this.pptContentType = pptContentType;
	}
	public String getPptFileName() {
		return pptFileName;
	}
	public void setPptFileName(String pptFileName) {
		this.pptFileName = pptFileName;
	}
	
	@Override
	public String execute() throws Exception {
		
		String path = "D:\\var/mcc/image/" + pptFileName;
		
		FileOutputStream out = new FileOutputStream(path);
		FileInputStream in = new FileInputStream(ppt);
		
		byte[] buf = new byte[1024];
		int len = 0;
		
		while((len = in.read(buf)) != -1){
			out.write(buf, 0, len);
		}
		
		out.close();
		in.close();
		
		return SUCCESS;
	}

}
