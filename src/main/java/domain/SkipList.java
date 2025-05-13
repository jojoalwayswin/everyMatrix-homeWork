package domain;

import java.util.Comparator;

public class SkipList<K, V> {
    private int level = 1;
    private final SkipListNode<K, V> header = new SkipListNode<>(level);
    private final Comparator<K> comparator;


    public SkipList(Comparator<K> comparator) {
        this.comparator = comparator;
    }
    // 插入操作（O(log n)）
    public void put(K key, V value) {
        SkipListNode<K, V>[] update = new SkipListNode[level];
        SkipListNode<K, V> p = header;

        for (int i = level - 1; i >= 0; i--) {
            while (p.next[i] != null && comparator.compare(p.next[i].key, key) < 0) {
                p = p.next[i];
            }
            update[i] = p;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) {
                update[i] = header;
            }
            level = newLevel;
        }

        SkipListNode<K, V> x = new SkipListNode<>(level);
        x.key = key;
        x.value = value;
        x.next = new SkipListNode[newLevel];

        for (int i = 0; i < newLevel; i++) {
            x.next[i] = update[i].next[i];
            update[i].next[i] = x;
        }
    }
    public V get(K key) {
        SkipListNode<K, V> current = header;

        // 从最高层级开始搜索
        for (int i = level - 1; i >= 0; i--) {
            // 向右遍历直到找到等于或大于目标的节点
            while (current.next[i] != null
                    && comparator.compare(current.next[i].key, key) < 0) {
                current = current.next[i];
            }

            // 检查是否找到准确匹配
            if (current.next[i] != null
                    && comparator.compare(current.next[i].key, key) == 0) {
                return current.next[i].value;
            }
        }
        return null; // 未找到
    }
    // 删除操作（O(log n)）
    public boolean remove(K key, V value) {
        SkipListNode[] update = new SkipListNode[level];
        SkipListNode<K, V> p = header;

        for (int i = level - 1; i >= 0; i--) {
            while (p.next[i] != null && comparator.compare(p.next[i].key, key) < 0) {
                p = p.next[i];
            }
            update[i] = p;
        }

        p = p.next[0];
        if (p == null || !p.value.equals(value)) {
            return false;
        }

        for (int i = 0; i < level; i++) {
            if (update[i].next[i] != p) {
                break;
            }
            update[i].next[i] = p.next[i];
        }

        // 调整层级
        while (level > 1 && header.next[level - 1] == null) {
            level--;
        }
        return true;
    }
    // 添加在类中
    private int randomLevel() {
        int lvl = 1;
        while (Math.random() < 0.5 && lvl < level + 1) {
            lvl++;
        }
        return lvl;
    }
    static class SkipListNode<k, v> {
        k key;
        v value;
        SkipListNode<k, v>[] next;
        // 新增构造方法初始化 next 数组
        SkipListNode(int level) {
            this.next = new SkipListNode[level];
        }
    }
    public boolean isEmpty() {
        // 检查头节点最底层（level 0）是否有后继节点
        return header.next[0] == null;
    }
}
