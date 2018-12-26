import java.util.Scanner;
import static sun.swing.MenuItemLayoutHelper.max;

public class ChipFactoryFormat {

    final static int MAX = (int)1e5;
    static int[] arrayS = new int[MAX];

    public static class Trie {

        /**
         * root:        当前根节点位置
         * total:         记录新结点表示的值
         * trieData[][]:    记录该生成的字典树的数据，trieData[i][j]!= -1则表示该结点下面有子树
         * countOfLeafNode[]:       countOfLeafNode[x]为从x结点下面叶子数
         * endValue[]:       endValue[x]为位置为x叶子结点存储的数值
         * */
        private int root;
        private int total;
        private int[][] trieData = new int[MAX][2];
        private int[] countOfLeafNode = new int[MAX];
        private int[] endValue = new int[MAX];

        // 初始化要插入的结点
        public int getNewNode() {
            for (int i = 0; i < trieData[total].length; i++) {
                trieData[total][i] = -1;
            }
            countOfLeafNode[total] = 0;
            endValue[total] = 0;
            return total++;
        }

        // 初始化整棵树
        public void initTrie() {
            total = 0;
            root = getNewNode();
        }

        // 将数字x插入该树
        public void insertIntoTrie(int x) {
            int p = root;
            // 跳过一个单元
            countOfLeafNode[p] += 1;
            for (int i = 31; i >= 0 ; i--) {
                int index = (((1 << i) & x) != 0)? 1:0;
                if (trieData[p][index] == -1) {
                    trieData[p][index] = getNewNode();
                }
                p = trieData[p][index];
                countOfLeafNode[p] += 1;
            }
            endValue[p] = x;
        }

        // 从树中移除数字x
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

        // 查找树中与x异或最大的值的叶子结点
        // mTrie.search(a[i] + a[j])
        public int searchX(int x) {
            int p = root;
            for (int i = 31; i >= 0 ; i--) {
                int index = (((1 << i) & x) != 0)?1:0;
                if (index == 0) {
                    if ((trieData[p][1] != -1) && countOfLeafNode[trieData[p][1]] != 0) {
                        p = trieData[p][1];
                    } else {
                        p = trieData[p][0];
                    }
                }
                else {
                    if (trieData[p][0] != -1 && countOfLeafNode[trieData[p][0]] != 0) {
                        p = trieData[p][0];
                    } else {
                        p = trieData[p][1];
                    }
                }
            }
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

    public static int inputInt(int low, int high, String str, String name) {
        if (str == null) {
            str = "";
        }
        if (name == null) {
            name = "";
        }
        int temp = 0;
        while (true) {
            print(str);
            temp = new Scanner(System.in).nextInt();
            if (temp >= low && temp <= high) {
                break;
            } else {
                print(name + " should in range(" + low + "," + (high+1) + ")");
            }
        }
        return temp;
    }

    // 简化
    public static void print(String x) {
        System.out.println(x);
    }

    public static void main(String[] args) {
        int T = 0;
        T = inputInt(1,1000, "T:", "T");
        Trie mTrie = new Trie();

        for (int t = T; t > 0; t--) {
            mTrie.initTrie();
            int n = 0;
            int answer = 0;

            n = inputInt(3,1000, "n:", "n");

            for (int i = 0; i < n; i++) {
                arrayS[i] = inputInt(0, (int)1e9, "a[" + i + "]:", "a[" + i + "]:");
                mTrie.insertIntoTrie(arrayS[i]);
            }
            for (int i = 0; i < n; i++) {
                for (int j = i+1; j < n; j++) {
                    mTrie.deleteX(arrayS[i]);
                    mTrie.deleteX(arrayS[j]);
                    answer = max(answer, mTrie.searchX(arrayS[i] + arrayS[j]));

                    mTrie.insertIntoTrie(arrayS[i]);
                    mTrie.insertIntoTrie(arrayS[j]);
                }
            }

            // 打印数据
            /*
            print("next[][]:");
            mTrie.printTrie();
            print("cnt[]:");
            mTrie.printCountOfLeafNode();
            print("\nend[]:");
            mTrie.printEndValue();
            */

            print("answer: " + answer);
        }
    } // end of main function
}
