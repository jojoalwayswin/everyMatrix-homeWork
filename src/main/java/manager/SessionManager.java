package manager;

import domain.SessionEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

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
    private final ConcurrentHashMap<Integer, SessionEntry> sessionMap = new ConcurrentHashMap<>();
    private final long EXPIRE_TIME = 10*60*1000; // 10 minutes

    public SessionManager() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::cleanExpiredSessions, 0, 1, TimeUnit.SECONDS);
    }
    public String getSessionKey(int customerId){
        long now = System.currentTimeMillis();
        SessionEntry sessionEntry = sessionMap.get(customerId);
        if (sessionEntry != null && now - sessionEntry.getCreateTime() <= EXPIRE_TIME){
            return sessionEntry.getSessionKey();
        }
        String sessionKey = UUID.randomUUID().toString().replace("-", "");
        SessionEntry  newSessionEntry = new SessionEntry(sessionKey, customerId);
        sessionMap.put(customerId, newSessionEntry);
        return sessionKey;
    }

    public Integer invalidateSession(String sessionKey){
        for (SessionEntry entry : sessionMap.values()){
            if (entry.getSessionKey().equals(sessionKey)){
                long now = System.currentTimeMillis();
                if (now - entry.getCreateTime() <= EXPIRE_TIME){
                    return entry.getCustomerId();
                }else {
                    sessionMap.remove(entry.getCustomerId());
                }

            }
        }
        return null;
    }

    /**
     *  clean expired sessions
     */
    private void cleanExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        List<Integer> expiredSessions = new ArrayList<>();
        for (SessionEntry entry : sessionMap.values()) {
            if (currentTime - entry.getCreateTime() > EXPIRE_TIME) {
                expiredSessions.add(entry.getCustomerId());
            }
        }
        expiredSessions.forEach(sessionMap::remove);
    }

}