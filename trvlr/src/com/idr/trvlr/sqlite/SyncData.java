package com.idr.trvlr.sqlite;

public class SyncData {
	
	private String trainNumer;
	private String phoneNumber ;
	private String dataTime;
	private String longitude;
	private String latitude;
	
	
	
	public SyncData() {
		super();
	}
	public SyncData(String trainNumer, String phoneNumber, String dataTime,
			String longitude, String latitude) {
		super();
		this.trainNumer = trainNumer;
		this.phoneNumber = phoneNumber;
		this.dataTime = dataTime;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public String getTrainNumer() {
		return trainNumer;
	}
	public void setTrainNumer(String trainNumer) {
		this.trainNumer = trainNumer;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getDataTime() {
		return dataTime;
	}
	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	

}
