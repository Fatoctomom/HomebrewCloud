package Tangwing.Cloud.FileRepository;

import Tangwing.Cloud.FileMetaData.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileMetaData, Long> 
{
    boolean existsByFilename(String filename);
}