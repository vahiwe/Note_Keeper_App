package com.example.editnote;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.editnote.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.editnote.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_ID = "com.example.editnote.NOTE_ID";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.editnote.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.editnote.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.editnote.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0), "", "");
    private boolean misNewNote;
    private Spinner mspinnerCourses;
    private EditText mtextNoteTitle;
    private EditText mtextNoteText;
    private int mNoteId;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTextPos;
    private int mNoteTitlePos;
    private SimpleCursorAdapter mAdapterCourses;
    private boolean mCoursesQueryFinished;
    private boolean mNotesQueryFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        mspinnerCourses = findViewById(R.id.spinner_courses);

//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mAdapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[] {android.R.id.text1}, 0);
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinnerCourses.setAdapter(mAdapterCourses);

//        loadCourseData();
        
        getSupportLoaderManager().initLoader(LOADER_COURSES,null,this);
        
        readDisplayStateValues();
        if (savedInstanceState == null){
            saveOriginalNoteValues();
        } else {
            restoreOriginalNoteValues(savedInstanceState);
        }


        mtextNoteTitle = findViewById(R.id.text_note_title);
        mtextNoteText = findViewById(R.id.text_note_text);

        if(!misNewNote)
//            loadNoteData();
            getSupportLoaderManager().initLoader(LOADER_NOTES, null, this);

        Log.d(TAG, "onCreate");
    }

    private void loadCourseData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] courseColumns = {
                CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID
        };
        Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns, null,
                null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);
        mAdapterCourses.changeCursor(cursor);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void loadNoteData() {
        SQLiteDatabase  db = mDbOpenHelper.getReadableDatabase();

        String courseId = "android_intents";
        String titleStart = "dynamic";
//
//        String selection = NoteInfoEntry.COLUMN_COURSE_ID + " = ? AND "
//                + NoteInfoEntry.COLUMN_NOTE_TITLE + " LIKE ?";

        String selection = NoteInfoEntry._ID + " = ?";

//        String[] selectionArgs = {courseId, titleStart + "%"};

        String[] selectionArgs = {Integer.toString(mNoteId)};

        String[] noteColums = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT
        };

        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColums, selection, selectionArgs, null,null,null);

        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteCursor.moveToNext();
        displayNotes();
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    private void saveOriginalNoteValues() {
        if(misNewNote)
            return;

        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();
    }

    private void displayNotes() {
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);
        String courseId = mNoteCursor.getString(mCourseIdPos);

//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
//        CourseInfo course = DataManager.getInstance().getCourse(courseId);
//        int courseIndex = courses.indexOf(mNote.getCourse());
        int courseIndex = getIndexOfCourseId(courseId);
        mspinnerCourses.setSelection(courseIndex);
        mtextNoteTitle.setText(noteTitle);
        mtextNoteText.setText(noteText);
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = mAdapterCourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while (more) {
            String cursorCourseId = cursor.getString(courseIdPos);
            if (courseId.equals(cursorCourseId))
                break;

            courseRowIndex++;
            cursor.moveToNext();
        }
        return courseRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
//        mNote = intent.getParcelableExtra(NOTE_ID);
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
//        misNewNote = mNote == null;
        misNewNote = mNoteId == ID_NOT_SET;
        if (misNewNote){
            createNewNote();
        }

        Log.i(TAG, "mNoteId: " + mNoteId);

//        mNote = DataManager.getInstance().getNotes().get(mNoteId);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNoteId = dm.createNewNote();
//        mNote = dm.getNotes().get(mNoteId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() -1;
        item.setEnabled(mNoteId < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        ++mNoteId;

        mNote = DataManager.getInstance().getNotes().get(mNoteId);
        saveOriginalNoteValues();

        displayNotes();
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling){
            Log.i(TAG, "Cancelling note at position: " + mNoteId);
            if (misNewNote) {
                DataManager.getInstance().removeNote(mNoteId);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
        Log.d(TAG, "onPause");

    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    private void saveNote() {
//        mNote.setCourse((CourseInfo) mspinnerCourses.getSelectedItem());
        mNote.setTitle(mtextNoteTitle.getText().toString());
        mNote.setText(mtextNoteText.getText().toString());
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mspinnerCourses.getSelectedItem();
        String subject = mtextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course \"" +
                course.getTitle() +"\"\n" + mtextNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(intent.EXTRA_SUBJECT, subject);
        intent.putExtra(intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        CursorLoader loader = null;
        if(i == LOADER_NOTES)
            loader = createLoaderNotes();
        else if (i == LOADER_COURSES)
            loader = createLoaderCourses();
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        mCoursesQueryFinished = false;
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] courseColumns = {
                        CourseInfoEntry.COLUMN_COURSE_TITLE,
                        CourseInfoEntry.COLUMN_COURSE_ID,
                        CourseInfoEntry._ID
                };
                return db.query(CourseInfoEntry.TABLE_NAME, courseColumns, null,
                        null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);
            }
        };
    }

    private CursorLoader createLoaderNotes() {
        mNotesQueryFinished = false;
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase  db = mDbOpenHelper.getReadableDatabase();

                String courseId = "android_intents";
                String titleStart = "dynamic";

                String selection = NoteInfoEntry._ID + " = ?";
                String[] selectionArgs = {Integer.toString(mNoteId)};

                String[] noteColums = {
                        NoteInfoEntry.COLUMN_COURSE_ID,
                        NoteInfoEntry.COLUMN_NOTE_TITLE,
                        NoteInfoEntry.COLUMN_NOTE_TEXT
                };

                return db.query(NoteInfoEntry.TABLE_NAME, noteColums, selection, selectionArgs, null,null,null);

            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_NOTES){
            loadFinishedNotes(cursor);
        } else if (loader.getId() == LOADER_COURSES){
            mAdapterCourses.changeCursor(cursor);
            mCoursesQueryFinished = true;
            displayNotesWhenQueriesFinished();
        }
    }

    private void loadFinishedNotes(Cursor cursor) {
        mNoteCursor = cursor;

        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteCursor.moveToNext();
        mNotesQueryFinished = true;
        displayNotesWhenQueriesFinished();
    }

    private void displayNotesWhenQueriesFinished() {
        if (mNotesQueryFinished && mCoursesQueryFinished)
            displayNotes();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES) {
            if (mNoteCursor != null)
                mNoteCursor.close();
        } else if (loader.getId() == LOADER_COURSES)
            mAdapterCourses.changeCursor(null);

    }
}
