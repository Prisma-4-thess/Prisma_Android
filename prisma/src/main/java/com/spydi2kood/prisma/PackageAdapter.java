package com.spydi2kood.prisma;

/**
 * Created by jim on 24/2/2014.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PackageAdapter extends BaseAdapter {

	private List<PackageItem> data;
	private Context context;

	public PackageAdapter(Context context, List<PackageItem> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public PackageItem getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// @Override
	// public boolean isEnabled(int position) {
	// if (position == 2) {
	// return false;
	// } else {
	// return true;
	// }
	// }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final PackageItem item = getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.drawer_list_item, parent, false);
			holder = new ViewHolder();
			holder.ivImage = (ImageView) convertView.findViewById(R.id.drawer_image);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.drawer_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.ivImage.setImageDrawable(item.getIcon());
		holder.tvTitle.setText(item.getName());

		return convertView;
	}

	static class ViewHolder {
		ImageView ivImage;
		TextView tvTitle;
	}

}
