package ru.zakharov.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFile {

    private String fileName;
    private String downloadURI;
    private String fileType;
    private long size;

    private static boolean isAuthenticated() {
        boolean isAuth = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (!authentication.getName().equals("anonymousUser")) {
                isAuth = true;
            }
        }
        return isAuth;
    }

    private static String downloadPath() {
        return isAuthenticated()?
                String.format("/download/%s", SecurityContextHolder.getContext().getAuthentication().getName()) :
                "/download/shared";
    }

    public static String createDownloadURI(MultipartFile file) {
        String download = downloadPath();
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/fs")
                .path(download)
                .path(String.format("/%s", file.getOriginalFilename()))
                .toUriString();
    }

    public static String createDownloadURI(Path file) {
        String download = downloadPath();
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/fs")
                .path(download)
                .path("/"+file.getFileName().toString())
                .toUriString();
    }

    public static String createSharedDownloadUri(Path file) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/fs")
                .path("/download/shared")
                .path("/"+file.getFileName().toString())
                .toUriString();
    }

    public static String decodeFilename(String downloadUri) {
        return URLDecoder.decode(downloadUri, StandardCharsets.UTF_8);
    }
}
