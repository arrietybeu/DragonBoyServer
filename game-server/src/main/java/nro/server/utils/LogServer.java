package nro.server.utils;


import nro.server.configs.main.ConfigServer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Arriety
 */
public class LogServer {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static LogWindow logWindow;
    private static final List<JSONObject> errorLogList = new ArrayList<>();

    private static void log(String type, String colorCode, String message) {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stackTrace[3];
            String strBuild = caller.getClassName() + "." + caller.getMethodName() + "(" + caller.getFileName() + ":"
                    + caller.getLineNumber() + ")";
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
        logWindow.appendLog(new JSONObject().put("message", message));
    }

    static class LogWindow {
        private final JFrame frame;
        private final JList<String> errorList;
        private final DefaultListModel<String> listModel;
        private final JTable stackTraceTable;
        private final DefaultTableModel tableModel;

        public LogWindow() {
            frame = new JFrame("Debug Logs");
            frame.setLayout(new BorderLayout());

            listModel = new DefaultListModel<>();
            errorList = new JList<>(listModel);
            errorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            errorList.addListSelectionListener(e -> {
                int selectedIndex = errorList.getSelectedIndex();
                if (selectedIndex != -1) {
                    showStackTrace(errorLogList.get(selectedIndex));
                }
            });

            String[] columnNames = {"File", "Method", "Line", "Class"};
            tableModel = new DefaultTableModel(columnNames, 0);
            stackTraceTable = new JTable(tableModel);
            JScrollPane scrollPaneTable = new JScrollPane(stackTraceTable);

            JScrollPane scrollPaneList = new JScrollPane(errorList);
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneList, scrollPaneTable);
            splitPane.setDividerLocation(150);

            frame.add(splitPane, BorderLayout.CENTER);
            frame.setSize(800, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        }

        public void appendLog(JSONObject logJson) {
            SwingUtilities.invokeLater(() -> {
                listModel.addElement("Lỗi #" + (errorLogList.size() + 1));
                errorLogList.add(logJson);
            });
        }

        private void showStackTrace(JSONObject errorJson) {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ

            if (errorJson.has("stackTrace")) {
                JSONArray stackTraceArray = errorJson.getJSONArray("stackTrace");
                for (int i = 0; i < stackTraceArray.length(); i++) {
                    JSONObject trace = stackTraceArray.getJSONObject(i);
                    tableModel.addRow(new Object[]{trace.getString("file"), trace.getString("method"),
                            trace.getInt("line"), trace.getString("class")});
                }
            }
        }
    }

    public static void LogException(String message, Exception... e) {
        try {
            if (e.length > 0) {
                e[0].printStackTrace();
//            log("Error", "\033[1;31m", message + "\n=> " + Arrays.toString(e[0].getStackTrace()));
                OpenUIChatBug(message, e);
            } else {
                log("Error", "\033[1;31m", message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void OpenUIChatBug(String message, Exception... e) {
        if (ConfigServer.IS_OPEN_UI_LOG_BUG) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("type", "Error");
            errorJson.put("message", message);
            errorJson.put("exception", e[0].toString());

            JSONArray stackTraceArray = new JSONArray();
            Arrays.stream(e[0].getStackTrace()).forEach(trace -> {
                JSONObject traceJson = new JSONObject();
                traceJson.put("class", trace.getClassName());
                traceJson.put("method", trace.getMethodName());
                traceJson.put("file", trace.getFileName());
                traceJson.put("line", trace.getLineNumber());
                stackTraceArray.put(traceJson);
            });

            errorJson.put("stackTrace", stackTraceArray);

            if (logWindow == null || !logWindow.frame.isVisible()) {
                logWindow = new LogWindow();
            }
            logWindow.appendLog(errorJson);
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

    public static void LogInfo(String message) {
        log("Info", "\033[0;36m", message);
    }
}
