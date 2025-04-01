//import javax.swing.*;
//import java.awt.*;
//
//public class MapViewer extends JPanel {
//    private static final int TILE_SIZE = 16;
//
//    private int[][] tiles;
//
//    public MapViewer(int[][] tiles) {
//        this.tiles = tiles;
//        int rows = tiles.length;
//        int cols = tiles[0].length;
//        setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        for (int row = 0; row < tiles.length; row++) {
//            for (int col = 0; col < tiles[row].length; col++) {
//                int tile = tiles[row][col];
//
//                // Vẽ màu theo giá trị tile
//                g.setColor(getTileColor(tile));
//                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
//
//                // Viền ô
//                g.setColor(Color.BLACK);
//                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
//            }
//        }
//    }
//
//    private Color getTileColor(int tile) {
//        return switch (tile) {
//            case 0 -> Color.WHITE;
//            case 33 -> Color.GRAY;
//            case 21 -> new Color(139, 69, 19); // nâu
//            case 42 -> Color.GREEN.darker();
//            default -> Color.CYAN; // mặc định cho block chưa định nghĩa
//        };
//    }
//
//    public static void main(String[] args) {
//        // Ví dụ tiles 2D từ database
//        int[][] tiles = {
//                {33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33},
//                {0, 0, 0, 33, 0, 0, 0, 0, 33, 0, 0},
//                {0, 21, 21, 21, 21, 21, 21, 21, 21, 21, 0},
//                {0, 0, 0, 0, 0, 42, 0, 0, 0, 0, 0},
//                {33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33}
//        };
//
//        JFrame frame = new JFrame("Map Viewer");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(new JScrollPane(new MapViewer(tiles))); // scroll nếu map lớn
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//}