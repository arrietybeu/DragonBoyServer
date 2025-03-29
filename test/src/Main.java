public class Main {

    public static void main(String[] args) throws Exception {
        try {
            Area area = new Area(1);
            Thread areaThread = new Thread(area);
            areaThread.start();

            Player player1 = new Player(1);
            Player player2 = new Player(2);

            area.submitEvent(new AddPlayerEvent(player1));
            area.submitEvent(new AddPlayerEvent(player2));

            area.submitEvent(new RemovePlayerEvent(player1));

            Thread.sleep(1000);

            // stop the area and wait for the thread to finish
            area.stop();
            areaThread.join();
        } catch (Exception e) {
            OpenAIChatClient.sendMessage(e.getMessage());
        }
    }
}
