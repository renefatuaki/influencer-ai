package dev.elfa.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String id;
    private String name;
    private String contentType;
    private long size;
    private String twitterId;
    private boolean approved;
}
