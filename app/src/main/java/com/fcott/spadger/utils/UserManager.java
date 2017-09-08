package com.fcott.spadger.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fcott.spadger.App;
import com.fcott.spadger.model.entity.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/9/6.
 */

public class UserManager {
    private Context context = App.getInstance().getApplicationContext();

    public void Regist(String username,String password,String email){
        BmobUser user = new BmobUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser,BmobException e) {
                if(e==null){
                    Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Login(String username,String password){
        BmobUser.loginByAccount(username, password, new LogInListener<BmobUser>() {

            @Override
            public void done(BmobUser user, BmobException e) {
                if(user!=null){
                    Log.i("smile","用户登陆成功");
                }
            }
        });
    }

    public static User getCurrentUser(){
        User bmobUser = BmobUser.getCurrentUser(User.class);
        if(bmobUser != null){
            // 允许用户使用应用
            LogUtil.log("getCurrentUser","true");
            return bmobUser;
        }else{
            //缓存用户对象为空时， 可打开用户注册界面…
            LogUtil.log("getCurrentUser","false");
            return null;
        }
    }

    public void updateUserInfo(BmobUser newUser){
        BmobUser bmobUser = BmobUser.getCurrentUser();
        newUser.update(bmobUser.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){

                }else{

                }
            }
        });
    }

    public void queryUser(){
        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username", "lucky");
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> object,BmobException e) {
                if(e==null){

                }else{

                }
            }
        });
    }

    public void Logout(){
        BmobUser.logOut();   //清除缓存用户对象
        BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
    }

    public void updatePassword(String oldPassword,String newPassword){
        BmobUser.updateCurrentUserPassword(oldPassword,newPassword, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){

                }else{

                }
            }

        });
    }

    public void loginByAccount(String account,String password){
        BmobUser.loginByAccount(account, password, new LogInListener<BmobUser>() {

            @Override
            public void done(BmobUser user, BmobException e) {
                if(user!=null){
                    Log.i("smile","用户登陆成功");
                }
            }
        });
    }

    public void resetPasswordByEmail(){
        final String email = "xxx@163.com";
        BmobUser.resetPasswordByEmail(email, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){

                }else{

                }
            }
        });
    }
}
