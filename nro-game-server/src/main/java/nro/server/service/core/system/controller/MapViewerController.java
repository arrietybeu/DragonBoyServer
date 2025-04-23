package nro.server.service.core.system.controller;

import nro.consts.ConstTypeObject;
import nro.server.manager.MapManager;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.map.areas.Area;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MapViewerController {
    private JComboBox<GameMap> mapSelector;
    private JComboBox<AreaDisplayWrapper> areaSelector;
    private MapViewerPanel mapViewerPanel;

    public MapViewerController() {
        initUI();
        startRealtimeUpdate();
    }

    private void initUI() {
        mapSelector = new JComboBox<>(MapManager.getInstance().getGameMaps().values().toArray(new GameMap[0]));
        areaSelector = new JComboBox<>();

        mapSelector.addActionListener(e -> {
            GameMap selectedMap = (GameMap) mapSelector.getSelectedItem();
            if (selectedMap != null) {
                updateAreaSelector(selectedMap);
                areaSelector.setSelectedIndex(0);
                mapViewerPanel.setGameMap(selectedMap, areaSelector.getItemAt(0).area());
            }
        });

        areaSelector.addActionListener(e -> {
            AreaDisplayWrapper wrapper = (AreaDisplayWrapper) areaSelector.getSelectedItem();
            if (wrapper != null) {
                mapViewerPanel.setGameMap((GameMap) Objects.requireNonNull(mapSelector.getSelectedItem()), wrapper.area());
            }
        });

        mapViewerPanel = new MapViewerPanel();

        JFrame frame = new JFrame("Map Viewer Real-time");
        frame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(mapSelector);
        topPanel.add(areaSelector);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(mapViewerPanel), BorderLayout.CENTER);

        frame.setSize(1200, 800);
        frame.setVisible(true);
    }

    private void startRealtimeUpdate() {
        Timer refreshTimer = new Timer(100, e -> {
            mapViewerPanel.repaint();
            updateAreaSelectorLabels();
        });
        refreshTimer.start();
    }

    private void updateAreaSelector(GameMap map) {
        areaSelector.removeAllItems();
        for (Area area : map.getAreas()) {
            areaSelector.addItem(new AreaDisplayWrapper(area));
        }
    }

    private void updateAreaSelectorLabels() {
        GameMap selectedMap = (GameMap) mapSelector.getSelectedItem();
        if (selectedMap == null) return;

        AreaDisplayWrapper selected = (AreaDisplayWrapper) areaSelector.getSelectedItem();

        areaSelector.removeAllItems();
        for (Area area : selectedMap.getAreas()) {
            areaSelector.addItem(new AreaDisplayWrapper(area));
        }

        // giữ lại area đang chọn nếu có
        if (selected != null) {
            for (int i = 0; i < areaSelector.getItemCount(); i++) {
                if (Objects.equals(areaSelector.getItemAt(i).area(), selected.area())) {
                    areaSelector.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public record AreaDisplayWrapper(Area area) {
        @Override
        public String toString() {
            int playerCount = area.getEntitysByType(ConstTypeObject.TYPE_PLAYER).size();
            return String.format("Area %d players [%d/%d]", area.getId(), playerCount, area.getMaxPlayers());
        }
    }
}
