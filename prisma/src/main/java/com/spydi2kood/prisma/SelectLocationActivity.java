package com.spydi2kood.prisma;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jim on 12/3/2014.
 */
public class SelectLocationActivity extends ActionBarActivity {

	private static final String TAG = "Select Location Activity";
	private GoogleMap googleMap;
	private Marker myMarker;
	private Boolean predefined;
	private HashMap<String, String> locDet;
	private String did;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		did = getIntent().getStringExtra("id");

		locDet = new HashMap<String, String>();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.location_list, new PlaceholderFragment())
					.commit();
			//			getSupportFragmentManager().beginTransaction()
			//					.add(R.id.location_map, new MapFragment())
			//					.commit();
		}

	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
					R.id.map)).getMap();
			googleMap.setOnMapLongClickListener(new ListenForLongClick());
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
					new LatLng(40.626, 22.947)).zoom(10).build();

			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private class ListenForLongClick implements GoogleMap.OnMapLongClickListener {

		@Override
		public void onMapLongClick(LatLng latLng) {
			if (myMarker != null) myMarker.remove();
			MarkerOptions marker = new MarkerOptions().position(latLng).title("Νέα Θέση");
			myMarker = googleMap.addMarker(marker);
			predefined = false;
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
					latLng).zoom(18).build();
			updateSpecList();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	private void updateSpecList() {
		double latitude = myMarker.getPosition().latitude;
		double longitude = myMarker.getPosition().longitude;
		PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.location_list);
		fragment.changeList(latitude, longitude);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.accept_location) {
			sendNewGeoData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void sendNewGeoData() {
		if (myMarker != null) {
			Intent returnIntent = new Intent();
			Log.d(TAG, "Latitude: " + Double.toString(myMarker.getPosition().latitude) + " Longitude: " + Double.toString(myMarker.getPosition().longitude));
			if (predefined) {
				//				Toast.makeText(this,"Predefined Location",Toast.LENGTH_LONG).show();
				Log.d(TAG,locDet.get("id"));
				returnIntent.putExtra("gid", locDet.get("id"));
				returnIntent.putExtra("did", did);
				setResult(RESULT_OK, returnIntent);
				finish();
			} else {
				//				Toast.makeText(this,"User-defined Location",Toast.LENGTH_LONG).show();
				uploadLocationInfo();
			}
			//			Toast.makeText(this,"Data Send!",Toast.LENGTH_LONG).show();
		} else {
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}
	}

	public void errorFields() {
		Toast.makeText(this, "Check fields again!", Toast.LENGTH_LONG).show();
	}

	private void uploadLocationInfo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View view = inflater.inflate(R.layout.upload_dialog, null);
		builder.setView(view)
				.setPositiveButton(R.string.uploadPassword, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Intent returnIntent = new Intent();
						EditText address = (EditText) view.findViewById(R.id.dialog_address);
						EditText namegrk = (EditText) view.findViewById(R.id.dialog_namegrk);
						String sAddress = address.getText().toString();
						String sNamegrk = namegrk.getText().toString();
						String latitude = Double.toString(myMarker.getPosition().latitude);
						String longitude = Double.toString(myMarker.getPosition().longitude);
						if (sNamegrk.equals("") || sAddress.equals("")) {
							errorFields();
							return;
						}
						returnIntent.putExtra("address", sAddress);
						returnIntent.putExtra("namegrk", sNamegrk);
						returnIntent.putExtra("latitude", latitude);
						returnIntent.putExtra("longitude", longitude);
						setResult(2, returnIntent);
						dialog.dismiss();
						finish();
					}
				})
				.setNegativeButton(R.string.cancelUploadPassword, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		Dialog uploadDialog = builder.create();
		uploadDialog.show();
	}


	public void addMarker(HashMap<String, String> locDet) {
		if (myMarker != null) myMarker.remove();
		double latitude = Double.parseDouble(locDet.get("latitude"));
		double longitude = Double.parseDouble(locDet.get("longitude"));
		String title = locDet.get("namegrk");
		MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(title);

		// adding marker
		myMarker = googleMap.addMarker(marker);
		predefined = true;
		this.locDet = locDet;
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				new LatLng(latitude, longitude)).zoom(16).build();

		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends android.support.v4.app.ListFragment {

		private SelectLocationActivity mAct;
		private static final String TAG = "ListFragment";
		AQuery aq;
		ArrayList<HashMap<String, String>> values;
		ArrayList<HashMap<String, String>> allValues;
		//		ArrayAdapter<String> adapter;
		SimpleAdapter geoAdapter;
		ProgressDialog pDialog;
		TextView error;
		private boolean first_time;

		public PlaceholderFragment() {
		}

		public void changeList(double latitude, double longitude) {
			values.clear();
			for (HashMap<String, String> item : allValues) {
				double lat = Double.parseDouble(item.get("latitude"));
				double lont = Double.parseDouble(item.get("longitude"));
				if (Math.abs(lat - latitude) < 0.001 && Math.abs(lont - longitude) < 0.001) {
					values.add(item);
				}
			}
			geoAdapter.notifyDataSetChanged();
		}


		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			first_time = true;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			aq = new AQuery(getActivity(), rootView);
			error = (TextView) rootView.findViewById(android.R.id.empty);
			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (first_time) {
				mAct = (SelectLocationActivity) getActivity();
				prepareList();
				showDialog();
				serverRequest();
				first_time = false;
			}
		}

		private void serverRequest() {
			String url = "http://83.212.109.124/Prisma/android/all";
			aq.ajax(url, JSONObject.class, this, "jsonCallback");
		}

		private void showDialog() {
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		private void prepareList() {
			String[] from = new String[]{"namegrk"};
			int[] to = new int[]{R.id.list_text};
			values = new ArrayList<HashMap<String, String>>();
			allValues = new ArrayList<HashMap<String, String>>();
			//			adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
			//					R.id.list_text, values);
			geoAdapter = new SimpleAdapter(getActivity(), values, R.layout.fragment_add_location_list_item, from, to);
			setListAdapter(geoAdapter);
		}

		public void jsonCallback(String url, JSONObject json, AjaxStatus status) {
			if (json != null) {
				//				Log.d(TAG, json.toString());
				new readJSON().execute(json);

			} else {
				Log.d(TAG, status.getMessage());
				error.setText(status.getMessage());
				pDialog.dismiss();
			}
		}

		private class readJSON extends AsyncTask<JSONObject, String, String> {

			@Override
			protected String doInBackground(JSONObject... params) {
				try {
					JSONArray jArray = params[0].getJSONArray("marker");
					ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject childJSONObject = jArray.getJSONObject(i);
						String namegrk = childJSONObject.getString("namegrk");
						String id = childJSONObject.getString("id");
						Double lat = childJSONObject.getDouble("latitude");
						Double lont = childJSONObject.getDouble("longitude");
						//						if (Math.abs(lat - 40.60681615178682) < 0.01 && Math.abs(lont - 22.982468830602212) < 0.01) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("namegrk", namegrk);
						map.put("id", id);
						map.put("latitude", lat.toString());
						map.put("longitude", lont.toString());
						temp.add(map);
						//						Log.d(TAG, "namegrk = " + namegrk + " lat = " + lat + " long = " + lont);
						//						}
					}
					updateList(temp);
				} catch (JSONException e) {
					e.printStackTrace();
					//					error.setText("Error");
				}
				return null;
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				geoAdapter.notifyDataSetChanged();
				pDialog.dismiss();
			}
		}

		private void updateList(ArrayList<HashMap<String, String>> temp) {
			values.clear();
			for (HashMap<String, String> element : temp) {
				values.add(element);
				allValues.add(element);
			}
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// do something with the data
			Log.d(TAG, "Item " + position + " pressed!");
			String lat = values.get(position).get("latitude");
			String lont = values.get(position).get("longitude");
			Log.d(TAG, lat + " " + lont);
			mAct.addMarker(values.get(position));
		}


	}
}
