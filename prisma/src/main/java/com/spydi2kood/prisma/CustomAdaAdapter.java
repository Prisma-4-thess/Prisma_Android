package com.spydi2kood.prisma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jim on 11/3/2014.
 */
public class CustomAdaAdapter extends SimpleAdapter{
	private ArrayList<HashMap<String,String>> data;
	private Context context;

	/**
	 * Constructor
	 *
	 * @param context  The context where the View associated with this SimpleAdapter is running
	 * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
	 *                 Maps contain the data for each row, and should include all the entries specified in
	 *                 "from"
	 * @param resource Resource identifier of a view layout that defines the views for this list
	 *                 item. The layout file should include at least those named views defined in "to"
	 * @param from     A list of column names that will be added to the Map associated with each
	 *                 item.
	 * @param to       The views that should display column in the "from" parameter. These should all be
	 *                 TextViews. The first N views in this list are given the values of the first N columns
	 */
	public CustomAdaAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
	}

	@Override
	public HashMap<String,String> getItem(int position) {
		return data.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final HashMap<String,String> item = getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.decision_item, parent, false);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.list_text);
			holder.subj = (TextView) convertView.findViewById(R.id.dec_sub);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (item.get("geo").equals("false")){
			holder.text.setBackgroundResource(R.color.WeakRed);
			holder.subj.setBackgroundResource(R.color.WeakRed);
		}
		else{
			holder.text.setBackgroundResource(R.color.WeakGreen);
			holder.subj.setBackgroundResource(R.color.WeakGreen);
		}

		holder.text.setText(item.get("ada"));
		holder.subj.setText(item.get("subject"));

		return convertView;
	}

	static class ViewHolder {
		TextView text;
		TextView subj;
	}
}
