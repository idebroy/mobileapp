package com.idr.trvlr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.idr.trvlr.sqlite.SyncData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GlobalService  extends Service implements LocationListener{

	private LocationManager locationManager;
	private SyncData syncData;
	AsyncHttpClient client;
	StringEntity entity;



	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		locationManager =(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000, 0, this);


		syncData = new SyncData();
		client = new AsyncHttpClient();
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onLocationChanged(Location location) {

		syncData.setLatitude(""+location.getLatitude());
		syncData.setLongitude(""+location.getLongitude());

		SyncData data1 = new SyncData();
		data1.setPhoneNumber("9090909091");
		data1.setTrainNumer("121");

		data1.setDataTime("2012-12-09 09:34:23");
		data1.setLongitude(""+location.getLongitude());
		data1.setLatitude(""+location.getLatitude());

		postSyncData(data1);
		//new networkingRequest().execute();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {		
	}

	private void basicHttpPostRequest()
	{
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		String uri = "http://208.109.191.68:8080/trvlr/syncdata";
		HttpPost httpPost = new HttpPost(uri.toString());
		httpPost.setHeader("Content-Type", "text/plain");
		
		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("email", "user@gmail.com"));
		nameValuePair.add(new BasicNameValuePair("message","Hi, trying Android HTTP post!"));
		
		JSONObject params = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			params.put("trainNumer", "12344");
			jsonArray.put(params);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		// Url Encoding the POST parameters
		try {
			// StringEntity(jsonArray.toString())
			httpPost.setEntity(new StringEntity(jsonArray.toString()));
		} 
		catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
	
			HttpResponse response = httpClient.execute(httpPost);

			// writing response to log
			if (response.getStatusLine().getStatusCode()==200) {
				Log.d("Http Response:", "success");
			}
			else {
				Log.d("Http Response:", "response code : "+ response.getStatusLine().getStatusCode());
			}
			
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();

		}
	}

	/*Method to post synch data */
	private void postSyncData(SyncData data) {


		RequestParams params = new RequestParams();
		params.put("latitude", "21.1414");
		params.put("latitude","12441");
		params.put("phoneNumber","12441");
		params.put("dataTime","12441");
		params.put("trainNumer","12441");



		// send by client: json format
		// [{"trainNumer":"8003","phoneNumber":null,"dataTime":"2012-12-09 09:34:23.0","longitude":"0.748476","latitude":"0.748476"}]

		// our input 
		// [{"longitude":"0.748476","latitude":"0.748476","dataTime":"2012-12-09 09:34:23.0","trainNumer":"8003"}]

		try {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("trainNumer", "8003");
			jsonObject.put("phoneNumber", "9718979013");
			jsonObject.put("dataTime", "2012-12-09 09:34:23.0");
			jsonObject.put("latitude", "0.748476");
			jsonObject.put("longitude", "0.748476");

			JSONArray  jsonArray = new JSONArray();
			jsonArray.put(jsonObject);

			Log.d("Global Service : json input", jsonArray.toString());

			entity = new StringEntity(jsonArray.toString(),"UTF-8");
						
//			entity = new StringEntity("[{'trainNumer':'8003','phoneNumber':null,'dataTime':'2012-12-09 09:34:23.0','longitude':'0.748476','latitude':'0.748476'}]");
			entity.setContentType("text/plain");
			Log.d("Global Service ", entity.getContentType()+"");			
			
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		} 

		//	client.post( "http://208.109.191.68:8080/trvlr/syncdata", params, new AsyncHttpResponseHandler() {
	
		client.post(getApplicationContext(), "http://208.109.191.68:8080/trvlr/syncdata", entity,"text/plain", new AsyncHttpResponseHandler() 
		{
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d("GlobalService", "Succeed");
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Log.d("GlobalService : fails ", "error code "+ arg0 + " error desc " + new String(arg2));
			}
		});
		
		


	}

	private class networkingRequest extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			basicHttpPostRequest();
			return null;
		}
		
	}
	
}
