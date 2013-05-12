package com.gulshan.batchapplistbackup;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String mPackageName;
	private String mName;
	private Drawable mIcon;
	private boolean mChecked;

	public AppInfo(String packageName, String name, Drawable icon,
			boolean checked) {
		mPackageName = packageName;
		mName = name;
		mIcon = icon;
		mChecked = checked;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public void setIcon(Drawable mIcon) {
		this.mIcon = mIcon;
	}

	public boolean isChecked() {
		return mChecked;
	}

	public void setChecked(boolean mChecked) {
		this.mChecked = mChecked;
	}
}
