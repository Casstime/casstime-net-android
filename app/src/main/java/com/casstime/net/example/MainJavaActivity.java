package com.casstime.net.example;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.casstime.net.*;
import com.franmontiel.persistentcookiejar.BuildConfig;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

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

        CTNetworkConfigInitHelper.Config config = CTNetworkConfigInitHelper.INSTANCE.initWithApplication(getApplication(), "www.casstime.com", !BuildConfig.DEBUG);
        config.setCacheStateSec((8 * 1024 * 1024));
        config.setConnectTimeOut(8);
        config.setReadTimeOut(8);

        CTOkHttpClient.Companion.init(config);

        CTRetrofitFactory.Companion.getInstance()
                .create(GitHubService.class)
                .listRepos("")
                .compose(new CTHttpTransformer<List<String>>())
                .subscribe();

        CTOkHttpClient.Companion.getInstance();

        PersistentCookieJar cookieJar = CTCookieJarManager.Companion.getCookieJar();

        CTCookieJarManager.Companion.clear();

    }

    public interface GitHubService {
        @GET("users/{user}/repos")
        Observable<List<String>> listRepos(@Path("user") String user);
    }
}
