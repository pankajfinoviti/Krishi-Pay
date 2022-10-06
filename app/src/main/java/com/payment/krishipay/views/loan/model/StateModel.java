package com.payment.krishipay.views.loan.model;

import com.google.gson.annotations.SerializedName;

public class StateModel{

	@SerializedName("stateid")
	private String stateid;

	@SerializedName("id")
	private String id;

	@SerializedName("statename")
	private String statename;

	public void setStateid(String stateid){
		this.stateid = stateid;
	}

	public String getStateid(){
		return stateid;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setStatename(String statename){
		this.statename = statename;
	}

	public String getStatename(){
		return statename;
	}
}