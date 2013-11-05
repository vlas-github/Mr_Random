package db_class;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mr_random_classes.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBConnector implements IDBConnector{
	
	private static final String DATABASE_NAME = "Mr.Random.db";
	private static final int DATABASE_VERSION = 2;
	
	private static final String TABLE_NAME_LIST 			= "LIST";
	private static final String TABLE_NAME_ELEMLIST 		= "ELEM_LIST";
		
	private static final String COLUMN_LIST_ID 				= "_id";
	private static final String COLUMN_LIST_NAME 			= "_name";
	private static final String COLUMN_LIST_DATE 			= "_date";
	
	private static final String COLUMN_ELEMLIST_ID 			= "_id";
	private static final String COLUMN_ELEMLIST_NAME 		= "_name";
	private static final String COLUMN_ELEMLIST_USED 		= "_used";
	private static final String COLUMN_ELEMLIST_PRIORITY	= "_priority";
	private static final String COLUMN_ELEMLIST_ID_LIST 	= "_idList";
	
	private static final int NUM_COLUMN_LIST_ID 			= 0;
	private static final int NUM_COLUMN_LIST_NAME 			= 1;
	private static final int NUM_COLUMN_LIST_DATE 			= 2;
	
	private static final int NUM_COLUMN_ELEMLIST_ID 		= 0;
	private static final int NUM_COLUMN_ELEMLIST_NAME 		= 1;
	private static final int NUM_COLUMN_ELEMLIST_USED 		= 2;
	private static final int NUM_COLUMN_ELEMLIST_PRIORITY	= 3;
	private static final int NUM_COLUMN_ELEMLIST_ID_LIST 	= 4;
	
	private SQLiteDatabase mrRandomDB;
	
	public DBConnector(Context context) 
	{
		OpenHelper openHelper = new OpenHelper(context);
		mrRandomDB = openHelper.getWritableDatabase();
	}
	
	@Override
	public List<IList> getLists() {
		
		List<IList> list = new ArrayList<IList>();
		
		Cursor cursor = mrRandomDB.query(TABLE_NAME_LIST, null, null, null, null, null, COLUMN_LIST_NAME);
		cursor.moveToFirst();
		
		if(!cursor.isAfterLast()){
			do{
				long id = cursor.getLong(NUM_COLUMN_LIST_ID);
				String name = cursor.getString(NUM_COLUMN_LIST_NAME);
				Date date = new Date(cursor.getInt(NUM_COLUMN_LIST_DATE));
				List<IElemList> elemList = getElemsList(id);
				
				list.add(new MyList(id, name, date, elemList));
			}while(cursor.moveToNext());
		}
		
		return list;
	}
	@Override
	public List<IElemList> getElemsList(long idList) {

		List<IElemList> elemList = new ArrayList<IElemList>();
		
		Cursor cursor = mrRandomDB.query(TABLE_NAME_ELEMLIST, 
				null, 
				COLUMN_ELEMLIST_ID_LIST + " = " + String.valueOf(idList), 
				null, null, null, 
				COLUMN_ELEMLIST_NAME);
		cursor.moveToFirst();
		
		if(!cursor.isAfterLast()){
			do{
				long id 		= cursor.getLong(NUM_COLUMN_ELEMLIST_ID);
				String name 	= cursor.getString(NUM_COLUMN_ELEMLIST_NAME);
				boolean uses 	= cursor.getInt(NUM_COLUMN_ELEMLIST_USED) == 1;
				float priority	= cursor.getFloat(NUM_COLUMN_ELEMLIST_PRIORITY);
				
				elemList.add(new ElemList(id, name, uses, priority, idList));
			}while(cursor.moveToNext());
		}
			
		return elemList;
	}
	@Override
	public IList getList(long id) {
		Cursor cursor = mrRandomDB.query(TABLE_NAME_LIST, 
				null, 
				COLUMN_LIST_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		
		if(!cursor.isAfterLast()){
			long _id = cursor.getLong(NUM_COLUMN_LIST_ID);
			String name = cursor.getString(NUM_COLUMN_LIST_NAME);
			Date date = new Date(cursor.getInt(NUM_COLUMN_LIST_DATE));
			List<IElemList> elemList = getElemsList(id);
			
			return new MyList(_id, name, date, elemList);
		}
		
		return null;		
	}
	@Override
	public IElemList getElemList(long idElemList) {
		Cursor cursor = mrRandomDB.query(TABLE_NAME_ELEMLIST, 
				null, 
				COLUMN_ELEMLIST_ID + " = " + idElemList, 
				null, null, null, null);
		cursor.moveToFirst();
		
		if(!cursor.isAfterLast()){
			long _id = cursor.getLong(NUM_COLUMN_ELEMLIST_ID);
			String name = cursor.getString(NUM_COLUMN_ELEMLIST_NAME);
			boolean uses = cursor.getInt(NUM_COLUMN_ELEMLIST_USED) == 1;
			long idList = cursor.getLong(NUM_COLUMN_ELEMLIST_ID_LIST);
			float priority	= cursor.getFloat(NUM_COLUMN_ELEMLIST_PRIORITY);
			
			return new ElemList(_id, name, uses, priority, idList);
		}
		
		return null;
	}
	
	public long setElemList(IElemList elemList) {
		
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_ELEMLIST_NAME, elemList.getName());
		cv.put(COLUMN_ELEMLIST_USED, elemList.getUsed());
		cv.put(COLUMN_ELEMLIST_PRIORITY, elemList.getPriority());
		cv.put(COLUMN_ELEMLIST_ID_LIST, elemList.getIDList());
		
		if(elemList.getID() != -1){
			
			mrRandomDB.update(TABLE_NAME_ELEMLIST, cv, COLUMN_ELEMLIST_ID + " = " + String.valueOf(elemList.getID()), null);
			
			return elemList.getID();
		} else {	
			
			return mrRandomDB.insert(TABLE_NAME_ELEMLIST, null, cv);
		}		
	}
	@Override
	public long setList(IList list) {
		
		ContentValues cv = new ContentValues();
		
		cv.clear();
		
		cv.put(COLUMN_LIST_NAME, list.getName());
		cv.put(COLUMN_LIST_DATE, (Long.getLong(list.getDate().toString())));
		
		if(list.getID() != -1){			
			
			mrRandomDB.update(TABLE_NAME_LIST, cv, COLUMN_LIST_ID + " = " + String.valueOf(list.getID()), null);
			
			for(IElemList elem: list.getElemsList()) {
				
				elem.setIDList(list.getID());
				
				long id = setElemList(elem);
				
				elem.setID(id);
			}
			
			return list.getID();
		} else {
			
			long index = mrRandomDB.insert(TABLE_NAME_LIST, null, cv);
			
			for(IElemList elem: list.getElemsList()) {
				
				elem.setIDList(index);
				
				long id = setElemList(elem);
				
				elem.setID(id);
			}
			
			return index;
		}		
	}
	
	@Override
	public int removeList(IList list) {
		
		return mrRandomDB.delete(TABLE_NAME_LIST, COLUMN_LIST_ID + " = ?", new String[] { String.valueOf(list.getID()) });
	}
	@Override
	public int removeElemList(IElemList elemList) {

		return mrRandomDB.delete(TABLE_NAME_ELEMLIST, COLUMN_ELEMLIST_ID + " = ?", new String[] { String.valueOf(elemList.getID()) });
	}
	
	private class OpenHelper extends SQLiteOpenHelper {	
		OpenHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}		
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			String query = "";
			
			query = "CREATE TABLE " + TABLE_NAME_LIST + " (" +
					COLUMN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COLUMN_LIST_NAME + " TEXT, " + 
					COLUMN_LIST_DATE + " DATE);";
			db.execSQL(query);
			
			query = "CREATE TABLE " + TABLE_NAME_ELEMLIST + " (" +
					COLUMN_ELEMLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COLUMN_ELEMLIST_NAME + " TEXT, " + 
					COLUMN_ELEMLIST_USED + " BINARY, " +
					COLUMN_ELEMLIST_PRIORITY + " FLOAT, " +
					COLUMN_ELEMLIST_ID_LIST + " INTEGER  REFERENCES " + TABLE_NAME_LIST + " (" + COLUMN_LIST_ID + ") NOT NULL);";
			db.execSQL(query);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.i("sdad", String.valueOf(oldVersion));
			Log.i("sasdas", String.valueOf(newVersion));
			
			if (oldVersion == 1 && newVersion == 2) {
				db.beginTransaction();
				
				try {					
					db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LIST);
					db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ELEMLIST);
					
					String query = "";
					
					query = "CREATE TABLE " + TABLE_NAME_LIST + " (" +
							COLUMN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							COLUMN_LIST_NAME + " TEXT, " + 
							COLUMN_LIST_DATE + " DATE);";
					db.execSQL(query);
					
					query = "CREATE TABLE " + TABLE_NAME_ELEMLIST + " (" +
							COLUMN_ELEMLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
							COLUMN_ELEMLIST_NAME + " TEXT, " + 
							COLUMN_ELEMLIST_USED + " BINARY, " + 
							COLUMN_ELEMLIST_PRIORITY + " FLOAT, " +
							COLUMN_ELEMLIST_ID_LIST + " INTEGER  REFERENCES " + TABLE_NAME_LIST + " (" + COLUMN_LIST_ID + ") NOT NULL);";
					
					db.execSQL(query);	
					db.setTransactionSuccessful();
				} finally {
			    	db.endTransaction();
				}
			}			      
		}
	}
}
