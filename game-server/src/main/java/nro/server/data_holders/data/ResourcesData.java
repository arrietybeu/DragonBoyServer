package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.database.Database;
import nro.server.data_holders.GameEngine;
import nro.server.model.templates.data.EffectDataTemplate;
import nro.server.utils.FileNio;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Arriety
 */
@Getter
public final class ResourcesData implements GameEngine {

    private static final Logger log = LoggerFactory.getLogger(ResourcesData.class);


    private final Map<Short, Map<Byte, EffectDataTemplate>> effectData = new HashMap<>();
    private final Map<Short, Map<Byte, EffectDataTemplate>> effectDataMonster = new HashMap<>();
//    private final Map<Byte, HashMap<String, ImageByName>> dataImageByName = new HashMap<>();

    private final Map<Byte, byte[]> dataSmallVersion = new HashMap<>();
    private byte[][] backgroundVersion;

    @Override
    public void init() throws Throwable {
        logExecutionTime("loadEffectData", this::loadEffectData);
        logExecutionTime("loadEffectDataMonster", this::loadEffectDataMonster);
        initSmallVersion();
        initBgSmallVersion();
    }

    private void logExecutionTime(String methodName, Runnable method) {
        // long start = System.currentTimeMillis();
        method.run();
        // long elapsedTime = System.currentTimeMillis() - start;
        // LogServer.DebugLogic(methodName + " executed in " + elapsedTime + " ms");
    }

    @Override
    public void reload() throws Throwable {
        clear();
        initSmallVersion();
        log.info("ResourcesData reloaded successfully.");
    }

    @Override
    public void clear() throws Throwable {
        dataSmallVersion.clear();
        log.info("ResourcesData cleared successfully.");
    }

