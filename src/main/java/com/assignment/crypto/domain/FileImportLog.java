package com.assignment.crypto.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "file_import_log")
@Getter
@Setter
@NoArgsConstructor
public class FileImportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_time")
    private Instant dateTime;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_hash")
    private String fileHash;

}
