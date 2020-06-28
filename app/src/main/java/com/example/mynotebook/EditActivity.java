package com.example.mynotebook;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Calendar;
public class EditActivity extends AppCompatActivity {
    Spinner spinner;
    private Long id = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        spinner=findViewById(R.id.spinner_show);


        // Create action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_item);
        toolbar.setTitle(R.string.edit_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        // Null check required
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        // ID of the data passed to the activity from ListView click
        id = Long.parseLong(getIntent().getStringExtra(MainActivity.ID_EXTRA));

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        String[] data;
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            data = DatabaseHelper.getDatabaseSingleEntry(db, id);
        } catch (SQLException e) {
            Toast.makeText(this, R.string.data_get_error, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                              public void onItemSelected(AdapterView<?> spin, View v, int i, long id) {
                                                  TextView result = (TextView) findViewById(R.id.field_category);
                                                  result.setText(" " + spin.getSelectedItem());
                                              }

                                              public void onNothingSelected(AdapterView<?> parent) {
                                                  /*empty*/
                                              }
                                          });

        // Set all field values
       /* ((EditText) findViewById(R.id.field_category)).setText(data[0]);*/
        ((EditText) findViewById(R.id.field_content)).setText(data[1]);
        ((EditText) findViewById(R.id.field_date)).setText(data[2]);
    }


    public void onClick_imageButton_setDate(View view) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                EditText editText_date = (EditText)findViewById(R.id.field_date);
                editText_date.setText(day + "/" + (month + 1) + "/" + year);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        switch ( item.getItemId() ) {

            case R.id.save_edit:
                String category = ((EditText) findViewById(R.id.field_category) ).getText().toString();
                String content = ((EditText) findViewById(R.id.field_content) ).getText().toString();
                String date = ((EditText) findViewById(R.id.field_date) ).getText().toString();

                // Check if all fields contain data
                if ( category.equals("") || content.equals("") ) {
                    Toast.makeText(this, R.string.request_fill_fields, Toast.LENGTH_LONG);
                    return false;
                }

                try {
                    SQLiteDatabase db = databaseHelper.getReadableDatabase();
                    DatabaseHelper.updateRecord(db, id, category, content, date);
                    Toast.makeText(this, R.string.item_save_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (SQLException e) {
                    Toast.makeText(this, R.string.item_save_failed, Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.delete_item:
                try {
                    showDeleteAllDataDialog(databaseHelper, id);
                } catch (SQLException e) {
                    Toast.makeText(this, R.string.item_delete_failed, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteAllDataDialog(final SQLiteOpenHelper databaseHelper, final long id) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.warning_message);
        String message = getResources().getString(R.string.delete_single_confirm);
        alertDialog.setMessage(message + " " +  Long.toString(id) + "?");
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEntry(databaseHelper, id);
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

    private void deleteEntry(SQLiteOpenHelper databaseHelper, Long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        DatabaseHelper.deleteRecord(db, id);
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, R.string.entry_deleted, Toast.LENGTH_SHORT).show();
    }
}
