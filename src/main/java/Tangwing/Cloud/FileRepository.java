package Tangwing.Cloud;

import Tangwing.Cloud.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileMetaData, Long> 
{
    boolean existsByFileName(String fileName);
}