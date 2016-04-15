package com.example.sparked.sampleapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.example.sparked.sampleapp.utils.SparkedTracker;
import com.snowplowanalytics.snowplow.tracker.*;
import com.snowplowanalytics.snowplow.tracker.events.PageView;
import com.snowplowanalytics.snowplow.tracker.events.Unstructured;
import com.snowplowanalytics.snowplow.tracker.payload.SelfDescribingJson;

public class MyActivity extends AppCompatActivity {

    // declare the sparked tracker.
    private Tracker tracker;
    // declare the context object to send to the tracker too.
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        // this function sets up a tracker and gathers user info
        setupTracker();

        final ImageView left = (ImageView) findViewById(R.id.imageView1);
        final ImageView right = (ImageView) findViewById(R.id.imageView2);
        final Button clickMe = (Button) findViewById(R.id.button);

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);
        left.setAnimation(fadeOut);
        right.setAnimation(fadeOut);

        // track page view here!
        trackPageView("test.foo.com");

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                clickMe.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int i = r.nextInt(2);
                clickMe.setVisibility(View.GONE);
                if (i == 0){
                    left.setVisibility(View.VISIBLE);
                    left.startAnimation(fadeOut);
                } else {
                    right.setVisibility(View.VISIBLE);
                    right.startAnimation(fadeOut);
                }

                // track unstructured event here!
                String eventType = "Chose direction";
                String direction = (i == 0) ? "left" : "right";
                Map attributes = new HashMap();
                attributes.put("direction", direction);
                trackUnstructuredEvent(eventType, attributes);
            }
        });
    }


    /**
     * Setup Sparked Tracker
     * Also update user info to attach to tracker data
     *
     */
    private void setupTracker() {
        /* collector url goes here */
        String url = "snowplow.dev:7089";
        /* Insert appId here */
        String appId = "mPRDaNDbLahPPR/+svo7PwySjP0y";
        /* set context */
        context = getApplicationContext();
        /* create Sparked Tracker instance here */
        tracker = SparkedTracker.createTracker(appId, url, context);

        /* Insert your logged in account id here (in quotes) */
        String accountId = "nobody";
        /* Set the start date of the account in YYYY-MM-DD format */
        String accountStartDate = "2016-01-01";
        /* Account name */
        String accountName = "John Q. Nobody";
        /* Account email */
        String accountEmail = "nobody@devnull";
        /* Insert static account attributes here. */
        Map accountAttributes = new HashMap();
        accountAttributes.put("testKey","testValue");
        /* If your business has multiple users per account, set the user id here */
        String userId = "";
        /* User name */
        String userName = "";
        /* User email */
        String userEmail = "";
        /* Insert static user attributes here. */
        Map userAttributes = new HashMap();
        /* example adding attribute:
         * userAttributes.put("age", "30");
         */
        /* call the utility function that updates the user with the data above */
        SparkedTracker.updateUser(tracker, accountId, accountName, accountEmail,
                accountStartDate, accountAttributes, userId, userName,
                userEmail, userAttributes, context);
    }

    /*
     * track page view function
     * @param url - url or screen name
     */
    public void trackPageView(String url) {
        tracker.track(PageView.builder().pageUrl(url).build());
    }

    /*
     * track page view function
     * @param eventType string containing event type
     * @param attributes dictionary containing more event attributes
     */
    public void trackUnstructuredEvent(String eventType, Map attributes) {
        Map data = new HashMap();
        data.put("evt", eventType);
        data.put("attributes", attributes);
        SelfDescribingJson sdj = new SelfDescribingJson(".", data);
        tracker.track(Unstructured.builder().eventData(sdj).build());
    }
}