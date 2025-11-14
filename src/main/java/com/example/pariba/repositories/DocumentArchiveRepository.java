package com.example.pariba.repositories;

import com.example.pariba.models.DocumentArchive;
import com.example.pariba.models.TontineGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentArchiveRepository extends JpaRepository<DocumentArchive, String> {
    
    List<DocumentArchive> findByGroupId(String groupId);
    
    List<DocumentArchive> findByGroup(TontineGroup group);
    
    List<DocumentArchive> findByGroupAndType(TontineGroup group, com.example.pariba.enums.DocumentType type);
    
    List<DocumentArchive> findByType(com.example.pariba.enums.DocumentType type);
    
    List<DocumentArchive> findByGroupIdOrderByCreatedAtDesc(String groupId);
}
