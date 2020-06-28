package com.example.mynotebook;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String ID_EXTRA = "com.example.mynotebook._ID";
    private SQLiteDatabase db;
    private Cursor cursor;
    ViewCursorAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.log_title);
        setSupportActionBar(toolbar);
        //List<String> cattList = new ArrayList<String>();
        DatabaseHelper mydatebase=new DatabaseHelper(getApplicationContext());
        mydatebase.getReadableDatabase();



        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        ListView listView = (ListView) findViewById(R.id.Listview_records);



        try {
            db = databaseHelper.getReadableDatabase();
            cursor = db.query("RECORD", new String[]{"_id", "CATEGORY", "CONTENT", "DATE"},
                    null, null, null, null, null);

            listAdapter = new ViewCursorAdapter(this, cursor, 0);
            listView.setAdapter(listAdapter);
        } catch(SQLiteException e) {
            Toast.makeText(this,R.string.database_unavailable, Toast.LENGTH_SHORT).show();
        }
        listView.setOnItemClickListener(onListClick);
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            intent.putExtra(ID_EXTRA, String.valueOf(id));
            startActivity(intent);
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override/*menude secilen elemana gore gerekli islemlerin yapıldıgı yer*/
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        switch ( item.getItemId() ) {

            case R.id.new_item:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                return true;

            case R.id.send_data:
                Intent intent2=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent2);

              /*  try {
                    SQLiteDatabase db = databaseHelper.getReadableDatabase();
                    String data = DatabaseHelper.getDatabaseContentsAsString(db);

                    Helper.createFileWithContent(MainActivity.this, "data", "data.csv", data);
                    Helper.sendEmailWithFile(MainActivity.this, "data", "data.csv");
                } catch (SQLException e) {
                    Toast.makeText(this, R.string.unable_send_data, Toast.LENGTH_LONG).show();
                }*/
                return true;

            case R.id.delete_all:
                try {
                    showDeleteAllDataDialog(databaseHelper);
                } catch (SQLException e) {
                    Toast.makeText(this, R.string.unable_delete_data, Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDeleteAllDataDialog(final SQLiteOpenHelper databaseHelper) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.warning_message);
        alertDialog.setMessage(R.string.delete_all_confirm);
        alertDialog.setCancelable(true);

        alertDialog.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllData(databaseHelper);
            }
        });
        alertDialog.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void deleteAllData(SQLiteOpenHelper databaseHelper) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        DatabaseHelper.deleteAllRecords(db);
        Toast.makeText(this, R.string.all_data_deleted, Toast.LENGTH_SHORT).show();
        Intent updateIntent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(updateIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null) cursor.close();
        if(db != null) db.close();
    }

    }

