package com.sparked.utils;

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
 * Utility methods for integrating a snowplow tracker with Sparked analytics.
 * Created by rabihsaliba on 4/12/16.
 */
public class SparkedTracker {
    public static final String namespace = "SparkedTracker";

    /**
     * Returns a new snowplow Tracker, configured to connect to Sparked.
     *
     * @param appId the app ID assigned during sparked onboarding process
     * @param url collector url
     * @param context the application context
     * @return a new Classic Tracker - JAD: what is classic? can scrap it?
     */
    public static Tracker createTracker(String appId, String url, Context context) {
        Emitter emitter = SparkedTracker.getEmitter(url, context);
        return SparkedTracker.getTracker(appId, emitter, context);
    }

    /**
     * Updates the tracker with account information. All optional parameters may be null.
     *
     * @param context app context
     * @param tracker the tracker object
     * @param accountId required, the id of the logged-in account
     * @param accountName optional name of the account
     * @param accountEmail optional email address of the account
     * @param accountStartDate optional account sign up date
     * @param accountAttributes optional object containing more account data
     * @param userId only if account has more that 1 user
     * @param userName optional name of the user; only if account has more than 1 user
     * @param userEmail optional email of the user; only if account has more than 1 user
     * @param userAttributes optional object containing more user data; only if account has more
     *      than 1 user
     */
    public static void updateUser(
            Context context,
            Tracker tracker,
            String accountId,
            String accountName,
            String accountEmail,
            String accountStartDate,
            Map<String, Object> accountAttributes,
            String userId,
            String userName,
            String userEmail,
            Map<String, Object> userAttributes
    ) {

        // form userId here
        Map<String, Object> additionalData = new HashMap<String, Object>();
        additionalData.put("an", accountName);
        additionalData.put("ae", accountEmail);
        additionalData.put("as", accountStartDate);
        additionalData.put("ac", accountAttributes);
        additionalData.put("un", userName);
        additionalData.put("ue", userEmail);
        additionalData.put("uc", userAttributes);

        ArrayList<Object> al = new ArrayList<Object>();
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
                .security(RequestSecurity.HTTPS)
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
