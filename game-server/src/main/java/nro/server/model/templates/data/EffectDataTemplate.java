package nro.server.model.templates.data;

import lombok.Getter;
import lombok.Setter;
import nro.server.utils.FileNio;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * @author Arriety
 */
@Getter
@Setter
public class EffectDataTemplate {

    private final int id;
    private final int type;
    private byte[] img;

    private short[] arrFrame;
    private ImageInfo[] imgInfo;
    private int[][][] frame;
    private int typeData;
    public int[][] frameBigMonsters;

    private byte[] dataEffect;
    private byte[] dataEffectMonster;
    private byte[] dataEffectBigMonster;

    public EffectDataTemplate(int id, String jsonStr, int type) throws ParseException {
        this.id = id;
        this.type = type;
        this.parseEffectData(jsonStr);
    }

    private void parseEffectData(String jsonStr) throws ParseException {
        JSONArray effectDataArray = (JSONArray) JSONValue.parseWithException(jsonStr);
        if (effectDataArray == null || effectDataArray.size() < 3) {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, "Effect JSON structure is invalid.");
        }

        JSONArray imageInfoJsonArray = (JSONArray) effectDataArray.getFirst();
        int imageInfoCount = imageInfoJsonArray.size();
        imgInfo = new ImageInfo[imageInfoCount];
        for (int i = 0; i < imageInfoCount; i++) {
            JSONArray imageInfoEntry = (JSONArray) imageInfoJsonArray.get(i);
            ImageInfo info = new ImageInfo();
            info.setId(Integer.parseInt(imageInfoEntry.get(0).toString()));
            info.setX0(Integer.parseInt(imageInfoEntry.get(1).toString()));
            info.setY0(Integer.parseInt(imageInfoEntry.get(2).toString()));
            info.setW(Integer.parseInt(imageInfoEntry.get(3).toString()));
            info.setH(Integer.parseInt(imageInfoEntry.get(4).toString()));
            imgInfo[i] = info;
        }

        JSONArray frameJsonArray = (JSONArray) effectDataArray.get(1);
        int frameCount = frameJsonArray.size();
        frame = new int[frameCount][][];
        for (int i = 0; i < frameCount; i++) {
            JSONArray frameDetailJsonArray = (JSONArray) frameJsonArray.get(i);
            int frameDetailCount = frameDetailJsonArray.size();
            frame[i] = new int[frameDetailCount][];
            for (int j = 0; j < frameDetailCount; j++) {
                JSONArray framePointJsonArray = (JSONArray) frameDetailJsonArray.get(j);
                int[] framePoint = new int[3];
                for (int k = 0; k < 3; k++) {
                    framePoint[k] = Integer.parseInt(framePointJsonArray.get(k).toString());
                }
                frame[i][j] = framePoint;
            }
        }

        JSONArray arrFrameJsonArray = (JSONArray) effectDataArray.get(2);
        int arrFrameLength = arrFrameJsonArray.size();
        arrFrame = new short[arrFrameLength];
        for (int i = 0; i < arrFrameLength; i++) {
            arrFrame[i] = Short.parseShort(arrFrameJsonArray.get(i).toString());
        }
    }

    public void loadImageEffect(int zoomLevel) {
        String filePath = String.format("resources/x%d/effect/%d.png", zoomLevel, id);
        this.img = FileNio.loadDataFileCache(filePath);
    }

    public void loadImageMonster(int zoomLevel, int typeData) {
        this.typeData = typeData;
        String filePath = String.format("resources/x%d/monster/%d.png", zoomLevel, id);
        this.img = FileNio.loadDataFileCache(filePath);
    }

    public void loadFrameBigMonster(String str) throws ParseException {
        JSONArray jarr = (JSONArray) JSONValue.parseWithException(str);
        this.frameBigMonsters = new int[jarr.size()][];
        for (int i = 0; i < jarr.size(); i++) {
            JSONArray jarr2 = (JSONArray) jarr.get(i);
            this.frameBigMonsters[i] = new int[jarr2.size()];
            for (int j = 0; j < jarr2.size(); j++) {
                this.frameBigMonsters[i][j] = Byte.parseByte(jarr2.get(j).toString());
            }
        }
    }

    @Setter
    @Getter
    public static class ImageInfo {
        private int id;
        private int x0;
        private int y0;
        private int w;
        private int h;
    }
}
