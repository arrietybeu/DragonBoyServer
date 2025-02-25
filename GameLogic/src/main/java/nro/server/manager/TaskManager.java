package nro.server.manager;

import nro.model.task.TaskMain;
import nro.repositories.DatabaseConnectionPool;
import nro.server.LogServer;
import nro.server.config.ConfigDB;

import java.sql.*;
import java.util.*;

import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

@SuppressWarnings("ALL")
public class TaskManager implements IManager {

    @Getter
    private static final TaskManager instance = new TaskManager();

    @Getter
    private static final Map<Integer, TaskMain> TASKS = new HashMap<>();

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
                    var id = rs.getInt("id");
                    var name = rs.getString("name");
                    var detail = rs.getString("detail");
                    TaskMain task = new TaskMain(id, name, detail, loadListSubNameTask(connection, id));
                    TASKS.put(task.getId(), task);
                }
                LogServer.LogInit("Load task thành công (" + TASKS.size() + ")");
            }
        } catch (SQLException e) {
            LogServer.LogException("Error in TaskManager.loadTask(): " + e.getMessage());
        }
    }

    private List<TaskMain.SubName> loadListSubNameTask(Connection connection, int taskId) {
        List<TaskMain.SubName> subNameList = new ArrayList<>();

        String query = "SELECT name, max_count, content, npc_list, map_id FROM task_sub WHERE task_main_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskMain.SubName subName = new TaskMain.SubName();
                    subName.setName(rs.getString("name"));
                    subName.setMaxCount(rs.getInt("max_count"));
                    subName.setContentInfo(rs.getString("content"));
                    var npcJson = rs.getString("npc_list");

                    JSONArray dataArray = (JSONArray) JSONValue.parse(npcJson);

                    subName.npcList = new short[dataArray.size()];
                    for (int i = 0; i < dataArray.size(); i++) {
                        subName.npcList[i] = Short.parseShort(dataArray.get(i).toString());
                    }
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

    public TaskMain getTaskMainById(int id) {
        LogServer.LogWarning("getTaskMainById: " + id);

        TaskMain task = TaskManager.getInstance().getTaskMainById(id);
        if (task == null) {
            LogServer.LogWarning("⚠️ Không tìm thấy TaskMain ID: " + id);
            return null;
        }

        return task;
    }


}
