package Tangwing.Cloud;

import Tangwing.Cloud.model.FileMetaData;
import Tangwing.Cloud.repo.FileMetaDataRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.asserThat;

@DataJpaTest
public class FileMetaDataTest {
    @Autowired
    private FileMetaDataRepository repository;

    @Test
    void testSaveAndRetrieveFileMetaData() {
        // Create a new FileMetaData instance
        FileMetaData fileTest = File.builder()
                .fileName("testFile.txt")
                .path("")
                .size(1024L)
                .uploadedAt(LocalDateTime.now())
                .build();
    
        // Save the FileMetaData instance to the repository
        repository.save(fileTest);

        var retrievedFiles = repository.findAll();

        asserThat(retrievedFiles).hasSize(1);
        asserThat(retrievedFiles.get(0).getFileName()).isEqualTo("testFile.txt");
    }
}