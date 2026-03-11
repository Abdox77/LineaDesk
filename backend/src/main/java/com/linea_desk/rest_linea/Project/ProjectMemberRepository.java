package com.linea_desk.rest_linea.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Query("SELECT m FROM ProjectMember m JOIN FETCH m.user WHERE m.project.id = :projectId")
    List<ProjectMember> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT m FROM ProjectMember m JOIN FETCH m.user JOIN FETCH m.project WHERE m.user.id = :userId")
    List<ProjectMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM ProjectMember m WHERE m.project.id = :projectId AND m.user.id = :userId")
    Optional<ProjectMember> findByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}


