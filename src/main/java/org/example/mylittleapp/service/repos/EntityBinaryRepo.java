package org.example.mylittleapp.service.repos;

import org.example.mylittleapp.model.entity.EntityBinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EntityBinaryRepo extends JpaRepository<EntityBinary, String> {

  @Query("SELECT b FROM EntityBinary b " +
      "WHERE lower(b.fileName) = lower(:fileName)")
  Optional<EntityBinary> findByFileName(String fileName);

  @Query("SELECT b.url FROM EntityBinary b " +
      "WHERE b.parent.id = :id")
  List<String> findByParentId(String id);

  @Query("SELECT b.parent.id AS parentId, b.url AS url FROM EntityBinary b ")
  List<ParentUrlPair> findAllParentIdAndUrl();

}
