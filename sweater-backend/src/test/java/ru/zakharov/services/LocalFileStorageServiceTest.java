package ru.zakharov.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.zakharov.exceptions.FileStorageException;
import ru.zakharov.models.UploadedFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalFileStorageServiceTest {

    private Path storage;
    private Path sharedDir;

    @InjectMocks
    private LocalFileStorageService underTest = new LocalFileStorageService();

    public LocalFileStorageServiceTest() {
        this.storage = Paths.get("/bla");
        this.sharedDir = Paths.get("/bla");
    }

    @Test
    void test() {
        assertThat(true).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "../hello/", "src/../../../../school/"})
    void shouldThrowAnExceptionBecauseOfName(String givenName) {
        MockMultipartFile file =
                spy(new MockMultipartFile("data", "data", "application/json", "text".getBytes()));
        when(file.getOriginalFilename()).thenReturn(givenName);
        assertThatThrownBy(() -> underTest.storeFile(file))
                .isInstanceOf(FileStorageException.class)
                .hasMessage("Error while store while upload file!");
    }

    @Test
    void shouldReturnEmptyList() {
        //given
        MockedStatic<Files> mockedStatic = mockStatic(Files.class);
        mockedStatic.when(() -> Files.notExists(any())).thenReturn(true);
        underTest.setStorage(spy(Paths.get("/")));
        when(underTest.getStorage().resolve("spk4")).thenReturn(Paths.get("/spk4"));
        //when
        List<UploadedFile> files = underTest.getFilesInDirectory("spk4");
        //then
        assertThat(files.size()).isEqualTo(0);
    }

}
