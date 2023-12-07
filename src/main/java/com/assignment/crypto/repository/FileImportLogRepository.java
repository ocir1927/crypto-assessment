package com.assignment.crypto.repository;

import com.assignment.crypto.domain.FileImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileImportLogRepository extends JpaRepository<FileImportLog,Long> {

    @Query("""
            SELECT import
            FROM FileImportLog import
            WHERE import.fileHash = :fileHash
            """)
    Optional<FileImportLog> existsByFileHash(@Param("fileHash") String fileHash);


    @Modifying
    void deleteByFileName(String fileName);
}
