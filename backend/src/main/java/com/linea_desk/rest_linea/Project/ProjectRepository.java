package com.linea_desk.rest_linea.Project;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.linea_desk.rest_linea.User.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
        Optional<Project> findByProjectName(String projectName);

        Optional<Project> findByProjectNameAndUser(String projectName, User user);
        
        @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.user = :user")
        Collection<Project> findAllByUserWithTasks(@Param("user") User user);

        @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id")
        Optional<Project> findByIdWithTasks(@Param("id") Long id);
        
        Collection<Project> findAllByUser(User user);
}
