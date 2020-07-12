package org.potaska;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.*;

public class JSPlugin extends JavaPlugin {
    Context ctx;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();

        List<Map<?, ?>> map = config.getMapList("graal-options");
        String entryFile = config.getString("entry-file");

        try {
            Map<String, String> options = new HashMap<String, String>();
            for (Map<?, ?> entry : map) {
                String name = String.valueOf(entry.get("name"));
                String value = String.valueOf(entry.get("value"));
                if (name == null || value == null) {
                    continue;
                }
                options.put(name, value);
            }
            System.out.println(options);
            System.out.println(entryFile);

            Path entry = Paths.get(entryFile);

            Files.createDirectories(entry.getParent());
            File initFile = entry.toFile();
            initFile.createNewFile();

            ctx = Context.newBuilder("js").allowExperimentalOptions(true).allowIO(true).allowHostAccess(HostAccess.ALL)
                    .allowHostClassLookup(className -> true).options(options).build();
            ctx.getBindings("js").putMember("__ctx", ctx);
            ctx.eval(Source.newBuilder("js", initFile).build());
        } catch (PolyglotException | IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void onDisable() {
        ctx.close();
    }
}
