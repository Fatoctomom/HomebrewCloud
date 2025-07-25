package Tangwing.Cloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testFileUploadList() throws Exception {
        // upload
        MockMultipartFile testFile = new MockMultipartFile(
            "testFile",                        //name
            "testfile.txt",    //original file name
            "text/plain",           //contentType
            "hello world!".getBytes()            //content
            );

            mockMvc.perform(multipart("/api/files/upload").file(testFile))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value("testfile.txt"));

        // list
            mockMvc.perform(get("/api/files/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].filename").value("testfile.txt"));
    }


    @Test
    void testDownloadFile() throws Exception {
        // Download
        mockMvc.perform(get("/api/files/download/testfile.txt"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"testfile.txt\""))
        .andExpect(content().bytes("hello world!".getBytes()));
    }

    @Test
    void testDeleteFile() throws Exception {
        //Deletion
        mockMvc.perform(delete("api/files/testfile.txt"))
            .andExpect(status().isNoContent());
    }
}
