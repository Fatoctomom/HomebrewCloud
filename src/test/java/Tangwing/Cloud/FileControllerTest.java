package Tangwing.Cloud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void testFileUploadList() throws Exception {
        // upload
        MockMultipartFile file = new MockMultipartFile(
            "file",                        //name
            "testfile.txt",    //original file name
            "text/plain",           //contentType
            "hello world!".getBytes()            //content
            );

            mockMvc.perform(multipart("/api/files/upload").file(file).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileName").value("testfile.txt"));

        // list
            mockMvc.perform(get("/api/files/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fileName").value("testfile.txt"));
    }


    @Test
    @Order(2)
    void testDownloadFile() throws Exception {
        // Download
        mockMvc.perform(get("/api/files/download/testfile.txt"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"testfile.txt\""))
        .andExpect(content().bytes("hello world!".getBytes()));
    }

    @Test
    @Order(3)
    void testDeleteFile() throws Exception {
        //Deletion
        mockMvc.perform(delete("/api/files/testfile.txt"))
            .andExpect(status().isNoContent());
    }
}
