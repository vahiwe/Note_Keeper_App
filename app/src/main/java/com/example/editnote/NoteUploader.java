package com.example.editnote;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.editnote.NoteKeeperProviderContract.Notes;

public class NoteUploader {
    private static final String TAG = NoteUploader.class.getSimpleName();

    private final Context mContext;
    private boolean mCanceled;

    public NoteUploader (Context context) { mContext = context;}

    public boolean isCanceled () { return mCanceled;}

    public void cancel () {mCanceled = true;}

    public void doUpload(Uri uri) {

        String[] columns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT
        };


        Cursor cursor = mContext.getContentResolver().query(uri, columns, null, null, null);
        int noteTitlePos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TEXT);
        int courseIdPos = cursor.getColumnIndex(Notes.COLUMN_COURSE_ID);


        Log.i(TAG, ">>>***  UPLOAD START - " + uri + "     ***<<<");
        mCanceled = false;

        while (!mCanceled && cursor.moveToNext()) {
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);
            String courseId = cursor.getString(courseIdPos);

            if (!noteTitle.equals("")) {
                Log.i(TAG, ">>>Uploading Note<<< " + courseId + "|" + noteTitle + "|" + noteText );
                simulateLongRunningWork();
            }

        }

        Log.i(TAG, ">>>***    UPLOAD COMPLETE    ***<<< ");
        cursor.close();
    }

    private static void simulateLongRunningWork() {
        try {
            Thread.sleep(1000);
        } catch(Exception ex) {}
    }

}
