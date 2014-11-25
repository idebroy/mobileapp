package com.idr.trvlr.sqlite;

import java.io.Serializable;

/**
 * Created by hadoop on 5/30/14.
 */
public class RoutePoint implements Serializable  {
	private String description; 
	private String geohash;

	private double latitude;
	private double longitude;
	private String routeName;
	private long scheduleTime;
	private double distance;
	private int id;

	private long actualTime;

	private int includeInJourney;





	public RoutePoint() {
		super();
	}







	public RoutePoint( String routeName,String description, String geohash, double latitude,
			double longitude,long scheduleTime,
			double distance) {
		super();
		this.description = description;
		this.geohash = geohash;
		this.latitude = latitude;
		this.longitude = longitude;
		this.routeName = routeName;
		this.scheduleTime = scheduleTime;
		this.distance = distance;
	}







	@Override
	public boolean equals(Object o) {
		if(o instanceof RoutePoint){
			RoutePoint newRoutePoint = (RoutePoint)o;
			if(this.latitude == newRoutePoint.getLatitude() &&
					this.longitude == newRoutePoint.getLongitude() &&
					this.geohash.equals(newRoutePoint.getGeohash()) &&
					this.description.equals(newRoutePoint.getDescription()) &&
					this.routeName.equals(newRoutePoint.getRouteName()))
			{
				return true;
			} else {
				return false;
			}
		}
		return false;
	}



	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getGeohash() {
		return geohash;
	}

	public void setGeohash(String geohash) {
		this.geohash = geohash;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public long getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(long scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public long getActualTime() {
		return actualTime;
	}

	public void setActualTime(long actualTime) {
		this.actualTime = actualTime;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getIncludeInJourney() {
		return includeInJourney;
	}

	public void setIncludeInJourney(int includeInJourney) {
		this.includeInJourney = includeInJourney;
	}

	public static class RoutePointDistance {
		private RoutePoint routePoint;
		private float distance;

		public RoutePointDistance(int distance,RoutePoint point){
			this.distance = distance;
			this.routePoint=point;
		}

		public RoutePoint getRoutePoint() {
			return routePoint;
		}

		public void setRoutePoint(RoutePoint routePoint) {
			this.routePoint = routePoint;
		}

		public float getDistance() {
			return distance;
		}

		public void setDistance(float distance) {
			this.distance = distance;
		}
	}
}
