package com.example.mynotebook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SearchActivity extends AppCompatActivity {
    TextView tv_result;
    EditText et_input;
    TextView kategori;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tv_result= (TextView) findViewById(R.id.tv_result);
        et_input= (EditText) findViewById(R.id.et_input);
        kategori=findViewById(R.id.kategori);
        listView=findViewById(R.id.arama_sonuc);

    }
    public void searchOnClick(View view) {
        String input= et_input.getText().toString();
        if (input.isEmpty())
        {
            tv_result.setText("Geçerli bir kelime giriniz");
            return;
        }
        try {
            DatabaseHelper myDatabase= new DatabaseHelper(this);
            String result=myDatabase.Query(input);
            tv_result.setText(result);
        }
        catch (Exception ex )
        {
            tv_result.setText(ex.toString());
            Log.v("test",ex.getMessage());
        }

    }





}
