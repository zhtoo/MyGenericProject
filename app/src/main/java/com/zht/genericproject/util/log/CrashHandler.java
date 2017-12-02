package com.zht.genericproject.util.log;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import com.zht.genericproject.MyApplication;
import com.zht.genericproject.util.PermissionCheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description: 异常退出管理类
 *
 * @instructions:在MyApplication的onCreate()方法中调用--->CrashHandler.getInstance().init(this);
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;
    private static final String PATH = Environment.getExternalStorageDirectory().toString() + "/MyException";
    //文件名称
    private static final String FILE_NAME = "crash-";
    // log文件的后缀名
    private static final String FILE_NAME_SUFFIX = ".trace";
    // 系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    // 构造方法私有，防止外部构造多个实例，即采用单例模式
    private CrashHandler() {
    }

    //对外提供获取对象的方法
    public static CrashHandler getInstance() {
        return CrashHandlerInstance.instance;
    }

    private static class CrashHandlerInstance {
        static CrashHandler instance = new CrashHandler();
    }

    // 这里主要完成初始化工作
    public void init(Context context) {
        // 获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     * 未被捕获的异常：在程序出现异常的时候APP并未捕获的异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 打印出当前调用栈信息
        ex.printStackTrace();

        // 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (!handleException(ex) && mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);//当前线程休眠3s，方便做异常收集的操作。
            } catch (InterruptedException e) {
                Logger.e(TAG, "error : ", e);
            }
            // 退出程序
            // ActivityUtils.onExit();//在这里处理异常退出时，Activity的一些操作。
            android.os.Process.killProcess(android.os.Process.myPid());//杀死当前进程
            /**
             System.exit(0)和System.exit(1)
             1、都是退出程序
             2、区别：
             system.exit（0）:正常退出，程序正常执行结束退出
             system.exit(1):是非正常退出，就是说无论程序正在执行与否，都退出，
             3、含义：
             System.exit(0)是将你的整个虚拟机里的内容都停掉了 ，
             而dispose()只是关闭这个窗口，但是并没有停止整个application exit() 。
             无论如何，内存都释放了！也就是说连JVM都关闭了，内存里根本不可能还有什么东西

             总结：
             System.exit(0)是正常退出程序，而System.exit(1)或者说非0表示非正常退出程序

             说明：
             System.exit(status)不管status为何值都会退出程序。
             和return 相比有以下不同点：
             return是回到上一层，而System.exit(status)是回到最上层
             */
            System.exit(1);//异常退出
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // 导出异常信息到SD卡中
        try {
            dumpExceptionToSDCard(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
        uploadExceptionToServer();
        return true;
    }

    /**
     * 导出异常信息到SD卡中
     *
     * @param ex
     * @throws IOException
     */
    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        Context context = MyApplication.getContext();
        // 如果没有读写SD卡的权限，则不写入文件
        if (null != context && !PermissionCheck.getInstance().checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }

        // 如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Logger.w(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat(DateFormatter.SS.getValue()).format(new Date(current));
        // 以当前时间创建log文件
        File file = new File(PATH + File.separator + FILE_NAME + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            // 导出发生异常的时间
            pw.println(time);

            // 导出手机信息
            dumpPhoneInfo(pw);
            collectDeviceInfo(pw);

            pw.println();
            // 导出异常的调用栈信息
            ex.printStackTrace(pw);

            pw.close();
        } catch (Exception e) {
            Logger.e(TAG, "dump crash info failed");//转储崩溃信息失败
        }
    }

    /**
     * 手机信息
     *
     * @param pw
     * @throws NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
        // 应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        // 应用版本
        pw.print("App Version Name: ");
        pw.println(pi.versionName);
        // 应用版本号
        pw.print("App Version Code: ");
        pw.println(pi.versionCode);

        // android版本
        pw.print("Android OS Version Name: ");
        pw.println(Build.VERSION.RELEASE);
        // android版本号
        pw.print("Android OS Version Code: ");
        pw.println(Build.VERSION.SDK_INT);

        // 手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        // 手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
    }

    /**
     * 收集设备参数信息
     */
    public void collectDeviceInfo(PrintWriter pw) {
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                pw.print(field.getName() + ": ");
                pw.println(field.get(null).toString());
                Logger.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Logger.e(TAG, "an error occurred when collect crash info", e);
            }
        }
    }

    /**
     * 上传log到服务端
     */
    private void uploadExceptionToServer() {
        // TODO: 2017/10/12  上传异常信息到你的服务器上

    }
}
