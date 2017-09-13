package com.fcott.spadger.ui.activity.look;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.LoginBean;
import com.fcott.spadger.model.bean.TokenResultBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.RequestTokenService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.activity.MainActivity;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.NativeUtil;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TokenCheckActivity extends BaseActivity {
    public static final String TAG = TokenCheckActivity.class.getSimpleName();

    private RequestBody loginBody;

    @Bind(R.id.contain)
    View contain;
    @Bind(R.id.tv_token)
    EditText editText;

    @Override
    protected View getLoadingTargetView() {
        return contain;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_token_check;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {

        SharedPreferences pref = getSharedPreferences(Config.SP_TOKEN, Context.MODE_PRIVATE);
        String token = pref.getString("token", "");//第二个参数为默认值
        String endTime = pref.getString("endTime", "");//第二个参数为默认值
        if (!endTime.equals("") && NativeUtil.isBeforEndTime(endTime) && !endTime.equals(token)) {
            login(token);
        } else {
            requestToken();
        }
    }

    public void login(String token) {
        toggleShowLoading(true);
        loginBody = new FormBody.Builder()
                .add("Token", token)
                .build();
        RetrofitUtils.getInstance().create(LookMovieService.class)
                .login(loginBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginBean>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response", "completed");
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
                    public void onNext(LoginBean loginBean) {

                        LogUtil.log(TAG, GsonUtil.toJson(loginBean));
                        if (loginBean.getResult() == 1) {
                            startActivity(new Intent(TokenCheckActivity.this, MainActivity.class));
                            finish();
                        } else {
                            requestToken();
                        }
                    }
                });
    }

    public void requestToken() {
        toggleShowLoading(true);
        loginBody = new FormBody.Builder()
                .add("data", "{\"Action\":\"CreateToken\",\"Message\":{\"UID\":\"866693021858191\"}}")
                .build();
        RetrofitUtils.getInstance().create(RequestTokenService.class)
                .requestToken(loginBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TokenResultBean>() {
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
                    public void onNext(TokenResultBean tokenResultBean) {
                        if (tokenResultBean.getResult() == 1) {
                            SharedPreferences.Editor sharedata = getSharedPreferences(Config.SP_TOKEN, Context.MODE_PRIVATE).edit();
                            sharedata.putString("token", tokenResultBean.getMessage().getToken());
                            sharedata.putString("endTime", tokenResultBean.getMessage().getEndTime());
                            sharedata.commit();
                            login(tokenResultBean.getMessage().getToken());
                        } else {
                            toggleShowError("请求出错,点击重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    initViews();
                                }
                            });
                        }
                    }
                });

    }
}
