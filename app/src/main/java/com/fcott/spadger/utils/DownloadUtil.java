package com.fcott.spadger.utils;

import android.util.Log;

import com.fcott.spadger.model.http.LoadfileService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/5.
 */
public class DownloadUtil {

    /**
     * 返回下载文件的Observable
     * @param url 文件url
     * @return
     */
    public static Observable<String> getLoadObservable(final String url){
        LoadfileService loadfile = RetrofitUtils.getInstance().create1(LoadfileService.class);
        return loadfile.downloadFile(url)
                .retry(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ResponseBody, String>() {
                    @Override
                    public String call(ResponseBody responseBody) {
//                        FileUtil.writeToFile(responseBody, FileUtil.returnLocalMapfileName(url));
                        FileUtil.writeToFile(responseBody, "aabb.mp4");
                        return url;
                    }
                }).subscribeOn(Schedulers.io());
    }

    public static Observable<InputStream> getLoadObservable1(final String url){
        LoadfileService loadfile = RetrofitUtils.getInstance().create1(LoadfileService.class);
        return loadfile.downloadFile(url)
                .retry(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ResponseBody, InputStream>() {
                    @Override
                    public InputStream call(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                }).subscribeOn(Schedulers.io());
    }
    /**
     * 多线程有序下载
     * @param observables
     */
    public static void concatDownload(List<Observable<String>> observables) {
        //Observable的concatEager将所有的Observable合成一个Observable,所有的observable同时发射数据。
        Observable.concatEager(observables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String b) {
                        LogUtil.log(b+"--");
                    }
                });
    }

    public static void concatDownload1(List<Observable<InputStream>> observables) {
        final ArrayList<InputStream> al=new ArrayList();
        //Observable的concatEager将所有的Observable合成一个Observable,所有的observable同时发射数据。
        Observable.concatEager(observables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InputStream>() {
                    @Override
                    public void onCompleted() {
                        Enumeration en= Collections.enumeration(al);
                        SequenceInputStream sis=new SequenceInputStream(en);
                        LogUtil.log("gogogogo");
                        FileUtil.writeInputStreamToDisk(sis,"aaaa.mp4");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(InputStream b) {
                        al.add(b);
                        LogUtil.log(al.size()+"--");
                    }
                });
    }

    /**
     * 多线程无序下载
     * @param observables
     */
    public static void mergeDownload(List<Observable<String>> observables) {
        //Observable的merge将所有的Observable合成一个Observable,所有的observable同时发射数据。
        Observable.merge(observables).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String b) {

                    }
                });
    }
    public static void mergeDownload(List<Observable<String>> observables,final OnLoadListener onLoadListener) {
        //Observable的merge将所有的Observable合成一个Observable,所有的observable同时发射数据。
        Observable.merge(observables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        onLoadListener.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoadListener.onError(e.toString());
                    }

                    @Override
                    public void onNext(String url) {

                    }
                });
    }

    public static void singleDownload(final String url, final OnLoadListener onLoadListener){
        getLoadObservable(url).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoadListener.onError(url);
                    }

                    @Override
                    public void onNext(String url) {
                        onLoadListener.onSuccess();
                    }
                });
    }

    public interface OnLoadListener {
        void onSuccess();
        void onError(String error);
    }

}
