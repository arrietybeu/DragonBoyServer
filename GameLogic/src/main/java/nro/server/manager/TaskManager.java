package nro.server.manager;

import nro.model.task.TaskMain;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
public class TaskManager implements IManager {

    private static final List<TaskMain> TASKS = new ArrayList<>();

    @Override
    public void init() {
        loadTask();
    }

    @Override
    public void reload() {
        clear();
        loadTask();
    }

    @Override
    public void clear() {
        TASKS.clear();
    }

    private void loadTask() {
        String mainTaskQuery = "SELECT id, name, detail FROM task_main";

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
            if (connection == null) {
                LogServer.LogException("Database connection is null!");
                return;
            }

            try (PreparedStatement ps = connection.prepareStatement(mainTaskQuery);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    TaskMain task = new TaskMain();
                    
                    task.setId(rs.getInt("id"));
                    task.setName(rs.getString("name"));
                    task.setDetail(rs.getString("detail"));

                    // Load sub tasks
                    task.setSubNameList(loadListSubNameTask(connection, task.getId()));

                    TASKS.add(task);
                }
                LogServer.LogInit("Load task thành công (" + TASKS.size() + ")");
            }
        } catch (SQLException e) {
            LogServer.LogException("Error in TaskManager.loadTask(): " + e.getMessage());
        }
    }

    private List<TaskMain.SubName> loadListSubNameTask(Connection connection, int taskId) {
        List<TaskMain.SubName> subNameList = new ArrayList<>();
        String query = "SELECT name, max_count, content, npc_id, map_id FROM task_sub WHERE task_main_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskMain.SubName subName = new TaskMain.SubName();
                    subName.setName(rs.getString("name"));
                    subName.setMax(rs.getInt("max_count"));
                    subName.setContentInfo(rs.getString("content"));
                    subName.setNpcId(rs.getByte("npc_id"));
                    subName.setMapId(rs.getShort("map_id"));
                    subNameList.add(subName);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error in TaskManager.loadListSubNameTask(): " + e.getMessage());
            return Collections.emptyList();
        }
        return subNameList;
    }
}
