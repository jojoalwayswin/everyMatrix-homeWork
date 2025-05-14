package domain;

import utils.StakeSkipList;

import java.util.concurrent.locks.ReentrantLock;

public class BetOffer {
    private final StakeSkipList  stakeSkipList = new StakeSkipList();
    private final ReentrantLock lock = new ReentrantLock();
    public void putStake(int customerId, int stake){
        lock.lock();
        try {
            stakeSkipList.put(customerId, stake);
        }finally {
            lock.unlock();
        }
    }
    public StakeSkipList.Node[] getTop20(){
        return stakeSkipList.getTopN(20);
    }
}
