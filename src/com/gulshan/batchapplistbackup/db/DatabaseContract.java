package com.gulshan.batchapplistbackup.db;

import android.provider.BaseColumns;

public final class DatabaseContract {
	public static final class AppSet implements BaseColumns {
		public static final String TABLE_NAME = "app_sets";
		public static final String COLUMN_NAME_TITLE = "title";
	}

	public static final class AppList implements BaseColumns {
		public static final String TABLE_NAME = "app_lists";
		public static final String COLUMN_NAME_SET_ID = "set_id";
		public static final String COLUMN_NAME_APP_ID = "app_id";
	}

	public static final class Apps implements BaseColumns {
		public static final String TABLE_NAME = "apps";
		public static final String COLUMN_NAME_NAME = "name";
	}
}
