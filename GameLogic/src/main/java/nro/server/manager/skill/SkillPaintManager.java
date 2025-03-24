package nro.server.manager.skill;

import lombok.Getter;
import nro.server.network.Message;
import nro.server.config.ConfigDB;
import nro.service.model.template.skill.SkillPaintTemplate;
import nro.service.repositories.DatabaseConnectionPool;
import nro.service.model.template.entity.SkillPaintInfo;
import nro.server.manager.IManager;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class SkillPaintManager implements IManager {

    @Getter
    private static final SkillPaintManager instance = new SkillPaintManager();
    private List<SkillPaintTemplate> skillPaintTemplateList;
    private byte[] skillPaintsData;

    @Override
    public void init() {
        this.loadSkillPaint();

    }

    @Override
    public void reload() {
    }

    @Override
    public void clear() {
    }

    private void loadSkillPaint() {
        String querySkillPaint = "SELECT * FROM skill_paint";
        String querySkillStand = "SELECT * FROM skill_stand WHERE id_skill_paint = ?";
        String querySkillFly = "SELECT * FROM skill_fly WHERE id_skill_paint = ?";

        this.skillPaintTemplateList = new ArrayList<>();
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement psSkillPaint = connection.prepareStatement(querySkillPaint)) {

            ResultSet rsSkillPaint = psSkillPaint.executeQuery();

            while (rsSkillPaint.next()) {
                SkillPaintTemplate skillPaintTemplate = new SkillPaintTemplate();
                skillPaintTemplate.id = rsSkillPaint.getInt("id");
                skillPaintTemplate.effectHappenOnMob = rsSkillPaint.getInt("effect_happen_on_mob");
                skillPaintTemplate.numEff = rsSkillPaint.getInt("num_eff");

                // Load skillStand
                try (PreparedStatement psSkillStand = connection.prepareStatement(querySkillStand)) {
                    psSkillStand.setInt(1, skillPaintTemplate.id);
                    ResultSet rsSkillStand = psSkillStand.executeQuery();
                    List<SkillPaintInfo> skillStandList = new ArrayList<>();
                    while (rsSkillStand.next()) {
                        SkillPaintInfo skillInfoPaint = new SkillPaintInfo();
                        skillInfoPaint.status = rsSkillStand.getInt("status");
                        skillInfoPaint.effS0Id = rsSkillStand.getInt("effS0Id");
                        skillInfoPaint.e0dx = rsSkillStand.getInt("e0dx");
                        skillInfoPaint.e0dy = rsSkillStand.getInt("e0dy");
                        skillInfoPaint.effS1Id = rsSkillStand.getInt("effS1Id");
                        skillInfoPaint.e1dx = rsSkillStand.getInt("e1dx");
                        skillInfoPaint.e1dy = rsSkillStand.getInt("e1dy");
                        skillInfoPaint.effS2Id = rsSkillStand.getInt("effS2Id");
                        skillInfoPaint.e2dx = rsSkillStand.getInt("e2dx");
                        skillInfoPaint.e2dy = rsSkillStand.getInt("e2dy");
                        skillInfoPaint.arrowId = rsSkillStand.getInt("arrowId");
                        skillInfoPaint.adx = rsSkillStand.getInt("adx");
                        skillInfoPaint.ady = rsSkillStand.getInt("ady");
                        skillStandList.add(skillInfoPaint);
                    }
                    skillPaintTemplate.skillStand = skillStandList;
                }

                // Load skillFly
                try (PreparedStatement psSkillFly = connection.prepareStatement(querySkillFly)) {
                    psSkillFly.setInt(1, skillPaintTemplate.id);
                    ResultSet rsSkillFly = psSkillFly.executeQuery();
                    List<SkillPaintInfo> skillFlyList = new ArrayList<>();
                    while (rsSkillFly.next()) {
                        SkillPaintInfo skillInfoPaint = new SkillPaintInfo();
                        skillInfoPaint.status = rsSkillFly.getInt("status");
                        skillInfoPaint.effS0Id = rsSkillFly.getInt("effS0Id");
                        skillInfoPaint.e0dx = rsSkillFly.getInt("e0dx");
                        skillInfoPaint.e0dy = rsSkillFly.getInt("e0dy");
                        skillInfoPaint.effS1Id = rsSkillFly.getInt("effS1Id");
                        skillInfoPaint.e1dx = rsSkillFly.getInt("e1dx");
                        skillInfoPaint.e1dy = rsSkillFly.getInt("e1dy");
                        skillInfoPaint.effS2Id = rsSkillFly.getInt("effS2Id");
                        skillInfoPaint.e2dx = rsSkillFly.getInt("e2dx");
                        skillInfoPaint.e2dy = rsSkillFly.getInt("e2dy");
                        skillInfoPaint.arrowId = rsSkillFly.getInt("arrowId");
                        skillInfoPaint.adx = rsSkillFly.getInt("adx");
                        skillInfoPaint.ady = rsSkillFly.getInt("ady");
                        skillFlyList.add(skillInfoPaint);
                    }
                    skillPaintTemplate.skillfly = skillFlyList;
                }

                this.skillPaintTemplateList.add(skillPaintTemplate);
            }
            this.setData();
//            LogServer.LogInit("SkillPaintManager initialized size: " + this.skillPaintTemplateList.size() + " data size: " + this.skillPaintsData.length);
        } catch (SQLException e) {
//            e.printStackTrace();
            LogServer.LogException("Error loading skill paint: " + e.getMessage());
        }
    }

    private void setData() {
        try (Message ms = new Message()) {
            try (DataOutputStream dataOutputStream = ms.writer()) {
                dataOutputStream.writeShort(skillPaintTemplateList.size());
//                System.out.println("skillPaintTemplateList.size(): " + skillPaintTemplateList.size());
                for (SkillPaintTemplate skillPaintTemplate : skillPaintTemplateList) {
                    dataOutputStream.writeShort(skillPaintTemplate.id);
                    dataOutputStream.writeShort(skillPaintTemplate.effectHappenOnMob);
                    dataOutputStream.writeByte(skillPaintTemplate.numEff);

                    if (skillPaintTemplate.skillStand != null) {
                        dataOutputStream.writeByte(skillPaintTemplate.skillStand.size());
                        for (SkillPaintInfo skillInfo : skillPaintTemplate.skillStand) {
                            dataOutputStream.writeByte(skillInfo.status);
                            dataOutputStream.writeShort(skillInfo.effS0Id);
                            dataOutputStream.writeShort(skillInfo.e0dx);
                            dataOutputStream.writeShort(skillInfo.e0dy);
                            dataOutputStream.writeShort(skillInfo.effS1Id);
                            dataOutputStream.writeShort(skillInfo.e1dx);
                            dataOutputStream.writeShort(skillInfo.e1dy);
                            dataOutputStream.writeShort(skillInfo.effS2Id);
                            dataOutputStream.writeShort(skillInfo.e2dx);
                            dataOutputStream.writeShort(skillInfo.e2dy);
                            dataOutputStream.writeShort(skillInfo.arrowId);
                            dataOutputStream.writeShort(skillInfo.adx);
                            dataOutputStream.writeShort(skillInfo.ady);
                        }
                    } else {
                        dataOutputStream.writeByte(0);
                    }
                    if (skillPaintTemplate.skillfly != null) {
                        dataOutputStream.writeByte(skillPaintTemplate.skillfly.size());// 14
                        for (SkillPaintInfo skillInfo : skillPaintTemplate.skillfly) {
                            dataOutputStream.writeByte(skillInfo.status);// 13
                            dataOutputStream.writeShort(skillInfo.effS0Id);// 12
                            dataOutputStream.writeShort(skillInfo.e0dx);
                            dataOutputStream.writeShort(skillInfo.e0dy);
                            dataOutputStream.writeShort(skillInfo.effS1Id);
                            dataOutputStream.writeShort(skillInfo.e1dx);
                            dataOutputStream.writeShort(skillInfo.e1dy);
                            dataOutputStream.writeShort(skillInfo.effS2Id);
                            dataOutputStream.writeShort(skillInfo.e2dx);
                            dataOutputStream.writeShort(skillInfo.e2dy);
                            dataOutputStream.writeShort(skillInfo.arrowId);
                            dataOutputStream.writeShort(skillInfo.adx);
                            dataOutputStream.writeShort(skillInfo.ady);
                        }
                    } else {
                        dataOutputStream.writeByte(0);
                    }
                }
                dataOutputStream.flush();
                this.skillPaintsData = ms.getData();
            } catch (Exception e) {
                LogServer.LogException("Error in setData: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            LogServer.LogException("Error creating message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public byte[] getSkillPaintsData() {
        return this.skillPaintsData;
    }

}
