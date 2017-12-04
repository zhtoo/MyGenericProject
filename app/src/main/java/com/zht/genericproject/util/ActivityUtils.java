package com.zht.genericproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zht.genericproject.MyApplication;
import com.zht.genericproject.util.log.Logger;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * @DES:ACTIVITY管理类
 * 需要要注意的是：堆栈中需要该Activity存在。
 * 也就是它并不能完成跳转需要创建的Activity,
 * 如果要跳转，就需要在Activity的onCreate（）方法中加上
 * ActivityUtils.push(this); 将Activity push 到栈中
 */
public final class ActivityUtils {
    private static final String TAG   = "ActivityUtils";
    // 堆栈管理对象
    private static final ActivityStack STACK = new ActivityStack();

    /**
     * 将activity推到堆栈中
     */
    public static void push(Activity activity) {
        Logger.i(TAG, "push = " + activity);
        STACK.pushToStack(activity);
    }

    /**
     * 从堆栈中弹出顶部activity
     */
    public static void pop() {
        Activity activity = STACK.popFromStack();
        activity.finish();
        Logger.i(TAG, "pop = " + activity);
    }

    /**
     * 从堆栈中删除该activity，可能是null
     */
    public static void remove(Activity activity) {
        Logger.i(TAG, "remove = " + activity);
        STACK.removeFromStack(activity);
    }

    /**
     * peek top activity from stack, maybe is null
     */
    public static Activity peek() {
        Activity activity = STACK.peekFromStack();
        Logger.i(TAG, "peek = " + activity);
        return activity;
    }

    /**
     * pop activities until this Activity
     */
    @SuppressWarnings("unchecked")
    public static <T extends Activity> T popUntil(final Class<T> clazz) {
        if (clazz != null) {
            while (!STACK.isEmpty()) {
                final Activity activity = STACK.popFromStack();
                if (activity != null) {
                    if (clazz.isAssignableFrom(activity.getClass())) {
                        return (T) activity;
                    }
                    activity.finish();
                }
            }
        }
        return null;
    }

    /**
     * 最后一次尝试退出的时间戳
     */
    private static       long lastExitPressedMills  = 0;
    /**
     * 距上次尝试退出允许的最大时间差
     */
    private static final long MAX_DOUBLE_EXIT_MILLS = 2000;

    /**
     * 退出APP
     */
    public static void onExit() {
        final long now = System.currentTimeMillis();
        if (now <= lastExitPressedMills + MAX_DOUBLE_EXIT_MILLS) {
            finishAll();
            MyApplication.getWatcher().stopWatch();
            System.exit(0);
        } else {
            Context context = peek();
            if (context != null) {
                Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            }
            lastExitPressedMills = now;
        }
    }

    /**
     * 退出APP
     */
//    public static void onExitFrg(MainVM viewmodel) {
//        final long now = System.currentTimeMillis();
//        if (now <= lastExitPressedMills + MAX_DOUBLE_EXIT_MILLS) {
//            viewmodel.clearFrg();
//            finishAll();
//            MyApplication.getInstance().watcher.stopWatch();
//            System.exit(0);
//        } else {
//            Context context = peek();
//            if (context != null) {
//                Toast.makeText(context, context.getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
//            }
//            lastExitPressedMills = now;
//        }
//    }


    /**
     * 当APP退出的时候，结束所有Activity
     */
    private static void finishAll() {
        Logger.i(TAG, "********** Exit **********");
        while (!STACK.isEmpty()) {
            final Activity activity = STACK.popFromStack();
            if (activity != null) {
                Logger.i(TAG, "Exit = " + activity);
                activity.finish();
            }
        }
    }

    /**
     * activity堆栈，用以管理APP中的所有activity
     */
    private static class ActivityStack {
        // activity堆对象
        private final Stack<WeakReference<Activity>> activityStack = new Stack<>();

        /**
         * @return 堆是否为空
         */
        public boolean isEmpty() {
            return activityStack.isEmpty();
        }

        /**
         * 向堆中push此activity
         */
        public void pushToStack(Activity activity) {
            activityStack.push(new WeakReference<>(activity));
        }

        /**
         * @return 从堆栈中pop出一个activity对象
         */
        public Activity popFromStack() {
            while (!activityStack.isEmpty()) {
                final WeakReference<Activity> weak     = activityStack.pop();
                final Activity activity = weak.get();
                if (activity != null) {
                    return activity;
                }
            }
            return null;
        }

        /**
         * @return 从堆栈中查看一个对象，且不会pop
         */
        public Activity peekFromStack() {
            while (!activityStack.isEmpty()) {
                final WeakReference<Activity> weak     = activityStack.peek();
                final Activity activity = weak.get();
                if (activity != null) {
                    return activity;
                } else {
                    activityStack.pop();
                }
            }
            return null;
        }

        /**
         * @return 从堆栈中删除指定对象
         */
        public boolean removeFromStack(Activity activity) {
            for (WeakReference<Activity> weak : activityStack) {
                final Activity act = weak.get();
                if (act == activity) {
                    return activityStack.remove(weak);
                }
            }
            return false;
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // 启动activity
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 启动新的Activity
     */
    public static void push(Activity a, Class<? extends Activity> clazz, Intent intent, int requestCode) {
        Logger.w(TAG, a.getClass().getSimpleName() + " -> " + clazz.getSimpleName());
        intent = getIntent(a, clazz, intent);
        if (requestCode >= 0) {
            a.startActivityForResult(intent, requestCode);
        } else {
            a.startActivity(intent);
        }
    }

    public static void push(Class<? extends Activity> clazz, Intent intent) {
        push(peek(), clazz, intent, -1);
    }

    public static void push(Activity a, Class<? extends Activity> clazz, Intent intent) {
        push(a, clazz, intent, -1);
    }

    public static void push(Class<? extends Activity> clazz, Intent intent, int code) {
        push(peek(), clazz, intent, code);
    }

    public static void push(Class<? extends Activity> clazz) {
        push(peek(), clazz, null, -1);
    }

    public static void push(Activity a, Class<? extends Activity> clazz) {
        push(a, clazz, null, -1);
    }

    public static void push(Activity a, Class<? extends Activity> clazz, int code) {
        push(a, clazz, null, code);
    }

    /**
     * 根据入参，获得intent
     */
    private static Intent getIntent(Activity a, Class<? extends Activity> clazz, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(a, clazz);
        return intent;
    }

    public static void pop(final Activity a) {
        pop(a, null);
    }

    public static void pop(final Activity a, int code) {
        pop(a, code, null);
    }

    public static void pop(final Activity a, Intent intent) {
        pop(a, Activity.RESULT_OK, intent);
    }

    /**
     * 关闭Activity
     */
    public static void pop(final Activity a, int code, Intent intent) {
        if (intent != null) {
            a.setResult(code, intent);
        }
        a.finish();
    }
}
