package com.fcott.spadger.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * 检查权限的工具类
 * Created by Administrator on 2016/12/8.
 */
public class PermissionsChecker {
    private final Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public String[] filterPermissions(String... permissions){
        ArrayList<String> perList = new ArrayList();
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                perList.add(permission);
            }
        }
        return perList.toArray(new String[perList.size()]);
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}
