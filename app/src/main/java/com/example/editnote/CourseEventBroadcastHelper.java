package com.example.editnote;

import android.content.Context;
import android.content.Intent;

public class CourseEventBroadcastHelper {
    public static final String ACTION_COURSE_EVENT = "com.example.editnote.action.COURSE_EVENT";

    public static final String EXTRA_COURSE_ID = "com.example.editnote.extra.COURSE_ID";
    public static final String EXTRA_COURSE_MESSAGE = "com.example.editnote.extra.COURSE_MESSAGE";

    public static void sendEventBroadcast(Context context, String courseId, String message) {
        Intent intent = new Intent(ACTION_COURSE_EVENT);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_COURSE_MESSAGE, message);

        context.sendBroadcast(intent);
    }


}
