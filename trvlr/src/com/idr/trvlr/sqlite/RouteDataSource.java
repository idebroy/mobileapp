package com.idr.trvlr.sqlite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jgeohash.GeoHashUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.idr.trvlr.util.Utils;

/**
 * Created by hadoop on 5/30/14.
 */
public class RouteDataSource   {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TrainDbOpenHelper dbHelper;
    private String[] allColumns = { TrainDbOpenHelper.COLUMN_ID,
            TrainDbOpenHelper.COLUMN_LATITUDE, TrainDbOpenHelper.COLUMN_LONGITUDE,
            TrainDbOpenHelper.COLUMN_GEOHASH, TrainDbOpenHelper.COLUMN_DESCRIPTION,
            TrainDbOpenHelper.COLUMN_SCHEDULED_TIME,TrainDbOpenHelper.COLUMN_ACTUAL_TIME,
            TrainDbOpenHelper.DISTANCE_FROM_PREV_STOP,TrainDbOpenHelper.INCLUDE_IN_TRIP,
            TrainDbOpenHelper.COLUMN_ROUTE_NAME};

    public static class TrainStation  {
        /**
		 * 
		 */
		private static final long serialVersionUID = 2L;
		private int id;
        private String stationName;
        private String stationCode;
        private String latitude;
        private String longitude;
        public float distanceFromOrigin;
        
        

        public TrainStation(int id, String stationName, String stationCode) {
            this.id = id;
            this.stationName = stationName;
            this.stationCode = stationCode;
        }

        
        public TrainStation(int id, String stationName, String stationCode,
				String latitude, String longitude) {
			super();
			this.id = id;
			this.stationName = stationName;
			this.stationCode = stationCode;
			this.latitude = latitude;
			this.longitude = longitude;
		}


		public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public String getStationCode() {
            return stationCode;
        }

        public void setStationCode(String stationCode) {
            this.stationCode = stationCode;
        }


		public String getLatitude() {
			return latitude;
		}


		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}


