package Tangwing.Cloud;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileMetaData, Long> 
{
    boolean existsByFileName(String fileName);
    FileMetaData findByFilename(String filename);
}