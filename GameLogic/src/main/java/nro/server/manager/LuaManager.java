package nro.server.manager;

import lombok.Getter;
import nro.server.system.LogServer;
import nro.service.model.entity.player.Player;
import nro.server.config.ConfigServer;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class LuaManager implements IManager {

    @Getter
    private static final LuaManager instance = new LuaManager();

    private final Globals globals;
    private final Map<String, LuaValue> loadedScripts;

    private LuaManager() {
        this.globals = JsePlatform.standardGlobals();
        this.loadedScripts = new HashMap<>();
    }

    @Override
    public void init() {
        loadAllScripts();
    }

    @Override
    public void reload() {
        clear();
        loadAllScripts();
        LogServer.DebugLogic("Reloaded all Lua scripts.");
    }

    @Override
    public void clear() {
        loadedScripts.clear();
        LogServer.DebugLogic("Clear all Lua scripts.");
    }

    public void loadAllScripts() {
        try {
            Files.walk(Paths.get(ConfigServer.SCRIPT_FOLDER))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".lua"))
                    .forEach(path -> {
                        String scriptName = path.getFileName().toString().replace(".lua", "");
                        loadScript(scriptName, path.toString());
                    });
        } catch (IOException e) {
            LogServer.LogException(" Error loading Lua scripts: " + e.getMessage(), e);
        }
    }

    public void loadScript(String scriptName, String filePath) {
        try {
            String luaCode = Files.readString(Paths.get(filePath));
            LuaValue script = globals.load(luaCode);
            script.call();
            loadedScripts.put(scriptName, script);
        } catch (LuaError | IOException e) {
            LogServer.LogException(" Error loading Lua script: " + scriptName + " - " + e.getMessage(), e);
        }
    }

    public LuaValue getFunction(String scriptName, String functionName) {
        LuaValue script = loadedScripts.get(scriptName);
        if (script == null) {
            LogServer.LogException(" Script not found: " + scriptName);
            return LuaValue.NIL;
        }
        return script.get(functionName);
    }

    public void setGlobals(Player player) {
        if (player == null) {
            return;
        }
        globals.set("getPlayer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(player);
            }
        });
    }


}
