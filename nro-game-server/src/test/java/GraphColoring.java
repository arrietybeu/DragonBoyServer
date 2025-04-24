import java.util.*;

public class GraphColoring {

    static class Graph {
        int vertices;
        List<List<Integer>> adj;

        Graph(int v) {
            this.vertices = v;
            adj = new ArrayList<>();
            for (int i = 0; i < v; i++) {
                adj.add(new ArrayList<>());
            }
        }

        void addEdge(int u, int v) {
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        int[] greedyColoring() {
            int[] result = new int[vertices];
            Arrays.fill(result, -1); // -1: chưa tô màu

            result[0] = 0; // tô màu đầu tiên cho đỉnh 0

            for (int u = 1; u < vertices; u++) {
                boolean[] used = new boolean[vertices];
                for (int neighbor : adj.get(u)) {
                    if (result[neighbor] != -1) {
                        used[result[neighbor]] = true;
                    }
                }

                int color = 0;
                while (used[color]) {
                    color++;
                }

                result[u] = color;
            }

            return result;
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph(6); // 6 môn: v0 → v5

        // Tạo cạnh theo đề bài
        g.addEdge(0, 1); // v1-v2
        g.addEdge(0, 3); // v1-v4
        g.addEdge(2, 4); // v3-v5
        g.addEdge(1, 5); // v2-v6
        g.addEdge(3, 4); // v4-v5
        g.addEdge(4, 5); // v5-v6
        g.addEdge(0, 5); // v1-v6

        int[] colors = g.greedyColoring();

        // In kết quả
        for (int i = 0; i < colors.length; i++) {
            System.out.printf("Môn v%d được xếp vào giờ học %d\n", i + 1, colors[i] + 1);
        }
    }
}
