package com.ritzjucy.technicaldrawingsbackend.controller;

import com.ritzjucy.technicaldrawingsbackend.Ai;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectStatus;
import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import com.ritzjucy.technicaldrawingsbackend.exception.NotFoundException;
import com.ritzjucy.technicaldrawingsbackend.exception.Validation;
import com.ritzjucy.technicaldrawingsbackend.exception.ValidationException;
import com.ritzjucy.technicaldrawingsbackend.model.Mapper;
import com.ritzjucy.technicaldrawingsbackend.model.request.CreateProjectRequestModel;
import com.ritzjucy.technicaldrawingsbackend.model.request.PatchProjectRequestModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ProjectResponseModel;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ProjectRepo;
import com.ritzjucy.technicaldrawingsbackend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("store/projects")
public class ProjectController
{
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Mapper mapper;

    @GetMapping(
            path = "",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "project",
            description = "list all projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectResponseModel.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody List<ProjectResponseModel> getAllProjects(
            HttpServletRequest request)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        List<ProjectEntity> projects = projectRepo.findAllByUser(user);

        return projects.stream()
                .map(project -> mapper.map(project))
                .toList();
    }

    @GetMapping(
            path = "{projectId}",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "project",
            description = "get a single project by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ProjectResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody ProjectResponseModel getProjectById(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }

        return mapper.map(project);
    }

    @PostMapping(
            path = "",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "project",
            description = "create a new project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ProjectResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public @ResponseBody ProjectResponseModel createProject(
            HttpServletRequest request,
            @RequestBody CreateProjectRequestModel createProjectRequestModel)
    {
        tokenService.validateToken(request);

        Validation.validateNonNull(createProjectRequestModel.name, "name");
        Validation.validateNonNull(createProjectRequestModel.description, "description");
        Validation.validateNonNull(createProjectRequestModel.aiModelId, "ai_model_id");

        UserEntity user = tokenService.getUserFromToken(request);


        Ai ai = Ai.all.stream()
                .filter(n -> n.id.equals(createProjectRequestModel.aiModelId))
                .findAny()
                .orElseThrow(() -> new ValidationException("could not find ai model with id %d".formatted(createProjectRequestModel.aiModelId)));

        ProjectEntity project = ProjectEntity.builder()
                .name(createProjectRequestModel.getName())
                .description(createProjectRequestModel.getDescription())
                .aiModelId(ai.id)
                .status(ProjectStatus.PENDING)
                .user(user)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        user.getProjects().add(project);

        project = projectRepo.save(project);

        return getProjectById(request, project.getId());
    }

    @PatchMapping(
            path = "{projectId}",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "project",
            description = "update an existing project by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ProjectResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public @ResponseBody ProjectResponseModel updateProject(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @RequestBody PatchProjectRequestModel patchProjectRequestModel)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);

        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }

        if (patchProjectRequestModel.name != null) {
            project.setName(patchProjectRequestModel.getName());
        }
        if (patchProjectRequestModel.description != null) {
            project.setDescription(patchProjectRequestModel.getDescription());
        }

        project.setUpdatedAt(OffsetDateTime.now());

        project = projectRepo.save(project);

        return getProjectById(request, project.getId());
    }

    @DeleteMapping(
            path = "{projectId}",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "project",
            description = "delete an existing project by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public ResponseEntity<Void> deleteProject(
            HttpServletRequest request,
            @PathVariable Long projectId)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);

        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("Could not find project with id %d".formatted(projectId));
        }

        projectRepo.delete(project);

        return ResponseEntity.ok().build();
    }

}