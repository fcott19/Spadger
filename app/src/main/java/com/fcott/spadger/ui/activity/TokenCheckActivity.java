package com.fcott.spadger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.LoginBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.LogUtil;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TokenCheckActivity extends BaseActivity{
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
        if (!token.equals("")) {
            loginBody = new FormBody.Builder()
                    .add("Token", token)
                    .build();
            login(null);
        }
    }

    public void login(final View view) {
        toggleShowLoading(true);
        if (view != null) {
            loginBody = new FormBody.Builder()
                    .add("Token", editText.getText().toString().trim())
                    .build();
        }
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
                        toggleShowLoading(false);
                        Log.w("response", e.toString());
                    }

                    @Override
                    public void onNext(LoginBean loginBean) {

                        LogUtil.log(TAG, GsonUtil.toJson(loginBean));
                        if (loginBean.getResult() == 1) {
                            if(view != null){
                                SharedPreferences.Editor sharedata = getSharedPreferences(Config.SP_TOKEN, Context.MODE_PRIVATE).edit();
                                sharedata.putString("token", editText.getText().toString().trim());
                                sharedata.commit();
                            }
                            startActivity(new Intent(TokenCheckActivity.this, LookMovieActivity.class));
                            finish();
                        }else {
                            toggleShowLoading(false);
                            Toast.makeText(TokenCheckActivity.this,"请正确输入验证码",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
