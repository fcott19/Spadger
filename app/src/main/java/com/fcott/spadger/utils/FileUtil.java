package com.fcott.spadger.utils;

/**
 * Created by Administrator on 2016/12/5.
 */

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;

/**
 * 文件工具类
 */

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    public static boolean writeResponseBodyToDisk(ResponseBody body, String downloadName) {
        String path = getDefaultMapDirectory()+downloadName;

        File futureFile = new File(path);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        long fileSize = body.contentLength();

        try {
            try {
                byte[] fileReader = new byte[1024 * 1024];
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }

    }

    public static boolean writeInputStreamToDisk(InputStream inputStream , String downloadName) {
        String path = getDefaultMapDirectory()+downloadName;

        File futureFile = new File(path);
        OutputStream outputStream = null;
        try {
            try {
                byte[] fileReader = new byte[1024 * 1024];
                outputStream = new FileOutputStream(futureFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }

    }

    public static boolean writeToFile(ResponseBody body, String downloadName) {
        String path = getDefaultMapDirectory()+downloadName;
        try {
            File futureStudioIconFile = new File(path);

            if (!futureStudioIconFile.exists()) {
                futureStudioIconFile.createNewFile();
            }

            RandomAccessFile oSavedFile = new RandomAccessFile(futureStudioIconFile, "rw");

            oSavedFile.seek(futureStudioIconFile.length());

            InputStream inputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    oSavedFile.write(fileReader, 0, read);
                }
                return true;
            } finally {

                oSavedFile.close();

                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean fileExists(String path){
        File file=new File(path);
        return file.exists();
    }

    /**
     * 根据mapfile url返回mapfile保存在本地的文件名
     */
    public static String returnLocalMapfileName(String url){
        String[] s = url.split("/");
        return (s[s.length-1]).replace("ts","mp4").replace(" ","");
    }

    /**
     * 返回app 默认根目录
     *
     * @return
     */
    public static String getDefaultRootDirectory() {
        String var0 = getFilesDir() + "Spadger" + File.separator;
        File var1 = new File(var0);
        if (!var1.exists()) {
            var1.mkdirs();
        }
        return var0;
    }

    /**
     * 返回地图存储目录
     *
     * @return
     */
    public static String getDefaultMapDirectory() {
        String var0 = getDefaultRootDirectory() + "download" + File.separator;
        File var1 = new File(var0);
        if (!var1.exists()) {
            var1.mkdirs();
        }

        return var0;
    }
    /**
     * 返回debug存储目录
     *
     * @return
     */
    public static String getDefaultDebugDirectory() {
        String var0 = getDefaultRootDirectory() + "debug" + File.separator;
        File var1 = new File(var0);
        if (!var1.exists()) {
            var1.mkdirs();
        }

        return var0;
    }

    /**
     * 返回APP根目录
     *
     * @return
     */
    private static String getFilesDir() {
        if (isMounted()) {
//            return INApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath()+File.separator;
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        } else {
            throw new NullPointerException("no memory to save some files !");
        }
    }

    /**
     * 正常挂载
     *
     * @return
     */
    private static boolean isMounted() {
        String var0 = Environment.getExternalStorageState();
        return var0.equals(Environment.MEDIA_MOUNTED);
    }


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static File getStorageDir(String dirName) {
        File file = Environment.getExternalStoragePublicDirectory(dirName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "文件目录创建失败!");
            }
        }
        return file;
    }

    public static void writeObjectToFile(Object obj, File file) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(obj);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object readObjectFromFile(File file) {
        Object obj = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            obj = ois.readObject();
            ois.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }


    public static void writeStringToFile(String s, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(s.getBytes("UTF-8"));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
