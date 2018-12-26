import java.util.Scanner;
import static sun.swing.MenuItemLayoutHelper.max;

public class ChipFactory {

    final static int MAX = (int)1e5;

    // a[x]为每个case中第x个输入的值，临时保存数据
    static int[] a = new int[MAX];

    public static class Trie {
        /**
         * root:        当前根节点位置
         * tot:         记录新结点表示的值
         * next[][]:    记录该生成的字典树的数据，next[i][j]!= -1则表示该结点下面有子树
         * cnt[]:       cnt[x]为从x结点下面叶子数
         * end[]:       end[x]为位置为x叶子结点存储的数值
         * */
        private int root;
        private int total;
        private int[][] trieData = new int[MAX][2];
        private int[] countOfLeafNode = new int[MAX];
        private int[] endValue = new int[MAX];

        /**
         * 初始化要插入的结点，被初始化的next[i][j]==-1
         * */
        public int getNewNode() {
            for (int i = 0; i < trieData[total].length; i++) {
                trieData[total][i] = -1;
            }
            countOfLeafNode[total] = 0;
            endValue[total] = 0;
            return total++;
        }

        /**
         * 初始化整棵树
         * */
        public void initTrie() {
            total = 0;
            root = getNewNode();
        }

        /**
         * 将数字x插入该树
         * 1. 获取当前根节点位置
         * 2. 将要插入的x表示为长度为32位的二进制数
         * 3. 从next[0]遍历，不断将数据插入
         * 4. 保存第p个位置代表的叶子结点的值
         * */
        public void insertX(int x) {
            int p = root;
            // 跳过一个单元
            countOfLeafNode[p] += 1;
            for (int i = 31; i >= 0 ; i--) {
                int index = (((1 << i) & x) != 0)? 1:0;
                if (trieData[p][index] == -1) {
                    // 未赋值则新建结点，next[][] == tot.
                    trieData[p][index] = getNewNode();
                }
                // 使用p保存刚插入的数据tot值
                p = trieData[p][index];
                // 刚插入的下标为p的结点下方叶子结点数量更新
                countOfLeafNode[p] += 1;
            }
            // 记录x被插入结束后其二进制终点位置表示的值 x
            endValue[p] = x;
        }

        /**
         * 从树中移除数字x，由于字典树不重复的特点，x唯一。
         * p记录当前结点位置
         * cnt[p]-- -> 结点下叶子结点数量先减一，因为有一空单元。
         * 不断更新每个结点位置记录的叶子数量，标记性删除
         * */
        public void deleteX(int x) {
            int p = root;
            // 移除跳过的单元下方叶子结点数据
            countOfLeafNode[p] --;
            for (int i = 31; i >= 0 ; i--) {
                int index = (((1 << i) & x) != 0)?1:0;
                p = trieData[p][index];
                countOfLeafNode[p] --;
            }
        }

        /**
         * 查找树中与x异或最大的值的叶子结点
         * 将x表示为32位长的二进制
         * */
        //mTrie.search(a[i] + a[j])
        public int searchX(int x) {
            int p = root;
            for (int i = 31; i >= 0 ; i--) {
                int index = (((1 << i) & x) != 0)?1:0;
                // 因为要找异或结果最大值，因此一定是尽量找对应0/1上与index不同的
                if (index == 0) {
                    // 如果index==0，那么尽量沿着next[][]==1的找，实在没有再找0的
                    if ((trieData[p][1] != -1) && countOfLeafNode[trieData[p][1]] != 0) {
                        p = trieData[p][1];
                    } else {
                        p = trieData[p][0];
                    }
                }
                else {
                    // 同理
                    if (trieData[p][0] != -1 && countOfLeafNode[trieData[p][0]] != 0) {
                        p = trieData[p][0];
                    } else {
                        p = trieData[p][1];
                    }
                }
            }
            // end[p] 就是next[h][x]对应的叶子结点代表的数字的值，即树中与x异或值最大的值
            return x ^ endValue[p];
        }

        // 打印可视化
        public void printTrie() {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 60; j++) {
                    System.out.print("" + trieData[j][i] + "\t");
                }
                System.out.println("");
            }
        }

        public void printCountOfLeafNode() {
            for (int i = 0; i < 60; i++) {
                System.out.print("" + countOfLeafNode[i] + "\t");
            }
        }

        public void printEndValue() {
            for (int i = 0; i < 60; i++) {
                System.out.print("" + endValue[i] + "\t");
            }
        }
    }

    // 输入int数据
    public static int inputInt() {
        int temp = 0;
        temp = new Scanner(System.in).nextInt();
        return temp;
    }

    // 简化System.out.print()
    public static void print(String x) {
        System.out.println(x);
    }

    public static void main(String[] args) {
        int T = 0;
        print("T");
        // 输入T：case的个数
        T = inputInt();
        // 新建字典树对象
        Trie mTrie = new Trie();

        for (int t = T; t > 0; t--) {
            // 每执行一个case计算初始化一次
            mTrie.initTrie();
            int n = 0;
            int answer = 0;

            print("n");
            n = inputInt();

            for (int i = 0; i < n; i++) {
                print("input a[" + i + "]:");
                a[i] = inputInt();
                // 将输入的数字插入到该字典树中
                mTrie.insertX(a[i]);
            }

            // j = i + 1来避免i=j，这里a[i]等价a[j]
            for (int i = 0; i < n; i++) {
                for (int j = i+1; j < n; j++) {
                    // i!=j!=k，因此在计算之前将a[i],a[j]剔除该树
                    mTrie.deleteX(a[i]);
                    mTrie.deleteX(a[j]);
                    // mTrie.search(x)返回树中与x异或的最大值
                    // answer 不断更新最大值
                    answer = max(answer, mTrie.searchX(a[i] + a[j]));

                    // 计算结束后将a[i],a[j]插回树
                    mTrie.insertX(a[i]);
                    mTrie.insertX(a[j]);
                }
            }

            // 打印数据
            print("next[][]:");
            mTrie.printTrie();
            print("cnt[]:");
            mTrie.printCountOfLeafNode();
            print("\nend[]:");
            mTrie.printEndValue();
            print("\n\n");

            print("answer:" + answer);
        }
    } // end of main function
}
