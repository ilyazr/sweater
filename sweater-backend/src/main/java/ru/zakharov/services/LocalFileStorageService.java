package ru.zakharov.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.zakharov.configs.FileStorageProperties;
import ru.zakharov.exceptions.FileStorageException;
import ru.zakharov.exceptions.UserNotFoundException;
import ru.zakharov.models.FileForDownload;
import ru.zakharov.models.UploadedFile;
import ru.zakharov.repos.UserRepo;
import ru.zakharov.utils.CustomExecutorService;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class LocalFileStorageService implements FileStorage {

    // 1. Note: now only authenticated users can upload/download files.
    // 2. If user not authenticated store files in shared dir.
    // 3. File location for user: ./server-files/{username}/{filename}.

    private Path storage;
    private Path sharedDir;
    private UserRepo userRepo;
    private ServletContext servletContext;
    private CustomExecutorService es;

    @Autowired
    public LocalFileStorageService(FileStorageProperties fsProps,
                                   UserRepo userRepo,
                                   ServletContext servletContext,
                                   CustomExecutorService es) {
        this.storage = Paths.get(fsProps.getUploadDir());
        this.sharedDir = Paths.get(fsProps.getSharedDir());
        this.userRepo = userRepo;
        this.servletContext = servletContext;
        this.es = es;
    }

    private boolean isAuthenticated() {
        boolean isAuth = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (!authentication.getName().equals("anonymousUser")) {
                isAuth = true;
            }
        }
        return isAuth;
    }

    @Override
    public UploadedFile storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..") || originalFilename.isBlank()) {
            throw new FileStorageException("Error while store while upload file!");
        }
        Path newPath = null;
        boolean authenticated = isAuthenticated();
        if (!authenticated) {
            newPath = sharedDir.resolve(originalFilename);
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Path userDir = username.equals("anonymousUser")? storage.resolve("shared") : storage.resolve(username);
            if (Files.notExists(userDir)) {
                try {
                    Files.createDirectory(userDir);
                } catch (IOException e) {
                    log.warn("Error while creating user directory for path {}", userDir);
                    throw new FileStorageException("Error while creating user directory!");
                }
            }
            newPath = userDir.resolve(originalFilename);
            if (Files.exists(newPath)) {
                log.warn("Already exists: {}", newPath);
                throw new FileStorageException(String.format("%s already exists",
                        newPath.getFileName().toString()));
            }
        }
        try {
            Files.write(newPath, file.getBytes());
            log.info("file was stored: {}", newPath);
            return createUploadedFile(file);
        } catch (IOException e) {
            throw new FileStorageException("Error while writing file!");
        }
    }

    public UploadedFile createUploadedFile(MultipartFile file) {
        return new UploadedFile(
                file.getOriginalFilename(),
                UploadedFile.createDownloadURI(file),
                file.getContentType(),
                file.getSize()
        );
    }

    @Override
    public List<UploadedFile> getFilesInDirectory(final Path dir) {
        Path path = dir.equals(storage)? dir : storage.resolve(dir);
        List<UploadedFile> allFiles = new ArrayList<>();
        if (Files.notExists(path)) {
            log.warn(String.format("%s doesn't exist", path));
            return allFiles;
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    allFiles.add(new UploadedFile(
                            file.getFileName().toString(),
                            path.equals(sharedDir)? UploadedFile.createSharedDownloadUri(file) : UploadedFile.createDownloadURI(file),
                            servletContext.getMimeType(file.toAbsolutePath().toString()),
                            Files.size(file)
                    ));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Error while walking file tree for path {}", path);
            throw new FileStorageException("Error");
        }
        log.info("Get all files in {}", path);
        return allFiles;
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        Path file = null;
        file = storage.resolve(filename).normalize();
        if (Files.notExists(file)) {
            throw new FileStorageException(String.format("File with name %s not found!",
                    file.getFileName().toString()));
        }
        try {
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new FileStorageException(String.format("File with name %s not found!",
                    file.getFileName().toString()));
        }
    }

    @Override
    public String getContentType(Resource fileAsResource) {
        String contentType = null;
        try {
            contentType = servletContext.getMimeType(fileAsResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    @Override
    public HttpHeaders createAttachments(Resource fileAsResource) {
        HttpHeaders headers = new HttpHeaders();
        //check https://developer.mozilla.org/ru/docs/Web/HTTP/Headers/Content-Disposition
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileAsResource.getFilename());
        return headers;
    }

    @Override
    public FileForDownload downloadFile(String whose, String fileName) {
        if (!userRepo.existsByUsername(whose)) throw new UserNotFoundException(whose);
        String fullName = whose + File.separator + fileName;
        Resource fileAsResource = loadFileAsResource(fullName);
        String contentType = getContentType(fileAsResource);
        HttpHeaders headers = createAttachments(fileAsResource);
        log.info("file was received: {}", fullName);
        return new FileForDownload(fileAsResource, contentType, headers);
    }

    public Path getPathOfUserDirInLocalStorage(String username) {
        return storage.resolve(Paths.get(username));
    }

    public CompletableFuture<Void> changeNameOfUserDirectoryAndCopyFiles(String prevName, String newName) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path oldPath = getPathOfUserDirInLocalStorage(prevName);
                if (Files.exists(oldPath)) {
                    Path newDir = getPathOfUserDirInLocalStorage(newName);

                    // 1. create
                    Files.createDirectory(newDir);
                    log.info("created new directory: {}", newDir);

                    // 2. copy and delete regular files
                    Files.walk(oldPath)
                            .filter(Files::isRegularFile)
                            .forEach(file -> {
                                try {
                                    Path newPath = newDir.resolve(file.getFileName());
                                    Files.copy(file, newPath, StandardCopyOption.COPY_ATTRIBUTES);
                                    Files.delete(file);
                                    log.info("{} was copied to {}", file, newPath);
                                } catch (IOException e) {
                                    log.error("Error while copying file: {}", file);
                                }
                            });

                    // 3. delete directory
                    Files.delete(oldPath);
                    log.info("deleted directory: {}", oldPath);

                }
            } catch (IOException e) {
                log.error("Error while creating user directory for path {}", newName);
            }
        }, es);
    }
}
