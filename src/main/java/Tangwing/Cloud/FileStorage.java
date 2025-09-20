package Tangwing.Cloud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorage {
    private final FileRepository repository;

    private final Path storagePath = Paths.get("/opt/minicloud/mnt/storage");

    public FileMetaData saveFile(MultipartFile file) throws IOException { // saves files to persitant DB
            if (!Files.exists(storagePath)) { // if hard coded directory doesnt exist create it
                Files.createDirectories(storagePath);
            }

            Path target = storagePath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING); // copies data from file to target

            FileMetaData metaData = FileMetaData.builder() // builds a file metadata
                    .fileName(file.getOriginalFilename())
                    .path(target.toString())
                    .size(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .build();
            return repository.save(metaData);
    }

    public List<FileMetaData> listFiles() {
        return repository.findAll();
    }

    public FileMetaData getFileData(String fileName) throws IOException {
        return repository.findByFilename(fileName);
    }

    public byte[] getFile(String fileName) throws IOException {
        Path file = storagePath.resolve(fileName);
        return Files.readAllBytes(file);
    }

    public void deleteFile(String fileName) throws IOException {
        Path file = storagePath.resolve(fileName);
        Files.deleteIfExists(file);
        repository.deleteAll(repository.findAll().stream()
            .filter(meta -> meta.getFileName().equals(fileName))
            .toList());
    }

}