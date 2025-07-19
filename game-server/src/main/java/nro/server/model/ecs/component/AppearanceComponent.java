package nro.server.model.ecs.component;

import com.artemis.Component;

/**
 * @author Arriety
 */
public class AppearanceComponent extends Component {

    public short head;
    public short body;
    public short leg;
    public short flagBag; // Cờ
    public short aura = -1; // Hào quang
    public short effSetItem = -1; // Hiệu ứng set đồ
    public short idHat = -1; // Nón thời trang
    public boolean isMonkey = false;

}

