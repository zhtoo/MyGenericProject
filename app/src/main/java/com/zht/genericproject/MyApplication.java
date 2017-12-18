package com.zht.genericproject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.zht.genericproject.gesturelock.logic.LockLogic;
import com.zht.genericproject.listener.GestureLockWatcher;
import com.zht.genericproject.util.log.CrashHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局单例 APP启动时最先运行。
 * Created by zhanghaitao on 2017/5/18.
 *
 * 功能：数据传递，数据共享，数据缓存等操作。
 * 注意：当APP长期在后台的时候，该类很可能被kill来保证系统的内存。
 * App从后台切换到前台的时候，被kill的Application会被重新创建。
 * 因此，获取数据的时候需要做null判断；在该类不适合做
 * <p>
 * 1.当两个Activity要传输大数据的时候，应尽量避免使用Intent传输，
 * 而是在Application里面建立HashMap，
 * 然后发送方Activity把数据放到Application的HashMap里面，
 * 然后把数据的索引通过Intent传输到接收方Activity。
 * 接收方Activity就可以从Application里面获取到发送方的传递的数据。
 * 2.当从网络上下载的数据，可以将它暂时放到Application的HashMap里面，
 * 当app再次请求数据的时候如果发现Application里面已经有数据了，
 * 就不用再次从网上下载。
 * 3.app的内存是有限制的，所以如果缓存的数据太多，
 * 应该要将缓存数据写入到本地的rom或者是sdcard上。、
 * 4.Application是静态的，因此有一些对象和数据最后不要放到Application的引用。
 * Application的生命周期很长，
 * 假如说，如果Application有某一个Activity的控件引用时，
 * 则当Activity想要finish的时候，内存会释放不了，造成内存泄漏。
 * <p>
 * // long elapsedCpuTime = Process.getElapsedCpuTime();
 * // int myPid = Process.myPid();//获取该进程的ID
 * // int mMainThreadId = Process.myTid();//获取该线程的ID
 * // int myUid = Process.myUid();//获取该进程的用户ID
 * // boolean isSupportsProcesses = Process.supportsProcesses();//判断该进程是否支持多进程
 * <p>
 * // Process.getThreadPriority(mMainThreadId);//获取指定ID的线程的优先级。
 * <p>
 * //        Logger.e(TAG, "当前线程的优先级：" + Process.getThreadPriority(mMainThreadId));
 * //        Logger.e(TAG, "当前线程的线程的ID：" + Process.myTid());
 * //        Logger.e(TAG, "当前进程的ID：" + Process.myPid());
 * //        Logger.e(TAG, "当前进程的用户ID：" + Process.myUid());
 * //        Logger.e(TAG, Process.supportsProcesses() ? "支持多进程" : "不支持多进程");
 * //        Logger.e(TAG, "当前系统中数据文件夹环境变量：" + android.os.Environment.getDataDirectory());
 * //        Logger.e(TAG, "当前系统中下载缓存文件环境变量：" + android.os.Environment.getDownloadCacheDirectory());
 * //        Logger.e(TAG, "当前系统中外部存储文件环境变量：" + android.os.Environment.getExternalStorageDirectory());
 * //        Logger.e(TAG, "当前系统中根文件环境变量：" + android.os.Environment.getRootDirectory());
 * <p>
 * 常用语句：
 * android.os.Process
 * //获取当前进程的方法
 * android.os.Process.getElapsedCpuTime()：获取消耗的时间。
 * android.os.Process.myPid()：获取该进程的ID。
 * android.os.Process.myTid()：获取该线程的ID。
 * android.os.Process.myUid()：获取该进程的用户ID。
 * android.os.Process.supportsProcesses：判断该进程是否支持多进程。
 * <p>
 * // 获取/设置线程优先级
 * getThreadPriority(int tid)：
 * setThreadPriority(int priority)：设置当前线程的优先级。priority：-20 --->19 ，高优先级 ---> 低优先级。
 * setThreadPriority(int tid,int priority)：设置指定ID的线程的优先级。
 * <p>
 * //  优先级：
 * //  android.os.Process.THREAD_PRIORITY_AUDIO //标准音乐播放使用的线程优先级   -16
 * //  android.os.Process.THREAD_PRIORITY_BACKGROUND //标准后台程序      10
 * //  android.os.Process.THREAD_PRIORITY_DEFAULT// 默认应用的优先级    0
 * //  android.os.Process.THREAD_PRIORITY_DISPLAY//标准显示系统优先级，主要是改善UI的刷新     -4
 * //  android.os.Process.THREAD_PRIORITY_FOREGROUND //标准前台线程优先级             -2
 * //  android.os.Process.THREAD_PRIORITY_LESS_FAVORABLE //低于favorable               1
 * //  android.os.Process.THREAD_PRIORITY_LOWEST //有效的线程最低的优先级             19
 * //  android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE //高于favorable              -1
 * //  android.os.Process.THREAD_PRIORITY_URGENT_AUDIO //标准较重要音频播放优先级       -19
 * //  android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY //标准较重要显示优先级（输入事件也适用）。-8
 * <p>
 * //管理进程
 * //killProcess(int pid)：杀死指定的进程。
 * //sendSignal(int pid,int singal)：向指定的进程发送信号。
 * <p>
 * android.os.Environment
 * //获取系统环境变量
 * //getDataDirectory()：获取当前系统中数据文件夹环境变量。
 * //getDownloadCacheDirectory()：获取当前系统中下载缓存文件环境变量。
 * //getExternalStorageDirectory()：获取当前系统中外部存储文件环境变量。
 * //getRootDirectory()：获取当前系统中根文件环境变量。
 */
