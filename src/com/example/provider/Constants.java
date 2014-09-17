package com.example.provider;

import android.net.Uri;

public class Constants {
	public final static String DBNAME = "files.db";
	public final static String TABLE = "file";
	public final static String ID = "_id";
	public final static String URI = "uri";
	public static final String DEFAULT_SORT_ORDER = "_id asc";
	public final static String AUTHORITY = "com.pfg.testprovider";
	public final static int VERSION = 3;
	public final static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pfg.file"; //多个数据
	public final static String CONTENT_ITEMS_TYPE = "vnd.android.cursor.dir/vnd.pfg.file"; //多个数据
	
	public final static int ITEM = 1;
	public final static int ITEMS = 2;
	
	public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/file");
}
