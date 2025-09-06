package Tangwing.Cloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CloudApplication.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class FileRepoSQLiteTest {
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

        FileMetaData saved = repository.save(fileTest);
        assertThat(saved.getId()).isNotNull();

        Optional<FileMetaData> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("testFile.txt");
    }

    @Test
     void testDeleteRemovesRow() {
        FileMetaData fileTest = FileMetaData.builder()
                .fileName("testFile.txt")
                .path("")
                .size(1024L)
                .uploadedAt(LocalDateTime.now())
                .build();
        
                
        FileMetaData saved = repository.save(fileTest);
        Long id = saved.getId();

        repository.deleteById(id);
        assertThat(repository.findById(id)).isEmpty();
     }
}