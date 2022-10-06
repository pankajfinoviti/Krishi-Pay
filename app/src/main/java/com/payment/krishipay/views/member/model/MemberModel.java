package com.payment.krishipay.views.member.model;

import com.google.gson.annotations.SerializedName;

public class MemberModel {

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("scheme")
	private int scheme;

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("slug")
	private String slug;

	public String getUpdatedAt(){
		return updatedAt;
	}

	public int getScheme(){
		return scheme;
	}

	public String getName(){
		return name;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public String getSlug(){
		return slug;
	}
}