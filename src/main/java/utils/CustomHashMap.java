package utils;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zhangshu
 * @ClassName CustomHashMap
 * @description: custom hashmap , concurrent safe,the time complexity of put and get method is O(1)
 * @date 2025年05月13日
 * @version: 1.0
 */
public class CustomHashMap <K,V> {
    public static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        public K getKey() {
            return key;
        }
        public V getValue() {
            return value;
        }
        public Entry<K, V> getNext() {
            return next;
        }
    }

    // 哈希桶数组
    private Entry<K, V>[] buckets;
    private int size;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    // INITIAL_CAPACITY
    private static final int INITIAL_CAPACITY = 16;
    // LOAD_FACTOR
    private static final float LOAD_FACTOR = 0.75f;

    public CustomHashMap() {
        buckets = new Entry[INITIAL_CAPACITY];
        size = 0;
    }

    // hash
    private int hash(K key) {
        return (key == null) ? 0 : Math.abs(key.hashCode()) % buckets.length;
    }
    /**
     * put
     */
    public void put(K key, V value) {
        writeLock.lock();
        try {
            int index = hash(key);
            Entry<K, V> entry = buckets[index];
            while (entry != null) {
                if ((key == null && entry.key == null) || (key != null && key.equals(entry.key))) {
                    entry.value = value; // if key exists, update value
                    return;
                }
                entry = entry.next;
            }

            // put new entry
            Entry<K, V> newEntry = new Entry<>(key, value);
            newEntry.next = buckets[index];
            buckets[index] = newEntry;
            size++;

            // resize
            if (size > buckets.length * LOAD_FACTOR) {
                resize();
            }
        } finally {
            writeLock.unlock();
        }
    }
    /**
     * get
     */
    public V get(K key) {
        readLock.lock();
        try {
            int index = hash(key);
            Entry<K, V> entry = buckets[index];
            while (entry != null) {
                if ((key == null && entry.key == null) || (key != null && key.equals(entry.key))) {
                    return entry.value;
                }
                entry = entry.next;
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }
    /**
     * 删除指定键
     */
    public void remove(K key) {
        writeLock.lock();
        try {
            int index = hash(key);
            Entry<K, V> prev = null;
            Entry<K, V> current = buckets[index];

            while (current != null) {
                if ((key == null && current.key == null) || (key != null && key.equals(current.key))) {
                    if (prev == null) {
                        buckets[index] = current.next; // 删除头节点
                    } else {
                        prev.next = current.next; // 删除中间或尾节点
                    }
                    size--;
                    return;
                }
                prev = current;
                current = current.next;
            }
        } finally {
            writeLock.unlock();
        }
    }
    /**
     * 检查是否包含指定键
     */
    public boolean containsKey(K key) {
        readLock.lock();
        try {
            int index = hash(key);
            Entry<K, V> entry = buckets[index];
            while (entry != null) {
                if ((key == null && entry.key == null) || (key != null && key.equals(entry.key))) {
                    return true;
                }
                entry = entry.next;
            }
            return false;
        } finally {
            readLock.unlock();
        }
    }
    /**
     * 获取当前键值对数量
     */
    public int size() {
        readLock.lock();
        try {
            return size;
        } finally {
            readLock.unlock();
        }
    }
    /**
     * 扩容：将桶数组大小翻倍，并重新哈希所有元素
     */
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        buckets = new Entry[oldBuckets.length * 2];
        size = 0;

        for (Entry<K, V> oldEntry : oldBuckets) {
            Entry<K, V> current = oldEntry;
            while (current != null) {
                put(current.key, current.value); // 重新插入
                current = current.next;
            }
        }
    }

}
