package com.spydi2kood.prisma;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jim on 12/3/2014.
 */
public class SelectLocationActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.location_list, new PlaceholderFragment())
					.commit();
		}

	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends android.support.v4.app.ListFragment {

		private static final String TAG = "ListFragment";
		AQuery aq;
		ArrayList<HashMap<String, String>> values;
		//		ArrayAdapter<String> adapter;
		SimpleAdapter geoAdapter;
		ProgressDialog pDialog;
		TextView error;
		private boolean first_time;

		public PlaceholderFragment() {
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
				prepareList();
				showDialog();
				serverRequest();
				first_time = false;
			}
		}

		private void serverRequest() {
			String url = "http://83.212.109.124/Prisma/android/index";
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
			String[] from = new String[]{"namegrk", "id"};
			int[] to = new int[]{R.id.list_text, R.id.list_id};
			values = new ArrayList<HashMap<String, String>>();
			//			adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
			//					R.id.list_text, values);
			geoAdapter = new SimpleAdapter(getActivity(), values, R.layout.list_item, from, to);
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
						temp.add(map);
						Log.d(TAG, "namegrk = " + namegrk + " lat = " + lat + " long = " + lont);
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
			}
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// do something with the data
			Log.d(TAG, "Item " + position + " pressed!");
		}


	}
}
