package org.potaska;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
        String mainDir = config.getString("js-directory", "js");
        String entryFile = config.getString("entry-file", "internal/init.js");
        String jsZip = config.getString("js-zip");

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
            System.out.println(mainDir + ", " + entryFile);

            Path dir = Paths.get(mainDir);

            if (!Files.exists(dir)) {
                Path zipPath = Files.createTempFile("craftjs", ".zip");
                Path tmpDir = Files.createTempDirectory("craftjs");
                Files.createDirectories(dir);
                URL zipUrl = new URL(jsZip);
                ReadableByteChannel rbc = Channels.newChannel(zipUrl.openStream());
                FileOutputStream os = new FileOutputStream(zipPath.toFile());
                FileChannel fc = os.getChannel();
                os.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                Path extracted = Utils.unzip(zipPath.toFile(), tmpDir.toFile()).toPath();
                Stream<Path> files = Files.list(extracted);
                files.forEach(file -> {
                    Path rel = extracted.relativize(file);
                    Path target = dir.resolve(rel);
                    try {
                        Files.move(file, target);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            Path entry = Paths.get(mainDir, entryFile);

            Files.createDirectories(entry.getParent());
            File initFile = entry.toFile();
            initFile.createNewFile();

            ctx = Context.newBuilder("js").allowExperimentalOptions(true).allowIO(true).allowHostAccess(HostAccess.ALL)
                    .allowHostClassLookup(className -> true).options(options).build();
            ctx.getBindings("js").putMember("__ctx", ctx);
            ctx.getBindings("js").putMember("__plugin", this);
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
