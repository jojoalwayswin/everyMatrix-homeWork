package manager;

import utils.CustomHashMap;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author S10162
 * @ClassName SessionManager
 * @description: SessionManager
 * @date 2025年04月29日
 * @version: 1.0
 */
public class SessionManager {
    // 静态final实例保证唯一性
    private static final SessionManager INSTANCE = new SessionManager();
    public static SessionManager getInstance() {
        return INSTANCE;
    }
    /**
     * concurrent safe session map
     */
    private final CustomHashMap<Integer, SessionNode> customerMap = new CustomHashMap<>();
    private final CustomHashMap<String, Integer> sessionMap = new CustomHashMap<>();
    private final long EXPIRE_TIME = 10*60*1000; // 10 minutes
    private final SessionNode head;
    private final SessionNode tail;
    private final Lock lock = new ReentrantLock(); // smaller lock
    public SessionManager() {
        head = new SessionNode(null, null);
        tail = new SessionNode(null, null);
        head.next = tail;
        tail.prev = head;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::cleanExpiredSessions, 0, 1, TimeUnit.SECONDS);
    }
    public String getSessionKey(int customerId){
        long now = System.currentTimeMillis();
        SessionNode sessionEntry = customerMap.get(customerId);
        if (sessionEntry != null && now - sessionEntry.getCreateTime() <= EXPIRE_TIME){
            return sessionEntry.getSessionKey();
        }
        lock.lock();
        try {
            return createSession(customerId);
        }finally {
            lock.unlock();
        }
    }
    private String createSession(int customerId){
        String sessionKey = UUID.randomUUID().toString().replace("-", "");
        SessionNode sessionEntry = new SessionNode(customerId, sessionKey);
        customerMap.put(customerId, sessionEntry);
        sessionMap.put(sessionKey, customerId);
        addNode(sessionEntry);
        return sessionKey;

    }
    public Integer invalidateSession(String sessionKey){
        Integer customerId = sessionMap.get(sessionKey);
        if (customerId == null) {
            return null;
        }
        SessionNode sessionEntry = customerMap.get(customerId);
        // 检查是否过期（基于最后访问时间）
        if (System.currentTimeMillis() - sessionEntry.getCreateTime() > EXPIRE_TIME) {
            lock.lock();
            try {
                removeNode(sessionEntry);
            }finally {
                lock.unlock();
            }
            return null;
        }
        return sessionEntry.getCustomerId();
    }
    private void addNode(SessionNode node) {
        node.next = tail;
        node.prev = tail.prev;
        tail.prev.next = node;
        tail.prev = node;
    }
    private void removeNode(SessionNode node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
        sessionMap.remove(node.sessionKey);
        customerMap.remove(node.customerId);
    }

    /**
     *  clean expired sessions
     */
    private void cleanExpiredSessions() {
        lock.lock();
        try {
            SessionNode current = head.next;
            while (current != tail) {
                SessionNode next = current.next;
                if (isExpired(current)) {
                    removeNode(current);
                }
                current = next;
            }
        } finally {
            lock.unlock();
        }
    }
    private boolean isExpired(SessionNode node) {
        return System.currentTimeMillis() > node.createTime + EXPIRE_TIME;
    }
    static class SessionNode {
        Integer customerId;
        String sessionKey;
        long createTime;
        SessionNode prev;
        SessionNode next;
        public SessionNode(Integer customerId, String sessionKey) {
            this.customerId = customerId;
            this.sessionKey = sessionKey;
            this.createTime = System.currentTimeMillis();
            this.prev = null;
            this.next = null;
        }
        public int getCustomerId() {
            return customerId;
        }
        public String getSessionKey() {
            return sessionKey;
        }
        public long getCreateTime() {
            return createTime;
        }
    }
}