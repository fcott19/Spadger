package com.fcott.spadger.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.User;
import com.fcott.spadger.ui.widget.BottomSelectDialog;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.PermissionsChecker;
import com.fcott.spadger.utils.UserManager;
import com.fcott.spadger.utils.glideutils.GlideCircleTransform;
import com.fcott.spadger.utils.glideutils.GlideImageLoader;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
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
            Glide.with(MineActivity.this)
                    .load(user.getHeadImage())
                    .transform(new GlideCircleTransform(MineActivity.this))
                    .centerCrop()
                    .into(ivHead);
        }

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
                Glide.with(MineActivity.this)
                        .load(photoList.get(0))
                        .centerCrop()
                        .into(ivHead);
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
