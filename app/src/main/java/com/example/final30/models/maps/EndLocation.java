package com.example.final30.models.maps;

import com.google.gson.annotations.SerializedName;

public class EndLocation{

	@SerializedName("lng")
	private Object lng;

	@SerializedName("lat")
	private Object lat;

	public void setLng(Object lng){
		this.lng = lng;
	}

	public Object getLng(){
		return lng;
	}

	public void setLat(Object lat){
		this.lat = lat;
	}

	public Object getLat(){
		return lat;
	}
}