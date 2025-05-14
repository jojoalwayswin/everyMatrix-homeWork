package domain;

import utils.CustomHashMap;
import utils.StakeSkipList;

import java.util.concurrent.locks.ReentrantLock;

public class BetOffer {
    private final StakeSkipList  stakeSkipList = new StakeSkipList();
    private final CustomHashMap<Integer, StakeSkipList.Node> stakeMap = new CustomHashMap();
    private final ReentrantLock lock = new ReentrantLock();
    public void putStake(int customerId, int stake){
        lock.lock();
        try {
            // 1. 如果已存在该用户，先删除旧节点
            StakeSkipList.Node node = stakeMap.get(customerId);
            if (node != null) {
                stakeSkipList.deleteByKeyAndValue(node);
                node.setValue(node.getValue()+stake);
                stakeMap.remove(customerId);
            }else {
                node = new StakeSkipList.Node(customerId, stake);
            }
            stakeMap.put(customerId, node);
            stakeSkipList.put(customerId, node.getValue());
        }finally {
            lock.unlock();
        }
    }
    public StakeSkipList.Node[] getTop20(){
        return stakeSkipList.getTopN(20);
    }
}
