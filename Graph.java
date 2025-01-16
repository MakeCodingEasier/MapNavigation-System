package demo.graph;

import java.util.*;

//GraphNode
record GraphNode(int v, double weight) {
}

//Edge
record GraphEdge(int s, int d, double weight) {
}

public class Graph {
    protected static final int NODE_COUNT = 26;
    private final List<List<GraphNode>> adjacencyList = new ArrayList<>();
    private final double[][] adjacencyMatrix;

    public Graph() {
        for (int i = 0; i < NODE_COUNT; i++) {
            adjacencyList.addLast(new ArrayList<>());     //初始化邻接链表
        }

        adjacencyMatrix = new double[NODE_COUNT][NODE_COUNT];
        for (int i = 0; i < NODE_COUNT; i++) {
            Arrays.fill(adjacencyMatrix[i], Double.MAX_VALUE);
            adjacencyMatrix[i][i] = 0; // Distance to self is 0
        }
    }

    //添加边
    public void addEdge(int start, int end, double weight) {
        adjacencyList.get(start).add(new GraphNode(end, weight));
        adjacencyList.get(end).add(new GraphNode(start, weight));
        adjacencyMatrix[start][end] = weight;
        adjacencyMatrix[end][start] = weight;
    }

    //Dijkstra算法 单源最短路径
    public List<String> findShortestPathDijkstra(char s, char s2) {
        int start = s - 'A', end = s2 - 'A';
        double[] d = new double[NODE_COUNT];  //距离
        ArrayList<ArrayList<Integer>> lists = new ArrayList<>();  //记录前一个节点
        for (int i = 0; i < NODE_COUNT; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(-1); // 初始化
            lists.add(list);
        }
        boolean[] visited = new boolean[NODE_COUNT];  //记录是否已经访问过了

        Arrays.fill(d, Integer.MAX_VALUE);
        d[start] = 0;

        PriorityQueue<Integer> heap = new PriorityQueue<>(Comparator.comparingDouble(a -> d[a])); //最小堆
        heap.add(start);

        while (!heap.isEmpty()) {
            int index = heap.poll();
            if (visited[index]) continue;
            visited[index] = true;
            int l = adjacencyList.get(index).size();
            for (int i = 0; i < l; i++) {
                int ver = adjacencyList.get(index).get(i).v();      //提取相邻的节点信息
                double weight = adjacencyList.get(index).get(i).weight();

                if (visited[ver]) continue;

                if (d[index] + weight < d[ver]) {
                    d[ver] = d[index] + weight;
                    lists.remove(ver);
                    ArrayList<Integer> al = new ArrayList<>();
                    al.add(index);
                    lists.add(ver, al);
                    heap.add(ver);
                } else if (!visited[ver] && d[index] + weight == d[ver]) {
                    if (lists.get(ver).getFirst() == -1) {
                        lists.get(ver).removeFirst();
                        lists.get(ver).add(index);
                    } else {
                        lists.get(ver).add(index);
                    }
                }
            }
        }
        Deque<Integer> stack = new LinkedList<>();
        stack.add(end);
        List<String> paths = new ArrayList<>();
        collectPaths(lists, end, end, start, stack, 0, paths);
        return paths;
    }

    //Bellman-Ford算法
    public List<String> findShortestPathBellmanFord(char s, char s2) {
        int x = s - 'A', end = s2 - 'A';

        double[] d = new double[NODE_COUNT]; //初始化距离数组
        Arrays.fill(d, Integer.MAX_VALUE);
        d[x] = 0;

        ArrayList<ArrayList<Integer>> lists = new ArrayList<>(); //记录前一个节点是哪个
        for (int i = 0; i < NODE_COUNT; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(-1);
            lists.add(list);
        }

        for (int i = 0; i < NODE_COUNT - 1; i++) { //循环次数为顶点数减1
            boolean[] visited = new boolean[NODE_COUNT]; //记录每次循环中节点是否被访问过
            for (int j = 0; j < NODE_COUNT; j++) { //遍历所有边
                int m = adjacencyList.get(j).size();
                for (int k = 0; k < m; k++) {
                    int ver = adjacencyList.get(j).get(k).v();
                    double weight = adjacencyList.get(j).get(k).weight();

                    if (d[j] != Integer.MAX_VALUE && d[j] + weight < d[ver]) { //成功松弛
                        d[ver] = d[j] + weight;
                        lists.remove(ver);
                        ArrayList<Integer> al = new ArrayList<>();
                        al.add(j);
                        lists.add(ver, al);
                        visited[ver] = true;
                    } else if (d[j] != Integer.MAX_VALUE && d[j] + weight == d[ver]) {
                        if (!visited[ver]) {
                            lists.remove(ver);
                            ArrayList<Integer> al = new ArrayList<>();
                            al.add(j);
                            lists.add(ver, al);
                        } else {
                            lists.get(ver).add(j);
                        }
                        visited[ver] = true;
                    }
                }
            }
        }

        Deque<Integer> stack = new LinkedList<>();
        stack.add(end);
        List<String> paths = new ArrayList<>();
        collectPaths(lists, end, end, x, stack, 0, paths);
        return paths;
    }

