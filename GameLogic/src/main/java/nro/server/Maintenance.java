/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import nro.server.LogServer;

/**
 * @author Arriety
 */
public class Maintenance {

    @Getter
    private static final Maintenance instance = new Maintenance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isMaintenance = false;
    private int seconds = 0;

    public void active(int seconds) {
        if (this.isMaintenance) {
            return;
        }
        this.isMaintenance = true;
        this.seconds = seconds;
        this.executor.submit(this::start);
    }

    public void start() {
        try {
            while (this.seconds > 0) {
                this.seconds--;
                LogServer.DebugLogic("Maintenance in " + this.seconds + " seconds");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogServer.LogException("Error Maintenance 2: " + e.getMessage());
                }
            }
            MainServer.getInstance().shutdown();

            LogServer.DebugLogic("Maintenance stop server");
        } catch (Exception e) {
            LogServer.LogException("Error Maintenance 1: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        this.isMaintenance = false;
        this.seconds = 0;
    }

    public boolean isMaintenance() {
        return this.isMaintenance;
    }
}
