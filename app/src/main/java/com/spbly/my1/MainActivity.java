package com.spbly.my1;

import com.google.firebase.analytics.FirebaseAnalytics;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import java.util.HashMap;
import com.spbly.my1.InstagramApp.OAuthAuthenticationListener;

import org.w3c.dom.Text;

import static com.spbly.my1.R.layout.activity_main;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private InstagramApp mApp;
    private ImageView img1;
    private TextView txt1;
    private Button btnConnect;
    private Button btnMe, btnOS, btnCS, btnGraph;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    ViewGroup myLayout;
    private FirebaseAnalytics mFirebaseAnalytics;
    String log = "log";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        myLayout = (ViewGroup) findViewById(R.id.myLayout);

        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);

        mApp.setListener(new OAuthAuthenticationListener() {
                             @Override
                             public void onSuccess() {
                                 mApp.fetchUserName(handler);
                             }

                             @Override
                             public void onFail(String error) {
                                 Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT)
                                         .show();
                             }
                         }
        );

        setWidgetReference();
        bindEventHandlers();

        if (mApp.hasAccessToken()) {
            btnConnect.setText("Disconnect");
            btnMe.setVisibility(View.VISIBLE);
            btnCS.setVisibility(View.VISIBLE);
            btnOS.setVisibility(View.VISIBLE);
            btnGraph.setVisibility(View.VISIBLE);
            txt1.setVisibility(View.INVISIBLE);
            img1.setVisibility(View.INVISIBLE);
            mApp.fetchUserName(handler);

        }
    }

    private void setWidgetReference() {

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnMe = (Button) findViewById(R.id.btnMy);
        btnOS = (Button) findViewById(R.id.btnOS);
        btnCS = (Button) findViewById(R.id.btnCS);
        btnGraph = (Button) findViewById(R.id.btnGraph);
        txt1 = (TextView) findViewById(R.id.txt1);
        img1= (ImageView) findViewById(R.id.img1);

    }

    private void bindEventHandlers() {
        btnConnect.setOnClickListener(this);
        btnMe.setOnClickListener(this);
        btnOS.setOnClickListener(this);
        btnCS.setOnClickListener(this);
        btnGraph.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btnConnect) {
            connectOrDisconnectUser();
        } else {
            if (v == btnMe) {
                Log.v(log, "This is your informations");
                displayInfoDialogView();
            } else if (v == btnOS) {

                Log.v(log, "Service started");
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                startService(intent);
            } else if (v == btnCS) {

                Log.v(log, "Service closed");
                Intent intent = new Intent(getApplicationContext(), MyService.class);
                stopService(intent);
            } else if (v == btnGraph) {
                Intent intent=new Intent(getApplicationContext(),GraphClass.class);
                startActivity(intent);
            }
        }
    }

        private void connectOrDisconnectUser() {
            if (mApp.hasAccessToken()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivity.this);
                builder.setMessage("Disconnect from Instagram?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mApp.resetAccessToken();
                                        btnConnect.setText("Connect");
                                        btnMe.setVisibility(View.INVISIBLE);
                                        btnCS.setVisibility(View.INVISIBLE);
                                        btnOS.setVisibility(View.INVISIBLE);
                                        btnGraph.setVisibility(View.INVISIBLE);
                                        txt1.setVisibility(View.VISIBLE);
                                        img1.setVisibility(View.VISIBLE);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                mApp.authorize();
            }
        }
        private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
                btnConnect.setText("Disconnect");
                btnMe.setVisibility(View.VISIBLE);
                btnCS.setVisibility(View.VISIBLE);
                btnOS.setVisibility(View.VISIBLE);
                btnGraph.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.INVISIBLE);
                img1.setVisibility(View.INVISIBLE);

            } else if (msg.what == InstagramApp.WHAT_ERROR) {
                Toast.makeText(MainActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });



    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(this, "Welcome to Detector App", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Toast.makeText(this, "Welcome to onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // Toast.makeText(this, "It is onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();

       // Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Bye Bye from Detector :(", Toast.LENGTH_SHORT).show();
    }

    private void displayInfoDialogView() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(userInfoHashmap.get(InstagramApp.TAG_USERNAME));

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_follower_list, null);
        alertDialog.setView(view);

        TextView tvName = (TextView) view.findViewById(R.id.textView3);
        TextView tvNoOfFollwers = (TextView) view.findViewById(R.id.textView2);
        TextView tvNoOfFollowing = (TextView) view.findViewById(R.id.textView4);
        tvName.setText(userInfoHashmap.get(InstagramApp.TAG_USERNAME));
        tvNoOfFollowing.setText(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
        tvNoOfFollwers.setText(userInfoHashmap.get(InstagramApp.TAG_FOLLOWED_BY));
        alertDialog.create().show();
    }
}


