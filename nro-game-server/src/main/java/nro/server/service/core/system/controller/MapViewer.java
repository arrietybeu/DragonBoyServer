package nro.server.service.core.system.controller;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapViewer extends JPanel {

    private static final int TILE_SIZE = 24;
    private final int[][] tiles;
    private final int tileSetId;
    private final Map<Integer, Image> tileImageCache = new HashMap<>();

    public MapViewer(int tileSetId, int[][] tiles) {
        this.tileSetId = tileSetId;
        this.tiles = tiles;
        int rows = tiles.length;
        int cols = tiles[0].length;
        setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                int tileIndex = tiles[row][col];

                Image img = getTileImage(tileIndex);
                if (img != null) {
                    g.drawImage(img, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                } else {
                    g.setColor(getTileColor(tileIndex));
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }

                // Vẽ viền
                g.setColor(Color.BLACK);
                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Vẽ số tile index
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.PLAIN, 10));
                String text = String.valueOf(tileIndex);
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int x = col * TILE_SIZE + (TILE_SIZE - textWidth) / 2;
                int y = row * TILE_SIZE + (TILE_SIZE + textHeight) / 2 - 2;
                g.drawString(text, x, y);
            }
        }
    }

    private Image getTileImage(int index) {
        if (tileImageCache.containsKey(index)) {
            return tileImageCache.get(index);
        }

        String path = "resources/x4/res/x4/t/" + tileSetId + "$" + index + ".png";
        File file = new File(path);
        if (file.exists()) {
            Image img = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
            tileImageCache.put(index, img);
            return img;
        }

        return null;
    }

    private Color getTileColor(int tile) {
        return switch (tile) {
            case 0 -> Color.WHITE;
            case 33 -> Color.GRAY;
            case 21 -> new Color(139, 69, 19);
            case 42 -> Color.GREEN.darker();
            default -> Color.CYAN;
        };
    }

    public static void main(String[] args) {
        int tileSetId = 1;
        int width = 64;
        int height = 24;

        int[] flatTiles = new int[]{39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 36, 8, 8, 35, 8, 9, 8, 16, 7, 8, 7, 8, 8, 8, 7, 35, 8, 9, 8, 16, 7, 8, 7, 8, 8, 0, 0, 14, 15, 8, 13, 8, 10, 11, 8, 8, 8, 11, 8, 10, 16, 17, 8, 14, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 5, 6, 3, 4, 4, 5, 6, 4, 4, 4, 6, 4, 4, 4, 4, 31, 31, 31, 31, 32, 31, 32, 32, 32, 39, 39, 2, 2, 3, 3, 2, 4, 5, 6, 31, 32, 31, 32, 31, 32, 31, 32, 2, 18, 16, 8, 16, 8, 13, 8, 14, 15, 8, 8, 7, 13, 8, 8, 13, 8, 7, 8, 23, 25, 26, 26, 25, 26, 26, 27, 26, 26, 27, 26, 26, 25, 26, 26, 24, 33, 34, 33, 34, 33, 34, 34, 33, 33, 39, 39, 27, 26, 26, 27, 26, 26, 27, 24, 33, 34, 33, 34, 33, 34, 33, 33, 23, 24, 32, 31, 32, 1, 3, 5, 6, 18, 31, 32, 31, 1, 5, 6, 18, 31, 32, 31, 23, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 21, 22, 22, 22, 22, 20, 21, 24, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 33, 33, 34, 23, 25, 25, 26, 24, 33, 34, 33, 23, 27, 27, 24, 33, 33, 33, 23, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 21, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 22, 22, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 0, 0, 0, 23, 22, 22, 21, 24, 0, 0, 0, 23, 21, 22, 24, 0, 0, 0, 23, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 22, 22, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 0, 0, 0, 23, 22, 22, 22, 24, 0, 0, 0, 23, 22, 22, 24, 0, 0, 0, 23, 22, 22, 22, 22, 22, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 22, 21, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 0, 0, 0, 23, 22, 21, 22, 24, 0, 0, 0, 23, 22, 22, 24, 0, 0, 0, 23, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 22, 22, 22, 22, 21, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 23, 24, 0, 0, 0, 23, 19, 22, 22, 24, 0, 0, 0, 23, 20, 21, 24, 0, 0, 0, 23, 22, 22, 22, 22, 22, 22, 22, 22, 22, 21, 22, 22, 22, 22, 22, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 39};

        int[][] tiles = new int[height][width];
        for (int i = 0; i < flatTiles.length; i++) {
            int row = i / width;
            int col = i % width;
            tiles[row][col] = flatTiles[i];
        }

        JFrame frame = new JFrame("Map Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(new MapViewer(tileSetId, tiles)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
