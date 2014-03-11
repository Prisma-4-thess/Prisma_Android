package com.spydi2kood.prisma;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class MainActivity extends ActionBarActivity {

	private String[] mDrawerOptions;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String[] mTitle = new String[] {"Αναζήτηση","Προσδιορισμός"};
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

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);


		mItem = new ArrayList<PackageItem>();
		getSupportActionBar().setTitle("Αναζήτηση");
		for (String opt : mTitle){
			PackageItem temp = new PackageItem();
			temp.setName(opt);
			if (opt.equals("Αναζήτηση")) temp.setIcon(getResources().getDrawable(R.drawable.search_geo));
			if (opt.equals("Προσδιορισμός")) temp.setIcon(getResources().getDrawable(R.drawable.location_new));
			mItem.add(temp);
		}

//		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//				R.layout.drawer_list_item,R.id.drawer_text, mDrawerOptions));

		mDrawerList.setAdapter(new PackageAdapter(this,mItem));

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerList.setItemChecked(0, true);


		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment())
					.commit();
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
		switch (position){
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);

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

	public void showDetails(String id) {
		DetailFragment newFragment = new DetailFragment();
		Bundle args = new Bundle();
		args.putString(DetailFragment.PID, id);
		newFragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.container, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	public void updateTitle(String title,int off){
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
			temp.updateTitle("Αναζήτηση",0);
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
			MainActivity temp = (MainActivity) getActivity();
			temp.showDecision(values.get(position).get("id"));
		}


	}

}
