package com.spbly.my1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.spbly.my1.MyService.TAG_FOLLOWED_BY;

public class GraphClass extends MainActivity {

    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    Timer myTimer;
    private Button btnGoBack;
    int hour,min,day,sec;
    String TAG="Graph output";
    private int lastX = 0;
    private static final Random RANDOM = new Random();


    private LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_class);

        //hour = c.get(Calendar.HOUR_OF_DAY);

        //day=c.get(Calendar.DAY_OF_YEAR);


        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);

        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(270);
        viewport.setMaxY(280);
        viewport.setScrollable(true);

        btnGoBack=(Button) findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 1000; i++) {
                    Calendar c = Calendar.getInstance();
                    sec=c.get(Calendar.SECOND);
                    try {
                        lilParser();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        if(v==btnGoBack)
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }

    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(sec, Double.parseDouble(userInfoHashmap.get(TAG_FOLLOWED_BY))), true, 10);    }


    public void lilParser()  throws IOException, JSONException {

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

        userInfoHashmap.put(TAG_FOLLOWED_BY,counts_obj.getString(TAG_FOLLOWED_BY));
        Log.i(TAG,"followedby=>[" + userInfoHashmap.get(TAG_FOLLOWED_BY) + "]");

    }


}
