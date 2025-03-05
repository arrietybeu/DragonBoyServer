package nro.model.template.entity;


import lombok.Getter;
import lombok.Setter;
import nro.server.network.Session;

@Getter
@Setter
public class UserInfo {

    private final String username;
    private final String password;
    private final Session session;

    private int id;

    private boolean admin;
    private boolean ban;
    private boolean active;

    private long lastTimeLogin;
    private long lastTimeLogout;


    public UserInfo(Session session, String username, String password) {
        this.session = session;
        this.username = username;
        this.password = password;
        this.session.setUserInfo(this);
        this.session.getSessionInfo().setLogin(true);
    }

}
