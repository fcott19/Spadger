package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fcott.spadger.R;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,WebviewActivity.class));
            }
        });

        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getVideo("http://sd.52avhd.com:9888/rh/餌食牝 稲垣紗栄子/SD/playlist.m3u8")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response","completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response",e.toString());
                    }

                    @Override
                    public void onNext(String s) {
//                        Log.w("response",s.toString().split("'")[1]);

//                        JsoupUtil.parseImagsTitle(s);

//                        JsoupUtil.parseVideoList(s);

//                        JsoupUtil.parseVideoDetial(s);

                        tv.setText(s);
                        Log.w("aaa",s);
                    }
                });
    }
}
