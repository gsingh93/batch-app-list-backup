package com.gulshan.batchapplistbackup;

import java.util.List;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AppListAdapter extends ArrayAdapter<AppInfo> {

	private Context mContext;
	private List<AppInfo> mPackages;

	public AppListAdapter(Context context, List<AppInfo> packages) {
		super(context, R.layout.app_list_item, packages);
		mContext = context;
		mPackages = packages;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.inflate(mContext,
					R.layout.app_list_item);
			holder = new ViewHolder();
			holder.appIcon = (ImageView) convertView
					.findViewById(R.id.app_icon);
			holder.appName = (TextView) convertView.findViewById(R.id.app_name);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.app_checkbox);
			holder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AppInfo info = mPackages.get(position);
					info.setChecked(!info.isChecked());
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AppInfo info = mPackages.get(position);
		holder.appIcon.setImageDrawable(info.getIcon());
		holder.appName.setText(info.getName());
		holder.checkBox.setChecked(info.isChecked());

		return convertView;
	}

	private static class ViewHolder {
		ImageView appIcon;
		TextView appName;
		CheckBox checkBox;
	}

}
