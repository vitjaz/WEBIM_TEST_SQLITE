package com.example.webim_sqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editText_title, editText_desc;

    private SQLiteDatabase mDataBase;

    private NoteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_title = findViewById(R.id.edit_text_title);
        editText_desc = findViewById(R.id.edit_text_desc);

        NoteDBHelper dbHelper = new NoteDBHelper(this);
        mDataBase = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NoteAdapter(this, getAllNotes());
        recyclerView.setAdapter(mAdapter);

        //обрабатываем свайпы по элементам recyclerview
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeNote((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        //окрываем диалог с описанием заметки
        mAdapter.setListener(new NoteAdapter.Listener() {
            @Override
            public void onClick(int position, long id) {
                Cursor cursor = mDataBase.query("NOTES",
                        new String[] {"TITLE", "DESCRIPTION"},
                        "_id = ?",
                        new String[] {Long.toString(id)},
                        null, null, null);
                if(cursor.moveToFirst()) {
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    String desc = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                    openDialog(title, desc);
                }
                    cursor.close();
            }
        });

    }

    public void onClickAdd(View view){
        addNote();
    }

    //добавляем записи
    private void addNote(){
        if(editText_title.getText().toString().trim().length() == 0 || editText_desc.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Title and desc must be fill", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = editText_title.getText().toString();
        String desc = editText_desc.getText().toString();

        ContentValues cv = new ContentValues();
        cv.put("TITLE", title);
        cv.put("DESCRIPTION", desc);
        mDataBase.insert("NOTES", null, cv);

        mAdapter.swapCursor(getAllNotes());

        editText_title.getText().clear();
        editText_desc.getText().clear();
    }

    //удаляем по свайпу
    private void removeNote(long id){
        mDataBase.delete("NOTES", "_id = " + id, null);
        mAdapter.swapCursor(getAllNotes());
    }

    //получаем все записи
    private Cursor getAllNotes(){
        return mDataBase.query("NOTES",
                null,
                null,
                null,
                null,
                null,
                "TIMESTAMP" + " DESC"); //сортировка по времени добавления заметки
    }

    //открываем диалог
    private void openDialog(String title, String desc){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(desc)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

    }
}