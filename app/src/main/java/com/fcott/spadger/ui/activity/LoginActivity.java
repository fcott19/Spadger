package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.User;
import com.fcott.spadger.ui.activity.look.TokenCheckActivity;
import com.fcott.spadger.ui.widget.MaterialProgressBar;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.NativeUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {
    private static final int LOGIN = 0;
    private static final int REGIST_BY_EMAIL = 1;
    private static final int REGIST_BY_PHONE_NUMBER = 2;
    private static final int FORGET_PWD = 3;
    private int status = LOGIN;

    @Bind(R.id.til_nick_name)
    TextInputLayout tilNickName;
    @Bind(R.id.til_user_name)
    TextInputLayout tilUserName;
    @Bind(R.id.til_password)
    TextInputLayout tilPassword;
    @Bind(R.id.til_phone_number)
    TextInputLayout tilPhoneNumber;
    @Bind(R.id.til_email)
    TextInputLayout tilEmail;
    @Bind(R.id.nick_name)
    EditText mNickNameView;
    @Bind(R.id.user_name)
    EditText mUserNameView;
    @Bind(R.id.phone_number)
    EditText mPhoneNumberView;
    @Bind(R.id.email)
    EditText mEmailView;
    @Bind(R.id.password)
    EditText mPasswordView;
    @Bind(R.id.login_progress)
    MaterialProgressBar mProgressView;
    @Bind(R.id.tv_more)
    TextView tvMore;
    @Bind(R.id.email_sign_in_button)
    Button mSignInButton;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected boolean needAddActivityList() {
        return false;
    }

    @Override
    protected void initViews() {
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLoginOrRegist();
                    return true;
                }
                return false;
            }
        });

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLoginOrRegist();
            }
        });

        tvMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case LOGIN:
                        showMore();
                        break;
                    default:
                        changeStatus(LOGIN);
                        break;
                }
            }
        });
        changeStatus(LOGIN);
    }

    /**
     * 尝试登陆或者注册
     */
    private void attemptLoginOrRegist() {
        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mNickNameView.setError(null);
        mPhoneNumberView.setError(null);
        mEmailView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();
        String phoneNumber = mPhoneNumberView.getText().toString();
        String nickName = mNickNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password) && status != FORGET_PWD) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!isUserNameValid(userName) && status != FORGET_PWD) {
            mUserNameView.setError(getString(R.string.error_invalid_user_name));
            focusView = mUserNameView;
            cancel = true;
        }

        if (!isNickNameValid(nickName) && (status == REGIST_BY_EMAIL || status == REGIST_BY_PHONE_NUMBER)) {
            mNickNameView.setError(getString(R.string.error_invalid_nick_name));
            focusView = mNickNameView;
            cancel = true;
        }

        if (status == REGIST_BY_PHONE_NUMBER && !isPhoneNumberValid(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_invalid_phonenumber));
            focusView = mPhoneNumberView;
            cancel = true;
        }
        if (status == REGIST_BY_EMAIL && !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else if (status == LOGIN) {
            setOnTask(true);
            User user = new User();
            user.setUsername(userName);
            user.setPassword(password);
            user.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    setOnTask(false);
                    if (e == null) {
                        if(bmobUser.getEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, TokenCheckActivity.class));
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "请前往邮箱激活该帐号", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                    }
                }
            });
        }else if(status == FORGET_PWD){
            if(NativeUtil.canFindPwd(email)){
                setOnTask(true);
                BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        setOnTask(false);
                        if(e==null){
                            Toast.makeText(LoginActivity.this,"重置密码请求成功，请到邮箱进行密码重置操作",Toast.LENGTH_SHORT).show();
                            changeStatus(LOGIN);
                        }else{
                            showError(e);
                        }
                    }
                });
            }else {
                Toast.makeText(LoginActivity.this,"每个帐号每天只能找回一次",Toast.LENGTH_SHORT).show();
            }
        }else{
            setOnTask(true);
            User user = new User();
            user.setUsername(userName);
            user.setPassword(password);
            user.setNickName(nickName);
            if (status == REGIST_BY_EMAIL) {
                user.setEmail(email);
            } else if (status == REGIST_BY_PHONE_NUMBER) {
                user.setMobilePhoneNumber(phoneNumber);
            }
            user.signUp(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    setOnTask(false);
                    if (e == null) {
                        Toast.makeText(LoginActivity.this, "验证邮件已发送,请前往邮箱激活", Toast.LENGTH_SHORT).show();
                        changeStatus(LOGIN);
                    } else {
                        showError(e);
                    }
                }
            });
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4])|(18[0-9])|(17[0-9])|(147))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
        return !TextUtils.isEmpty(phoneNumber) && m.matches();
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && email.contains("@");
    }

    private boolean isUserNameValid(String userName) {
        return userName.length() > 4 && userName.length() < 13;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5 && password.length() < 13;
    }

    private boolean isNickNameValid(String nickName) {
        return nickName.length() > 2 && nickName.length() < 11;
    }

    PopupWindow window;
    private void showMore() {
        View popupView = LoginActivity.this.getLayoutInflater().inflate(R.layout.popupwindow, null);
        if (window == null)
            window = new PopupWindow(popupView);
        else {
            window.showAsDropDown(tvMore, 0, -tvMore.getMeasuredHeight(), Gravity.RIGHT);
            return;
        }

        TextView tvRegistByEmail = (TextView) popupView.findViewById(R.id.tv_regist_by_email);
        tvRegistByEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus(REGIST_BY_EMAIL);
                window.dismiss();
            }
        });
        TextView tvRegistByPhoneNumber = (TextView) popupView.findViewById(R.id.tv_regist_by_phone_number);
        tvRegistByPhoneNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus(REGIST_BY_PHONE_NUMBER);
                window.dismiss();
            }
        });
        TextView tvForgetPwd = (TextView) popupView.findViewById(R.id.tv_forget_pwd);
        tvForgetPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus(FORGET_PWD);
                window.dismiss();
            }
        });
        TextView tvTourist = (TextView) popupView.findViewById(R.id.tv_tourist);
        tvTourist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,TokenCheckActivity.class));
                finish();
            }
        });

        window.setWidth((int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        window.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
        window.setOutsideTouchable(true);
//        window.setAnimationStyle(R.style.popup_window_anim);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        window.setElevation(3);


        window.update();
        window.showAsDropDown(tvMore, 0, -tvMore.getMeasuredHeight(), Gravity.RIGHT);
    }

    private void changeStatus(int status) {
        this.status = status;
        switch (status) {
            case LOGIN:
                tilNickName.setVisibility(View.GONE);
                tilUserName.setVisibility(View.VISIBLE);
                tilPassword.setVisibility(View.VISIBLE);
                tilPhoneNumber.setVisibility(View.GONE);
                tilEmail.setVisibility(View.GONE);
                tvMore.setText(getString(R.string.prompt_more));
                mSignInButton.setText(getString(R.string.action_sign_in));
                break;
            case REGIST_BY_EMAIL:
                tilNickName.setVisibility(View.VISIBLE);
                tilUserName.setVisibility(View.VISIBLE);
                tilPassword.setVisibility(View.VISIBLE);
                tilPhoneNumber.setVisibility(View.GONE);
                tilEmail.setVisibility(View.VISIBLE);
                tvMore.setText(getString(R.string.prompt_back));
                mSignInButton.setText(getString(R.string.action_regist));
                break;
            case REGIST_BY_PHONE_NUMBER:
                tilNickName.setVisibility(View.VISIBLE);
                tilUserName.setVisibility(View.VISIBLE);
                tilPassword.setVisibility(View.VISIBLE);
                tilPhoneNumber.setVisibility(View.VISIBLE);
                tilEmail.setVisibility(View.GONE);
                tvMore.setText(getString(R.string.prompt_back));
                mSignInButton.setText(getString(R.string.action_regist));
                break;
            case FORGET_PWD:
                tilNickName.setVisibility(View.GONE);
                tilUserName.setVisibility(View.GONE);
                tilPassword.setVisibility(View.GONE);
                tilPhoneNumber.setVisibility(View.GONE);
                tilEmail.setVisibility(View.VISIBLE);
                tvMore.setText(getString(R.string.prompt_back));
                mSignInButton.setText(getString(R.string.action_post_token));
                break;
        }
    }

    private void setOnTask(boolean onTask){
        if(onTask){
            mProgressView.setVisibility(View.VISIBLE);
            mSignInButton.setEnabled(false);
            tvMore.setEnabled(false);
        }else {
            mProgressView.setVisibility(View.GONE);
            mSignInButton.setEnabled(true);
            tvMore.setEnabled(true);
        }
    }

    private void activationEmail(final String email){
        BmobUser.requestEmailVerify(email, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(LoginActivity.this,"请求验证邮件成功，请到" + email + "邮箱中进行激活",Toast.LENGTH_SHORT).show();
                }else{

                }
            }
        });
    }

    private void showError(BmobException e){
        LogUtil.log(e.getErrorCode()+":"+e.getMessage());
        StringBuffer stringBuffer = new StringBuffer();
        if(e.getErrorCode() == 202){
            stringBuffer.append("该帐号已经被注册");
        }else if(e.getErrorCode() == 203){
            stringBuffer.append("该邮箱已经被注册");
        }else if(e.getErrorCode() == 205){
            stringBuffer.append("该邮箱还未注册");
        }else  {
            stringBuffer.append("请求失败");
        }
        Toast.makeText(LoginActivity.this, stringBuffer.toString(), Toast.LENGTH_SHORT).show();
    }

}

