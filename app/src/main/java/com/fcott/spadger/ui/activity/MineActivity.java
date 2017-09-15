package com.fcott.spadger.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.App;
import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.MainMenu;
import com.fcott.spadger.model.entity.User;
import com.fcott.spadger.ui.activity.kv.KvMainActivity;
import com.fcott.spadger.ui.activity.look.MovieListActivity;
import com.fcott.spadger.ui.adapter.MineMenuAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.widget.AlertDialogFragment;
import com.fcott.spadger.ui.widget.BottomSelectDialog;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.PermissionsChecker;
import com.fcott.spadger.utils.UserManager;
import com.fcott.spadger.utils.db.DBManager;
import com.fcott.spadger.utils.db.DatabaseHelper;
import com.fcott.spadger.utils.glideutils.GlideImageLoader;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MineActivity extends BaseActivity {
    public static final String TAG = MineActivity.class.getSimpleName();
    public static final int PERMISSION_REQUEST_CODE = 0;                                  // 权限请求码
    private String [] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private List<String> path = new ArrayList<>();
    private GalleryConfig galleryConfig;
    private IHandlerCallBack iHandlerCallBack;
    private PermissionsChecker mPermissionsChecker = null;                                  // 权限检测器
    private MineMenuAdapter adapter;
    private String[] mNavMenuTitles;
    private TypedArray mNavMenuIconsTypeArray;
    private TypedArray mNavMenuIconTintTypeArray;
    private ArrayList<MainMenu> mainMenus;

    @Bind(R.id.tv_nick_name)
    TextView tvNickName;
    @Bind(R.id.rcy)
    public RecyclerView recyclerView;
    @Bind(R.id.contain)
    View mContainer;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @OnClick({R.id.iv_head})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_head:
                //进入权限配置页面
                if (mPermissionsChecker.lacksPermissions(permissions)) {
                    PermissionsActivity.startActivityForResult(this, PERMISSION_REQUEST_CODE, permissions);
                    return;
                }
                new BottomSelectDialog().setSelectFromListener(new BottomSelectDialog.SelectFromListener() {
                    @Override
                    public void fromCamera() {
                        galleryConfig.getBuilder().isOpenCamera(true).build();
                        GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(MineActivity.this);
                    }

                    @Override
                    public void fromGallery() {
                        galleryConfig.getBuilder().isOpenCamera(false).build();
                        GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(MineActivity.this);
                    }
                }).show(getFragmentManager(),"tag");
                break;
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_mine;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionsChecker = new PermissionsChecker(this);//权限检测器
    }

    @Override
    protected void initViews() {
        initGallery();

        User user = UserManager.getCurrentUser();
        if(user.getHeadImage() != null){
            ImageLoader.getInstance().loadCircle(MineActivity.this,user.getHeadImage(),ivHead);
            tvNickName.setText(user.getNickName());
        }

        mNavMenuTitles = getResources().getStringArray(R.array.mine_menu);
        mNavMenuIconsTypeArray = getResources()
                .obtainTypedArray(R.array.mine_menu_ic);
        mNavMenuIconTintTypeArray = getResources()
                .obtainTypedArray(R.array.mine_tint);
        mainMenus = new ArrayList<MainMenu>();
        for (int i = 0; i < mNavMenuTitles.length; i++) {
            mainMenus.add(new MainMenu(mNavMenuTitles[i], mNavMenuIconsTypeArray
                    .getResourceId(i, -1), mNavMenuIconTintTypeArray.getResourceId(i, -1)));
        }
        mNavMenuIconsTypeArray.recycle();

        adapter = new MineMenuAdapter(MineActivity.this, mainMenus, false);
        adapter.setOnItemClickListener(new OnItemClickListeners<MainMenu>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, MainMenu data, int position) {
                Intent intent = new Intent(MineActivity.this, KvMainActivity.class);
                Bundle bundle = new Bundle();
                switch (position){
                    case 0:
                        intent.setClass(MineActivity.this, BbsActivity.class);
                        bundle.putString(Config.DATA_FROM, Config.DATA_FROM_MY_POST);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(MineActivity.this, BbsActivity.class);
                        bundle.putString(Config.DATA_FROM, Config.DATA_FROM_LIKE_POST);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(MineActivity.this, MovieListActivity.class);
                        bundle.putString("TYPE", Config.typeRecord);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 3:
                        new AlertDialogFragment().setMessage("确定清除浏览历史?").setSelectFromListener(new AlertDialogFragment.SelectFromListener() {
                            @Override
                            public void positiveClick() {
                                DBManager dbManager = new DBManager(MineActivity.this);
                                dbManager.clearTable(DatabaseHelper.RECORD_TABLE);
                                dbManager.closeDB();
                                Toast.makeText(MineActivity.this,"浏览历史已清除",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void negativeClick() {

                            }
                        }).show(getFragmentManager(),"record");
                        break;
                    case 4:
                        new AlertDialogFragment().setMessage("确定退出?").setSelectFromListener(new AlertDialogFragment.SelectFromListener() {
                            @Override
                            public void positiveClick() {
                                BmobUser.logOut();   //清除缓存用户对象
                                startActivity(new Intent(MineActivity.this, LoginActivity.class));
                                App.getInstance().cleanActivity(false);
                            }

                            @Override
                            public void negativeClick() {

                            }
                        }).show(getFragmentManager(),"logout");
                        break;
                }
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(MineActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == PERMISSION_REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            return;
        }else if(requestCode == PERMISSION_REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_GRANTED){
            ivHead.performClick();
        }
    }

    private void initGallery() {
        iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: 开启");
            }

            @Override
            public void onSuccess(List<String> photoList) {
                Log.i(TAG, "onSuccess: 返回数据");
                path.clear();
                path.add(photoList.get(0));
                updateHeadUrl(photoList.get(0));
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 取消");
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: 结束");
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: 出错");
            }
        };

        galleryConfig = new GalleryConfig.Builder()
                .imageLoader(new GlideImageLoader())    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                .provider("com.fcott.spadger.fileProvider")   // provider(必填)
                .pathList(path)                         // 记录已选的图片
                .multiSelect(false)                      // 是否多选   默认：false
                .multiSelect(false, 1)                   // 配置是否多选的同时 配置多选数量   默认：false ， 9
                .maxSize(1)                             // 配置多选时 的多选数量。    默认：9
                .crop(true)                             // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
                .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                .filePath("/Gallery/Pictures")          // 图片存放路径
                .build();

    }

    private void updateHeadUrl(final String path){
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    User user = UserManager.getCurrentUser();
                    if(user.getHeadImage() != null){
                        BmobFile file = new BmobFile();
                        file.setUrl(user.getHeadImage());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
                        file.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                            }
                        });
                    }

                    ImageLoader.getInstance().loadCircle(MineActivity.this,bmobFile.getFileUrl(),ivHead);
                    User newUser = new User();
                    newUser.setHeadImage(bmobFile.getFileUrl());
                    newUser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            setResult(Config.HEAD_CHANGE_RESULT_CODE);
                        }
                    });
                }else{
                    LogUtil.log("上传文件失败：" + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }



}
