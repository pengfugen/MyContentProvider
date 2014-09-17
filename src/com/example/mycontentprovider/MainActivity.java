package com.example.mycontentprovider;

import com.example.provider.Constants;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private Button btn_set;
	private Button btn_update;
	private ImageView image;
	
	private Uri URI1 = Uri.parse("/storage/sdcard0/DCIM/Camera/IMG1.jpg");
	private Uri URI2 = Uri.parse("/storage/sdcard0/DCIM/Camera/IMG2.jpg");
	private Uri URI3 = Uri.parse("/storage/sdcard0/DCIM/Camera/IMG3.jpg");
	private Uri URI4 = Uri.parse("/storage/sdcard0/DCIM/Camera/IMG4.jpg");
	private int i = 0;
	
	public static String TAG = "MyConProvider";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_set = (Button)findViewById(R.id.btn_set);
        btn_update = (Button)findViewById(R.id.btn_update);
        image = (ImageView)findViewById(R.id.image);
        
        btn_set.setOnClickListener(new MyBtnOnClickListener());
        btn_update.setOnClickListener(new MyBtnOnClickListener());
    }
    
    class MyBtnOnClickListener implements View.OnClickListener
    {

		@Override
		public void onClick(View v) {
			switch(v.getId())
			{
			case R.id.btn_set:
				handleSetBtnOnClick();
				break;
			case R.id.btn_update:
				handleUpdateBtnOnClick();
				break;
			default:
				break;
			}
			
		}
    	
    }
    
    private void handleSetBtnOnClick()
    {
    	deleteFiles();
		//i++;
		if(i == 3)
		{
			i = 0;
		}
		ContentValues values = new ContentValues();
		if(i == 0)
		{
			values.put(Constants.URI, URI1.toString());
		}
		else if(i == 1)
		{
			values.put(Constants.URI, URI2.toString());
		}
		else
		{
			values.put(Constants.URI, URI3.toString());
		}
		i++;
		this.getContentResolver().insert(Constants.CONTENT_URI, values);
		queryFilesAndSetImage();
    }
    
    private void handleUpdateBtnOnClick()
    {
    	Log.d(TAG, "handleUpdateBtnOnClick ====== ");
    	Uri uri = ContentUris.withAppendedId(Constants.CONTENT_URI, 0);
    	ContentValues values = new ContentValues();
    	values.put(Constants.URI, URI4.toString());
    	int row = this.getContentResolver().update(uri, values, null, null);
    	queryFilesAndSetImage();
    }
    
    private void deleteFiles()
    {
    	this.getContentResolver().delete(Constants.CONTENT_URI, null, null);
    }
    
    private void queryFilesAndSetImage()
    {
    	Cursor cursor = this.getContentResolver().query(Constants.CONTENT_URI, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			if(!cursor.isAfterLast())
			{
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.ID));
				String uri = cursor.getString(cursor.getColumnIndexOrThrow(Constants.URI));
				Log.d(TAG, "_id = " + id + " uri = " + uri);
				Bitmap bit = BitmapFactory.decodeFile(uri); //自定义//路径 
				image.setImageBitmap(bit);
				cursor.moveToNext();
			}
		}
		cursor.close();
    }
}
