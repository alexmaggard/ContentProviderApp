package com.example.a660252397.contentproviderapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addBirthdayClick(View view) {
        //add a new birthday record
        ContentValues values = new ContentValues();
        values.put(BirthProvider.NAME,
                ((EditText) findViewById(R.id.firstNameET)).getText().toString());
        values.put(BirthProvider.BIRTHDAY,
                ((EditText) findViewById(R.id.birthdayET)).getText().toString());

        Uri uri = getContentResolver().insert(BirthProvider.CONTENT_URI,values);
        Toast.makeText(this, "Maggard: " + uri.toString() + " inserted ", Toast.LENGTH_SHORT).show();//TODO: FIX NULL POINTER EXCEPTIONS
    }

    public void showBirthdayClick(View view) {
        //show all the birthdays sorted by Friends name
        String URL = "content://com.example.a660252397.provider.BirthdayProv/friends";
        Uri friends = Uri.parse(URL);
        String result = "Mag Results: ";
        Cursor c = getContentResolver().query(friends, null, null, null, "name");
        if(!c.moveToFirst()){//TODO: FIX NULL POINTER EXCEPTIONS
            Toast.makeText(this, result + " no content yet.",Toast.LENGTH_LONG).show();
        } else {
            do {
                result = result + "\n" + c.getString(c.getColumnIndex(BirthProvider.NAME)) + " with id " +
                        c.getString(c.getColumnIndex(BirthProvider.ID)) + " has birthday: " +
                        c.getString(c.getColumnIndex(BirthProvider.BIRTHDAY));
            } while (c.moveToNext());

        }
    }

    public void deleteBirthdayClick(View view) {

        //delete all the records and the table of the database provider
        String URL = "content://com.example.a660252397.provider.BirthdayProv/friends";
        Uri friends = Uri.parse(URL);

        int count = getContentResolver().delete(friends, null, null);

        String countNum = "Rabor: " + count + " records deleted.";
        Toast.makeText(getBaseContext(), countNum, Toast.LENGTH_SHORT).show();

    }
}
