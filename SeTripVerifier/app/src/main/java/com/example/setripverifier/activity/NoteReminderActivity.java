package com.example.setripverifier.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.setripverifier.R;
import com.example.setripverifier.helper.NoteHelper;
import com.example.setripverifier.model.Note;

import java.util.ArrayList;
import java.util.Collections;

public class NoteReminderActivity extends AppCompatActivity {

    private EditText etSubject;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_reminder);

        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);

        setTitle(R.string.pengingatmu);

    }

    public void newNote(View view) {
        etSubject.setText("");
        etMessage.setText("");
        Toast.makeText(this, R.string.clearing_file, Toast.LENGTH_SHORT).show();
    }

    public void openNote(View view) {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, getFilesDir().list());
        final CharSequence[] items = arrayList.toArray(new CharSequence[arrayList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pilih);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadData(items[which].toString());
            }
        });
        builder.create().show();
    }

    private void loadData(String title) {
        Note note = NoteHelper.readFromFile(this, title);
        etSubject.setText(note.getFilename());
        etMessage.setText(note.getData());
        Toast.makeText(this, "Loading " + note.getFilename() + " data", Toast.LENGTH_SHORT).show();
    }

    public void saveNote(View view) {
        if(etSubject.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.title_empty, Toast.LENGTH_SHORT).show();
        } else if (etMessage.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.message_empty, Toast.LENGTH_SHORT).show();
        } else {
            String title = etSubject.getText().toString();
            String message = etMessage.getText().toString();
            Note note = new Note();
            note.setFilename(title);
            note.setData(message);
            NoteHelper.writeToFile(note, this);
            Toast.makeText(this, "Saving " + note.getFilename() + " file", Toast.LENGTH_SHORT).show();
        }
    }
}