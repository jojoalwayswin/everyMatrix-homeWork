package utils;

import java.util.Random;

/**
 * @author zhangshu
 * @ClassName  StakeSkipList
 * @description: use skip list to store stake
 * @date 2025年05月13日
 * @version: 1.0
 */
public class StakeSkipList{
    private Node head;
    private int maxLevel ;
    private Random random;
    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5;

    public StakeSkipList() {
        head = new Node();
        head.next = new Node[MAX_LEVEL];
        maxLevel = 1;
        random = new Random();
    }

    public static class Node {
        int key;
        int value;
        Node[] next;
        int level;

        public Node() {}
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
        public int getKey() {
            return key;
        }
        public int getValue() {
            return value;
        }
        public void setKey(int key) {
            this.key = key;
        }
        public void setValue(int value) {
            this.value = value;
        }

    }
    private int randomLevel() {
        int level = 1;
        while (random.nextDouble() < P && level < maxLevel) {
            level++;
        }
        return level;
    }
    public void put(int key, int value) {
        Node current = head;
        Node[] update = new Node[maxLevel];

        // 1. 查找每一层的插入位置
        for (int i = maxLevel - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].value > value) {
                current = current.next[i];
            }
            update[i] = current;
        }

        // 2. 随机生成层级
        int newLevel = randomLevel();
        if (newLevel > maxLevel) {
            for (int i = maxLevel; i < newLevel && i < MAX_LEVEL; i++) {
                update[i] = head;
            }
            maxLevel = Math.min(newLevel, MAX_LEVEL);
        }

        // 3. 创建新节点
        Node node = new Node();
        node.key = key;
        node.value = value;
        node.level = newLevel;
        node.next = new Node[newLevel];

        // 4. 插入到跳跃表中
        for (int i = 0; i < newLevel; i++) {
            node.next[i] = update[i].next[i];
            update[i].next[i] = node;
        }

    }
    public void deleteByKeyAndValue(Node targetNode) {
        if (targetNode == null) return;

        // 用于记录每一层的前驱节点
        Node current = head;
        Node[] update = new Node[maxLevel];

        for (int i = maxLevel - 1; i >= 0; i--) {
            while (current.next[i] != null &&
                    (current.next[i].value > targetNode.value ||
                            (current.next[i].value == targetNode.value && current.next[i].key < targetNode.key))) {
                current = current.next[i];
            }
            update[i] = current;
        }

        // 从底层开始检查是否有匹配的节点
        current = update[0].next[0];
        while (current != null) {
            if (current.key == targetNode.key && current.value == targetNode.value) {
                // 找到匹配节点，删除它在各层级中的引用
                for (int i = 0; i < current.level; i++) {
                    if (update[i].next[i] == current) {
                        update[i].next[i] = current.next[i];
                    }
                }
                // 继续查找下一个匹配节点（如果有）
                current = current.next[0];
            } else {
                break;
            }
        }

        // 调整最大层级
        while (maxLevel > 1 && head.next[maxLevel - 1] == null) {
            maxLevel--;
        }
    }
    public Node[] getTopN(int n) {
        if (n <= 0) {
            return new Node[0];
        }

        Node current = head.next[0]; // 从最底层开始顺序遍历
        Node[] result = new Node[Math.min(n, MAX_LEVEL)]; // 可选：限制最多不超过MAX_LEVEL
        int count = 0;

        while (current != null && count < n) {
            result[count++] = current;
            current = current.next[0];
        }

        // 如果实际数量不足n，截断数组
        if (count < n) {
            Node[] truncated = new Node[count];
            System.arraycopy(result, 0, truncated, 0, count);
            return truncated;
        }

        return result;
    }
}