		public String getLongitude() {
			return longitude;
		}


		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}


		public float getDistanceFromOrigin() {
			return distanceFromOrigin;
		}


		public void setDistanceFromOrigin(float distanceFromOrigin) {
			this.distanceFromOrigin = distanceFromOrigin;
		}





		public static Comparator<TrainStation> distanceComparator = new Comparator<RouteDataSource.TrainStation>() {
			
			@Override
			public int compare(TrainStation station1, TrainStation station2) {
				// TODO Auto-generated method stub
				return (int) (station1.getDistanceFromOrigin() - station2.getDistanceFromOrigin());
			}
		};

        
    }
    
    
    

    public static class Train  {
        /**
		 * 
		 */
		private static final long serialVersionUID = 3L;
		private int id;
        private String trainName;
        private int trainNo;
        private boolean isOnMonday;
        private boolean isOnTuesday;
        private boolean isOnWednesday;
        private boolean isOnThusday;
        private boolean isOnFriday;
        private boolean isOnSaturday;
        private boolean isOnSunday;

        public Train(int id, String trainName, int trainNo, boolean isOnMonday, boolean isOnTuesday, boolean isOnWednesday, boolean isOnThusday, boolean isOnFriday, boolean isOnSaturday, boolean isOnSunday) {
            this.id = id;
            this.trainName = trainName;
            this.trainNo = trainNo;
            this.isOnMonday = isOnMonday;
            this.isOnTuesday = isOnTuesday;
            this.isOnWednesday = isOnWednesday;
            this.isOnThusday = isOnThusday;
            this.isOnFriday = isOnFriday;
            this.isOnSaturday = isOnSaturday;
            this.isOnSunday = isOnSunday;
        }

        @Override
        public String toString() {
            return ""+trainNo+": "+trainName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTrainName() {
            return trainName;
        }

        public void setTrainName(String trainName) {
            this.trainName = trainName;
        }

        public int getTrainNo() {
            return trainNo;
        }

        public void setTrainNo(int trainNo) {
            this.trainNo = trainNo;
        }

        public boolean isOnMonday() {
            return isOnMonday;
        }

        public void setOnMonday(boolean isOnMonday) {
            this.isOnMonday = isOnMonday;
        }

        public boolean isOnTuesday() {
            return isOnTuesday;
        }

        public void setOnTuesday(boolean isOnTuesday) {
            this.isOnTuesday = isOnTuesday;
        }

        public boolean isOnWednesday() {
            return isOnWednesday;
        }

        public void setOnWednesday(boolean isOnWednesday) {
            this.isOnWednesday = isOnWednesday;
        }

        public boolean isOnThusday() {
            return isOnThusday;
        }

        public void setOnThusday(boolean isOnThusday) {
            this.isOnThusday = isOnThusday;
        }

        public boolean isOnFriday() {
            return isOnFriday;
        }

        public void setOnFriday(boolean isOnFriday) {
            this.isOnFriday = isOnFriday;
        }

        public boolean isOnSaturday() {
            return isOnSaturday;
        }

        public void setOnSaturday(boolean isOnSaturday) {
            this.isOnSaturday = isOnSaturday;
        }

        public boolean isOnSunday() {
            return isOnSunday;
        }

        public void setOnSunday(boolean isOnSunday) {
            this.isOnSunday = isOnSunday;
        }
    }

    private static RouteDataSource self = null;

    public static RouteDataSource getRouteDataSource(Context context){
        if( self == null){
            self = new RouteDataSource(context);
        }
        return self;
    }

    private RouteDataSource(Context context) {
        dbHelper = new TrainDbOpenHelper(context,"trainDb");
    }

    public void open() throws SQLException {
        dbHelper.openDataBase();
    }

    public void close() {
       dbHelper.close();
    }

    public List<RoutePoint> getRoute() {
        dbHelper.openDataBase();
        List<RoutePoint> routePoints = new ArrayList<RoutePoint>();

        Cursor cursor = dbHelper.database.query(TrainDbOpenHelper.TABLE_GEOHASHES,
                allColumns, null, null, null, null, null);
//        allColumns, TrvlrSQLiteOpenHelper.COLUMN_ROUTE_NAME+"='"+routeName+"'", null, null, null, null);
        cursor.moveToFirst();
     Log.d("RouteDatasource", "cursor length : "+cursor.getCount());
        while (!cursor.isAfterLast()) {
            RoutePoint routePoint = makeRoutePoint(cursor);
            routePoints.add(routePoint);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
//        dbHelper.close();
        return routePoints;
    }

    private RoutePoint makeRoutePoint(Cursor cursor) {
        RoutePoint routePoint = new RoutePoint();
        routePoint.setId(cursor.getInt(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_ID)));
        routePoint.setDescription(cursor.getString(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_DESCRIPTION)));
        routePoint.setGeohash(cursor.getString(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_GEOHASH)));
        routePoint.setLatitude(cursor.getDouble(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_LATITUDE)));
        routePoint.setLongitude(cursor.getDouble(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_LONGITUDE)));
        routePoint.setRouteName(cursor.getString(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_ROUTE_NAME)));
        routePoint.setScheduleTime(cursor.getLong(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_SCHEDULED_TIME)));
        routePoint.setActualTime(cursor.getLong(cursor.getColumnIndex(TrainDbOpenHelper.COLUMN_ACTUAL_TIME)));
        routePoint.setDistance(cursor.getFloat(cursor.getColumnIndex(TrainDbOpenHelper.DISTANCE_FROM_PREV_STOP)));
        routePoint.setIncludeInJourney(cursor.getInt(cursor.getColumnIndex(TrainDbOpenHelper.INCLUDE_IN_TRIP)));
        return routePoint;
    }

    public long insertRoutePoint(String routeName,String description,String geohash,double lat,double lon,long scheduleTime,float distance){
        dbHelper.openDataBase();
        ContentValues values = new ContentValues();
        values.put(TrainDbOpenHelper.COLUMN_DESCRIPTION, description);
        values.put(TrainDbOpenHelper.COLUMN_GEOHASH, geohash);
        values.put(TrainDbOpenHelper.COLUMN_LATITUDE, lat);
        values.put(TrainDbOpenHelper.COLUMN_LONGITUDE, lon);
        values.put(TrainDbOpenHelper.COLUMN_ROUTE_NAME, routeName);
        if(scheduleTime != -1){
            values.put(TrainDbOpenHelper.COLUMN_SCHEDULED_TIME, scheduleTime);
        }
        values.put(TrainDbOpenHelper.DISTANCE_FROM_PREV_STOP,distance);

        long insertId = dbHelper.database.insert(TrainDbOpenHelper.TABLE_GEOHASHES, null,
                values);
//        dbHelper.close();
        return insertId;
    }

    public void updateRoutePoint(int routePointId,long actualTime){
        dbHelper.openDataBase();
//        dbHelper.database.beginTransaction();
//        dbHelper.database.execSQL("UPDATE "+TrainDbOpenHelper.TABLE_GEOHASHES+" SET "+TrainDbOpenHelper.COLUMN_ACTUAL_TIME+
//                            "='"+actualTime+"' WHERE "+TrainDbOpenHelper.COLUMN_ID+"="+routePointId);
        ContentValues cv = new ContentValues();
        cv.put(TrainDbOpenHelper.COLUMN_ACTUAL_TIME, actualTime);
        long res =dbHelper.database.update(TrainDbOpenHelper.TABLE_GEOHASHES,cv,TrainDbOpenHelper.COLUMN_ID + "="+routePointId, null);
//        dbHelper.database.endTransaction();
        if( res > 0){
            Log.v(RouteDataSource.class.getName(),"Successful update");
        } else {
            Log.v(RouteDataSource.class.getName(),"Failed  update");
        }
    }

    public void deleteAll(){
        dbHelper.openDataBase();
        dbHelper.database.delete(TrainDbOpenHelper.TABLE_GEOHASHES, TrainDbOpenHelper.COLUMN_ID+">",new String[]{"0"});
//        dbHelper.close();
    }

    /**
     * Gets all the trains passing through the station but not terminating at the it
     * @param station
     * @return
     */
    public ArrayList<Train> getAllPassingTrains(String station){
        dbHelper.openDataBase();
        ArrayList<Train> trains = new ArrayList<Train>();
        Cursor trainCursor = dbHelper.database.rawQuery("SELECT t._id,t.trainName,t.trainNO from station_table as s, " +
                        "train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id and " +
                        "s.stationName like ? and r._id NOT IN (SELECT r._id  from station_table as s, train_table as t, " +
                        "route_table as r where r.trainId=t._id and r.stationId=s._id group by t.trainName order by r._id)",
                new String[]{"%" + station + "%"}
        );
       
        if (trainCursor.getCount() > 0) {
            trainCursor.moveToFirst();
            while (!trainCursor.isAfterLast()) {
                int id = trainCursor.getInt(0);
                String trainName = trainCursor.getString(1);
                int trainNo = trainCursor.getInt(2);

                Train aTrain = new Train(id,trainName,trainNo,true,true,true,true,true,true,true);
                trains.add(aTrain);
                trainCursor.moveToNext();
            }
        }
        trainCursor.close();
//        dbHelper.close();
        return trains;
    }

    public ArrayList<Train> getAllTrainBetweenStations(String source,String destination){
        dbHelper.openDataBase();
        ArrayList<Train> trains = new ArrayList<Train>();
        Cursor trainCursor = dbHelper.database.rawQuery("SELECT t._id,t.trainName,t.trainNO  from station_table as s, train_table as t, " +
            "route_table as r where r.trainId=t._id and r.stationId=s._id and s.stationName like ? and r.trainId in " +
            "(SELECT r.trainId from station_table as s, train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id " +
            "and s.stationName like ? ) and r._id not in " +
            "(SELECT r._id  from station_table as s, train_table as t, route_table as r where r.trainId=t._id and r.stationId=s._id group by " +
            "t.trainName order by r._id)",
                new String[]{"%" + source + "%","%" + destination + "%"}
        );
        if (trainCursor.getCount() > 0) {
            trainCursor.moveToFirst();
            while (!trainCursor.isAfterLast()) {
                int id = trainCursor.getInt(0);
                String trainName = trainCursor.getString(1);
                int trainNo = trainCursor.getInt(2);

                Train aTrain = new Train(id,trainName,trainNo,true,true,true,true,true,true,true);
                trains.add(aTrain);
                trainCursor.moveToNext();
            }
        }
        trainCursor.close();
//        dbHelper.close();
        return trains;
    }

    public void insertSampleRoute(){
        String routeName="Sample";
        double latStart = 24.7975;
        double lonStart = 92.8105;

        long startOfday = Utils.getStartOfDay(new Date());
        for(int i=0;i<10;i++){
            String des = "Sil "+i;
            String geohash = GeoHashUtils.encode(latStart,lonStart);
            insertRoutePoint(routeName,des,geohash,latStart,lonStart,startOfday+i*3600,i*20);
            latStart = latStart+2.0;
            lonStart = lonStart+1.0;
        }
    }

    public TrainDbOpenHelper getDbHelper() {
        return dbHelper;
    }
    
    
    public ArrayList<RouteDataSource.TrainStation> getStationsByGeoDistance(double distance,
			double latitude, double longitude) {
    	
    	
    	double longitude1 =longitude -57.3*( Math.asin(Math.sin(distance/3959)/Math.cos(Math.PI*latitude/180)));
    	double longitude2 =longitude + 57.3*( Math.asin(Math.sin(distance/3959)/Math.cos(Math.PI*latitude/180)));
    	
    	
    	double latitude1 = latitude - 57.3*(distance/3959);
    	double latitude2 = latitude + 57.3*(distance/3959);
    	
		// TODO Auto-generated method stub
		final String query = "select _id,stationCode,stationName,latitude,longitude from station_table  where "+
				"(latitude >= "+latitude1+") AND "+
				"(latitude <= "+latitude2 +" ) AND " +
				"(longitude >= "+longitude1+") AND "+ 
				"(longitude <="+longitude2+")"  ;
		
		
		Log.d("RouteDatasource ", "query : "+query);


		 dbHelper.openDataBase();
		Cursor stationCursor = dbHelper.database.rawQuery(query, null);
		ArrayList<RouteDataSource.TrainStation> trainStations = new ArrayList<RouteDataSource.TrainStation>();
		
		if (stationCursor.getCount() > 0) {
			stationCursor.moveToFirst();
			//   String [] columns =   stationCursor2.getColumnNames();
			while (!stationCursor.isAfterLast()) {
				int stationId = stationCursor.getInt(0);
				String stationCode = stationCursor.getString(1);
				String stationName = stationCursor.getString(2);
				String stationLatitude = stationCursor.getString(3);
				String stationLongitude = stationCursor.getString(4);
				
				if(stationName.equalsIgnoreCase("shivajinagar"))
				{
					Log.d("RouteDatasource ", "Index number : "+trainStations.size());
				}
				
				RouteDataSource.TrainStation trainStation = new RouteDataSource.TrainStation(stationId,stationName,stationCode,stationLatitude,stationLongitude);
				trainStations.add(trainStation);
				
				

				stationCursor.moveToNext();
			}
		}
		stationCursor.close();
		
		Log.d("TrainDBOpenHelper cursor count",""+stationCursor.getCount() );

		return trainStations;

		
	}
    
    
}
