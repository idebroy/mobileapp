package com.idr.trvlr.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.idr.trvlr.HomeActivity;
import com.idr.trvlr.R;
import com.idr.trvlr.sqlite.RouteDataSource;
import com.idr.trvlr.sqlite.RoutePoint;
import com.idr.trvlr.sqlite.TrainDbOpenHelper;

import org.jgeohash.GeoHashUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hadoop on 5/28/14.
 * SELECT s.stationName,t.trainName from station_table as s, train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id and s.stationName like '%guwahati%' and r._id NOT IN (SELECT r._id  from station_table as s, train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id group by t.trainName order by r._id)
 * SELECT r._id,s.stationName,t.trainName  from station_table as s, train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id and s.stationName like '%guwahati%' and r.trainId in (SELECT r.trainId from station_table as s, train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id and s.stationName like '%secun%' ) and r._id not in (SELECT r._id  from station_table as s, train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id group by t.trainName order by r._id)
 */
public class Utils  {
	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/*
	 * Constants for location update parameters
	 */
	// Milliseconds per second
	public static final int MILLISECONDS_PER_SECOND = 1000;

	// The update interval
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

	// A fast interval ceiling
	public static final int FAST_CEILING_IN_SECONDS = 1;

	// Update interval in milliseconds
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
			MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

	// A fast ceiling of update intervals, used when the app is visible
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
			MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

	// Create an empty string for initializing strings
	public static final String EMPTY_STRING = new String();

	/**
	 * Get the latitude and longitude from the Location object returned by
	 * Location Services.
	 *
	 * @param currentLocation A Location object containing the current location
	 * @return The latitude and longitude of the current location, or null if no
	 * location is available.
	 */
	public static String getLatLng(Context context, Location currentLocation) {
		// If the location is valid
		if (currentLocation != null) {

			// Return the latitude and longitude as strings
			return context.getString(
					R.string.abc_action_mode_done,
					currentLocation.getLatitude(),
					currentLocation.getLongitude());
		} else {

			// Otherwise, return the empty string
			return EMPTY_STRING;
		}
	}
	public final static double AVERAGE_RADIUS_OF_EARTH = 6371;

