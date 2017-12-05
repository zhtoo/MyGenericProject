package com.zht.genericproject.DataStorage;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Description: 软引用map
 * 1.  SoftReference<T>：软引用
 * -->当虚拟机内存不足时，将会回收它指向的对象；
 * 需要获取对象时，可以调用get方法。
 * 2.  WeakReference<T>：弱引用
 * -->随时可能会被垃圾回收器回收，不一定要等到虚拟机内存不足时才强制回收。
 * 要获取对象时，同样可以调用get方法。
 * <p>
 * 对比：
 * WeakReference一般用来防止内存泄漏，要保证内存被虚拟机回收，
 * SoftReference多用作来实现缓存机制(cache);
 */
public class SoftMap<K, V> {
    private SoftReference<HashMap<K, V>> innerMap = new SoftReference<>(null);

    /**
     * 向map中存入Key-Value
     */
    public void put(K k, V v) {
        HashMap<K, V> map = innerMap.get();
        if (map == null) {
            map = new HashMap<>();
            map.put(k, v);
            innerMap = new SoftReference<>(map);
        } else {
            map.put(k, v);
        }
    }

    /**
     * 从map中获取Key对应的Value
     */
    public V get(K k) {
        final HashMap<K, V> map = innerMap.get();
        if (map != null) {
            return map.get(k);
        }
        return null;
    }

    /**
     * 从map中删除该Key对应的value
     */
    public void remove(K k) {
        final HashMap<K, V> map = innerMap.get();
        if (map != null && map.containsKey(k)) {
            map.remove(k);
        }
    }

    /**
     * map中是否有此键值对
     */
    public boolean containsKey(K k) {
        HashMap<K, V> map = innerMap.get();
        return map != null && map.containsKey(k);
    }
}