    //路径信息
    private void collectPaths(ArrayList<ArrayList<Integer>> record, int temp, int next, int x, Deque<Integer> stack, double weight, List<String> paths) {
        //向前寻找路径信息
        while (record.get(temp).getFirst() != x) {
            if (temp < 0 || temp >= NODE_COUNT) {
                return;
            }
            int m = record.get(temp).size();
            if (m > 1) { //如果在同一级中有多个节点，就复制相关信息进入递归
                for (int i = 1; i < m; i++) {
                    int v = record.get(temp).get(i);
                    Deque<Integer> newStack = new LinkedList<>(stack);
                    newStack.push(v);
                    collectPaths(record, v, v, x, newStack, weight + adjacencyMatrix[v][next], paths);
                }
            }
            int v = record.get(temp).getFirst();
            if (v < 0 || v >= NODE_COUNT) {
                break;
            }
            stack.push(v);
            temp = v;
            weight += adjacencyMatrix[temp][next];
            next = temp;
        }
        weight += adjacencyMatrix[x][next];

        StringBuilder path = new StringBuilder();
        path.append((char) (x + 'A'));
        while (!stack.isEmpty()) {
            int index = stack.pop();
            path.append("->").append((char) ('A' + index));
        }
        path.append("\n最短距离为 ").append(String.format("%.2f", weight)).append(" km.\n");
        paths.add(path.toString());
    }

    private int find(int[] father, int x) {
        if (father[x] != x) {
            father[x] = find(father, father[x]);
        }
        return father[x];
    }

    private void union(int[] father, int x, int y) {
        int fx = find(father, x);
        int fy = find(father, y);
        father[fx] = fy;
    }

    //Kruskal算法
    public List<String> Kruskal_subway() {
        int[] father = new int[NODE_COUNT]; // 并查集初始化
        for (int i = 0; i < NODE_COUNT; i++) father[i] = i;
        ArrayList<GraphEdge> edges = new ArrayList<>();
        for (int i = 0; i < NODE_COUNT; i++) {
            for (int j = i + 1; j < NODE_COUNT; j++) {
                if (adjacencyMatrix[i][j] != Double.MAX_VALUE)
                    edges.add(new GraphEdge(i, j, adjacencyMatrix[i][j]));
            }
        }
        // 按边权重从小到大排序
        edges.sort(Comparator.comparingDouble(GraphEdge::weight));
        List<String> result = new ArrayList<>();
        double totalWeight = 0;
        // 选择边构建最小生成树
        for (GraphEdge edge : edges) {
            if (find(father, edge.s()) != find(father, edge.d())) {
                union(father, edge.s(), edge.d());
                result.add("( " + (char) (edge.s() + 'A') + " , " + (char) (edge.d() + 'A') + " ,  " + String.format("%.2f", edge.weight()) + "  km)\n");
                totalWeight += edge.weight();
            }
        }
        // 检查是否所有节点都被连接
        int root = find(father, 0);
        for (int i = 1; i < NODE_COUNT; i++) {
            if (find(father, i) != root) {
                return List.of("无法构建连通图，无法满足条件！");
            }
        }
        result.add("最短路径总长度为: " + String.format("%.2f", totalWeight) + " km.\n");
        return result;
    }

