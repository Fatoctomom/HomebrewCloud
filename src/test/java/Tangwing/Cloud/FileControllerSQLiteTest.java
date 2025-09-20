package Tangwing.Cloud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;




@SpringBootTest(classes = CloudApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class FileControllerSQLiteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private FileRepository fileRepo;

    @MockBean private FileStorage fileStorage;

    @BeforeEach 
    void setUp() {
        fileRepo.deleteAll();
    }
    
    @Test
    @Order(1)
    void testFileUploadList() throws Exception {
        // upload

        Mockito.when(fileStorage.saveFile(any()))
            .thenAnswer( inv -> {
                var mf = inv.getArgument(0, org.springframework.web.multipart.MultipartFile.class);
                var metaFile = FileMetaData.builder()
                .fileName(mf.getOriginalFilename())
                .path("")
                .size(mf.getSize())
                .uploadedAt(LocalDateTime.now())
                .build();
                return fileRepo.save(metaFile);
            });

        
        var file = new MockMultipartFile(
            "file", "testfile.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "hello world!".getBytes(StandardCharsets.UTF_8)
        );
            
            mockMvc.perform(multipart(HttpMethod.POST,"/api/files").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileName").value("testfile.txt"));
    }

    @Test
    @Order(2)
    void testListFile() throws Exception {
        fileRepo.saveAll(List.of(
                FileMetaData.builder().fileName("a.txt").path("").size(1L).uploadedAt(LocalDateTime.now()).build(),
                FileMetaData.builder().fileName("b.txt").path("").size(2L).uploadedAt(LocalDateTime.now()).build()
        ));

        Mockito.when(fileStorage.listFiles())
            .thenAnswer(inv -> fileRepo.findAll());

        mockMvc.perform(get("/api/files"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].fileName").value("a.txt"))
        .andExpect(jsonPath("$[1].fileName").value("b.txt"));

    }

    @Test
    @Order(3)
    void testRetrieveFile() throws Exception {
        fileRepo.saveAll(List.of(
                FileMetaData.builder().fileName("a.txt").path("").size(1L).uploadedAt(LocalDateTime.now()).build(),
                FileMetaData.builder().fileName("b.txt").path("").size(2L).uploadedAt(LocalDateTime.now()).build()
        ));
        
        Mockito.when(fileStorage.getFileData("a.txt"))
            .thenAnswer(inv -> fileRepo.findByFileName("a.txt"));

        
            mockMvc.perform(get("/api/files/a.txt"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fileName").value("a.txt"))
            .andExpect(jsonPath("$.size").value(1L))
            .andExpect(jsonPath("$.uploadedAt").exists());
    }

    @Test
    @Order(4)
    void testDownloadFile() throws Exception {
        // Download
        byte[] payload = "hello world!".getBytes(StandardCharsets.UTF_8);
        Mockito.when(fileStorage.getFile(eq("testfile.txt"))).thenReturn(payload);
        
        mockMvc.perform(get("/api/files/testfile.txt/download"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"testfile.txt\""))
        .andExpect(content().bytes(payload))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @Order(5)
    void testDeleteFile() throws Exception {
        //Deletion
        var meta = fileRepo.save(
                FileMetaData.builder()
                        .fileName("gone.txt")
                        .path("")
                        .size(12L)
                        .uploadedAt(LocalDateTime.now())
                        .build()
        );

        Mockito.doAnswer( inv -> {
            String name = inv.getArgument(0, String.class);
            fileRepo.deleteAll(
                    fileRepo.findAll().stream()
                            .filter(m -> name.equals(m.getFileName()))
                            .toList()
            );
            return null;
        }).when(fileStorage).deleteFile(eq("gone.txt"));

        mockMvc.perform(delete("/api/files/{fileName}", "gone.txt"))
        .andExpect(status().isNoContent());

        assertThat(fileRepo.findById(meta.getId())).isEmpty();
        Mockito.verify(fileStorage).deleteFile("gone.txt");
    }
}
