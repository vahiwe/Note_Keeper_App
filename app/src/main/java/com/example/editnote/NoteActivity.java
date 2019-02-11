package com.example.editnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.example.editnote.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNotes;
    private boolean misNewNote;
    private Spinner mspinnerCourses;
    private EditText mtextNoteTitle;
    private EditText mtextNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mspinnerCourses = findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();

        mtextNoteTitle = findViewById(R.id.text_note_title);
        mtextNoteText = findViewById(R.id.text_note_text);

        if(!misNewNote)
            displayNotes(mspinnerCourses, mtextNoteTitle, mtextNoteText);
    }

    private void displayNotes(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNotes.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNotes.getTitle());
        textNoteText.setText(mNotes.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
//        mNotes = intent.getParcelableExtra(NOTE_POSITION);
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
//        misNewNote = mNotes == null;
        misNewNote = position == POSITION_NOT_SET;
        if (!misNewNote)
            mNotes = DataManager.getInstance().getNotes().get(position);
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
        }

        return super.onOptionsItemSelected(item);
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
}
