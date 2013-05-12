package com.gulshan.batchapplistbackup;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gulshan.batchapplistbackup.db.AppSetOpenHelper;
import com.gulshan.batchapplistbackup.db.DatabaseContract.Apps;
import com.gulshan.batchapplistbackup.db.DuplicateSetException;

public class AppListActivity extends Activity {

	private EditText mTitle;
	private ListView mListView;
	private String mName;
	private AppSetOpenHelper mDb = new AppSetOpenHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application_list);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Save");
		actionBar.setIcon(R.drawable.ic_save_light);
		actionBar.setHomeButtonEnabled(true);

		mTitle = (EditText) findViewById(R.id.app_list_title);
		mListView = (ListView) findViewById(R.id.app_list);

		mName = getIntent().getStringExtra("SET_NAME");
		if (mName != null) {
			mTitle.setText(mName);
		}

		mTitle.setSelection(mTitle.getText().length());

		populateAppList();
	}

	private void populateAppList() {
		Set<String> checkedApps = new HashSet<String>();
		if (mName != null) {
			Cursor c = mDb.getAppsInSet(mName);
			int index = c.getColumnIndexOrThrow(Apps.COLUMN_NAME_NAME);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				checkedApps.add(c.getString(index));
			}
		}

		PackageManager pm = getPackageManager();
		List<ApplicationInfo> installedPackages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);

		// Get launchable packages and sort them
		// TODO There's a cleaner way to do this
		List<AppInfo> packages = new ArrayList<AppInfo>();
		for (ApplicationInfo info : installedPackages) {
			if (pm.getLaunchIntentForPackage(info.packageName) != null) {
				String name = (String) info.loadLabel(pm);
				packages.add(new AppInfo(info.packageName, name, info
						.loadIcon(pm), checkedApps.contains(name)));
			}
		}
		Collections.sort(packages, new PackageComparator());

		mListView.setAdapter(new AppListAdapter(this, packages));
	}

	private class PackageComparator implements Comparator<AppInfo> {

		private Collator mCollator = Collator.getInstance();

		@Override
		public int compare(AppInfo package1, AppInfo package2) {
			return mCollator.compare(package1.getName(), package2.getName());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater()
				.inflate(R.menu.activity_application_list, menu);
		if (mName != null) {
			menu.findItem(R.id.menu_backup).setEnabled(true);
			menu.findItem(R.id.menu_restore).setEnabled(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (save()) {
				finish();
			}
			break;
		case R.id.menu_backup:
			backup();
			break;
		case R.id.menu_restore:
			restore();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void backup() {
		getCheckedApps();
	}

	private void restore() {
		// TODO
	}

	private boolean save() {
		AppSetOpenHelper db = new AppSetOpenHelper(this);
		String title = mTitle.getText().toString();
		if (title.equals("")) {
			Toast.makeText(this, "You must enter a title", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			List<String> apps = getCheckedApps();
			try {
				db.saveApplicationSet(title, apps, mName);
			} catch (DuplicateSetException e) {
				Toast.makeText(this,
						"This name was already used. Choose another one.",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	private List<String> getCheckedApps() {
		List<String> apps = new ArrayList<String>();
		for (int i = 0; i < mListView.getCount(); i++) {
			View v = (View) mListView.getAdapter().getView(i, null, null);
			CheckBox cb = (CheckBox) v.findViewById(R.id.app_checkbox);
			if (cb.isChecked()) {
				apps.add(((TextView) v.findViewById(R.id.app_name)).getText()
						.toString());
			}
		}

		return apps;
	}
}
