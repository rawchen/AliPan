package com.rawchen.alipan.entity;

import java.io.Serializable;

/**
 * @author RawChen
 * @since 2021-10-27 20:43
 */
public class PanFile implements Serializable {
	private Integer id;

	public PanFile() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "PanFile{" +
				"id=" + id +
				'}';
	}
}
