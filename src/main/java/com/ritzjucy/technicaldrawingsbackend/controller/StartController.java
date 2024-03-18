package com.ritzjucy.technicaldrawingsbackend.controller;

import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ImageRepo;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ProjectRepo;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ResultRepo;
import com.ritzjucy.technicaldrawingsbackend.exception.NotFoundException;
import com.ritzjucy.technicaldrawingsbackend.model.Mapper;
import com.ritzjucy.technicaldrawingsbackend.service.AiDetectionService;
import com.ritzjucy.technicaldrawingsbackend.service.TokenService;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectStatus;
import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("store/projects")
public class StartController
{
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private ResultRepo resultRepo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AiDetectionService aiDetectionService;

    @Autowired
    private Mapper mapper;

    @PostMapping(
            path = "{projectId}/start",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "start",
            description = "start ai generation for all image in a given project. all previous results are deleted upon starting the generation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public ResponseEntity<Void> start(
            HttpServletRequest request,
            @PathVariable Long projectId)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }

        for (ImageEntity image : project.getImages()) {
            if (image.getResult() == null) {
                continue;
            }

            resultRepo.delete(image.getResult());
            image.setResult(null);

            imageRepo.save(image);
        }

        project.setStatus(ProjectStatus.PROCESSING);

        projectRepo.save(project);

        aiDetectionService.runAsync(projectId, true);

        return ResponseEntity.ok().build();
    }

    @PostMapping(
            path = "{projectId}/start_rest",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "start",
            description = "start ai generation for all image without results in a given project. existing results remain unchanged")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public ResponseEntity<Void> startRest(
            HttpServletRequest request,
            @PathVariable Long projectId)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }

        project.setStatus(ProjectStatus.PROCESSING);

        projectRepo.save(project);

        aiDetectionService.runAsync(projectId, false);

        return ResponseEntity.ok().build();
    }

}