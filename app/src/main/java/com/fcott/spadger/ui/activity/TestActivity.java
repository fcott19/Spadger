package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.glideutils.SDFileHelper;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.tencent.tinker.android.dex.util.FileUtils.readStream;

public class TestActivity extends BaseActivity {
    public static final String TAG = TestActivity.class.getSimpleName();
    public Handler handler = new Handler();
    SDFileHelper helper = new SDFileHelper(App.getInstance());

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_test;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestByGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Get方式请求
    public void requestByGet() throws Exception {
        String path = "http://wap.wangxiao.cn/Tiku/?sign=jsz&subjectID=19ebffce-cc5d-4466-bf04-503b23817d5c";
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readStream(urlConn.getInputStream());
            Log.i(TAG, "Get方式请求成功，返回数据如下：");

            JsoupUtil.parseWangxiao(new String(data, "UTF-8"), new loadCallBack() {
                @Override
                public void success(final String a,final String b,final String u,final String name) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            helper.savePicture(a,b,name,u);
                        }
                    });
                }
            });
//            LogUtil.log("aabbcc", GsonUtil.toJson(wangxiaoBean));
//            for(ZhangjieBean zhangjieBean:wangxiaoBean.getZhangjieBeanList()){
//                for(String key:zhangjieBean.getDetialMap().keySet()){
//                    try {
//                        requestImageGet(NativeUtil.ascii2Native(zhangjieBean.getDetialMap().get(key)));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        } else {
            Log.i(TAG, "Get方式请求失败");
        }
        // 关闭连接
        urlConn.disconnect();
    }

    // Get方式请求
    public void requestImageGet(String u) throws Exception {
        String path = "http://wap.wangxiao.cn"+u;
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readStream(urlConn.getInputStream());
            Log.i(TAG, "Get方式请求成功，返回数据如下：");
            Log.i(TAG, new String(data, "UTF-8"));

        } else {
            Log.i(TAG, "Get方式请求失败");
        }
        // 关闭连接
        urlConn.disconnect();
    }

    public interface loadCallBack{
        void success(String a,String b,String u,String name);
    }
}
