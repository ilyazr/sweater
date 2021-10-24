package ru.zakharov.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.zakharov.models.FileForDownload;
import ru.zakharov.models.UploadedFile;
import ru.zakharov.services.FileStorage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/fs")
public class FileStorageController {

    private final FileStorage fileStorageService;

    @Autowired
    public FileStorageController(@Qualifier("localFileStorageService") FileStorage fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadedFile> uploadFile(@RequestParam(name = "file") MultipartFile file) {
        UploadedFile uploadedFile = fileStorageService.storeFile(file);
        return ResponseEntity.ok(uploadedFile);
    }

    @PostMapping("/uploadMultiple")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam(name = "files") MultipartFile[] files) {
        return ResponseEntity.ok(
                Arrays.stream(files)
                        .map(this::uploadFile)
                        .map(ResponseEntity::getBody)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/download/{whose}/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "whose") String whose,
                                                 @PathVariable(name = "filename") String filename) {
        FileForDownload file = fileStorageService.downloadFile(whose, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .headers(file.getHeaders())
                .body(file.getFileAsResource());
    }

    @GetMapping("/files/shared")
    public ResponseEntity<List<UploadedFile>> getAllSharedFiles() {
        List<UploadedFile> allFiles = fileStorageService
                .getFilesInDirectory(fileStorageService.getSharedDir());
        return ResponseEntity.ok(allFiles);
    }

    @GetMapping("/files/{username}")
    public ResponseEntity<List<UploadedFile>> getAllFilesOfUser(
            @PathVariable(name = "username") String username) {
        List<UploadedFile> allFiles = fileStorageService.getFilesInDirectory(username);
        return ResponseEntity.ok(allFiles);
    }

}
