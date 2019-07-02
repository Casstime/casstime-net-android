package com.casstime.net.example;

import android.os.Bundle;
import com.casstime.net.CTNetworkInitHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CTNetworkInitHelper.Builder builder = CTNetworkInitHelper.INSTANCE.initWithApplication(getApplication(), "www.casstime.com", !BuildConfig.DEBUG);
        builder.setCacheStateSec((8 * 1024 * 1024));
        builder.setConnectTimeOut(8);
        builder.setReadTimeOut(8);
    }

}
