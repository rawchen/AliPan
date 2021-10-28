package com.rawchen.alipan.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author RawChen
 * @since 2021-10-27 20:43
 */
public class PanFile implements Serializable {

	private String fileId;

	private String name;

	private String type;

	private long size;

	private String contentType;

	private Date createdAt;

	private String fileExtension;

	private String password;

	private String url;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "PanFile{" +
				"fileId='" + fileId + '\'' +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				", size=" + size +
				", contentType='" + contentType + '\'' +
				", createdAt=" + createdAt +
				", fileExtension='" + fileExtension + '\'' +
				", password='" + password + '\'' +
				", url='" + url + '\'' +
				'}';
	}
}
