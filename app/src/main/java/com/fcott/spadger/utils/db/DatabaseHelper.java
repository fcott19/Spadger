package com.fcott.spadger.utils.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/8/24.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库版本号
    private static final int DATABASE_VERSION = 1;
    // 数据库名
    private static final String DATABASE_NAME = "AvCollection.db";

    // 数据表名，一个数据库中可以有多个表（虽然本例中只建立了一个表）
    public static final String COLLECTION_TABLE = "CollectionMovies";
    public static final String RECORD_TABLE = "RecordMovies";

    // 构造函数，调用父类SQLiteOpenHelper的构造函数
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);

    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // CursorFactory设置为null,使用系统默认的工厂类
    }

    // 继承SQLiteOpenHelper类,必须要覆写的三个方法：onCreate(),onUpgrade(),onOpen()
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE [" + COLLECTION_TABLE + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[MovieID] TEXT,");
        sBuffer.append("[Name] TEXT,");
        sBuffer.append("[Description] TEXT,");
        sBuffer.append("[CreateTime] TEXT,");
        sBuffer.append("[Img] TEXT,");
        sBuffer.append("[CoverImg] TEXT)");
        db.execSQL(sBuffer.toString());

        sBuffer.setLength(0);
        sBuffer.append("CREATE TABLE [" + RECORD_TABLE + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[MovieID] TEXT,");
        sBuffer.append("[Name] TEXT,");
        sBuffer.append("[Description] TEXT,");
        sBuffer.append("[CreateTime] TEXT,");
        sBuffer.append("[Img] TEXT,");
        sBuffer.append("[CoverImg] TEXT)");
        db.execSQL(sBuffer.toString());
        // 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COLLECTION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RECORD_TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
