package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.M3u8Bean;
import com.fcott.spadger.model.bean.M3u8Item;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.DownloadUtil;
import com.fcott.spadger.utils.LogUtil;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TestActivity extends BaseActivity {
    String url = "http://hd.52avhd.com:9888/rh/%E3%83%91%E3%82%A4%E3%83%91%E3%83%B3%E5%A4%A7%E5%AD%A6%E7%94%9F%E3%81%AB%E4%B8%AD%E5%87%BA%E3%81%97/SD/playlist.m3u8";
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_test;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        parseMu38(s);
                    }
                });
    }

    ArrayList<String> urlList = new ArrayList<>();//下载任务列表
    private void parseMu38(String s){
        String res[] = s.replace("#EXT-X-ENDLIST","").replace("\n","").split("#EXTINF:");
        M3u8Bean m3u8Bean = new M3u8Bean();

        for(int i = 0;i < res.length;i++){
            if(i == 0)
                continue;
            String time = res[i].split(",")[0];
            String url = res[i].split(",")[1];
            m3u8Bean.getDataList().add(new M3u8Item(url,time));
            m3u8Bean.setTotalTime(m3u8Bean.getTotalTime()+Double.valueOf(time));
        }

        ArrayList<Observable<String>> taskList = new ArrayList<>();//下载任务列表

        for(M3u8Item item:m3u8Bean.getDataList()){
            taskList.add(DownloadUtil.getLoadObservable(url.replace("playlist.m3u8",item.getUrl() )));
            urlList.add(url.replace("playlist.m3u8",item.getUrl()));
        }
        DownloadUtil.concatDownload(taskList);

    }
}
