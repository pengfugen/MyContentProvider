package com.example.provider;

import java.util.HashMap;

import com.example.mycontentprovider.MainActivity;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PicContentProvider extends ContentProvider {
	DatabaseHelper dbHelper;
	private ContentResolver resolver = null;
	static UriMatcher matcher;
	static {
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(Constants.AUTHORITY, Constants.TABLE, Constants.ITEMS);
		matcher.addURI(Constants.AUTHORITY, Constants.TABLE + "/#",
				Constants.ITEM);
	}

	private static final HashMap<String, String> picProjectionMap;
	static {
		picProjectionMap = new HashMap<String, String>();
		picProjectionMap.put(Constants.ID, Constants.ID);
		picProjectionMap.put(Constants.URI, Constants.URI);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int row = 0;
		switch (matcher.match(uri)) {
		case Constants.ITEM:
			String id = uri.getPathSegments().get(1);
			row = dbHelper.getWritableDatabase().delete(
					Constants.TABLE,
					Constants.ID
							+ " = "
							+ id
							+ (!TextUtils.isEmpty(selection) ? " and ("
									+ selection + ')' : ""), selectionArgs);
		case Constants.ITEMS:
			row = dbHelper.getWritableDatabase().delete(Constants.TABLE, null, null);
		}

		return row;
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case Constants.ITEM:
			return Constants.CONTENT_ITEM_TYPE;
		case Constants.ITEMS:
			return Constants.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (matcher.match(uri) != Constants.ITEMS) { // 这里肯定是Constants.CONTENT_URI这种格式的
			throw new IllegalArgumentException("Unknown URI " + uri + " match:"
					+ matcher.match(uri));
		}
		
		long rowid = dbHelper.getWritableDatabase().insert(Constants.TABLE, null,
				values);
		Log.d(MainActivity.TAG, "insert  -----values:" + values+ " rowid:"+rowid);
		if (rowid > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, rowid);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		throw new IllegalArgumentException("Insert file failed: " + uri);
	}

	@Override
	public boolean onCreate() {
		Log.d(MainActivity.TAG, "onCreate  -----");
		dbHelper = new DatabaseHelper(this.getContext());
		resolver = this.getContext().getContentResolver();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String limit = null;

		switch (matcher.match(uri)) {
		case Constants.ITEMS: {
			sqlBuilder.setTables(Constants.TABLE);
			sqlBuilder.setProjectionMap(picProjectionMap);
			break;
		}
		case Constants.ITEM: {
			String id = uri.getPathSegments().get(1);
			sqlBuilder.setTables(Constants.TABLE);
			sqlBuilder.setProjectionMap(picProjectionMap);
			sqlBuilder.appendWhere(Constants.ID + "=" + id);
			break;
		}
		default:
			throw new IllegalArgumentException("Error Uri: " + uri);
		}

		Cursor cursor = sqlBuilder.query(db, projection, selection,
				selectionArgs, null, null,
				TextUtils.isEmpty(sortOrder) ? Constants.DEFAULT_SORT_ORDER
						: sortOrder, limit);
		cursor.setNotificationUri(resolver, uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.d(MainActivity.TAG, "update  -----matcher:"+matcher.match(uri));
		Log.d(MainActivity.TAG, "update  -----uri:"+uri+" values:"+values);
		switch(matcher.match(uri))
		{
		case Constants.ITEM:
			String id = uri.getPathSegments().get(1);
			Log.d(MainActivity.TAG, "update  -----id:"+id);
			//dbHelper.getWritableDatabase().q
			int row = dbHelper.getWritableDatabase().update(
					Constants.TABLE,
					values,
					Constants.ID
							+ "="
							+ id
							+ (!TextUtils.isEmpty(selection) ? " and ("
									+ selection + ')' : ""), selectionArgs);
			Log.d(MainActivity.TAG, "update  -----row:"+row);
			return row;
		case Constants.ITEMS:
			return dbHelper.getWritableDatabase().update(Constants.TABLE, values, selection, selectionArgs);
		default:
				throw new IllegalArgumentException("Error Uri: " + uri);
		}
	}

}
