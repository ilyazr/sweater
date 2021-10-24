package ru.zakharov.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@AllArgsConstructor
public class FileForDownload {

    private Resource fileAsResource;
    private String contentType;
    private HttpHeaders headers;

}
