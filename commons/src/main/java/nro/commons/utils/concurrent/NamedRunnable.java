package nro.commons.utils.concurrent;

/**
 * @author Arriety
 */
public class NamedRunnable implements Runnable {
    private final String name;
    private final Runnable task;

    public NamedRunnable(String name, Runnable task) {
        this.name = name;
        this.task = task;
    }

    @Override
    public void run() {
        Thread current = Thread.currentThread();
        String oldName = current.getName();
        try {
            current.setName(name);
            task.run();
        } finally {
            current.setName(oldName); // restore
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
