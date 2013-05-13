package com.gulshan.batchapplistbackup;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gulshan.batchapplistbackup.db.AppSetOpenHelper;
import com.gulshan.batchapplistbackup.db.DatabaseContract;

public class AppSetActivity extends ListActivity {

	private AppSetOpenHelper mDb;
	private Cursor mCursor;

	private ListView mListView;
	private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application_set);

		mListView = getListView();
		mDb = new AppSetOpenHelper(this);
		mCursor = mDb.getAppSets();

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					final View view, int pos, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AppSetActivity.this);
				final String[] options = new String[] { "Delete" };
				builder.setTitle("Options").setItems(options,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String option = options[which];
								if (option.equals("Delete")) {
									String setName = ((TextView) view
											.findViewById(android.R.id.text1))
											.getText().toString();
									mDb.deleteSet(setName);
									mCursor.requery();
									mAdapter.notifyDataSetChanged();
								}
							}
						});
				builder.create().show();
				return false;
			}
		});
		// getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mCursor.requery();
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, mCursor,
				new String[] { DatabaseContract.AppSet.COLUMN_NAME_TITLE },
				new int[] { android.R.id.text1 }, 0);
		mListView.setAdapter(mAdapter);
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
