package Tangwing.Cloud;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")

public class FileController {

    private final FileStorage fileStorage;

    @PostMapping // for uploading files POST /api/files

    public ResponseEntity<FileMetaData> uploadFile(@RequestParam("file") MultipartFile file) throws IOException { // response Entity is meta data requires multipart file

            FileMetaData savedMetaData = fileStorage.saveFile(file);
            if (savedMetaData != null) {
                return ResponseEntity.ok(savedMetaData);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

    }

    @GetMapping // for listing files GET /api/files
    public ResponseEntity<List<FileMetaData>> listFiles() { // returns the metadata of all files
        return ResponseEntity.ok(fileStorage.listFiles());
    }

    @GetMapping("/{fileName}") // listing meta data of one file GET /api/files/{fileName}
    public ResponseEntity<FileMetaData> getFileData(@PathVariable String fileName) throws IOException { // returns file metadata if not return not found
        FileMetaData quriedFileData = fileStorage.getFileData(fileName);
        if (quriedFileData != null) {
            return ResponseEntity.ok(quriedFileData);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{fileName}/download")  // listing downloading data of one file GET /api/files/{fileName}/donwload
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) throws IOException {
        byte[] fileData = fileStorage.getFile(fileName);
        HttpHeaders downloadHeader = new HttpHeaders();
        downloadHeader.setContentDisposition(ContentDisposition.attachment().filename(fileName).build()); // set content dispostion as attachment telling browser to download
        return ResponseEntity.ok() 
            .headers(downloadHeader)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(fileData); // return ok/attachment header/content type/content response
    }

    @DeleteMapping("/{fileName}") // for deleteing one file DELETE /api/files/{name}
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) throws IOException {
        fileStorage.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }
}