    //Prim算法
    public List<String> Prim_subway() {
        boolean[] visited = new boolean[NODE_COUNT];
        List<String> result = new ArrayList<>();
        PriorityQueue<GraphEdge> heap = new PriorityQueue<>(Comparator.comparingDouble(GraphEdge::weight));
        for (GraphNode node : adjacencyList.getFirst()) {
            heap.add(new GraphEdge(0, node.v(), node.weight()));
        }
        visited[0] = true;

        double weight = 0;
        while (!heap.isEmpty()) {
            GraphEdge edge = heap.poll();
            int v = edge.d();
            if (visited[v]) continue;
            weight += edge.weight();
            result.add("( " + (char) (edge.s() + 'A') + " , " + (char) (edge.d() + 'A') + " ,  " + String.format("%.2f", edge.weight()) + "  km)\n");
            visited[v] = true;
            for (GraphNode node : adjacencyList.get(v)) {
                heap.add(new GraphEdge(v, node.v(), node.weight()));
            }
        }
        result.add("最短距离为 " + String.format("%.2f", weight) + " km.\n");
        return result;
    }

    //实现满足功能的busRoutes函数，利用Dijkstra算法找出从给定起点出发的公交路线相关信息
    public List<String> bus_Dijkstra(char s) {
        int x = s - 'A'; // 起点

        double[] distance = new double[NODE_COUNT]; // 起点到其他各点的距离
        Arrays.fill(distance, Double.MAX_VALUE);
        distance[x] = 0; // 起点到自身的距离为 0

        ArrayList<ArrayList<Integer>> record = new ArrayList<>(); // 记录前一个节点
        for (int i = 0; i < NODE_COUNT; i++) {
            ArrayList<Integer> al = new ArrayList<>();
            al.add(-1);
            record.add(al);
        }

        boolean[] visited = new boolean[NODE_COUNT]; // 是否访问过
        PriorityQueue<Integer> heap = new PriorityQueue<>(Comparator.comparingDouble(a -> distance[a])); // 最小堆
        heap.add(x);

        // 使用 Dijkstra 算法计算从起点到所有点的最短路径
        while (!heap.isEmpty()) {
            int index = heap.poll();
            if (visited[index]) continue;
            visited[index] = true;
            for (GraphNode neighbor : adjacencyList.get(index)) {
                int ver = neighbor.v();
                double weight = neighbor.weight();

                if (visited[ver]) continue;

                if (distance[index] + weight < distance[ver]) { // 松弛操作
                    distance[ver] = distance[index] + weight;
                    record.set(ver, new ArrayList<>(List.of(index)));
                    heap.add(ver);
                } else if (distance[index] + weight == distance[ver]) { // 记录等长路径
                    record.get(ver).add(index);
                }
            }
        }

        // 构建公交路线图
        Graph busGraph = new Graph();
        for (int y = 0; y < NODE_COUNT; y++) {
            if (y == x) continue;
            Deque<Integer> stack = new LinkedList<>();
            stack.add(y);
            bus_Recursive(record, y, y, x, stack, busGraph);
        }

        // 使用 Prim 算法生成最小生成树
        return busGraph.Prim_subway();
    }

    private void bus_Recursive(ArrayList<ArrayList<Integer>> record, int temp, int next, int x, Deque<Integer> stack, Graph g) {
        // 向前追踪路径信息
        while (record.get(temp).get(0) != x) {
            int m = record.get(temp).size();
            if (m > 1) { // 如果同一级有多个节点，递归处理
                for (int i = 1; i < m; i++) {
                    int v = record.get(temp).get(i);
                    Deque<Integer> newStack = new LinkedList<>(stack);
                    newStack.push(v);
                    bus_Recursive(record, v, v, x, newStack, g);
                }
            }
            int v = record.get(temp).get(0);
            stack.push(v);
            temp = v;
            next = temp;
        }

        // 添加边到公交路线图
        g.addEdge(x, stack.peek(), adjacencyMatrix[x][stack.peek()]);
        while (!stack.isEmpty()) {
            int index = stack.pop();
            if (!stack.isEmpty()) g.addEdge(index, stack.peek(), adjacencyMatrix[index][stack.peek()]);
        }
    }
}