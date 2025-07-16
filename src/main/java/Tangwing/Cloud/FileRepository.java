package Tangwing.Cloud.FileRepository;

import Tangwing.Cloud.model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileMetaData, long> 
{
    boolean existsByFilename(String filename);
}