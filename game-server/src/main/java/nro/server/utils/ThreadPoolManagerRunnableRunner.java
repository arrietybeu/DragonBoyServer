package nro.server.utils;

import nro.commons.services.cron.RunnableRunner;

public class ThreadPoolManagerRunnableRunner extends RunnableRunner {

    @Override
    public void executeRunnable(Runnable r) {
        ThreadPoolManager.getInstance().execute(r);
    }

    @Override
    public void executeLongRunningRunnable(Runnable r) {
        ThreadPoolManager.getInstance().executeLongRunning(r);
    }

}
