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
    private int maxLevel;
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

        Node() {}
        public int getKey() {
            return key;
        }
        public int getValue() {
            return value;
        }
    }
    private int randomLevel() {
        int level = 1;
        while (random.nextDouble() < P && level < MAX_LEVEL) {
            level++;
        }
        return level;
    }
    public Node get(int key) {
        Node current = head;

        // 从最高层开始逐层向下搜索
        for (int i = maxLevel - 1; i >= 0; i--) {
            // 在当前层向右搜索，直到找到大于等于目标或到达末尾
            while (current.next[i] != null) {
                // 如果找到 key 匹配的节点直接返回
                if (current.next[i].key == key) {
                    return current.next[i];
                }
                // 如果下一节点的 value 仍大于当前 value，继续向右移动
                if (current.next[i].value > Integer.MIN_VALUE) { // 假设 value 不会为最小值
                    current = current.next[i];
                } else {
                    break;
                }
            }
        }
        // 最终检查底层链表
        current = current.next[0];
        return (current != null && current.key == key) ? current : null;
    }
    public void put(int key, int value) {
        while(true) {
            int newLevel = randomLevel();
            // 1. 提前处理层级扩展
            if (newLevel > maxLevel) {
                Node[] newNext = new Node[newLevel];
                System.arraycopy(head.next, 0, newNext, 0, maxLevel);
                head.next = newNext;
                maxLevel = newLevel;
            }
            // 2. 使用 maxLevel 初始化 update 数组（确保长度足够）
            Node[] update = new Node[maxLevel]; // 关键修改：使用 newLevel 作为数组长度
            Node current = head;

            // 3. 查找插入位置并检查 key 是否存在
            boolean keyExists = false;
            Node existNode = null;
            for (int i = maxLevel - 1; i >= 0; i--) {
                // 3.1 查找当前层的插入位置
                while (current.next[i] != null && current.next[i].value > value) {
                    current = current.next[i];
                }
                update[i] = current;
                // 检查当前层是否已有相同 key
                if (current.next[i] != null && current.next[i].key == key) {
                    existNode = current.next[i];
                    keyExists = true;
                    break; // 找到即可退出
                }
            }

            // 4. 如果 key 已存在，先删除再插入新节点
            if (keyExists) {
                value += existNode.value;
                delete(existNode, update); // 删除所有层级引用
            }
            // 5. 填充新增层级的 update 指针（如果 newLevel 更大）
            if (newLevel > maxLevel) {
                for (int i = maxLevel; i < newLevel; i++) {
                    update[i] = head;
                }
                maxLevel = newLevel;
            }
            // 6. 创建新节点并更新指针
            Node node = new Node();
            node.key = key;
            node.value = value;
            node.level = newLevel;
            node.next = new Node[newLevel];

            // 更新指针
            for (int i = 0; i < newLevel; i++) {
                node.next[i] = update[i].next[i];
                update[i].next[i] = node;
            }
            return;
        }
    }
    private void delete(Node node, Node[] update) {
        for (int i = 0; i < node.level; i++) {
            // 确保 update[i] 指向该层级的正确前驱节点
            if (i < update.length && update[i] != null && update[i].next[i] == node) {
                update[i].next[i] = node.next[i];
            }
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
