package nro.server.service.core.system;

import nro.consts.ConstsCmd;
import nro.server.network.Message;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.utils.CaptchaUtil;
import nro.utils.Rnd;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaptchaService {

    private static final class SingletonHolder {
        private static final CaptchaService instance = new CaptchaService();
    }

    public static CaptchaService getInstance() {
        return CaptchaService.SingletonHolder.instance;
    }

    public void sendCaptcha(Player player, int type) {
        try (Message message = new Message(ConstsCmd.MOB_CAPCHA)) {
            DataOutputStream writer = message.writer();

            // generate dynamic rules for the captcha
            CaptchaUtil.generateDynamicRules();

            String sampleDigits = generateCaptchaDigits();
            String captcha = this.shuffleDigits(sampleDigits);

            writer.writeByte(type);
            if (type == 0) {
                System.out.println("keyword: " + sampleDigits);
                byte[] data = CaptchaUtil.createCaptchaToBytes(sampleDigits);
                writer.writeShort(data.length);
                writer.write(data);
                writer.writeUTF(captcha);
            }

            player.sendMessage(message);
        } catch (Exception ex) {
            LogServer.LogException("Error in sendCaptcha: " + ex.getMessage(), ex);
        }
    }

    private String generateCaptchaDigits() {
        List<Character> validDigits = new ArrayList<>(CaptchaUtil.REVERSE_MAP.keySet());
        Collections.shuffle(validDigits);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CaptchaUtil.DIGITS_LENGTH; i++) sb.append(validDigits.get(i));
        return sb.toString();
    }

    private String shuffleDigits(String input) {
        List<Character> hintDigits = new ArrayList<>();
        int obfuscation = Rnd.nextInt(0, 5);
        for (char c : input.toCharArray()) hintDigits.add(c);
        Collections.shuffle(hintDigits);
        StringBuilder shuffled = new StringBuilder();
        for (char c : hintDigits) shuffled.append(c);

        // add obfuscation
        shuffled.append(obfuscation);

        return shuffled.toString();
    }

}
