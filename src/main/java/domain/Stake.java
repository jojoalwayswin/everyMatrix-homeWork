package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangshu
 * @ClassName Stake
 * @description: Stake map
 * @date 2025年04月30日
 * @version: 1.0
 */
public class Stake {
    private static final Map<Integer, BettingOffer> bettingOffers = new HashMap<>();
    public static void placeStake(Integer offerId, Integer customerId, Integer amount) {
        // 获取或创建投注选项
        BettingOffer offer = bettingOffers.computeIfAbsent(offerId, k -> new BettingOffer);

        // 更新客户最高赌注（哈希表）
        Integer currentMax = offer.customerMaxStakes.getOrDefault(customerId, 0);
        if (amount.compareTo(currentMax) > 0) {
            offer.customerMaxStakes.put(customerId, amount);

            // 更新跳表：先删除旧值（如有），再插入新值
            Integer oldAmount = currentMax;
            if (oldAmount.compareTo(0) > 0) {
                offer.stakeSkipList.remove(oldAmount, customerId);
            }
            offer.stakeSkipList.put(amount, customerId);
        }
    }
    public static String getTopStakes(Long offerId, Integer limit) {
        BettingOffer offer = bettingOffers.get(offerId);
        if (offer == null || offer.stakeSkipList.isEmpty()) {
            return ""; // 无数据时返回空字符串
        }

        // 获取前20个金额

        Integer[] top20Amounts = new Integer[limit];
        int count = 0;
        while (iterator.hasNext && count < 20) {
            Map.Entry<BigDecimal, Long> entry = iterator.next;
            topStakes.add(entry.getKey);
            count++;
        }

        // 拼接结果
        return topStakes.stream
                .map(BigDecimal::toString)
                .collect(Collectors.joining(","));
    }
}