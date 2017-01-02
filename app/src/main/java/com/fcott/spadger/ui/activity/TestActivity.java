package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.M3u8Bean;
import com.fcott.spadger.model.bean.M3u8Item;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.DownloadUtil;
import com.hly.easyretrofit.download.DownLoadBackListener;
import com.hly.easyretrofit.download.DownLoadManager;
import com.hly.easyretrofit.download.db.DownLoadEntity;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.fcott.spadger.utils.FileUtil.getDefaultMapDirectory;

public class TestActivity extends BaseActivity {
    String url = "http://sd.52avhd.com:9888/rh/%E3%83%91%E3%82%A4%E3%83%91%E3%83%B3%E5%A4%A7%E5%AD%A6%E7%94%9F%E3%81%AB%E4%B8%AD%E5%87%BA%E3%81%97/SD/playlist.m3u8";
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
//                        parseMu38(s);
                        downLoad(s);
                    }
                });
    }

    private void downLoad(String s){
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

        final List<DownLoadEntity> list = new ArrayList<>();
        for(M3u8Item item:m3u8Bean.getDataList()){
            DownLoadEntity downLoadModel = new DownLoadEntity();
            downLoadModel.url = url.replace("playlist.m3u8",item.getUrl() );
            downLoadModel.saveName = getDefaultMapDirectory()+item.getUrl();
            list.add(downLoadModel);
        }

        DownLoadManager.getInstance().downLoad(list, "MainActivity", new DownLoadBackListener() {

            @Override
            public void onError(DownLoadEntity downLoadEntity, Throwable throwable) {
                Toast.makeText(TestActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStart(double percent) {
                Toast.makeText(TestActivity.this, "开始下载", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(TestActivity.this, "取消了", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDownLoading(double percent) {
                Toast.makeText(TestActivity.this, String.valueOf(percent * 100), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCompleted() {
                Toast.makeText(TestActivity.this, "下完了", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseMu38(String s){
        String res[] = s.replace("#EXT-X-ENDLIST","").replace("\n","").split("#EXTINF:");
        M3u8Bean m3u8Bean = new M3u8Bean();

        for(int i = 0;i < res.length;i++){
            if(i == 0)
                continue;
            if(i == 4){
                break;
            }
            String time = res[i].split(",")[0];
            String url = res[i].split(",")[1];
            m3u8Bean.getDataList().add(new M3u8Item(url,time));
            m3u8Bean.setTotalTime(m3u8Bean.getTotalTime()+Double.valueOf(time));
        }

        ArrayList<Observable<String>> taskList = new ArrayList<>();//下载任务列表
        ArrayList<Observable<InputStream>> taskList1 = new ArrayList<>();//下载任务列表

        for(M3u8Item item:m3u8Bean.getDataList()){
            taskList.add(DownloadUtil.getLoadObservable(url.replace("playlist.m3u8",item.getUrl() )));
        }
        DownloadUtil.concatDownload(taskList);

    }
}
