package com.linea_desk.rest_linea.Project;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, Long> {
    Optional<ProjectInvite> findByToken(String token);
    Optional<ProjectInvite> findByProjectIdAndStatus(Long projectId, ProjectInvite.INVITE_STATUS status);
}

