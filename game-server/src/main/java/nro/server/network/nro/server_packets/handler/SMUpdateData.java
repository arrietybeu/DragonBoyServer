package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.data.*;
import nro.server.model.template.data.*;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.nio.ByteBuffer;
import java.util.List;

@ServerPacketCommand(ConstsCmd.UPDATE_DATA)
public class SMUpdateData extends NroServerPacket {

    private static byte[] CACHED_PACKET_DATA; // 188944 // 189046

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        if (CACHED_PACKET_DATA == null) {
            CACHED_PACKET_DATA = buildFullPacket();
            System.out.println("[SMUpdateData] Packet built and cached (" + CACHED_PACKET_DATA.length + " bytes)");
        }
        writeBytes(CACHED_PACKET_DATA);
    }

    private byte[] buildFullPacket() {
        ByteBuffer buf = ByteBuffer.allocate(2_000_000);

        buf.put((byte) ConfigServer.VERSION_DATA);

        writeByteArrayBlock(buf, buildNRDartData());
        writeByteArrayBlock(buf, buildNrArrowData());
        writeByteArrayBlock(buf, buildNrEffectData());
        writeByteArrayBlock(buf, buildNrImageData());
        writeByteArrayBlock(buf, buildNrPartData());
        writeByteArrayBlock(buf, buildNrSkillData());

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }

    private static void writeByteArrayBlock(ByteBuffer buf, byte[] bytes) {
        buf.putInt(bytes.length);
        buf.put(bytes);
    }

    private byte[] buildNRDartData() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);

        List<DartTemplate> darts = DartData.getInstance().darts;
        buf.putShort((short) darts.size());

        for (DartTemplate dart : darts) {
            buf.putShort((short) dart.getId());
            buf.putShort((short) dart.getNUpdate());
            buf.putShort((short) (dart.getVa() / 256));
            buf.putShort((short) dart.getXdPercent());

            writeShortArray(buf, dart.getTail());
            writeShortArray(buf, dart.getTailBorder());
            writeShortArray(buf, dart.getXd1());
            writeShortArray(buf, dart.getXd2());
            writeShort2DArray(buf, dart.getHead());
            writeShort2DArray(buf, dart.getHeadBorder());
        }

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }

    private byte[] buildNrArrowData() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        List<ArrowPaintTemplate> arrows = ArrowPaintData.getInstance().arrowPaintData;

        buf.putShort((short) arrows.size());
        for (ArrowPaintTemplate arrow : arrows) {
            buf.putShort((short) arrow.getId());
            int[] images = arrow.getImgId();
            buf.putShort((short) images[0]);
            buf.putShort((short) images[1]);
            buf.putShort((short) images[2]);
        }

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }

    private byte[] buildNrEffectData() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        List<EffectCharPaintTemplate> effects = EffectCharPaintData.getInstance().effectCharPaintTemplates;

        buf.putShort((short) effects.size());

        for (EffectCharPaintTemplate ef : effects) {
            buf.putShort((short) ef.getIdEf());

            EffectCharPaintTemplate.EffectInfoPaint[] infoList = ef.getArrEfInfo();
            buf.put((byte) infoList.length);

            for (EffectCharPaintTemplate.EffectInfoPaint info : infoList) {
                buf.putShort((short) info.idImg());
                buf.put((byte) info.dx());
                buf.put((byte) info.dy());
            }
        }

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }

    private byte[] buildNrImageData() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        int[][] versionImages = ImageData.getInstance().getVersionImage();

        buf.putShort((short) versionImages.length);
        for (int[] img : versionImages) {
            buf.put((byte) img[0]);
            buf.putShort((short) img[1]);
            buf.putShort((short) img[2]);
            buf.putShort((short) img[3]);
            buf.putShort((short) img[4]);
        }

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }

    private byte[] buildNrPartData() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        List<PartTemplate> parts = PartData.getInstance().templates;

        buf.putShort((short) parts.size());

        for (PartTemplate part : parts) {
            buf.put((byte) part.type());
            PartTemplate.PartImage[] pi = part.pi();
            for (PartTemplate.PartImage img : pi) {
                buf.putShort(img.id());
                buf.put(img.dx());
                buf.put(img.dy());
            }
        }

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }

    private byte[] buildNrSkillData() {
        ByteBuffer buf = ByteBuffer.allocate(1_000_000);

        List<SkillPaintTemplate> templates = SkillPaintData.getInstance().templates;

        buf.putShort((short) templates.size());

        for (int i = 0; i < templates.size(); i++) {
            SkillPaintTemplate tpl = templates.get(i);

            if (tpl == null) {
                buf.putShort((short) i);
                buf.putShort((short) 80);
                buf.put((byte) 0);
                buf.put((byte) 0);
                buf.put((byte) 0);
                continue;
            }

            short id = (short) tpl.getId();
            if (id == 1111) {
                id = (short) (templates.size() - 1);
            }

            buf.putShort(id);
            buf.putShort((short) tpl.getEffectHappenOnMob());
            buf.put((byte) tpl.getNumEff());

            SkillPaintTemplate.SkillInfoPaint[] stand = tpl.getSkillStand();
            buf.put((byte) stand.length);
            for (SkillPaintTemplate.SkillInfoPaint s : stand) {
                writeSkillInfo(buf, s);
            }

            SkillPaintTemplate.SkillInfoPaint[] fly = tpl.getSkillfly();
            buf.put((byte) fly.length);
            for (SkillPaintTemplate.SkillInfoPaint s : fly) {
                writeSkillInfo(buf, s);
            }
        }

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }

    private void writeSkillInfo(ByteBuffer buf, SkillPaintTemplate.SkillInfoPaint s) {
        buf.put((byte) s.status());
        buf.putShort((short) s.effS0Id());
        buf.putShort((short) s.e0dx());
        buf.putShort((short) s.e0dy());
        buf.putShort((short) s.effS1Id());
        buf.putShort((short) s.e1dx());
        buf.putShort((short) s.e1dy());
        buf.putShort((short) s.effS2Id());
        buf.putShort((short) s.e2dx());
        buf.putShort((short) s.e2dy());
        buf.putShort((short) s.arrowId());
        buf.putShort((short) s.adx());
        buf.putShort((short) s.ady());
    }

    private void writeShortArray(ByteBuffer buf, int[] arr) {
        buf.putShort((short) arr.length);
        for (int val : arr) {
            buf.putShort((short) val);
        }
    }

    private void writeShort2DArray(ByteBuffer buffer, int[][] arr) {
        buffer.putShort((short) arr.length);
        for (int[] sub : arr) {
            buffer.putShort((short) sub.length);
            for (int val : sub) {
                buffer.putShort((short) val);
            }
        }
    }

}
