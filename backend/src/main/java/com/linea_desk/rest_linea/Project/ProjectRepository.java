package com.linea_desk.rest_linea.Project;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linea_desk.rest_linea.User.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
        Optional<Project> findByProjectName(String projectName);
        Collection<Project> findAllByUser(User user);
}