	public static int calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {

		double latDistance = Math.toRadians(userLat - venueLat);
		double lngDistance = Math.toRadians(userLng - venueLng);

		double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
				(Math.cos(Math.toRadians(userLat))) *
				(Math.cos(Math.toRadians(venueLat))) *
				(Math.sin(lngDistance / 2)) *
				(Math.sin(lngDistance / 2));

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));

	}
	
	public static double calculateDistanceInFLoat(double userLat, double userLng, double venueLat, double venueLng) {

		double latDistance = Math.toRadians(userLat - venueLat);
		double lngDistance = Math.toRadians(userLng - venueLng);

		double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
				(Math.cos(Math.toRadians(userLat))) *
				(Math.cos(Math.toRadians(venueLat))) *
				(Math.sin(lngDistance / 2)) *
				(Math.sin(lngDistance / 2));

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return  AVERAGE_RADIUS_OF_EARTH * c;

	}

	public static int calculateDistance(String userGeoHash, String venueGeoHash){
		double [] userLatLong = GeoHashUtils.decode(userGeoHash);
		double [] venueLatLong = GeoHashUtils.decode(venueGeoHash);
		return calculateDistance(userLatLong[0],userLatLong[1],venueLatLong[0],venueLatLong[1]);
	}

	public static SortedMap<Integer,RoutePoint>
	getDistancesMapFromCurLocation(List<RoutePoint> routePoints,double latitude,double longitude){
		ArrayList<RoutePoint> res = new ArrayList<RoutePoint>();
		SortedMap<Integer,RoutePoint> distanceMap = new TreeMap<Integer, RoutePoint>();
		ListIterator<RoutePoint> listIterator = routePoints.listIterator();
		while(listIterator.hasNext()){
			RoutePoint routePoint = listIterator.next();
			double[] routeLatLong = GeoHashUtils.decode(routePoint.getGeohash());
			int distance = calculateDistance(latitude,longitude,routeLatLong[0],routeLatLong[1]);
			distanceMap.put(distance,routePoint);
		}
		return distanceMap;
	}

	public static ArrayList<RoutePoint>
	getDistancesFromCurLocation(List<RoutePoint> routePoints,double latitude,double longitude,boolean reverse){
		ArrayList<RoutePoint> res = new ArrayList<RoutePoint>();
		SortedMap<Integer,RoutePoint> distanceMap = getDistancesMapFromCurLocation(routePoints,latitude,longitude);

		if(reverse) {
			ArrayList<Integer> disKeys = new ArrayList<Integer>(distanceMap.keySet());
			for(int i=disKeys.size()-1;i>=0;i--){
				Integer distance = (Integer) disKeys.get(i);
				RoutePoint point = distanceMap.get(distance);
				res.add(point);
			}
		}
		else {
			Iterator disIterator = distanceMap.keySet().iterator();
			while (disIterator.hasNext()) {
				Integer distance = (Integer) disIterator.next();
				RoutePoint point = distanceMap.get(distance);
				res.add(point);
			}
		}
		return res;
	}

	public static ArrayList<RoutePoint>
	getDistancesFromOrigin(List<RoutePoint> routePoints){
		double latitude=-1,longitude=-1;
		ArrayList<RoutePoint> res = new ArrayList<RoutePoint>();
		ListIterator<RoutePoint> listIterator = routePoints.listIterator();
		//        while(listIterator.hasNext()){
			//            RoutePoint routePoint = listIterator.next();
			//            double[] routeLatLong = GeoHashUtils.decode(routePoint.getGeohash());
			//            boolean ignore = false;
			//            int distance=0;
			//            if(latitude != -1 && longitude != -1){
				//                distance = calculateDistance(latitude,longitude,routeLatLong[0],routeLatLong[1]);
		//                if(distance<500) {
		//                    latitude = routeLatLong[0];
		//                    longitude = routeLatLong[1];
		//                } else {
		//                    ignore = true;
		//                }
		//            } else {
		//                latitude = routeLatLong[0];
		//                longitude = routeLatLong[1];
		//            }
		//            if(!ignore) {
		//                RoutePoint routePointDistance = new RoutePoint(distance, routePoint);
		//                res.add(routePointDistance);
		//            }
		//        }
		while(listIterator.hasNext()){
			RoutePoint routePoint = listIterator.next();
			double[] routeLatLong = GeoHashUtils.decode(routePoint.getGeohash());
			boolean ignore = false;
			int distance=(int)routePoint.getDistance();
			if(!ignore) {
				res.add(routePoint);
			}
		}
		return res;
	}

	public static void populateRouteData(Context ctx,String trainName,RouteDataSource routeDataSource){
		routeDataSource.open();
		if(routeDataSource.getRoute().size() == 0) {
			SQLiteDatabase trainDb = routeDataSource.getDbHelper().getDb();
			Cursor trainCursor = trainDb.rawQuery("SELECT t.trainName,s.stationName,r.arrival,r.datePlus FROM route_table as r, " +
					"train_table as t, station_table as s where r.trainId=t._id AND r.stationId=s._id AND t.trainName like ?", new String[]{"%" + trainName + "%"});
			if (trainCursor.getCount() > 0) {
				routeDataSource.getDbHelper().getDb().beginTransaction();
				trainCursor.moveToFirst();
				float distance =0;
				double lat=-1,lon=-1;
				while (!trainCursor.isAfterLast()) {
					String aTName = trainCursor.getString(0);
					String aSName = trainCursor.getString(1);
					String aArrivalStr = trainCursor.getString(2);
					int dayOffset = trainCursor.getInt(3);

					long startOfday = getStartOfDay(new Date())+dayOffset*24*3600*1000;
					String[] hrAndminute = aArrivalStr.split("\\:");
					int hour = Integer.parseInt(hrAndminute[0]);
					int minute = Integer.parseInt(hrAndminute[1]);
					long arrivalTime = startOfday+3600*1000*hour+60*1000*minute;

					double[] coor = getStationLatLong(ctx, aSName);
					if( coor != null ) {
						String gHash = GeoHashUtils.encode(coor[0], coor[1]);
						if(lat !=-1 && lon !=-1){
							distance = Utils.calculateDistance(lat,lon,coor[0],coor[1]);
						}
						lat = coor[0];
						lon =coor [1];
						// more than 500 km between stations might be too much, so ignore as
						// lat long issue for now.
						if(distance<500) {
							routeDataSource.insertRoutePoint(aTName, aSName, gHash, coor[0], coor[1], arrivalTime, distance);
						} else {
							Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as distance more than 500km");
						}
					}else {
						Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as no lat,long found!");
					}
					trainCursor.moveToNext();
				}
				routeDataSource.getDbHelper().getDb().setTransactionSuccessful();
				routeDataSource.getDbHelper().getDb().endTransaction();
			}
			trainCursor.close();
		}
		//        routeDataSource.close();
	}
	
	
	/*get route data my method*/
	public static ArrayList<RoutePoint> getStationsFromTrain(Context ctx,int trainId,RouteDataSource routeDataSource){
		
		ArrayList<RoutePoint> routePointArray = new ArrayList<RoutePoint>();

		routeDataSource.open();
		//if(routeDataSource.getRoute().size() == 0) {
			SQLiteDatabase trainDb = routeDataSource.getDbHelper().getDb();
			Cursor trainCursor = trainDb.rawQuery("SELECT t.trainName,s.stationName,r.arrival,r.datePlus,s.latitude,s.longitude FROM route_table as r, " +
					"train_table as t, station_table as s where r.trainId=t._id AND r.stationId=s._id AND t._id="+trainId, null);
			if (trainCursor.getCount() > 0) {
				routeDataSource.getDbHelper().getDb().beginTransaction();
				trainCursor.moveToFirst();
				float distance =0;
				double lat=-1,lon=-1;
				
				 double nextLat=-1,nextLon=-1;
				while (!trainCursor.isAfterLast()) {
					String aTName = trainCursor.getString(0);
					String aSName = trainCursor.getString(1);
					String aArrivalStr = trainCursor.getString(2);
					int dayOffset = trainCursor.getInt(3);
					lat = trainCursor.getDouble(4);
					lon = trainCursor.getDouble(5);

					long startOfday = getStartOfDay(new Date())+dayOffset*24*3600*1000;
					String[] hrAndminute = aArrivalStr.split("\\:");
					int hour = Integer.parseInt(hrAndminute[0]);
					int minute = Integer.parseInt(hrAndminute[1]);
					long arrivalTime = startOfday+3600*1000*hour+60*1000*minute;

					//double[] coor = getStationLatLong(ctx, aSName);
					if( lat != 0 ) {
					//	String gHash = GeoHashUtils.encode(coor[0], coor[1]);
						if(nextLat !=-1 && nextLon !=-1){
							distance = Utils.calculateDistance(lat,lon,nextLat,nextLon);
						}
						
						nextLat = lat;
						nextLon = lon;
					//	lat = coor[0];
					//	lon =coor [1];
						
						
						// more than 500 km between stations might be too much, so ignore as
						// lat long issue for now.
						if(distance<500) {
						//routeDataSource.insertRoutePoint(aTName, aSName, gHash, coor[0], coor[1], arrivalTime, distance);
							
							RoutePoint routePoint = new RoutePoint(aTName, aSName, "", lat, lon, arrivalTime, distance);
							routePointArray.add(routePoint);
							
						} else {
							Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as distance more than 500km");
						}
					}else {
						Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as no lat,long found!");
					}
					trainCursor.moveToNext();
				}
				routeDataSource.getDbHelper().getDb().setTransactionSuccessful();
				routeDataSource.getDbHelper().getDb().endTransaction();
			}
			trainCursor.close();
		
		//        routeDataSource.close();
	return routePointArray;
	}

	public static void populateRouteFromExternalStorage(Context ctx,RouteDataSource routeDataSource){
		FileInputStream fileInputStream = null;
		try {
			routeDataSource.open();
			File file= new File(android.os.Environment.getExternalStorageDirectory(),"route.csv");
			fileInputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

			String line;
			float distance =0;
			double lat=-1,lon=-1;
			while ((line = reader.readLine()) != null) {
				String[] RowData = line.split(",");
				String aTName = RowData[0];
				String aSName = RowData[1];
				String aArrivalStr = RowData[2];
				double nlat = Double.parseDouble(RowData[3]);
				double nlon = Double.parseDouble(RowData[4]);

				long startOfday = getStartOfDay(new Date())+1*24*3600*1000;
				String[] hrAndminute = aArrivalStr.split("\\:");
				int hour = Integer.parseInt(hrAndminute[0]);
				int minute = Integer.parseInt(hrAndminute[1]);
				long arrivalTime = startOfday+3600*1000*hour+60*1000*minute;

				double[] coor = null;

				if(nlat != 0 && nlon != 0){
					coor = new double[]{nlat,nlon};
				} else {
					coor = getStationLatLong(ctx, aSName);
				}
				if( coor != null ) {
					String gHash = GeoHashUtils.encode(coor[0], coor[1]);
					if(lat !=-1 && lon !=-1){
						distance = Utils.calculateDistance(lat,lon,coor[0],coor[1]);
					}
					lat = coor[0];
					lon =coor [1];
					// more than 500 km between stations might be too much, so ignore as
					// lat long issue for now.
					if(distance<500) {
						routeDataSource.insertRoutePoint(aTName, aSName, gHash, coor[0], coor[1], arrivalTime, distance);
						Log.e(aTName,"----lat:"+coor[0]+",---long:"+coor[1]);
					} else {
						Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as distance more than 500km");
					}
				}else {
					Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as no lat,long found!");
				}
			}
		}
		catch (Throwable ex) {
			// handle exception
			ex.printStackTrace();
		}
		finally {
			try {
				if(fileInputStream != null) {
					fileInputStream.close();
				}
				//                routeDataSource.getDbHelper().getDb().setTransactionSuccessful();
				//                routeDataSource.getDbHelper().getDb().endTransaction();
			}
			catch (IOException e) {
				// handle exception
			}
		}
	}

	public static void populateRouteData(Context ctx,int trainId,RouteDataSource routeDataSource){
		routeDataSource.open();
	//	if(routeDataSource.getRoute().size() == 0) {
		routeDataSource.getRoute().clear();
			SQLiteDatabase trainDb = routeDataSource.getDbHelper().getDb();
			Cursor trainCursor = trainDb.rawQuery("SELECT t.trainName,s.stationName,r.arrival,r.datePlus,s.latitude,s.longitude FROM route_table as r, " +
					"train_table as t, station_table as s where r.trainId=t._id AND r.stationId=s._id AND t._id="+trainId, null);
			if (trainCursor.getCount() > 0) {
				routeDataSource.getDbHelper().getDb().beginTransaction();
				trainCursor.moveToFirst();
				float distance =0;
				double lat=-1,lon=-1;
				while (!trainCursor.isAfterLast()) {
					String aTName = trainCursor.getString(0);
					String aSName = trainCursor.getString(1);
					String aArrivalStr = trainCursor.getString(2);
					int dayOffset = trainCursor.getInt(3);
					double nlat = trainCursor.getDouble(4);
					double nlon = trainCursor.getDouble(5);

					long startOfday = getStartOfDay(new Date())+dayOffset*24*3600*1000;
					String[] hrAndminute = aArrivalStr.split("\\:");
					int hour = Integer.parseInt(hrAndminute[0]);
					int minute = Integer.parseInt(hrAndminute[1]);
					long arrivalTime = startOfday+3600*1000*hour+60*1000*minute;

					double[] coor = null;

					if(nlat>0 && nlon>0){
						coor = new double[]{nlat,nlon};
					} else {
						coor = getStationLatLong(ctx, aSName);
					}
					if( coor != null ) {
						String gHash = GeoHashUtils.encode(coor[0], coor[1]);
						if(lat !=-1 && lon !=-1){
							distance = Utils.calculateDistance(lat,lon,coor[0],coor[1]);
						}
						lat = coor[0];
						lon =coor [1];
						// more than 500 km between stations might be too much, so ignore as
						// lat long issue for now.
						if(distance<500) {
							routeDataSource.insertRoutePoint(aTName, aSName, gHash, coor[0], coor[1], arrivalTime, distance);
							Log.e(aTName,"----lat:"+coor[0]+",---long:"+coor[1]);
						} else {
							Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as distance more than 500km");
						}
					}else {
						Log.d(Utils.class.getName(),"Skipped station:"+aSName+" as no lat,long found!");
					}
					trainCursor.moveToNext();
				}
				routeDataSource.getDbHelper().getDb().setTransactionSuccessful();
				routeDataSource.getDbHelper().getDb().endTransaction();
			}
			trainCursor.close();
//		}
		//        routeDataSource.close();
	}

	public static List<RouteDataSource.TrainStation> getAllTrainStations(Context ctx,TrainDbOpenHelper trainDbOpenHelper) {
		ArrayList<RouteDataSource.TrainStation> trainStations = new ArrayList<RouteDataSource.TrainStation>();
		SQLiteDatabase trainDb = trainDbOpenHelper.getDb();
		trainDbOpenHelper.openDataBase();
		Cursor stationCursor = trainDb.rawQuery("SELECT _id,stationCode,stationName FROM station_table",null);


		if (stationCursor.getCount() > 0) {
			stationCursor.moveToFirst();
			//   String [] columns =   stationCursor2.getColumnNames();
			while (!stationCursor.isAfterLast()) {
				int stationId = stationCursor.getInt(0);
				String stationCode = stationCursor.getString(1);
				String stationName = stationCursor.getString(2);
				RouteDataSource.TrainStation trainStation = new RouteDataSource.TrainStation(stationId,stationName,stationCode);
				trainStations.add(trainStation);

				stationCursor.moveToNext();
			}
		}
		stationCursor.close();
		//        trainDbOpenHelper.close();
		return trainStations;
	}

	public static Cursor getAllTrainStationsCursor(Context ctx,TrainDbOpenHelper trainDbOpenHelper) {
		SQLiteDatabase trainDb = trainDbOpenHelper.getDb();
		trainDbOpenHelper.openDataBase();
		Cursor stationCursor = trainDb.rawQuery("SELECT _id,stationCode,stationName FROM station_table",null);
		//        trainDbOpenHelper.close();
		return stationCursor;
	}

	public static double[] getStationLatLong(Context ctx,String stationName){
		String searchString = stationName + ", India";
		Geocoder geocoder = new Geocoder(ctx);
		List<Address> foundAddresses=null;
		try {
			foundAddresses = geocoder.getFromLocationName(searchString,2);
		} catch (IOException e) {
			Log.d(Utils.class.getName(), e.getMessage());
		}
		if(foundAddresses != null && foundAddresses.size() > 0){
			Log.d(Utils.class.getName(),foundAddresses.toString());
			if(foundAddresses.get(0).hasLatitude() && foundAddresses.get(0).hasLongitude()) {
				return new double[]{foundAddresses.get(0).getLatitude(),foundAddresses.get(0).getLongitude()};
			}
		}
		return null;
	}


	/*gets station name from latlong*/
	public static  String getStationNameFromLatLong(TrainDbOpenHelper trainDbOpenHelper,Context context,double latitude,double longitude)
	{
		String cityName;
		List<Address> addresses;
		Geocoder gcd = new Geocoder(context, Locale.getDefault());
		try {
			addresses = gcd.getFromLocation(latitude, longitude, 1);
			cityName =  addresses.get(0).getLocality();

			SQLiteDatabase trainDb = trainDbOpenHelper.getDb();
			trainDbOpenHelper.openDataBase();
			Cursor stationCursor2 = trainDb.rawQuery("SELECT _id,stationCode,stationName FROM station_table where stationName like ?", new String[]{"%" + cityName + "%"});
			stationCursor2.moveToFirst();

			if (stationCursor2.getCount() > 0) {
				stationCursor2.moveToFirst();
				//   String [] columns =   stationCursor2.getColumnNames();
				while (!stationCursor2.isAfterLast()) {
					int stationId = stationCursor2.getInt(0);
					String stationCode = stationCursor2.getString(1);
					String stationName = stationCursor2.getString(2);
					return stationName;

				}
				stationCursor2.close();
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		return null;

	}
	
	

	
	public static long getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTime().getTime();
	}
}
