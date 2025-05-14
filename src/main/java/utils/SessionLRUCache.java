package utils;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zhangshu
 * @ClassName SessionLRUCache
 * @description: session LRU Cache,
 * @date 2025年05月13日
 * @version: 1.0
 */
public class SessionLRUCache {
    // use custom hashmap for quick lookup , the time complexity is O(1)
    private final CustomHashMap<Integer, SessionNode> table = new CustomHashMap<>();
    // LRU 双向链表头尾哨兵节点
    private final SessionNode head = new SessionNode(-1, null);
    private final SessionNode tail = new SessionNode(-1, null);
    private static final long expireTime = 10 * 60 * 1000; // 10 minutes

    private final ReadWriteLock lock = new ReentrantReadWriteLock(); // smaller lock

    public SessionLRUCache() {
        head.next = tail;
        tail.prev = head;
    }


    public String getSessionKey(int customerId) {
        long now = System.currentTimeMillis();

        // try to read first
        lock.readLock().lock();
        try {
            if (table.containsKey(customerId)) {
                SessionNode node = table.get(customerId);
                if (now - node.createTime <= expireTime) {
                    // moveToHead(node); // update least recently used
                    return node.sessionKey;
                } else {
                    // if session expired,  remove it
                    removeNode(node);
                    table.remove(customerId);
                }
            }
        }finally {
                lock.readLock().unlock();
        }
        // if  not found, try write
        lock.writeLock().lock();
        try {
            // 创建新 Session
            String sessionKey = UUID.randomUUID().toString().replace("-", "");
            SessionNode newNode = new SessionNode(customerId, sessionKey);

            addAfterHead(newNode);
            table.put(customerId, newNode);
            return sessionKey;
        } finally {
            lock.writeLock().unlock();
        }
    }
    private void moveToHead(SessionNode node) {
        removeNode(node);
        addAfterHead(node);
    }

    private void removeNode(SessionNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addAfterHead(SessionNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    static class SessionNode {
        int customerId;
        String sessionKey;
        long createTime;
        SessionNode prev;
        SessionNode next;
        public SessionNode(int customerId, String sessionKey) {
            this.customerId = customerId;
            this.sessionKey = sessionKey;
            this.createTime = System.currentTimeMillis();
            this.prev = null;
            this.next = null;
        }
    }



}
