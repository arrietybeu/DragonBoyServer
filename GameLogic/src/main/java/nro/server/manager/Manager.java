/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.manager;

import nro.server.ServerManager;
import nro.server.system.LogServer;

/**
 * @author Arriety
 */
public class Manager {

    private static Manager instance;

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    private Manager() {
        try {
            this.LoadData();
        } catch (Exception e) {
            LogServer.LogException("Error loadDataBase: " + e.getMessage(), e);
            System.exit(0);
        }
    }

    private void LoadData() {
        try {
            ManagerRegistry.initAll();
        } catch (Exception e) {
            LogServer.LogException("Error loadDataBase: " + e.getMessage(), e);
            ServerManager.getInstance().shutdown();
        } finally {
            System.gc();
        }
    }

    public void clearAllData() {
        try {
            ManagerRegistry.clearAll();
        } catch (Exception e) {
            LogServer.LogException("Error Clear All Data Manager: " + e.getMessage(), e);
            System.exit(0);
            ServerManager.getInstance().shutdown();
        } finally {
            System.gc();
        }
    }
}
