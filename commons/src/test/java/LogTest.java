import nro.commons.log.LogService;

import java.util.HashMap;
import java.util.Map;

public class LogTest {

    public static void main(String[] args) {

//        try {
//            throw new RuntimeException("TestExceptionOccurred");
//        } catch (Exception e) {
//            LogService.error("DemoTestException", e, Map.of("user", "tester", "env", "dev"));
//        }


        try {
            simulateError();
        } catch (Exception e) {
            Map<String, String> meta = new HashMap<>();
            meta.put("user_id", "test123");
            meta.put("action", "simulateError");
            meta.put("server", "test-server-01");

            LogService.error("TestExceptionOccurred", e, meta);
        }

        System.out.println("Log submitted.");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}

    }

    private static void simulateError() {
        int a = 10;
        int b = 0;
        int c = a / b;
    }

}
