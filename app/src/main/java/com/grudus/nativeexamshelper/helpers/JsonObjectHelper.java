package com.grudus.nativeexamshelper.helpers;


import android.content.Context;

import com.grudus.nativeexamshelper.pojos.JsonSubject;
import com.grudus.nativeexamshelper.pojos.Subject;
import com.grudus.nativeexamshelper.pojos.UserPreferences;

public class JsonObjectHelper {

    private final Context context;

    public JsonObjectHelper(Context context) {
        this.context = context;
    }

    public JsonSubject subjectObjectToJsonSubject(Subject subject, Long id, String change) {
        return new JsonSubject(
                id,
                new UserPreferences(context).getLoggedUser().getId(),
                subject.getTitle(),
                subject.getColor(),
                change);
    }
}
