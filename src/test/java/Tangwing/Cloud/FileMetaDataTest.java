package Tangwing.Cloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FileMetaDataTest {
    @Autowired
    private FileRepository repository;

    @Test
    void testSaveAndRetrieveFileMetaData() {
        // Create a new FileMetaData instance
        FileMetaData fileTest = FileMetaData.builder()
                .fileName("testFile.txt")
                .path("")
                .size(1024L)
                .uploadedAt(LocalDateTime.now())
                .build();
    
        // Save the FileMetaData instance to the repository
        repository.save(fileTest);

        var retrievedFiles = repository.findAll();

        assertThat(retrievedFiles).hasSize(1);
        assertThat(retrievedFiles.get(0).getFileName()).isEqualTo("testFile.txt");
    }
}