package com.spydi2kood.prisma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = "MainActivity";
	private String[] mDrawerOptions;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String[] mTitle = new String[]{"Αναζήτηση", "Προσδιορισμός"};
	private ArrayList<PackageItem> mItem;
	private ActionBarDrawerToggle mDrawerToggle;
	private String previousTitle, mDrawerTitle = "Επιλογές";
	private JSONObject sJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		previousTitle = mTitle[0];

		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_navigation_drawer,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(previousTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				previousTitle = getSupportActionBar().getTitle().toString();
				getSupportActionBar().setTitle(mDrawerTitle);
			}
		};

		Intent intent = getIntent();
		int message = intent.getIntExtra("fragment", 0);

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);


		mItem = new ArrayList<PackageItem>();
		getSupportActionBar().setTitle("Αναζήτηση");
		for (String opt : mTitle) {
			PackageItem temp = new PackageItem();
			temp.setName(opt);
			if (opt.equals("Αναζήτηση")) temp.setIcon(getResources().getDrawable(R.drawable.search_geo));
			if (opt.equals("Προσδιορισμός")) temp.setIcon(getResources().getDrawable(R.drawable.location_new));
			mItem.add(temp);
		}

		//		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		//				R.layout.drawer_list_item,R.id.drawer_text, mDrawerOptions));

		mDrawerList.setAdapter(new PackageAdapter(this, mItem));

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerList.setItemChecked(0, true);


		if (savedInstanceState == null) {
			if (message == 0) {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, new PlaceholderFragment())
						.commit();
			} else if (message == 1) {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, new AddGeo())
						.commit();
			}

		}

	}

	public JSONObject getsJson() {
		return sJson;
	}

	public void setsJson(JSONObject sJson) {
		this.sJson = sJson;
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}

	}

	private void selectItem(int position) {
		switch (position) {
			case 0:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, new PlaceholderFragment())
						.addToBackStack(null)
						.commit();
				break;
			case 1:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, new AddGeo())
						.addToBackStack(null)
						.commit();
		}
		previousTitle = mTitle[position];
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "entered " + requestCode + " " + resultCode);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Log.d(TAG, "All is ok");
				Log.d(TAG, data.getStringExtra("gid")+ " "+data.getStringExtra("did"));
				updateDecData(data.getStringExtra("gid"),data.getStringExtra("did"));
			} else if (resultCode == RESULT_CANCELED) {
				Log.d(TAG, "Nothing is ok");
			} else if (resultCode == 2) {
				Log.d(TAG, "User defined");
				if (data.getStringExtra("address").equals("")) {
					Log.d(TAG, "NULL");
				}
				if (data.getStringExtra("namegrk").equals("")) {
					Log.d(TAG, "NULL");
				}
				Log.d(TAG, data.getStringExtra("address") + " " + data.getStringExtra("namegrk") + " " + data.getStringExtra("latitude") + " " + data.getStringExtra("longitude") + " ");
			}
		}
	}

	private void updateDecData(String gid,String did) {
		AQuery aq = new AQuery(this);
		String url = "http://83.212.109.124/Prisma/android/predef";
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("gid", gid);
		param.put("did", did);
		aq.ajax(url,param, JSONObject.class, this, "sendNewData");
		Log.d(TAG,"update decision data");
	}

	public void sendNewData(String url, JSONObject json, AjaxStatus status) {
		Log.d(TAG,"sendNewData");
		if (json != null) {
			try {
				Toast.makeText(this,json.getString("status"),Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this,"Something went wrong...",Toast.LENGTH_LONG).show();
		}
	}

	public void showDecision(String id) {
		DecisionFragment newFragment = new DecisionFragment();
		Bundle args = new Bundle();
		args.putString(DecisionFragment.PID, id);
		newFragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.container, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	public void showAdaDecision() {
		AdaFragment newFragment = new AdaFragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.container, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void showDetails(String id, int temp) {
		DetailFragment newFragment = new DetailFragment();
		Bundle args = new Bundle();
		args.putString(DetailFragment.PID, id);
		newFragment.setArguments(args);
		if (temp == 1) args.putBoolean("geo", true);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.container, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	public void updateTitle(String title, int off) {
		getSupportActionBar().setTitle(title);
		mDrawerList.setItemChecked(off, true);
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
			MainActivity temp = (MainActivity) getActivity();
			temp.updateTitle("Αναζήτηση", 0);
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
			String[] from = new String[]{"namegrk", "counter", "address"};
			int[] to = new int[]{R.id.list_text, R.id.list_id, R.id.main_list_address};
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
						String counter = childJSONObject.getString("counter");
						String address = childJSONObject.getString("address");
						//						if (Math.abs(lat - 40.60681615178682) < 0.01 && Math.abs(lont - 22.982468830602212) < 0.01) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("namegrk", namegrk);
						map.put("id", id);
						map.put("latitute", lat.toString());
						map.put("longitude", lont.toString());
						map.put("counter", counter);
						map.put("address", address);
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
			MainActivity temp = (MainActivity) getActivity();
			temp.showDecision(values.get(position).get("id"));
		}

	}

}
