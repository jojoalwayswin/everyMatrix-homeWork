package domain;

import utils.CustomHashMap;

/**
 * @author zhangshu
 * @ClassName Stake
 * @description: Stake map
 * @date 2025年04月30日
 * @version: 1.0
 */
public class Stake {
    public static CustomHashMap<Integer, BetOffer> stakeMap = new CustomHashMap<>();
    public static BetOffer getBetOffer(int betOfferId) {
        return stakeMap.get(betOfferId);
    }
}