/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.manager;

import nro.server.MainServer;
import nro.server.LogServer;

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
            LogServer.LogException("Error loadDataBase: " + e.getMessage());
            System.exit(0);
        }
    }

    private void LoadData() {
        try {
            ManagerRegistry.initAll();
        } catch (Exception e) {
            LogServer.LogException("Error loadDataBase: " + e.getMessage());
            System.exit(0);
            MainServer.gI().close();
        } finally {
            System.gc();
        }
    }
}
