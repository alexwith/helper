package me.hyfe.helper.config;

import me.hyfe.helper.Schedulers;
import me.hyfe.helper.promise.Promise;
import me.hyfe.helper.promise.ThreadContext;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Config {
    private final String name;
    private final File file;
    private final Yaml yaml;
    private final Map<String, Object> map = new HashMap<>();

    public Config(String name, File file) throws IOException {
        this.name = name;
        this.file = file;
        this.yaml = new Yaml();
        this.createResource();
        this.load();
    }

    public static Config create(Plugin plugin, String name, UnaryOperator<Path> path) {
        try {
            return new Config(name, path.apply(plugin.getDataFolder().toPath().toAbsolutePath()).resolve(name).toFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public Object get(String key) {
        return this.map.get(key);
    }

    public Promise<Void> reload() {
        return Schedulers.async().run(() -> {
            try {
                this.load();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).exceptionally(ThreadContext.ASYNC, (ex) -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void load() throws IOException {
        FileInputStream inputStream = new FileInputStream(this.file);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String content = this.concatContent(bufferedReader);
        Map<?, ?> input = this.yaml.load(content);
        this.writeContentToMemory(input);
    }

    private String concatContent(BufferedReader reader) throws IOException {
        try {
            return reader.lines().map((line) -> line.concat("\n")).collect(Collectors.joining());
        } finally {
            reader.close();
        }
    }

    private void writeContentToMemory(Map<?, ?> input) {
        this.map.clear();
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            this.map.put(key, value);
        }
    }

    private void createResource() {
        if (!Files.exists(this.file.toPath())) {
            this.saveResource();
        }
    }

    private void saveResource() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(this.name);
        if (inputStream == null) {
            // TODO: log
        } else {
            try {
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                Files.write(this.file.toPath(), bytes, StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}