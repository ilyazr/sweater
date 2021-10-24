package ru.zakharov.listeners;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Slf4j
public class CustomServletContextListener implements ServletContextListener {

    private static final String SERVER_FILES_DIR_NAME = "server-files";
    private static final Path SERVER_FILES_PATH = Paths.get(SERVER_FILES_DIR_NAME);
    private static final Path INIT_DATA_PATH = Paths.get("src/main/resources/init_data/" + SERVER_FILES_DIR_NAME);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Creating server files directory and user directories...");
        createUserDirectories();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Removing server files directory and user directories...");
        deleteUserDirectories();
    }

    private void createUserDirectories() {
        try {
            Files.createDirectory(SERVER_FILES_PATH);
            Files.walk(INIT_DATA_PATH)
                    .filter(file -> !file.getFileName().toString().equals("server-files"))
                    .forEach(file -> {
                        try {
                            if (Files.isDirectory(file)) {
                                Files.createDirectory(SERVER_FILES_PATH.resolve(file.getFileName()));
                            } else if (Files.isRegularFile(file)) {
                                Path path = file.getParent().getFileName().resolve(file.getFileName());
                                Files.copy(file, SERVER_FILES_PATH.resolve(path));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteUserDirectories() {
        try {
            Files.walk(SERVER_FILES_PATH)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
