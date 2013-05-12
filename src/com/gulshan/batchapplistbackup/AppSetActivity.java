package com.gulshan.batchapplistbackup;

import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gulshan.batchapplistbackup.db.AppSetOpenHelper;
import com.gulshan.batchapplistbackup.db.DatabaseContract;

public class AppSetActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application_set);

		// getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Cursor c = new AppSetOpenHelper(this).getAppSets();
		getListView()
				.setAdapter(
						new SimpleCursorAdapter(
								this,
								android.R.layout.simple_list_item_1,
								c,
								new String[] { DatabaseContract.AppSet.COLUMN_NAME_TITLE },
								new int[] { android.R.id.text1 }, 0));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, AppListActivity.class);
		intent.putExtra("SET_NAME", ((TextView) v
				.findViewById(android.R.id.text1)).getText().toString());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_application_set, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_set:
			Intent intent = new Intent(this, AppListActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_import:
			importSet();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void importSet() {
		// TODO
	}

	// @Override
	// public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
	// switch (loaderId) {
	// case 0:
	// return new CursorLoader(this, DatabaseContract.AppSet.Uri,
	// new String[] { DatabaseContract.AppSet.COLUMN_NAME_TITLE });
	// break;
	// default:
	// return null;
	// }
	// return null;
	// }
	//
	// @Override
	// public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
	// }
	//
	// @Override
	// public void onLoaderReset(Loader<Cursor> arg0) {
	// }
}
