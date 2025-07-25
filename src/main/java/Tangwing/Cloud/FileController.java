package Tangwing.Cloud;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")

public class FileController {

    private final FileStorage fileStorage;

    @PostMapping("/upload")
    public ResponseEntity<FileMetaData> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

            FileMetaData savedMetaData = fileStorage.saveFile(file);
            if (savedMetaData != null) {
                return ResponseEntity.ok(savedMetaData);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

    }

    @GetMapping("/list")
    public ResponseEntity<List<FileMetaData>> listFiles() {
        return ResponseEntity.ok(fileStorage.listFiles());
    }

    @GetMapping("/download/{fileName}") 
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) throws IOException {
        byte[] fileData = fileStorage.getFile(fileName);
        HttpHeaders downloadHeader = new HttpHeaders();
        downloadHeader.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
        return ResponseEntity.ok()
            .headers(downloadHeader)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(fileData);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) throws IOException {
        fileStorage.deleteFile(fileName);
        return ResponseEntity.noContent().build()
    }
}