public class MyApplication extends MultiDexApplication {

    private final  String TAG =getClass().getSimpleName();

    /**全局的Context*/
    private static Context mContext;
    /**主线程的handler*/
    private static Handler mMainThreadHandler;
    /**主线程id*/
    private static int mMainThreadId;
    /**用于内存缓存的Map*/
    private Map<String, String> mMemProtocolCacheMap = new HashMap<>();
    /** 用于监听APP是否到后台*/
    private static GestureLockWatcher watcher;

    @Override
    public void onCreate() {//程序的入口方法
        //上下文
        mContext = getApplicationContext();
        //主线程的Handler
        mMainThreadHandler = new Handler();
        //主线程的线程id
        mMainThreadId = android.os.Process.myTid();
        // 抓取异常LOG，保存在本地
         CrashHandler.getInstance().init(this);

        // 监听APP是否到后台
        watcher = new GestureLockWatcher(this);
        watcher.setOnScreenPressedListener(new GestureLockWatcher.OnScreenPressedListener() {
            @Override
            public void onPressed() {
                LockLogic.getInstance().start();
            }
        });
        watcher.startWatch();

        int threadPriorityBackground = Process.THREAD_PRIORITY_BACKGROUND;

        super.onCreate();
    }

    /**
      当整个系统运行在内存不足的状态时会调用这个函数，而主动运行的进程应该修改内存的使用。
      虽然没有定义这个被调用的确切点，但通常在所有的后台进程都被终止的时候会发生。
      也就是说，在达到我们希望避免杀死的托管服务和前台UI的处理点之前。

     你应该实现这个方法来释放任何缓存或其他不必要的资源。
     从这个方法返回后，系统会为你执行垃圾回收。

     最好，你应该实现onTrimMemory(int)从 ComponentCallbacks2基于各种级别的内存需求逐步卸下你的资源。
     该API可用于API级别14和更高版本，因此您应该只使用此onLowMemory()方法作为旧版本的后备，
     这可以onTrimMemory(int)与TRIM_MEMORY_COMPLETE级别一样对待。

     如果您重写此方法，则必须调用super实现。
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }



    /**
     * 获取监听APP是否到后台的监听器
     *
     * @return
     */
    public static GestureLockWatcher getWatcher() {
        return watcher;
    }

    /**
     * 获取MEM协议缓存集合
     *
     * @return:Map集合
     */
    public Map<String, String> getMemProtocolCacheMap() {
        return mMemProtocolCacheMap;
    }

    /**
     * 得到上下文
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * 得到主线程里面的创建的一个hanlder
     *
     * @return
     */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /**
     * 得到主线程的线程id
     *
     * @return
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /**
     * 获取资源文件中颜色的int值
     *
     * @return
     */
    public static int getcolor(int id) {
        int color = getContext().getResources().getColor(id);
        return color;
    }


    /**
     * 存放Activity的对象
     */
    private static Map<String, Activity> destoryMap = new HashMap<>();

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            destoryMap.get(key).finish();
        }
    }

    /**
     * 将Activity添加到集合中
     *
     * @param activity
     * @param activityName
     */
    public static void addActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 获取到某个Activity
     *
     * @param activityName
     * @return
     */
    public static Activity getActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        for (String key : keySet) {
            return destoryMap.get(key);
        }
        return null;
    }

}

