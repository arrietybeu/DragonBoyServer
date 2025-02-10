import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Area implements Runnable {
    private final int id;

    private final Map<Integer, Player> players = new HashMap<>();
    private final BlockingQueue<AreaEvent> eventQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public Area(int id) {
        this.id = id;
    }

    // Phương thức cho phép các thread khác gửi sự kiện vào Area
    public void submitEvent(AreaEvent event) {
        eventQueue.offer(event);
    }

    // Phương thức xử lý sự kiện liên tục trong 1 vòng lặp
    @Override
    public void run() {
        while (running) {
            try {
                // Lấy sự kiện từ hàng đợi và xử lý nó
                AreaEvent event = eventQueue.take();
                event.process(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Các phương thức thao tác trên players, chỉ được gọi từ thread của Area
    public void addPlayer(Player player) {
        players.put(player.getId(), player);
        System.out.println("Player " + player.getId() + " đã được thêm vào Area " + id);
    }

    public void removePlayer(Player player) {
        players.remove(player.getId());
        System.out.println("Player " + player.getId() + " đã rời khỏi Area " + id);
    }

    public void stop() {
        running = false;
    }
}