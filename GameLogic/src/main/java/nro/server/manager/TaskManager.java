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

                    JSONArray detailArray = (JSONArray) JSONValue.parse(detail);
                    String[] detailList = new String[detailArray.size()];
                    for (int i = 0; i < detailArray.size(); i++) {
                        detailList[i] = detailArray.get(i).toString();
                    }

                    TaskMain task = new TaskMain(id, name, detailList, loadListSubNameTask(connection, id));
                    TASKS.put(task.getId(), task);
                }
                // LogServer.LogInit("Load task thành công (" + TASKS.size() + ")");
            }
        } catch (SQLException e) {
            LogServer.LogException("Error in TaskManager.loadTask(): " + e.getMessage());
        }
    }

    private List<TaskMain.SubName> loadListSubNameTask(Connection connection, int taskId) {
        List<TaskMain.SubName> subNameList = new ArrayList<>();

        String query = "SELECT name, max_count, content, npc_list, map_list FROM task_sub WHERE task_main_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskMain.SubName subName = new TaskMain.SubName();

                    var nameJson = rs.getString("name");
                    var npcJson = rs.getString("npc_list");
                    var mapJson = rs.getString("map_list");
                    var contentJson = rs.getString("content");
                    subName.setMaxCount(rs.getInt("max_count"));

                    JSONArray npcArray = (JSONArray) JSONValue.parse(npcJson);
                    JSONArray mapArray = (JSONArray) JSONValue.parse(mapJson);
                    JSONArray nameArray = (JSONArray) JSONValue.parse(nameJson);
                    JSONArray contentArray = (JSONArray) JSONValue.parse(contentJson);

                    subName.nameList = new String[nameArray.size()];
                    for (int i = 0; i < nameArray.size(); i++) {
                        subName.nameList[i] = nameArray.get(i).toString();
                    }

                    subName.contentInfo = new String[contentArray.size()];
                    for (int i = 0; i < contentArray.size(); i++) {
                        subName.contentInfo[i] = contentArray.get(i).toString();
                    }

                    subName.npcList = new short[npcArray.size()];
                    for (int i = 0; i < npcArray.size(); i++) {
                        subName.npcList[i] = Short.parseShort(npcArray.get(i).toString());
                    }

                    subName.mapList = new short[mapArray.size()];
                    for (int i = 0; i < mapArray.size(); i++) {
                        subName.mapList[i] = Short.parseShort(mapArray.get(i).toString());
                    }

                    subNameList.add(subName);
                }
            }
        } catch (Exception e) {
            LogServer.LogException(
                    "Error in TaskManager.loadListSubNameTask(): " + e.getMessage() + " - taskId: " + taskId, e);
            return Collections.emptyList();
        }
        return subNameList;
    }

    public TaskMain getTaskMainById(int id) {
        TaskMain task = TASKS.get(id);
        return task;
    }

}
