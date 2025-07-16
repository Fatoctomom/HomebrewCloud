package Tangwing.Cloud.repo;

import Tangwing.Cloud.model;
import org.springframework.data.jpa.repository.JpaRepository

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, long> 
{
    boolean existsByFilename(String filename);
}