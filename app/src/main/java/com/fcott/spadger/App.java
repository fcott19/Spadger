package com.fcott.spadger;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2016/12/27.
 */

public class App extends Application {
    public static final String APP_ID = "3802f3bd93"; // TODO 替换成bugly上注册的appid
    public static final String APP_CHANNEL = "DEBUG"; // TODO 自定义渠道
    public ArrayList<String> perLoadList = new ArrayList<>();

    private static App instance;
    private List<Activity> activityList;

    @Override
    public void onCreate() {
        super.onCreate();

        NetStateReceiver.registerNetworkStateReceiver(this);
        instance = this;
        activityList = new ArrayList<>();

        initBugly();
        Bmob.initialize(this, "3c7f5201908d517f4a5f299bc6d6509b");
//        initQbSdk();
//        setupLeakCanary();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetStateReceiver.unRegisterNetworkStateReceiver(this);
    }

    public static App getInstance() {
        return instance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void cleanActivity(boolean needKillProcess) {
        for (Activity activitie : activityList) {
            if (activitie != null) {
                activitie.finish();
            }
        }
        if(needKillProcess)
            Process.killProcess(Process.myPid());
    }
    public void cleanActivity() {
        cleanActivity(true);
    }

    private void initBugly() {

        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        Beta.initDelay = 1 * 1000;
        Beta.largeIconId = R.mipmap.ic_launcher_round;
        Beta.smallIconId = R.mipmap.ic_launcher_round;
        Beta.defaultBannerId = R.mipmap.ic_launcher_round;

        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
//        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Beta.storageDir = Environment.getDownloadCacheDirectory();

        /**
         * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = false;

        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
//        Beta.canShowUpgradeActs.add(MainActivity.class);

        /**
         * 设置自定义tip弹窗UI布局
         * 注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
         *  标题：beta_title，如：android:tag="beta_title"
         *  提示信息：beta_tip_message 如： android:tag="beta_tip_message"
         *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
         *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
         *  详见layout/tips_dialog.xml
         */
        Beta.tipsDialogLayoutId = R.layout.tips_dialog;
        Bugly.init(getApplicationContext(), APP_ID, true);
    }

    private void initQbSdk() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };

        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
