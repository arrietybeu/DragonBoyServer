package nro.server;

import nro.server.config.ConfigDB;
import nro.server.config.ConfigServer;
import nro.server.LogServer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupServer {

    private static void BackupDatabase() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFileName = ConfigServer.BACKUP_FOLDER_PATH + ConfigDB.DB_DYNAMIC_NAME + "_" + timeStamp + ".sql";
        File backupDir = new File(ConfigServer.BACKUP_FOLDER_PATH);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        try {

            String command = String.format(
                    "%s -h %s -u%s  %s -r \"%s\"",
                    ConfigServer.MYSQL_DUMP_PATH, "localhost", ConfigDB.DB_DYNAMIC_USER, ConfigDB.DB_DYNAMIC_NAME, backupFileName
            );

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (OutputStream os = process.getOutputStream(); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                writer.write("");
                writer.newLine();
                writer.flush();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int processComplete = process.waitFor();
            if (processComplete == 0) {
                System.out.println("Backup Complete");
            } else {
                System.out.println("Backup Failure");
            }

        } catch (IOException | InterruptedException e) {
            LogServer.LogException("Error in BackupDatabase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
