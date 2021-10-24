package ru.zakharov.services;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import ru.zakharov.models.FileForDownload;
import ru.zakharov.models.UploadedFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface FileStorage {
    UploadedFile storeFile(MultipartFile file);
    default List<UploadedFile> getFilesInDirectory(String dirName) {
        return getFilesInDirectory(Paths.get(dirName));
    }
    List<UploadedFile> getFilesInDirectory(Path dirName);
    Resource loadFileAsResource(String filename);
    HttpHeaders createAttachments(Resource fileAsResource);
    String getContentType(Resource fileAsResource);
    FileForDownload downloadFile(String whose, String fileName);
    Path getStorage();
    Path getSharedDir();
}
