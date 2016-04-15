package com.example.sparked.sampleapp.utils;

import android.content.Context;

import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.emitter.RequestSecurity;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.google.gson.Gson;

/**
 * Created by rabihsaliba on 4/12/16.
 */
public class SparkedTracker {
    public static final String namespace = "SparkedTracker";

    /**
     * Returns a Classic Tracker
     *
     * @param appId the app ID assigned during sparked onboarding process
     * @param url collector url
     * @param context the application context
     * @return a new Classic Tracker
     */
    public static Tracker createTracker(String appId, String url, Context context) {
        Emitter emitter = SparkedTracker.getEmitter(url, context);
        return SparkedTracker.getTracker(appId, emitter, context);
    }

    /**
     * Creates account info
     *
     * @param tracker the tracker object
     * @param accountId
     * @param accountName
     * @param accountEmail
     * @param accountStartDate account sign up date
     * @param accountAttributes object containing more account data
     * @param userId only if account has more that 1 user
     * @param userName
     * @param userEmail
     * @param userAttributes object containing more account data
     * @param context app context
     */
    public static void updateUser(Tracker tracker, String accountId,
                                  String accountName, String accountEmail,
                                  String accountStartDate, Map accountAttributes,
                                  String userId, String userName, String userEmail,
                                  Map userAttributes, Context context) {

        // form userId here
        Map additionalData = new HashMap();
        additionalData.put("an", accountName);
        additionalData.put("ae", accountEmail);
        additionalData.put("as", accountStartDate);
        additionalData.put("ac", accountAttributes);
        additionalData.put("un", userName);
        additionalData.put("ue", userEmail);
        additionalData.put("uc", userAttributes);

        ArrayList al = new ArrayList();
        al.add(accountId);
        al.add(userId);
        al.add(additionalData);

        String dataAsJson = new Gson().toJson(al);

        Subject s = SparkedTracker.getSubject(dataAsJson, context);
        tracker.setSubject(s);
    }

    /**
     * Returns a Classic Tracker
     *
     * @param emitter a Classic emitter
     * @param appId the app ID assigned during sparked onboarding process
     * @param context app context
     * @return a new Classic Tracker
     */
    private static Tracker getTracker(String appId, Emitter emitter, Context context) {
        return new Tracker.TrackerBuilder(emitter, namespace, appId, context,
                com.snowplowanalytics.snowplow.tracker.classic.Tracker.class)
                .base64(false)
                .sessionContext(true)
                .build();
    }

    /**
     * Returns a Classic Emitter
     *
     * @param url collector url
     * @param context the application context
     * @return a new Classic Emitter
     */
    private static Emitter getEmitter(String url, Context context) {
        return new Emitter.EmitterBuilder(url, context,
                com.snowplowanalytics.snowplow.tracker.classic.Emitter.class)
                .tick(1)
                .method(HttpMethod.GET)
                .security(RequestSecurity.HTTP)
                .sendLimit(500)
                .build();
    }

    /**
     * Returns a Subject Object
     *
     * @param userId contains a json string with user info
     * @param context the application context
     * @return a new subject
     */
    private static Subject getSubject(String userId, Context context) {
        Subject s = new Subject
                .SubjectBuilder()
                .context(context)
                .build();
        s.setUserId(userId);

        return s;
    }
}
