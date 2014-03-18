package com.spydi2kood.prisma;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

/**
 * Created by jim on 11/3/2014.
 */
public class AddGeo extends Fragment {
	private static final String TAG = "addGeo";
	private ProgressDialog pDialog;
	private boolean first_time;
	private AQuery aq;
	private EditText ada;
	private MainActivity mAct;
	private Button buttonSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.geo_add, container, false);
		aq = new AQuery(getActivity(), rootView);
		buttonSearch = (Button) rootView.findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(new buttonClickListener());
		ada = (EditText) rootView.findViewById(R.id.entered_ada);
		MainActivity temp = (MainActivity) getActivity();
		temp.updateTitle("Προσδιορισμός", 1);
		return rootView;
	}

	private class buttonClickListener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			if (ada.getText().toString() != null) {
				InputMethodManager inputManager =
						(InputMethodManager) getActivity().
								getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(
						getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				String mada = ada.getText().toString();
				showDialog();
				serverRequest(mada);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		first_time = true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (first_time) {
			mAct = (MainActivity) getActivity();
		}
		//		showDialog();
	}

	private void showDialog() {
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Φόρτωση. Παρακαλώ περιμένετε...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	private void serverRequest(String ada) {
		Log.d(TAG, ada);
		String url = "http://83.212.109.124/Prisma/android/ada/".concat(ada);
		aq.ajax(url, JSONObject.class, this, "jsonCallback");
	}

	public void jsonCallback(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {
			mAct.setsJson(json);
			pDialog.dismiss();
			mAct.showAdaDecision();
			Log.d(TAG, json.toString());
		} else {
			Log.d(TAG, status.getMessage());
			pDialog.dismiss();
			Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_LONG).show();
			//			error.setText(status.getMessage());
		}
	}

}
