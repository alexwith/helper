package me.hyfe.helper.config;

import me.hyfe.helper.Schedulers;
import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.promise.Promise;
import me.hyfe.helper.promise.ThreadContext;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
        this.createPath();
        this.createResource();
        this.load();
    }

    public static Config create(String name, UnaryOperator<Path> path) {
        try {
            return new Config(name, path.apply(LoaderUtils.getPlugin().getDataFolder().toPath().toAbsolutePath()).resolve(name).toFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Set<String> keys() {
        return this.map.keySet();
    }

    public String getName() {
        return this.name;
    }

    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T tryGet(String key) {
        return (T) this.get(key);
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
            if (value instanceof Map) {
                Map<?, ?> section = ((Map<?, ?>) value);
                this.writeSectionToMemory(section, key);
                continue;
            }
            this.map.put(key, value);
        }
    }

    private void writeSectionToMemory(Map<?, ?> input, String parent) {
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = parent + "." + entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<?, ?> section = ((Map<?, ?>) value);
                this.writeSectionToMemory(section, key);
                continue;
            }
            this.map.put(key, value);
        }
    }

    private void createPath() {
        String absolutePath = this.file.toPath().toString();
        new File(absolutePath.substring(0, absolutePath.lastIndexOf(File.separator))).mkdirs();
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