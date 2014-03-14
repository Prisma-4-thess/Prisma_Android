package com.spydi2kood.prisma;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
 * Created by jim on 9/3/2014.
 */
public class DecisionFragment extends ListFragment {

	private static final String TAG = "ListFragment";
	public static final String PID = "ADA";
	AQuery aq;
	ArrayList<HashMap<String, String>> values;
	SimpleAdapter adaAdapter;
	ProgressDialog pDialog;
	TextView error;
	private boolean first_time;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		first_time = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_decision, container, false);
		aq = new AQuery(getActivity(), rootView);
		error = (TextView) rootView.findViewById(android.R.id.empty);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (first_time == true) {
			prepareList();
			showDialog();
			serverRequest();
			first_time = false;
		}
	}

	private void serverRequest() {
		Bundle args = getArguments();
		String pid = args.getString(PID);
		if (Prisma.debugging) Log.d(TAG, pid);
		String url = "http://83.212.109.124/Prisma/android/rgeo/";
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("id", pid);
		aq.ajax(url, param, JSONObject.class, this, "jsonCallback");
	}

	private void prepareList() {
		String[] from = new String[]{"ada", "subject"};
		int[] to = new int[]{R.id.list_text, R.id.dec_sub};
		values = new ArrayList<HashMap<String, String>>();
		//			adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
		//					R.id.list_text, values);
		adaAdapter = new SimpleAdapter(getActivity(), values, R.layout.decision_item, from, to);
		setListAdapter(adaAdapter);
	}

	private void showDialog() {
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Φόρτωση. Παρακαλώ περιμένετε...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	public void jsonCallback(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			new readJSON().execute(json);
			error.setText("Καμία απόφαση");
		} else {
			if (Prisma.debugging) Log.d(TAG, status.getMessage());
			error.setText(status.getMessage());
			pDialog.dismiss();
		}
	}

	private class readJSON extends AsyncTask<JSONObject, String, String> {

		@Override
		protected String doInBackground(JSONObject... params) {
			ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
			try {
				JSONArray jArray = params[0].getJSONArray("marker");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject childJSONObject = jArray.getJSONObject(i);
					String ada = childJSONObject.getString("ada");
					String subject = childJSONObject.getString("subject");
					String id = childJSONObject.getString("id");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("ada", ada);
					map.put("subject", subject);
					map.put("id", id);
					temp.add(map);
					if (Prisma.debugging) Log.d(TAG, "ada = " + ada + " subject: " + subject);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			updateList(temp);
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			adaAdapter.notifyDataSetChanged();
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
		if (Prisma.debugging) Log.d(TAG, "Item " + position + " pressed! Ada: " + values.get(position).get("ada"));
		MainActivity temp = (MainActivity) getActivity();
		temp.showDetails(values.get(position).get("id"), 0);
	}

}
