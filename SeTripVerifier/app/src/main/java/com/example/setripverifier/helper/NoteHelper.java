package com.example.setripverifier.helper;

import android.content.Context;
import android.util.Log;

import com.example.setripverifier.model.Note;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class NoteHelper {
    private static String TAG = NoteHelper.class.getSimpleName();

   public static void writeToFile(Note note, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(note.getFilename(), Context.MODE_PRIVATE));
            outputStreamWriter.write(note.getData());
            outputStreamWriter.close();
        }catch (IOException e) {
            Log.e(TAG, "File Write Failed : " + e);
        }
    }

   public static Note readFromFile (Context context, String filename) {
        Note note = new Note();

        try {
            InputStream inputStream = context.openFileInput(filename);

            if(inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                note.setData(stringBuilder.toString());
                note.setFilename(filename);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found : ", e);
        } catch (IOException e) {
            Log.e(TAG, "File Read failed : ", e);
        }
        return note;
    }


}
