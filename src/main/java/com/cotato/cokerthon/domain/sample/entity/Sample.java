package com.cotato.cokerthon.domain.sample.entity;

import com.cotato.cokerthon.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "samples")
public class Sample extends BaseEntity {

	@Column(nullable = false, length = 50)
	private String title;

	@Column(nullable = false)
	private String content;

	protected Sample() {
	}

	private Sample(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public static Sample create(String title, String content) {
		return new Sample(title, content);
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
}
