package nro.service;

import nro.consts.ConstMsgNotMap;
import nro.model.player.Player;
import nro.model.resources.Effect;
import nro.model.resources.ImageByName;
import nro.model.template.CaptionTemplate;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.config.ConfigServer;
import nro.model.resources.DataGame;
import nro.server.manager.CaptionManager;
import nro.server.manager.MapManager;
import nro.server.manager.skill.SkillPaintManager;
import nro.server.manager.resources.ResourcesManager;
import nro.server.manager.resources.PartManager;
import nro.utils.FileNio;
import nro.server.LogServer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ResourceService {

    private final Map<Integer, List<File>> cachedResources = new HashMap<>();

    private static ResourceService instance;

    public static ResourceService getInstance() {
        if (instance == null) {
            instance = new ResourceService();
        }
        return instance;
    }

    public void createData(Session session) {
        try {
            try (Message message = new Message(-87)) {
                byte[][] date = this.getBytes();
                message.writer().writeByte(ConfigServer.VERSION_DATA);
                for (byte[] data : date) {
                    this.writeData(message, data);
                }
                session.doSendMessage(message);
            } catch (Exception e) {
                LogServer.LogException("Error in createData (Message): " + e.getMessage());
            }
        } catch (Exception e) {
            LogServer.LogException("Error in createData: " + e.getMessage());
        }
    }

    private byte[][] getBytes() {
        DataGame dataGame = DataGame.getInstance();
        PartManager partManager = PartManager.getInstance();
        SkillPaintManager skillPaintManager = SkillPaintManager.getInstance();

        return new byte[][]{dataGame.getDart(),
                dataGame.getArrow(),
                dataGame.getEffect(),
                dataGame.getImage(),
                partManager.getDataPart(),
                // dataGame.getSkill()};
                skillPaintManager.getSkillPaintsData()};
    }

    private void writeData(Message message, byte[] data) throws IOException {
        message.writer().writeInt(data.length);
        message.writer().write(data);
    }

    public void downloadResources(Session session, Message ms) {
        int zoomLevel = session.getClientInfo().getZoomLevel();
        List<File> datas = this.getCachedResources(zoomLevel);
        try {
            byte type = ms.reader().readByte();
            switch (type) {
                case 1:
                    this.sendNumberOfFiles(session, (short) datas.size());
                    break;
                case 2:
                    for (File file : datas) {
                        this.fileTransfer(session, file);
                    }
                    this.fileTransferCompleted(session, zoomLevel);
                    break;
                case 3:
                    // done downloading resources
                    break;
                default:
                    LogServer.LogException("Error downloadResources: Unknown type " + type);
                    break;
            }
        } catch (Exception ex) {
            LogServer.LogException("Error downloadResources: " + ex.getMessage());
        }
    }

    public void sendImageRes(Session session) {
        int zoomLevel = session.getClientInfo().getZoomLevel();
        int size = ResourcesManager.getInstance().getVersionImage(zoomLevel);
        try (Message message = new Message(-74)) {
            message.writer().writeByte(0);
            message.writer().writeInt(size);
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendImageRes: " + e.getMessage());
        }
    }

    public void sendImageRes(Session session, int id) {
        try (Message message = new Message(-67)) {
            String path = "resources/x" + session.getClientInfo().getZoomLevel() + "/icon/" + id + ".png";
            byte[] data = FileNio.loadDataFile(path);
            message.writer().writeInt(id);
            if (data == null) {
                LogServer.LogException("Error sendImageRes: Data is null: " + path);
                return;
            }
            message.writer().writeInt(data.length);
            message.writer().write(data);
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendImageRes: " + e.getMessage());
        }
    }

    private void sendNumberOfFiles(Session session, short size) {
        try (Message msg = new Message(-74)) {
            msg.writer().writeByte(1);
            msg.writer().writeShort(size);
            session.doSendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendNumberOfFiles: " + e.getMessage());
        }
    }

    private void fileTransfer(Session session, File file) {
        try (Message mss = new Message(-74)) {
            String strPath = FileNio.cutPng(file.getPath().replace("\\", "/"));
            DataOutputStream ds = mss.writer();
            ds.writeByte(2);
            ds.writeUTF(strPath);
            byte[] ab = FileNio.loadDataFile(file.getPath());
            if (ab == null) {
                LogServer.LogException("Error fileTransfer: Data is null: " + file.getPath());
                return;
            }
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            session.doSendMessage(mss);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSmallVersion(Session session) {
        var res = ResourcesManager.getInstance();
        var zoomLevel = session.getClientInfo().getZoomLevel();
        if (zoomLevel < 1 || zoomLevel > 4) {
            throw new IllegalArgumentException("Invalid zoom level: " + zoomLevel);
        }
        byte[] smallVersion = res.getDataSmallVersion().get((byte) zoomLevel);
        try (Message message = new Message(-77)) {
            message.writer().writeShort(smallVersion.length);
            message.writer().write(smallVersion);
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendSmallVersion: " + e.getMessage());
        }
    }

    public void sendBackgroundVersion(Session session) {
        var res = ResourcesManager.getInstance();
        var zoomLevel = session.getClientInfo().getZoomLevel();
        if (zoomLevel < 1 || zoomLevel > 4) {
            throw new IllegalArgumentException("Invalid zoom level: " + zoomLevel);
        }
        byte[][] backgroundVersion = res.getBackgroundVersion();
        byte[] data = backgroundVersion[zoomLevel - 1];

        try (Message message = new Message(-93)) {
            message.writer().writeShort(data.length);
            message.writer().write(data);
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendBackgroundVersion: " + e.getMessage());
        }
    }

    private void fileTransferCompleted(Session session, int zoomLevel) {
        int version = ResourcesManager.getInstance().getVersionImage(zoomLevel);
        try (Message msg = new Message(-74)) {
            msg.writer().writeByte(3);
            msg.writer().writeInt(version);
            session.doSendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error fileTransferCompleted: " + e.getMessage());
        }
    }

    private List<File> getCachedResources(int zoomLevel) {
        return this.cachedResources.computeIfAbsent(zoomLevel, level -> {
            String path = "resources/x" + level + "/res";
            File root = new File(path);

            if (!root.exists() || !root.isDirectory()) {
                throw new IllegalArgumentException("Invalid resource path: " + path);
            }

            ArrayList<File> resources = new ArrayList<>();
            FileNio.addPath(resources, root);

            return resources;
        });
    }

    public void sendDataImageVersion(Session session) {
        var zoomLevel = session.getClientInfo().getZoomLevel();
        String path = "resources/x" + zoomLevel + "/image_source/image_source";
        try (Message msg = new Message(-111)) {
            msg.writer().write(Objects.requireNonNull(FileNio.loadDataFile(path)));
            session.doSendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendDataImageVersion: " + e.getMessage());
        } finally {
            ResourceService.getInstance().sendImageRes(session);
        }
    }

    public void sendDataBackgroundMap(Session session, int id) {
        try (Message message = new Message(-32)) {
            String path = "resources/x" + session.getClientInfo().getZoomLevel() + "/image_background/" + id + ".png";
            byte[] data = FileNio.loadDataFile(path);
            message.writer().writeShort(id);
            assert data != null : "Data Background is null: " + id;
            message.writer().writeInt(data.length);
            message.writer().write(data);
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error requestBackgroundTemplate: " + e.getMessage());
        }
    }

    public void sendDataBackgroundMapTemplate(Session session) {
        try (Message msg = new Message(-31)) {
            MapManager mapManager = MapManager.getInstance();
            byte[] item_bg = mapManager.getBackgroundMapData();
            msg.writer().write(item_bg);
            session.doSendMessage(msg);
        } catch (Exception e) {
            LogServer.LogException("Error sendDataBackgroundMapTemplate: " + e.getMessage());
        }
    }

    public void sendVersionDataGame(Session session) {
        if (session.getSessionInfo().isUpdateItem()) {
            return;
        }
        try (Message message = new Message(-28)) {
            message.writer().writeByte(ConstMsgNotMap.SEND_VERSION);// type 4
            message.writer().writeByte(ConfigServer.VERSION_DATA);// GameScr.vsData = msg.reader().readByte();
            message.writer().writeByte(ConfigServer.VERSION_MAP);// GameScr.vsMap = msg.reader().readByte();
            message.writer().writeByte(ConfigServer.VERSION_SKILL);// GameScr.vsSkill = msg.reader().readByte();
            message.writer().writeByte(ConfigServer.VERSION_ITEM);// GameScr.vsItem = msg.reader().readByte();
            message.writer().writeByte(0);
            List<CaptionTemplate> captionTemplates = CaptionManager.getInstance().getCAPTIONS();
            message.writer().writeByte(captionTemplates.size());
            for (CaptionTemplate caption : captionTemplates) {
                message.writer().writeLong(caption.getExp());
            }
            session.getSessionInfo().setUpdateItem(true);
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendVersionDataGame: " + e.getMessage());
        }
    }

    public void sendTileSetInfo(Session session) {
        try (Message message = new Message(-82)) {
            byte[] data = FileNio.loadDataFile("resources/data/tile_data/tile_set_info");
            if (data == null)
                return;
            message.writer().write(data);
            session.doSendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error sendTileSetInfo: " + e.getMessage());
        }
    }

    public void sendMonsterData(Player player, short id) {
        try (Message message = new Message(11)) {
            ResourcesManager manager = ResourcesManager.getInstance();
            Effect effect = manager.getMonsterData(id, (byte) player.getSession().getClientInfo().getZoomLevel());
            DataOutputStream data = message.writer();
            data.writeShort(id);
            data.writeByte(effect.getType());

            if (effect.getType() != 0) {
                // data.write(effect.get());
            } else {
                data.writeInt(effect.getDataEffectMonster().length);
                data.write(effect.getDataEffectMonster());
            }

            data.writeInt(effect.getImg().length);
            data.write(effect.getImg());

            data.writeByte(effect.getTypeData());
            if (effect.getTypeData() == 1 || effect.getTypeData() == 2) {
                data.write(effect.getDataEffectBigMonster());
            }

            player.sendMessage(message);
        } catch (Exception e) {
            LogServer.LogException("Error requestMobTemplate: " + e.getMessage(), e);
        }
    }

    public void sendEffectData(Player player, short id) {
        try (Message message = new Message(-66)) {
            ResourcesManager manager = ResourcesManager.getInstance();
            Effect effect = manager.getEffectData(id, (byte) player.getSession().getClientInfo().getZoomLevel());
            DataOutputStream data = message.writer();
            data.writeShort(id);
            if (effect.getType() == 0) {
                data.writeInt(effect.getDataEffect().length);
                data.write(effect.getDataEffect());
            } else {
                // data new boss
            }
            data.writeByte(effect.getType());
            data.writeInt(effect.getImg().length);
            data.write(effect.getImg());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error send Effect Data id: " + id + " " + ex.getMessage());
        }
    }

    public void sendImageByName(Player player, String img) {
        var zoomLevel = player.getSession().getClientInfo().getZoomLevel();
        if (zoomLevel < 1 || zoomLevel > 4) {
            LogServer.LogException("Send Image Error ZoomLevel: " + zoomLevel);
            return;
        }
        ImageByName imageByName = ResourcesManager.getInstance().getDataImageByName().get((byte) zoomLevel).get(img);
        if (imageByName == null) {
            LogServer.LogException("Error Send Image By Name: " + img + " zoomLevel: " + zoomLevel);
            return;
        }
        try (Message message = new Message(66)) {
            DataOutputStream data = message.writer();
            data.writeUTF(imageByName.name());
            data.writeByte(imageByName.frame());
            data.writeInt(imageByName.data().length);
            data.write(imageByName.data());
            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("sendImageByName: " + ex.getMessage() + " Error Send Image By Name: " + img
                    + " zoomLevel: " + zoomLevel);
            ex.printStackTrace();
        }
    }

    // public void sendTileSetInfo(Session session) {
    // try (Message message = new Message(-82)) {
    // MapManager mapManager = MapManager.getInstance();
    // var data = mapManager.getTileSetData();
    // message.writer().write(data);
    // session.sendMessage(message);
    // } catch (Exception e) {
    // LogServer.LogException("Error sendTileSetInfo: " + e.getMessage());
    // }
    // }

    public void clientOk(Session session) {
        if (!session.getSessionInfo().isClientOk()) {

            this.sendDataBackgroundMapTemplate(session);// -31
            this.sendTileSetInfo(session); // -82
            this.sendSmallVersion(session);// -77
            this.sendBackgroundVersion(session);// -93
            session.getSessionInfo().setClientOk(true);
        }
    }

    public void sendResourcesLogin(Session session) {
        var zoomLevel = session.getClientInfo().getZoomLevel();
        if (zoomLevel < 1 || zoomLevel > 4) {
            LogServer.LogException("Error sendResourcesLogin: Invalid zoom level: " + zoomLevel);
            return;
        }
        this.sendSmallVersion(session);// -77
        this.sendBackgroundVersion(session);// -93
        this.sendVersionDataGame(session);
    }

}
