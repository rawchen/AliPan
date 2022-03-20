package com.rawchen.alipan.entity;

import java.io.Serializable;

/**
 * @author RawChen
 * @since 2021-10-27 20:43
 */
public class PanFile implements Serializable {

	private String fileId;

	private String type;

	private String name;

	private String parentFileId;

	private String createdAt;

	//以上属性为文件夹、文件共有属性

	private String fileExtension;

	private long size;

	private String url;

	//以上属性为文件共有属性

	private boolean encrypted;

	public PanFile() {
	}

	public PanFile(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public PanFile(String name, boolean encrypted) {
		this.name = name;
		this.encrypted = encrypted;
	}

	public PanFile(String name, boolean encrypted, String type) {
		this.name = name;
		this.encrypted = encrypted;
		this.type = type;
	}

	//以上属性为自定义属性

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentFileId() {
		return parentFileId;
	}

	public void setParentFileId(String parentFileId) {
		this.parentFileId = parentFileId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
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
				", type='" + type + '\'' +
				", name='" + name + '\'' +
				", parentFileId='" + parentFileId + '\'' +
				", createdAt='" + createdAt + '\'' +
				", fileExtension='" + fileExtension + '\'' +
				", size=" + size +
				", url='" + url + '\'' +
				", encrypted=" + encrypted +
				'}';
	}
}
