package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.App;
import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MenuBean;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.entity.MainMenu;
import com.fcott.spadger.model.entity.Post;
import com.fcott.spadger.model.entity.User;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.kv.KvMainActivity;
import com.fcott.spadger.ui.activity.look.LookMovieActivity;
import com.fcott.spadger.ui.activity.look.MovieListActivity;
import com.fcott.spadger.ui.activity.yiren.YirenMainActivity;
import com.fcott.spadger.ui.adapter.MainMenuAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.FragmentFactroy;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.NativeUtil;
import com.fcott.spadger.utils.UserManager;
import com.fcott.spadger.utils.db.DBManager;
import com.fcott.spadger.utils.db.DatabaseHelper;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NetChangeObserver {

    private User user;
    private MainMenuAdapter adapter;
    private String[] mNavMenuTitles;
    private TypedArray mNavMenuIconsTypeArray;
    private TypedArray mNavMenuIconTintTypeArray;
    private ArrayList<MainMenu> mainMenus;
    private MenuBean kvMenuBean;

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

        requestCollections();
        requestLikePosts();
        if (user != null) {
            tvNickName.setText(user.getNickName());
            if (user.getHeadImage() != null) {
                ImageLoader.getInstance().loadCircle(MainActivity.this,user.getHeadImage(),ivHead);
            }
        }
        rlPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, MineActivity.class);
                    Pair<View, String>[] p = new Pair[]{Pair.create(rlPersonalInfo, "share_bg"),
                            Pair.create(ivHead, "share_head"),
                            Pair.create(tvNickName, "share_mine_nickname")};
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, p);
                    ActivityCompat.startActivityForResult(MainActivity.this, intent,Config.NORMAL_REQUEST_CODE, activityOptionsCompat.toBundle());
                } else {
                    Toast.makeText(MainActivity.this, "请登陆", Toast.LENGTH_SHORT).show();
                }
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
                Intent intent = new Intent(MainActivity.this, KvMainActivity.class);
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
//                        startActivity(new Intent(MainActivity.this,KvMainActivity.class));
                        intent.setClass(MainActivity.this, KvMainActivity.class);
                        bundle.putParcelable("MENUBEAN", kvMenuBean);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, LookMovieActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, YirenMainActivity.class));
                        break;
                    case 3:
                        if (user == null) {
                            Toast.makeText(MainActivity.this, "请登陆", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(MainActivity.this, BbsActivity.class));
                        }
                        break;
                    case 4:
                        if (user == null) {
                            Toast.makeText(MainActivity.this, "请登陆", Toast.LENGTH_SHORT).show();
                        } else {
                            intent.setClass(MainActivity.this, MovieListActivity.class);
                            bundle.putString("TYPE", Config.typeCollection);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;
                }
            }
        });

        final LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        initKvMenuBean();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Config.HEAD_CHANGE_RESULT_CODE) {
            user = UserManager.getCurrentUser();
            ImageLoader.getInstance().loadCircle(MainActivity.this,user.getHeadImage(),ivHead);
        }
    }

    private void requestCollections() {
        BmobQuery<MovieBean.MessageBean.MoviesBean> query = new BmobQuery<>();
        query.addWhereRelatedTo("avCollections", new BmobPointer(user));
        query.findObjects(new FindListener<MovieBean.MessageBean.MoviesBean>() {

            @Override
            public void done(List<MovieBean.MessageBean.MoviesBean> object, BmobException e) {
                if (e == null && object.size() != 0) {
                    DBManager dbManager = new DBManager(MainActivity.this);
                    dbManager.clearTable();
                    for (MovieBean.MessageBean.MoviesBean moviesBean : object) {
                        dbManager.add(moviesBean);
                    }
                    dbManager.closeDB();
                }
                if (e != null)
                    LogUtil.log(e.toString());
            }
        });
    }

    private void requestLikePosts(){
        BmobQuery<Post> query = new BmobQuery<>();
        query.addWhereRelatedTo("likePosts", new BmobPointer(user));
        query.findObjects(new FindListener<Post>() {

            @Override
            public void done(List<Post> object, BmobException e) {
                if (e == null && object.size() != 0) {
                    DBManager dbManager = new DBManager(MainActivity.this, DatabaseHelper.LIKE_POST_TABLE);
                    dbManager.clearTable();
                    for (Post post : object) {
                        dbManager.add(post);
                    }
                    dbManager.closeDB();
                }
                if (e != null)
                    LogUtil.log(e.toString());
            }
        });
    }

    private void initKvMenuBean() {
        boolean needUpdate = NativeUtil.needUpdate(KvMainActivity.TAG);
        ACache mCache = ACache.get(MainActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(KvMainActivity.TAG);
        //显示缓存
        if (!TextUtils.isEmpty(value) && !needUpdate) {
            kvMenuBean = JsoupUtil.parseMenu(value);
            return;
        }
        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getMainPage("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        toggleShowError("请求出错,点击重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initViews();
                            }
                        });
                    }

                    @Override
                    public void onNext(String s) {
                        kvMenuBean = JsoupUtil.parseMenu(s);
                        //缓存
                        ACache aCache = ACache.get(MainActivity.this.getApplicationContext());
                        aCache.put(KvMainActivity.TAG, s);
                        FragmentFactroy.getFragment(kvMenuBean);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        //反注册网络监听
        NetStateReceiver.removeRegisterObserver(this);
        super.onDestroy();
    }

    @Override
    public void onNetConnected(NetUtils.NetType type) {
        if (type != NetUtils.NetType.WIFI) {
            if (GeneralSettingUtil.isProhibitNoWifi()) {
                Toast.makeText(MainActivity.this, getString(R.string.prohibit_no_wifi), Toast.LENGTH_SHORT).show();
                App.getInstance().cleanActivity();
            }
        }
    }

    @Override
    public void onNetDisConnect() {

    }
}
