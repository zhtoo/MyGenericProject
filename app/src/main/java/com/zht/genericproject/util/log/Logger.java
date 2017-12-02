package com.zht.genericproject.util.log;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.zht.genericproject.MyApplication;
import com.zht.genericproject.util.PermissionCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:日志打印类
 */
public final class Logger {
    private static final String TAG = "Logger";

    //每次截取的长度
    private static int printLength = 4000;
    //保存的路径
    private static final String SAVE_PATH;
    protected static final String LOG_PREFIX;
    protected static final String LOG_DIR;
    // Log switch open - development; close - released
    //日子的开关
    public static boolean debug = true;
    // Write file level
    // 存储为文件的异常信息的级别
    public static int level = Log.ERROR;

    static {
        SAVE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        LOG_PREFIX = setLogPrefix("");//设置文件名的前缀
        LOG_DIR = setLogPath("");//设置保存路径
    }

    /**
     * 复写Log的打印方法
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        piecewisePrint(Log.VERBOSE, tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        piecewisePrint(Log.VERBOSE, tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        piecewisePrint(Log.DEBUG, tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        piecewisePrint(Log.DEBUG, tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        piecewisePrint(Log.INFO, tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        piecewisePrint(Log.INFO, tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        piecewisePrint(Log.WARN, tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        piecewisePrint(Log.WARN, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        piecewisePrint(Log.ERROR, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        piecewisePrint(Log.ERROR, tag, msg, tr);
    }


    /**
     * 过长分段打印
     *
     * @param type 打印类型
     * @param tag
     * @param msg
     */
    private static void piecewisePrint(int type, String tag, String msg, final Throwable tr) {
        if (!TextUtils.isEmpty(msg) && msg.length() > printLength) {
            for (int i = 0; i < msg.length(); i += printLength) {
                if (i + printLength < msg.length()) {
                    trace(type, tag + i + "-" + (i + printLength), msg.substring(i, i + printLength), tr);
                } else {
                    trace(type, tag + i + "-" + msg.length(), msg.substring(i, msg.length()), tr);
                }
            }
        } else {
            trace(type, tag, TextUtils.isEmpty(msg) ? "空数据" : msg, tr);
        }
    }

    private static void piecewisePrint(int type, String tag, String msg) {
        if (!TextUtils.isEmpty(msg) && msg.length() > printLength) {
            for (int i = 0; i < msg.length(); i += printLength) {
                if (i + printLength < msg.length()) {
                    trace(type, tag + i + "-" + (i + printLength), msg.substring(i, i + printLength), null);
                } else {
                    trace(type, tag + i + "-" + msg.length(), msg.substring(i, msg.length()), null);
                }
            }
        } else {
            trace(type, tag, msg, null);
        }
    }

    /**
     * 自定义日志输出样式
     *
     * @param type Log type
     * @param tag  TAG
     * @param msg  Log message
     */
    private static void trace(final int type, final String tag, final String msg, final Throwable tr) {
        // LogCat
        if (debug) {
            switch (type) {
                case Log.VERBOSE:
                    Log.v(tag, msg);
                    break;
                case Log.DEBUG:
                    Log.d(tag, msg);
                    break;
                case Log.INFO:
                    Log.i(tag, msg);
                    break;
                case Log.WARN:
                    Log.w(tag, msg);
                    break;
                case Log.ERROR:
                    Log.e(tag, msg);
                    break;
            }
        }
        //  写入文件
        if (type >= level) {
            writeLog(type, tr == null ? msg : msg + '\n' + Log.getStackTraceString(tr));
        }
    }

    /**
     * 将日志文件写入SDCard
     */
    private static void writeLog(int type, String msg) {
        Context context = MyApplication.getContext();
        // 如果没有读写SD卡的权限，则不写入文件
        if (null != context && !PermissionCheck.getInstance().checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        try {
            SparseArray<String> logMap = new SparseArray<>();
            logMap.put(Log.VERBOSE, " VERBOSE ");
            logMap.put(Log.DEBUG, " DEBUG ");
            logMap.put(Log.INFO, " INFO ");
            logMap.put(Log.WARN, " WARN ");
            logMap.put(Log.ERROR, " ERROR ");

            final StackTraceElement tag = new Throwable().fillInStackTrace().getStackTrace()[2]; // TODO: ...

            msg = new StringBuilder()
                    .append("\r\n")
                    .append(getDateFormat(DateFormatter.SS.getValue()))
                    .append(logMap.get(type)).append(tag.getClassName())
                    .append(" - ").append(tag.getMethodName()).append("(): ")
                    .append(msg).toString();

            final String fileName = new StringBuffer()
                    .append(LOG_PREFIX)
                    .append(getDateFormat(DateFormatter.DD.getValue()))
                    .append(".log").toString();

            recordLog(LOG_DIR, fileName, msg, true);

        } catch (Exception e) {
            Logger.e("Logger: ", e.getMessage());
        }
    }

    /**
     * 写日志
     *
     * @param logDir   日志路径保存
     * @param fileName 日志文件的名字
     * @param msg      日志内容
     * @param append   保存为类型，错误覆盖保存，在文件添加保存之前是正确的
     */
    private static void recordLog(String logDir, String fileName, String msg, boolean append) {
        try {
            //创建文件
            createDir(logDir);

            final File saveFile = new File(new StringBuffer()
                    .append(logDir)
                    .append("/")
                    .append(fileName).toString());

            if (!append && saveFile.exists()) {
                saveFile.delete();
                saveFile.createNewFile();
                write(saveFile, msg, append);
            } else if (append && saveFile.exists()) {
                write(saveFile, msg, append);
            } else if (append && !saveFile.exists()) {
                saveFile.createNewFile();
                write(saveFile, msg, append);
            } else if (!append && !saveFile.exists()) {
                saveFile.createNewFile();
                write(saveFile, msg, append);
            }
        } catch (IOException e) {
            recordLog(logDir, fileName, msg, append);
        }
    }

    /**
     * 获取日期的样式
     * @param pattern
     * @return
     */
    private static String getDateFormat(String pattern) {
        final DateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    /**
     * 创建文件夹
     * @param dir
     * @return
     */
    private static File createDir(String dir) {
        final File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 将信息写入文件
     */
    private static void write(final File file, final String msg, final boolean append) {

        new SafeAsyncTask<Void>() { //开启一个一部任务栈
            @Override
            public Void call() throws Exception {
                final FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file, append);
                    try {
                        fos.write(msg.getBytes());
                    } catch (IOException e) {
                        Logger.e(TAG, "write fail!!!", e);
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                Logger.d(TAG, "Exception closing stream: ", e);
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    Logger.e(TAG, "write fail!!!", e);
                }
                return null;
            }
        }.execute();//开启任务
    }

    ///////////////////////////////////////////////////////////////////////////
    // 日志配置方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置日志文件名前缀
     *
     * @param prefix  示例：(prefix-20121212.log)
     */
    public static String setLogPrefix(final String prefix) {
        return prefix.length() == 0 ? "logger-" : prefix + "-";
    }

    /**
     * 设置日志文件存放路径
     *
     * @param subPath 示例：子路径("/Downloads/subPath")
     */
    public static String setLogPath(final String subPath) {
        return subPath.length() == 0 ? SAVE_PATH + "/logs" : subPath;
    }
}
