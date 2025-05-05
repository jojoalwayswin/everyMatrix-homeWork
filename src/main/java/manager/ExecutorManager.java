package manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorManager {

    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static ExecutorService getExecutor() {
        return EXECUTOR;
    }
}
