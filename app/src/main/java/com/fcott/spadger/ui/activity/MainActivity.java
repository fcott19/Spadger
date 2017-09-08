package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.App;
import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.MainMenu;
import com.fcott.spadger.model.entity.User;
import com.fcott.spadger.ui.activity.kv.KvMainActivity;
import com.fcott.spadger.ui.activity.look.MovieListActivity;
import com.fcott.spadger.ui.activity.look.TokenCheckActivity;
import com.fcott.spadger.ui.activity.yiren.YirenMainActivity;
import com.fcott.spadger.ui.adapter.MainMenuAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.UserManager;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.util.ArrayList;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements NetChangeObserver {

    private User user;
    private MainMenuAdapter adapter;
    private String[] mNavMenuTitles;
    private TypedArray mNavMenuIconsTypeArray;
    private TypedArray mNavMenuIconTintTypeArray;
    private ArrayList<MainMenu> mainMenus;

    @Bind(R.id.rlPersonalInfo)
    RelativeLayout rlPersonalInfo;
    @Bind(R.id.iv_head)
    public ImageView ivHead;
    @Bind(R.id.tv_nick_name)
    public TextView tvNickName;
    @Bind(R.id.rcy)
    public RecyclerView recyclerView;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        user = UserManager.getCurrentUser();
        //注册网络监听
        NetStateReceiver.registerObserver(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {

        if(user != null){
            tvNickName.setText(user.getNickName());
        }
        rlPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"后续开放编辑个人资料功能",Toast.LENGTH_SHORT).show();
            }
        });

        mNavMenuTitles = getResources().getStringArray(R.array.main_menu);
        mNavMenuIconsTypeArray = getResources()
                .obtainTypedArray(R.array.main_menu_ic);
        mNavMenuIconTintTypeArray = getResources()
                .obtainTypedArray(R.array.nav_drawer_tint);
        mainMenus = new ArrayList<MainMenu>();
        for (int i = 0; i < mNavMenuTitles.length; i++) {
            mainMenus.add(new MainMenu(mNavMenuTitles[i], mNavMenuIconsTypeArray
                    .getResourceId(i, -1), mNavMenuIconTintTypeArray.getResourceId(i, -1)));
        }
        mNavMenuIconsTypeArray.recycle();

        adapter = new MainMenuAdapter(MainActivity.this, mainMenus, false);
        adapter.setOnItemClickListener(new OnItemClickListeners<MainMenu>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, MainMenu data, int position) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this,KvMainActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this,TokenCheckActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this,YirenMainActivity.class));
                        break;
                    case 3:
                        if(user == null){
                            Toast.makeText(MainActivity.this,"请登陆",Toast.LENGTH_SHORT).show();
                        }else {
                            startActivity(new Intent(MainActivity.this,BbsActivity.class));
                        }
                        break;
                    case 4:
                        Intent intent = new Intent(MainActivity.this, MovieListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", Config.typeCollection);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this,SettingActivity.class));
                        break;
                }
            }
        });

        final LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        //反注册网络监听
        NetStateReceiver.removeRegisterObserver(this);
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onNetConnected(NetUtils.NetType type) {
        if(type != NetUtils.NetType.WIFI){
            if(GeneralSettingUtil.isProhibitNoWifi()){
                Toast.makeText(MainActivity.this,getString(R.string.prohibit_no_wifi),Toast.LENGTH_SHORT).show();
                App.getInstance().cleanActivity();
            }
        }
    }

    @Override
    public void onNetDisConnect() {

    }
}
