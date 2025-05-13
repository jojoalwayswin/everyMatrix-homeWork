package domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BettingOffer {
    // 客户ID到最高赌注的映射
    public final Map<Integer, Integer> customerMaxStakes = new HashMap<>();
    Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    };
    // 跳表：按金额降序排列，每个节点包含客户ID和金额
    public final SkipList<Integer, Integer> stakeSkipList = new SkipList<Integer, Integer>(comparator
    );

}
