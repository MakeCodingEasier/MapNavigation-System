package demo.graph;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@SuppressWarnings("ALL")
public class UI extends Application {

    @Override
    public void start(Stage primaryStage) {
        Graph graph = new Graph();

        // 创建左侧地图图片
        VBox leftPanel = new VBox();
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color:  \"#ADD8E6\"; -fx-border-width: 2;");
        String mapImagePath = "tagged.png"; // 实际图片路径
        Image mapImage = new Image(mapImagePath);
        ImageView mapView = new ImageView(mapImage);
        // 设置图片合适的大小
        mapView.setFitWidth(400); // 设置图片宽度
        mapView.setFitHeight(400); // 设置图片高度
        mapView.setPreserveRatio(true); // 保持宽高比
        mapView.setSmooth(true); // 启用平滑
        mapView.setVisible(true); // 显示图片
        leftPanel.getChildren().add(mapView);
        // 创建GridPane布局
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_RIGHT);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #ffffff;");  // 设置背景色

        // 创建右侧的组件
        Label label = new Label("MAP Navigation");
        label.setFont(new Font("Arial", 24));
        label.setTextFill(Color.DARKBLUE);

        // 创建用户名标签和文本框
        HBox LocationBox1 = new HBox(10);//设置间距
        Label label1 = new Label("Location 1:");
        label1.setTextFill(Color.BLACK); // 设置标签文本颜色
        TextField location1 = new TextField();
        location1.setPrefWidth(200);  // 设置文本框宽度
        LocationBox1.getChildren().addAll(label1, location1);

        HBox LocationBox2 = new HBox(10);
        Label label2 = new Label("Location 2:");
        label2.setTextFill(Color.BLACK); // 设置标签文本颜色
        TextField location2 = new TextField();
        location2.setPrefWidth(200);  // 设置文本框宽度
        LocationBox2.getChildren().addAll(label2, location2);

        // 创建按钮
        Button button1 = new Button("Operation 1");
        Button button2 = new Button("Operation 2");
        Button button3 = new Button("Operation 3");
        Button button4 = new Button("Operation 4");

        // 文本输出区域
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(220);
        outputArea.setFont(Font.font("Monospaced", 14)); // 设置字体为 Monospaced，大小为14
        // 从文件中读取边信息并构建图
        try {
            Scanner scanner = new Scanner(Objects.requireNonNull(UI.class.getResourceAsStream("/edge.txt")));
            // 跳过第一行
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // 读取并丢弃第一行
            }
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");
                if (line.length == 3) { // 确保读取到3个元素
                    char node1 = line[0].toCharArray()[0];
                    char node2 = line[1].toCharArray()[0];
                    double weight = Double.parseDouble(line[2]);
                    graph.addEdge(node1 - 'A', node2 - 'A', weight);
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 按钮事件处理
        button1.setOnAction(event -> {
            outputArea.clear();
            outputArea.appendText("Operation 1:\n");
            String start = location1.getText();
            String end = location2.getText();
            if (start.length() == 1 && end.length() == 1 &&
                    (start.charAt(0) >= 'A' && start.charAt(0) <= 'Z') &&
                    (end.charAt(0) >= 'A' && end.charAt(0) <= 'Z')) {
                outputArea.appendText("Following is Dijkstra Alogorithm.\n");
                // 使用Dijkstra算法求两点间最短路径
                List<String> resultD = graph.findShortestPathDijkstra(start.charAt(0), end.charAt(0));
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < resultD.size() - 1; i++) {
                    s.append(resultD.get(i));
                }
                s.append(resultD.get(resultD.size() - 1));
                outputArea.appendText("从 " + start + " 到 " + end + " 路线: " + s.toString() + "\n");
                outputArea.appendText("\nFollowing is Bellman-Ford Alogorithm.\n");
                // 使用Bellman-Ford算法求两点间最短路径（作为另一种解法示例，可按需选用或扩展更多算法对比）
                List<String> resultBF = graph.findShortestPathBellmanFord(start.charAt(0), end.charAt(0));
                StringBuilder s2 = new StringBuilder();
                for (int i = 0; i < resultD.size() - 1; i++) {
                    s2.append(resultBF.get(i));
                }
                s2.append(resultBF.get(resultBF.size() - 1));
                outputArea.appendText("从 " + start + " 到 " + end + " 路线: " + s2.toString() + "\n");
            } else {
                outputArea.appendText("输入地点错误，请重新输入。\n");
            }
        });
        button2.setOnAction(event -> {
            outputArea.clear();
            outputArea.appendText("Operation 2:\n");
            String target = location1.getText();
            if (target.length() == 1 && (target.charAt(0) >= 'A' && target.charAt(0) <= 'Z')) {
                outputArea.appendText("到 " + target + " 的最短路径:\n");
                outputArea.appendText("Following is Dijkstra Alogorithm.\n");
                for (int i = 0; i < Graph.NODE_COUNT; i++) { // 求所有点到指定点的最短路径（Dijkstra算法）
                    List<String> resultAllD = graph.findShortestPathDijkstra((char) ('A' + i), target.charAt(0));
                    StringBuilder s = new StringBuilder();
                    for (int j = 0; j < resultAllD.size() - 1; j++) {
                        s.append(resultAllD.get(i));
                    }
                    s.append(resultAllD.get(resultAllD.size() - 1));
                    outputArea.appendText("从 " + (char) ('A' + i) + " 到 " + target.charAt(0) + " 路线: " + s.toString() + "\n");
                }
                outputArea.appendText("\nFollowing is Bellman-Ford Alogorithm.\n");
                for (int i = 0; i < Graph.NODE_COUNT; i++) { // 使用Bellman-Ford算法求所有点到指定点最短路径
                    List<String> resultAllBF = graph.findShortestPathBellmanFord((char) ('A' + i), target.charAt(0));
                    StringBuilder s2 = new StringBuilder();
                    for (int j = 0; j < resultAllBF.size() - 1; j++) {
                        s2.append(resultAllBF.get(i));
                    }
                    s2.append(resultAllBF.get(resultAllBF.size() - 1));
                    outputArea.appendText("从 " + (char) ('A' + i) + " 到 " + target.charAt(0) + " 路线: " + s2.toString() + "\n");
                }
            } else {
                outputArea.appendText("输入地点错误，请重新输入。\n");
            }
        });
        //subway
        button3.setOnAction(event -> {
            outputArea.clear();
            outputArea.appendText("Operation 3:\n");
            // 设计地铁路线
            outputArea.appendText("Following is Kruskal Alogorithm.\n");
            // Kruskal算法
            List<String> resultK = graph.Kruskal_subway();
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < resultK.size() - 1; i++) {
                s.append(resultK.get(i));
            }
            s.append(resultK.get(resultK.size() - 1));
            outputArea.appendText(s.toString() + "\n");
            outputArea.appendText("Following is Prim Alogorithm.\n");
            // Prim算法
            List<String> resultP = graph.Prim_subway();
            StringBuilder s2 = new StringBuilder();
            for (int i = 0; i < resultP.size() - 1; i++) {
                s2.append(resultP.get(i));
            }
            s2.append(resultP.get(resultP.size() - 1));
            outputArea.appendText(s2.toString() + "\n");
        });
        //bus
        button4.setOnAction(event -> {
            outputArea.clear();
            outputArea.appendText("Operation 4:\n");
            String start = location1.getText();
            if (start.length() == 1 && (start.charAt(0) >= 'A' && start.charAt(0) <= 'Z')) {
                // 求从指定点出发的公交路线
                List<String> busRoutes = graph.bus_Dijkstra(start.charAt(0));
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < busRoutes.size() - 1; i++) {
                    s.append(busRoutes.get(i));
                }
                s.append(busRoutes.get(busRoutes.size() - 1));
                outputArea.appendText(s.toString() + "\n");
            } else {
                outputArea.appendText("输入地点错误，请重新输入。\n");
            }
        });

        // 布局设计
        HBox buttonBox1 = new HBox(50, button1, button2);
        HBox buttonBox2 = new HBox(50, button3, button4);
        VBox vBox = new VBox(10, label, LocationBox1, LocationBox2, buttonBox1, buttonBox2, outputArea);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(0, 0, 0, 10)); // 增加左侧填充

        // 主布局
        GridPane root = new GridPane();
        root.add(leftPanel, 0, 0);
        root.add(vBox, 1, 0);

        // 将右侧布局的列宽度自适应
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        root.getColumnConstraints().add(columnConstraints);

        // 创建场景并显示
        Scene scene = new Scene(root, 800, 450);

        // 设置窗口标题和场景
        primaryStage.setTitle("Map UI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}