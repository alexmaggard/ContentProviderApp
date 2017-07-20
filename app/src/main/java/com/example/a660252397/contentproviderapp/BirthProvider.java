package com.example.a660252397.contentproviderapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class BirthProvider extends ContentProvider {

    /***************************SQL CODE FOR DATABASE******************************************/

    //fields for my content provider
    static final String PROVIDER_NAME = "com.example.a660252397.contentproviderapp.BirthdayProv";
    static final String URL = "content://" + PROVIDER_NAME + "/friends";
    //this will identify this particular content provider
    static final Uri CONTENT_URI = Uri.parse(URL);

    //define database variables
    DBHelper dbHelper;
    private SQLiteDatabase database;
    static final String ID = "id";  //primary key
    static final String NAME = "name";
    static final String BIRTHDAY = "birthday";

    //integer values used in the content URI
    static final int FRIENDS = 1;
    static final int FRIENDS_ID = 2;

    //project map for a query
    private static HashMap<String, String> birthMap;


    //maps content URI pattersn to the integer values that were set above
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "friends", FRIENDS);
        uriMatcher.addURI(PROVIDER_NAME, "friends/#", FRIENDS_ID);
        //uri matcher is a helper class
    }

    //datbase declarations (dbHelper class)
    static final String DATABASENAME = "Birthday.db";
    static final String TABLE_NAME = "birthTable";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " birthday TEXT NOT NULL);";

    //class to create and manage the providers database
    private static class DBHelper extends SQLiteOpenHelper {

        private DBHelper(Context context) {
            super(context, DATABASENAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.v(DBHelper.class.getName(),
                    "Upgrading databse from version " + oldVersion +
                    " to " + newVersion + ". Old data will be destroyed.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    /*********************************************end sql code***********************************/
    public BirthProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {//TODO: FIX NULL POINTER EXCEPTIONS
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {//TODO: FIX NULL POINTER EXCEPTIONS

        switch(uriMatcher.match(uri)){
            case FRIENDS:
                return "vnd.android.cursor.dir/vnd.example.friends";
            case FRIENDS_ID:
                return "vnd.android.cursor.item/vnd.example.friends";
            default:
                throw new IllegalArgumentException("Unsupported URI: " +  uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {//TODO: FIX NULL POINTER EXCEPTIONS

        long row = database.insert(TABLE_NAME,"", values);
        if(row > 0){
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);//TODO: FIX NULL POINTER EXCEPTIONS
            return newUri;
        }
        throw new SQLException("Failed to add new record into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        //need to get permissions to be writable
        database = dbHelper.getWritableDatabase();
        if(database == null){
            return false;
        } else{
            return true;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,//TODO: FIX NULL POINTER EXCEPTIONS
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder(); //get instance of class
        queryBuilder.setTables(TABLE_NAME); //specify which table we are working with
        //use Uri matcher class to determine if this is single entry or all entries
        switch (uriMatcher.match(uri)){
            //map all database column names
            case FRIENDS:
                queryBuilder.setProjectionMap(birthMap);
            case FRIENDS_ID:
                queryBuilder.appendWhere( ID +  "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(sortOrder == null||sortOrder == ""){
            //no sorting, just sort on names by default
            sortOrder = NAME;
        }

        Cursor cursor = queryBuilder.query(database,projection,
                selection,selectionArgs,null,null, sortOrder);

        //register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);//TODO: FIX NULL POINTER EXCEPTIONS
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,//TODO: FIX NULL POINTER EXCEPTIONS
                      String[] selectionArgs) {

        int count = 0;

        switch (uriMatcher.match(uri)){
            case FRIENDS:
                count = database.update(TABLE_NAME,values,selection,selectionArgs);
                break;
            case FRIENDS_ID:
                count = database.update(TABLE_NAME,values,ID +
                        "=" + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection)? " AND (" +
                                selection + ')':""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI");
        }

        getContext().getContentResolver().notifyChange(uri, null);//TODO: FIX NULL POINTER EXCEPTIONS
        return count;
    }
}
