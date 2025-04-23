package nro.server.service.core.system.controller;

import nro.consts.ConstTypeObject;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.map.areas.Area;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapViewerPanel extends JPanel {

    private final Map<Integer, Image> tileImageCache = new HashMap<>();
    private GameMap gameMap;
    private Area currentArea;

    public void setGameMap(GameMap map, Area area) {
        this.gameMap = map;
        this.currentArea = area;
        this.setPreferredSize(new Dimension(map.getPixelWidth(), map.getPixelHeight()));
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameMap == null) return;

        // Vẽ tile nền
        drawTiles(g);

        // Vẽ người chơi
        drawPlayers(g);
    }

    private void drawTiles(Graphics g) {
        if (gameMap == null || gameMap.getTileMap() == null) return;

        int tileSetId = gameMap.getTileId(); // lấy từ GameMap
        int[] tiles = gameMap.getTileMap().tiles();
        int width = gameMap.getTileMap().width();

        for (int i = 0; i < tiles.length; i++) {
            int row = i / width;
            int col = i % width;
            int tileIndex = tiles[i];

            Image img = getTileImage(tileSetId, tileIndex);
            if (img != null) {
                g.drawImage(img, col * 24, row * 24, 24, 24, this);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(col * 24, row * 24, 24, 24);
            }

            g.setColor(Color.BLACK);
            g.drawRect(col * 24, row * 24, 24, 24);

            //draw tile id
//             g.drawString(String.valueOf(tileIndex), col * 24 + 6, row * 24 + 16);
        }
    }

    private Image getTileImage(int tileSetId, int tileIndex) {
        int key = tileSetId * 1000 + tileIndex;
        if (tileImageCache.containsKey(key)) return tileImageCache.get(key);

        String path = "resources/x4/res/x4/t/" + tileSetId + "$" + tileIndex + ".png";
        File file = new File(path);
        if (file.exists()) {
            Image img = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
            tileImageCache.put(key, img);
            return img;
        }
        return null;
    }

    private void drawPlayers(Graphics g) {
        if (currentArea == null) return;

        g.setColor(Color.RED);
        Collection<Entity> players = currentArea.getEntitysByType(ConstTypeObject.TYPE_PLAYER);
        for (Entity entity : players) {
            Player player = (Player) entity;
            int px = player.getX();
            int py = player.getY();

            g.fillOval(px - 6, py - 6, 12, 12); // avatar người chơi
            g.drawString(player.getName(), px - 10, py - 8); // tên
        }
    }
}
