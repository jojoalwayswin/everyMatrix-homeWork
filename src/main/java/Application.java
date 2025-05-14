import com.sun.net.httpserver.HttpServer;
import controller.HomeWorkController;
import http.intercepter.HomeworkHttpHandler;
import manager.ExecutorManager;
import manager.RouteManager;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author zhangshu
 * @ClassName Application
 * @description: Application
 * @date 2025年04月29日
 * @version: 1.0
 */
public class Application {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080),0);
        // 设置线程池以支持高并发
        httpServer.setExecutor(ExecutorManager.getExecutor());
        // 注册控制器
        RouteManager.registerController(new HomeWorkController());
        // 创建上下文并绑定处理器
        httpServer.createContext("/", new HomeworkHttpHandler());
        httpServer.start();
        System.out.println("Server started at http://localhost:8080");
    }

}
