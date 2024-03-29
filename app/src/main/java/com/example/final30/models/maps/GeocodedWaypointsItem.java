package com.example.final30.models.maps;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GeocodedWaypointsItem{

	@SerializedName("types")
	private List<String> types;

	@SerializedName("geocoder_status")
	private String geocoderStatus;

	@SerializedName("place_id")
	private String placeId;

	public void setTypes(List<String> types){
		this.types = types;
	}

	public List<String> getTypes(){
		return types;
	}

	public void setGeocoderStatus(String geocoderStatus){
		this.geocoderStatus = geocoderStatus;
	}

	public String getGeocoderStatus(){
		return geocoderStatus;
	}

	public void setPlaceId(String placeId){
		this.placeId = placeId;
	}

	public String getPlaceId(){
		return placeId;
	}
}