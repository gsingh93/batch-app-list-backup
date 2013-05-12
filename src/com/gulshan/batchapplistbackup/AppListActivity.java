package com.gulshan.batchapplistbackup;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Toast;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

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
	private AppListAdapter mAdapter;

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
		// Get a list of all of the installed packages
		List<ApplicationInfo> installedPackages = getPackageManager()
				.getInstalledApplications(PackageManager.GET_META_DATA);

		// Filter out the ones that aren't launchable
		List<AppInfo> packages = filterLaunchablePackages(installedPackages);

		mAdapter = new AppListAdapter(this, packages);
		mListView.setAdapter(mAdapter);
	}

	private List<AppInfo> filterLaunchablePackages(
			List<ApplicationInfo> installedPackages) {
		// Get the names of all of the apps that should be checked
		Set<String> checkedApps = new HashSet<String>();
		if (mName != null) {
			Cursor c = mDb.getAppsInSet(mName);
			int index = c.getColumnIndexOrThrow(Apps.COLUMN_NAME_NAME);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				checkedApps.add(c.getString(index));
			}
		}

		// Create a list of launchable applications
		PackageManager pm = getPackageManager();
		List<AppInfo> packages = new ArrayList<AppInfo>();
		for (ApplicationInfo info : installedPackages) {
			if (pm.getLaunchIntentForPackage(info.packageName) != null) {
				String name = (String) info.loadLabel(pm);
				packages.add(new AppInfo(info.packageName, name, info
						.loadIcon(pm), checkedApps.contains(name)));
			}
		}

		// Sort the apps lexicographically
		Collections.sort(packages, new PackageComparator());

		return packages;
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
			List<AppInfo> apps = getCheckedApps();
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

	private List<AppInfo> getCheckedApps() {
		List<AppInfo> info = mAdapter.getPackageInfo();
		List<AppInfo> apps = new ArrayList<AppInfo>();
		for (AppInfo app : info) {
			if (app.isChecked()) {
				apps.add(app);
			}
		}
		return apps;
	}
}
