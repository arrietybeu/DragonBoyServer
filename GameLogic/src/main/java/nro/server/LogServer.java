package nro.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * @author Arriety
 */
public class LogServer {

    private static final Logger logger = LogManager.getLogger(LogServer.class);
    private static LogWindow logWindow;

    private static void log(String type, String colorCode, String message) {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stackTrace[3]; // Lấy caller từ stacktrace
            String strBuild = caller.getClassName() +
                    "." +
                    caller.getMethodName() +
                    "(" +
                    caller.getFileName() +
                    ":" +
                    caller.getLineNumber() +
                    ")";
            message += " => [from: " + strBuild + "]";
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(colorCode + "[" + type + "] " + message + "\033[0m");
    }

    public static void LogForm(String message) {
        if (logWindow == null) {
            logWindow = new LogWindow();
        }
        logWindow.appendLog(message);
    }

    static class LogWindow {
        private final JFrame frame;
        private final JTextArea textArea;

        public LogWindow() {
            frame = new JFrame("Debug Logs");
            textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        }

        public void appendLog(String log) {
            SwingUtilities.invokeLater(() -> {
                textArea.append(log + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }

    public static void LogException(String message, Exception... e) {
        if (e.length > 0) {
            log("Error", "\033[1;31m", message + " => " + Arrays.toString(e[0].getStackTrace()));
        } else {
            log("Error", "\033[1;31m", message);
        }
    }

    public static void LogWarning(String message) {
        log("Warning", "\033[0;33m", message);
    }

    public static void DebugLogic(String message) {
        log("Logic", "\033[0;34m", message);
    }

    public static void LogInit(String message) {
        log("Init", "\033[0;32m", message);
    }

}

