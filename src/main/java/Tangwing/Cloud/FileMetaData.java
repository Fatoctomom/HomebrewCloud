package Tangwing.Cloud.FileMetaData;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String fileName;
    private String Path;
    private Long size;
    private LocalDateTime uploadedAt;
}

