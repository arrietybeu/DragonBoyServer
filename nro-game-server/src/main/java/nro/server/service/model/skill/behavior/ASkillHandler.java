package nro.server.service.model.skill.behavior;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ASkillHandler {
    int[] value(); // skillId[]
}