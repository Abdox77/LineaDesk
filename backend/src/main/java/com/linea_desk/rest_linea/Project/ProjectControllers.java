package com.linea_desk.rest_linea.Project;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;


import org.springframework.web.bind.annotation.PathVariable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Optional;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.dto.ApiResponse;


@Log4j2
@RestController
@RequestMapping(path="/api")
public class ProjectControllers{

    private final ProjectServices projectServices;
    private static final Logger log = LogManager.getLogger(ProjectControllers.class);


    public ProjectControllers(
            ProjectServices projectServices
    ){
        this.projectServices = projectServices;
    }

    @RequestMapping(path="/project", method=POST)
    public ResponseEntity<ApiResponse<?>>
    createNewProject(
            @AuthenticationPrincipal User currentUser,
            @RequestBody ProjectRequestDto request
    ) {
        ApiResponse<?> response;

        try {
            Optional<ProjectResponseDto> project = projectServices.createNewProject(request, currentUser);
            if (project.isEmpty()) {
                throw new Exception("invalid operation project dto is empty");
            }

            ProjectResponseDto responseDto = project.get();
            response = new ApiResponse<>(
                    true,
                    "Project created successfuly",
                    responseDto
            );
        }
        catch (Exception e) {
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error"
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(path="/project/{id}", method=GET)
    public ResponseEntity<ApiResponse<?>>
    getUserProjects(
        @AuthenticationPrincipal User user,
        @PathVariable Long id
    ) {
        ApiResponse<?> response;
        try {
            Optional<ProjectResponseDto> project = projectServices.getProjectById(id, user);

            if(project.isEmpty()){
                response = new ApiResponse<>(
                    false,
                    "Project not found"
                );

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response = new ApiResponse(
                true,
                "Project search was a success",
                project
            );
        }
        catch (Exception e) {
            response = new ApiResponse<>(
                    false,
                    "Internal Server Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @RequestMapping(path="/project", method="PUT")
//    public ResponseEntity<ApiResponse<?>> UpdateProject() {
//        try {

//        }
//        catch (Exception e) {

//        }
//    }


//    @RequestMapping(path="/project", method="DELETE")
//    public ResponseEntity<ApiResponse<?>> deleteProject() {
//        try {
//
//        }
//        catch (Exception e) {
//
//        }
//    }

}
