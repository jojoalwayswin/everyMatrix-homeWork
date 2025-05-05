package domain;

/**
 * @author zhangshu
 * @ClassName SessionEntry
 * @description: SessionEntry
 * @date 2025年04月29日
 * @version: 1.0
 */
public class SessionEntry {
    private String sessionKey;
    private int customerId;
    private long createTime;

    public SessionEntry(String sessionKey, int customerId) {
        this.sessionKey = sessionKey;
        this.customerId = customerId;
        this.createTime = System.currentTimeMillis();
    }
    public long getCreateTime() {
        return createTime;
    }

    public String getSessionKey() {
        return sessionKey;
    }
    public int getCustomerId() {
        return customerId;
    }
}