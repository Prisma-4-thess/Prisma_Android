package com.spydi2kood.prisma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jim on 9/3/2014.
 */
public class DetailFragment extends Fragment {

	private static final String TAG = "DetailFragment";
	public static final String PID = "ID";
	AQuery aq;
	TextView error, tv1, tv2, tv3, tv4, tv5, tv6;
	ProgressDialog pDialog;
	private TextView organization;
	private TextView unit;
	private Button bAddGeo;
	private String pid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		aq = new AQuery(getActivity(), rootView);
		unit = (TextView) rootView.findViewById(R.id.unit);
		organization = (TextView) rootView.findViewById(R.id.organization);
		error = (TextView) rootView.findViewById(android.R.id.empty);
		bAddGeo = (Button) rootView.findViewById(R.id.button_add_geo);
		bAddGeo.setOnClickListener(new AddGeoListener());
		tv1 = (TextView) rootView.findViewById(R.id.textView);
		tv2 = (TextView) rootView.findViewById(R.id.textView2);
		tv3 = (TextView) rootView.findViewById(R.id.textView3);
		tv4 = (TextView) rootView.findViewById(R.id.textView4);
		tv5 = (TextView) rootView.findViewById(R.id.textView5);
		tv6 = (TextView) rootView.findViewById(R.id.textView6);
		return rootView;
	}

	private class AddGeoListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), SelectLocationActivity.class);
			intent.putExtra("id", pid);
			getActivity().startActivityForResult(intent, 1);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showDialog();
		serverRequest();
	}

	private void showDialog() {
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Φόρτωση. Παρακαλώ περιμένετε...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	private void serverRequest() {
		Bundle args = getArguments();
		pid = args.getString(PID);
		if (Prisma.debugging) Log.d(TAG, pid);
		String url = "http://83.212.109.124/Prisma/android/show/";
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("id", pid);
		aq.ajax(url, param, JSONObject.class, this, "jsonCallback");
	}

	public void jsonCallback(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			//				if (Prisma.debugging) Log.d(TAG, json.toString());
			JSONArray jArray = null;
			try {
				jArray = json.getJSONArray("marker");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("ada", "NAN");
				map.put("subject", "NAN");
				map.put("unit", "NAN");
				map.put("doc_url", "NAN");
				map.put("org", "NAN");
				map.put("geo", "false");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject childJSONObject = jArray.getJSONObject(i);
					String ada = childJSONObject.getString("ada");
					String subject = childJSONObject.getString("subject");
					String unit = childJSONObject.getString("unit");
					String doc_url = childJSONObject.getString("url");
					String org = childJSONObject.getString("org");
					map.put("ada", ada);
					map.put("subject", subject);
					map.put("unit", unit);
					map.put("doc_url", doc_url);
					map.put("org", org);
					if (childJSONObject.getBoolean("geo")) map.put("geo", "true");
				}
				updateUI(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			if (Prisma.debugging) Log.d(TAG, status.getMessage());
			error.setText(status.getMessage());
			pDialog.dismiss();
		}
	}

	private void updateUI(HashMap<String, String> map) {
		tv1.setText(map.get("ada"));
		tv2.setText(map.get("subject"));
		tv4.setText(map.get("unit"));
		tv5.setText(
				Html.fromHtml(
						"<a href=\"http://static.diavgeia.gov.gr/doc/" + map.get("ada") + "\">Download Link</a>"));
		tv5.setMovementMethod(LinkMovementMethod.getInstance());
		tv3.setText(map.get("org"));
		unit.setText("Unit");
		organization.setText("Organization");
		if (getArguments().getBoolean("geo") && map.get("geo").equals("false")) {
			bAddGeo.setVisibility(View.VISIBLE);
		} else if (getArguments().getBoolean("geo") && map.get("geo").equals("true")) {
			tv6.setVisibility(View.VISIBLE);
		}
		pDialog.dismiss();
	}


}
