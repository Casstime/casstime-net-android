package com.casstime.net.example

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.casstime.net.CTCookieJarManager
import com.casstime.net.CTHttpTransformer
import com.casstime.net.CTNetworkInitHelper
import com.casstime.net.CTRetrofitFactory
import com.casstime.net.example.converter.CTGsonConverterFactory
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        CTNetworkInitHelper.initWithApplication(application, "https://ec-test.casstime.com", false)
            .apply {
                cacheStateSec = (5 * 1024 * 1024).toLong()
                readTimeOut = 5 * 1000
                connectTimeOut = 5 * 1000
                interceptors = arrayOf(HttpLoggingInterceptor())
                convertFactories = arrayOf(CTGsonConverterFactory.create())
            }


        CTRetrofitFactory.instance
            .create(GitHubService::class.java)
            .listRepos()
            .compose(CTHttpTransformer())
            .subscribe(object : Observer<CTResponse> {
                override fun onComplete() {
                    Log.i("onComplete", "onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    Log.i("onSubscribe", "onSubscribe")

                }

                override fun onNext(t: CTResponse) {
                    Log.i("onNext", t.toString())
                }

                override fun onError(e: Throwable) {
                    Log.i("onError", "onError")

                }

            })

        val cookieJar = CTCookieJarManager.cookieJar
        cookieJar.clear()
        CTCookieJarManager.clear()

    }

    interface GitHubService {
        @GET("/terminal-api-v2/perferences/app_config")
        fun listRepos(): Observable<CTResponse>
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
