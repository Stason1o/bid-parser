package parser.service;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import parser.processor.FileProcessor;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Log4j2
public class WatchDirService {

    private final WatchService watcher;

    private final Map<WatchKey, Path> keys;

    private final FileProcessor fileProcessor;

    private final String inputFileName;

    public WatchDirService(final Path dir, final FileProcessor fileProcessor, final String inputFileName) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.fileProcessor = fileProcessor;
        this.inputFileName = inputFileName;

        keys.put(dir.register(watcher, ENTRY_MODIFY), dir);
    }

    @SneakyThrows
    public void processEvents() {
        for (; ; ) {

            log.info("Initialized file listener..");
            WatchKey key = watcher.take();

            Path dir = keys.get(key);
            Path sourceFile = dir.resolve(Paths.get(inputFileName));

            for (WatchEvent<?> event : key.pollEvents()) {

                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                if (child.toString().equals(sourceFile.toString())) {
                    fileProcessor.processFile(child);
                }
            }

            key.reset();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
}