    private void loadEffectData() {
        String sql = "SELECT * FROM data_effect";
        Database.select(sql, rs -> {
            while (rs.next()) {
                HashMap<Byte, EffectDataTemplate> effdatas = new HashMap<>();
                var idEffect = rs.getShort(1);
                var json = rs.getString("data");
                var type = rs.getByte("type");

                for (byte i = 1; i <= 4; i++) {
                    EffectDataTemplate effect;
                    try {
                        effect = new EffectDataTemplate(idEffect, json, type);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    effect.loadImageEffect(i);
                    effect.setDataEffect(this.writeDataMonsterOld(effect));
                    effdatas.put(i, effect);
                }
                effectData.put(idEffect, effdatas);
            }
        });
    }

    private void loadEffectDataMonster() {
        String sql = "SELECT * FROM data_monster";
        Database.select(sql, rs -> {
            while (rs.next()) {
                HashMap<Byte, EffectDataTemplate> effdataMonsters = new HashMap<>();
                var idMonster = rs.getShort(1);
                var type = rs.getByte("type");
                var typeData = rs.getByte("type_data");
                var json = rs.getString("data");
                var json2 = rs.getString("data_big_monster");
                for (byte i = 1; i <= 4; i++) {
                    EffectDataTemplate effect;
                    try {
                        effect = new EffectDataTemplate(idMonster, json, type);
                        effect.loadImageMonster(i, typeData);
                        effect.setDataEffectMonster(this.writeDataMonsterOld(effect));
                        effect.loadFrameBigMonster(json2);
                        effect.setDataEffectBigMonster(this.writeFrameBigMonster(effect));
                        effdataMonsters.put(i, effect);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                this.effectDataMonster.put(idMonster, effdataMonsters);
            }
        });
    }

    private byte[] writeDataMonsterOld(EffectDataTemplate eff) {

        int cap = 1 + eff.getImgInfo().length * 5
                + 2 + java.util.Arrays.stream(eff.getFrame()).mapToInt(fr -> 1 + fr.length * 5).sum()
                + 2 + eff.getArrFrame().length * 2;

        ByteBuffer data = ByteBuffer.allocate(cap);


        if (eff.getImgInfo().length > Byte.MAX_VALUE) {
            throw new RuntimeException("ImgInfo too large");
        }

        data.put((byte) eff.getImgInfo().length);
        for (int i = 0; i < eff.getImgInfo().length; i++) {
            data.put((byte) eff.getImgInfo()[i].getId());
            data.put((byte) eff.getImgInfo()[i].getX0());
            data.put((byte) eff.getImgInfo()[i].getY0());
            data.put((byte) eff.getImgInfo()[i].getW());
            data.put((byte) eff.getImgInfo()[i].getH());
        }

        data.putShort((short) eff.getFrame().length);
        for (int j = 0; j < eff.getFrame().length; j++) {
            data.put((byte) eff.getFrame()[j].length);
            for (int k = 0; k < eff.getFrame()[j].length; k++) {
                data.putShort((short) eff.getFrame()[j][k][0]);
                data.putShort((short) eff.getFrame()[j][k][1]);
                data.put((byte) eff.getFrame()[j][k][2]);
            }
        }

        data.putShort((short) eff.getArrFrame().length);
        for (int l = 0; l < eff.getArrFrame().length; l++) {
            data.putShort(eff.getArrFrame()[l]);
        }

        data.flip();
        byte[] out = new byte[data.remaining()];

        data.get(out);
        return out;
    }

    private byte[] writeFrameBigMonster(EffectDataTemplate eff) {
        ByteBuffer data = ByteBuffer.allocate(100_000);

        data.put((byte) eff.frameBigMonsters.length);
        for (int i = 0; i < eff.frameBigMonsters.length; i++) {
            data.put((byte) eff.frameBigMonsters[i].length);
            for (int j = 0; j < eff.frameBigMonsters[i].length; j++) {
                data.put((byte) eff.frameBigMonsters[i][j]);
            }
        }
        data.flip();
        byte[] out = new byte[data.remaining()];

        data.get(out);
        return out;
    }

    private void initSmallVersion() {
        this.dataSmallVersion.clear();
        try {
            for (int i = 1; i <= 4; i++) {
                byte[] array = new byte[Short.MAX_VALUE];
                File dir = new File(String.format("resources/x%d/icon", i));
                File[] files = dir.listFiles();
                if (files == null) continue;
                int max = -1;
                for (File file : files) {
                    String name = file.getName();
                    try {
                        if (name.contains(".")) name = name.substring(0, name.lastIndexOf("."));

                        int id = Integer.parseInt(name);
                        array[id] = (byte) (file.length() % 127);
                        if (id > max) max = id;

                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý file: " + file.getName() + " - " + e.getMessage());
                    }
                }
                if (max >= 0) this.dataSmallVersion.put((byte) i, Arrays.copyOf(array, max + 1));
            }
        } catch (Exception e) {
            log.error("Error initializing small version resources: {}", e.getMessage(), e);
        }
    }

    private void initBgSmallVersion() {
        try {
            this.backgroundVersion = new byte[4][];
            for (int i = 1; i <= 4; i++) {
                String path = "resources/x" + i + "/image_background";
                File file = new File(path);
                File[] files = file.listFiles();

                if (files == null || files.length == 0) {
                    log.warn("No files found in path: {}", path);
                    this.backgroundVersion[i] = new byte[0];
                    continue;
                }

                try {
                    backgroundVersion[i - 1] = processFiles(files, path);
                } catch (Exception e) {
                    log.warn("Error processing files in path: {} - {}", path, e.getMessage());
                    backgroundVersion[i - 1] = new byte[0];
                }
            }
        } catch (Exception e) {
            log.error("Error initializing background small version resources: {}", e.getMessage(), e);
        }
    }

    private byte[] processFiles(File[] files, String path) {
        int max = 0;
        List<Integer> ids = new ArrayList<>(files.length);

        for (File f : files) {
            String name = f.getName();
            try {
                int id = Integer.parseInt(FileNio.cutPng(name));
                ids.add(id);
                if (id > max) {
                    max = id;
                }
            } catch (NumberFormatException e) {
                log.error("Error processing file: {} - {}", f.getName(), e.getMessage());
                ids.add(-1);
            }
        }

        byte[] fileData = new byte[max + 1];

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            int id = ids.get(i);
            if (id < 0) {
                continue;
            }
            try {
                fileData[id] = (byte) (f.length() % 127);
            } catch (Exception e) {
                log.error("Error reading file: {} - {}", f.getName(), e.getMessage());
            }
        }
        return fileData;
    }


    public EffectDataTemplate getMonsterData(short id, byte zoomLevel) {
        try {
            return effectDataMonster.get(id).get(zoomLevel);
        } catch (Exception e) {
            return null;
        }
    }

    public EffectDataTemplate getEffectData(short id, byte zoomLevel) {
        try {
            return effectData.get(id).get(zoomLevel);
        } catch (Exception e) {
            return null;
        }
    }

    private static final class SingletonHolder {
        private static final ResourcesData INSTANCE = new ResourcesData();
    }

    public static ResourcesData getInstance() {
        return ResourcesData.SingletonHolder.INSTANCE;
    }
}
