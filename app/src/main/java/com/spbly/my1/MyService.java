package com.spbly.my1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Calendar;


//Add graph. takipçi sayımın değişimini takip ederek graph oluşuturucak. gösterilen bilgiler kullanıcılar için daha kullanışlı hale gelicek.
// daha düzgün bi şekilde butonları koy.


/**
 * Created by Spbly on 10.05.2017.
 */

public class MyService extends Service {

        Timer myTimer;
    String TAG="Insta--***--";
    int hour;
    int min;

    private  DatabaseReference mDatabase;


    public static final String TAG_FOLLOWED_BY = "followed_by";
    public static final String TAG_USER="username";
    HashMap<String, String> userInfoHashmap = new HashMap<String, String>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   /* @IgnoreExtraProperties
    public class Time
    {



        public String hour;
        public String min;

        public  Time(){}
        public Time(String hour,String min)
        {
            this.hour=hour;
            this.min=min;
        }
    }

    private void setTime(String hour,String min)
    {
        Time time = new Time(hour,min);

        mDatabase.child("hour").child(hour).setValue(min);
    }*/

    @Override
    public void onCreate() {
        Toast.makeText(this,"You started to get notifications", Toast.LENGTH_LONG).show();

      //  FirebaseDatabase database = FirebaseDatabase.getInstance();
        // DatabaseReference myRef = database.getReference("message");
       // myRef.setValue("ssss");


        mDatabase = FirebaseDatabase.getInstance().getReference();

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                String fo = userInfoHashmap.get(TAG_FOLLOWED_BY);
                try {
                    lilParser();
                }
                    catch (IOException e) {e.printStackTrace();}
                    catch (JSONException e) {e.printStackTrace();}

          if(new String(userInfoHashmap.get(TAG_FOLLOWED_BY)).equals(fo)==true){}
          else {
              getnotification();

/*
              Calendar c = Calendar.getInstance();
              hour = c.get(Calendar.HOUR_OF_DAY);
              min=c.get(Calendar.MINUTE);

              String x= Integer.toString(hour);
              String y= Integer.toString(min);

              setTime(x,y);
*/
              //mDatabase.child("secondsss").setValue(seconds);

              //myRef.setValue(seconds);
              //Log.i(TAG, String.valueOf(seconds));

          }
            }
        }, 0, 15000);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"You stopped to get Notifications", Toast.LENGTH_LONG).show();

        myTimer.cancel();
        super.onDestroy();
    }

    private void callInstagram() {
        String apppackage = "com.instagram.android";
        Context cx=this;
        try {
            Intent i = cx.getPackageManager().getLaunchIntentForPackage(apppackage);
            cx.startActivity(i);
        } catch (Exception  e) {
            Toast.makeText(this, "Sorry, Instagram Apps Not Found", Toast.LENGTH_LONG).show();
        }
    }

    public void getnotification() {
        //Uri uri = Uri.parse("www.instagram.com/"+userInfoHashmap.get(TAG_USER));
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //String url = "www.instagram.com/";
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("www.instagram.com/"));
        intent.setPackage("com.instagram.android");
        //intent.setData(Uri.parse(url));
       // webIntent.setPackage("com.yapan.dietdiary");

       // Intent intent=getPackageManager().getLaunchIntentForPackage("com.google.android");
        //intent.setPackage("com.yapan.dietdiary");


            NotificationManager notificationmgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
          //  Intent intent = new Intent(this, InstagramApp.class);

            int requestID = (int) System.currentTimeMillis();
            int flags = PendingIntent.FLAG_CANCEL_CURRENT;
            PendingIntent pintent = PendingIntent.getActivity(this, requestID, intent, flags);
            Notification notif = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.common_full_open_on_phone)
                    .setContentTitle("Detector App")
                    .setContentText("Yeni takipçi sayınız= "+userInfoHashmap.get(TAG_FOLLOWED_BY))
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pintent)
                    .build();
            notificationmgr.notify(0,notif);
    }

       public void lilParser()  throws IOException, JSONException{

           URL url = new URL("https://api.instagram.com/v1/users/1522558667/?access_token="
                            +"1522558667.bf5d583.b4b60c37d7cb407199e5a2d251e33863");
           HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
           urlConnection.setRequestMethod("GET");
           urlConnection.setDoInput(true);
           urlConnection.connect();
           String response = Utils.streamToString(urlConnection
                   .getInputStream());
           //System.out.println(response);


           JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
           JSONObject data_obj = jsonObj.getJSONObject("data");
           JSONObject counts_obj = data_obj.getJSONObject("counts");


           //String name = jsonObj.getJSONObject("data").getString("full_name");
           userInfoHashmap.put(TAG_FOLLOWED_BY,counts_obj.getString(TAG_FOLLOWED_BY));
           userInfoHashmap.put(TAG_USER,data_obj.getString(TAG_USER));

           Log.i(TAG,"followedby=>[" + userInfoHashmap.get(TAG_FOLLOWED_BY) + "]");

       }
    }
