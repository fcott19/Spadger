package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExplainActivity extends BaseActivity {
    private String type = Config.typeChannel;
    private String channelId = "4";
    private String actorId;
    private String classId;
    private String searchData;
    private String cacheTag;
    private RequestBody moviceBody;
    private int cPage = 1;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_explain;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        requestData(cPage);
    }

    private void requestData(final int page) {
        if (type.equals(Config.typeChannel) && channelId != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(page))
                    .add("PageSize", String.valueOf(50))
                    .add("Type", "1")
                    .add("ID", channelId)
                    .add("Data", "")
                    .build();
        } else if ((type.equals(Config.typeActor) && actorId != null) || (type.equals(Config.searchTypeActor) && searchData != null)) {
            String id;
            if (actorId != null)
                id = actorId;
            else
                id = searchData;
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(1))
                    .add("PageSize", String.valueOf(50000))
                    .add("Type", "5")
                    .add("ID", id)
                    .add("Data", "")
                    .build();
        } else if ((type.equals(Config.typeClass) && classId != null) || (type.equals(Config.searchTypeClass) && searchData != null)) {
            String id;
            if (classId != null)
                id = classId;
            else
                id = searchData;
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(1))
                    .add("PageSize", String.valueOf(50000))
                    .add("Type", "2")
                    .add("ID", id)
                    .add("Data", "")
                    .build();
        } else if (type.equals(Config.searchTypeNormal) && searchData != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(1))
                    .add("PageSize", String.valueOf(50000))
                    .add("Type", "1")
                    .add("ID", "-1")
                    .add("Data", searchData)
                    .build();
        }

        RetrofitUtils.getInstance().create(LookMovieService.class)
                .requestMovie(moviceBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        toggleShowError("请求出错");
                    }

                    @Override
                    public void onNext(MovieBean bean) {
                        if(bean.getMessage().getPageCount() < cPage)
                            return;
                        List<BmobObject> beans = new ArrayList<BmobObject>();
                        beans.addAll(bean.getMessage().getMovies());
                        //第二种方式：v3.5.0开始提供
                        new BmobBatch().insertBatch(beans).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> o, BmobException e) {
                                if(e==null){
                                    for(int i=0;i<o.size();i++){
                                        BatchResult result = o.get(i);
                                        BmobException ex =result.getError();
                                        if(ex==null){
                                            LogUtil.log("第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                                        }else{
                                            LogUtil.log("第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                                        }
                                    }
                                    cPage += 1;
                                    requestData(cPage);
                                }else{
                                    requestData(cPage);
                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                }
                            }
                        });
                    }
                });


    }
}
