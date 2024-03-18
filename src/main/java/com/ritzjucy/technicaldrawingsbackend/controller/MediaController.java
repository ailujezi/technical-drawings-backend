package com.ritzjucy.technicaldrawingsbackend.controller;

import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ResultEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ImageRepo;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ProjectRepo;
import com.ritzjucy.technicaldrawingsbackend.exception.NotFoundException;
import com.ritzjucy.technicaldrawingsbackend.exception.ValidationException;
import com.ritzjucy.technicaldrawingsbackend.service.TokenService;
import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;


@Controller
public class MediaController
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ImageRepo imageRepo;

    @GetMapping(
            path = "media/project_{projectId}/{imagePath}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "media",
            description = "Get image bytes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getInputImage(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable String imagePath)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));
        ImageEntity image = project.getImages().stream().filter(n -> n.getDisplayName().equalsIgnoreCase(imagePath)).findAny().orElseThrow(
                () -> new NotFoundException("could not find image with name %s in project with id %d".formatted(imagePath, projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", imagePath);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(image.getData());
    }

    @GetMapping(
            path = "media/outputs/project_{projectId}/{imageName}/text_recognition/final/visual/{imagePath}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "media",
            description = "Get image bytes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getRecognitionImage(
            HttpServletRequest request,
            @PathVariable Long projectId,
            @PathVariable String imageName,
            @PathVariable String imagePath)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));
        ImageEntity image = project.getImages().stream().filter(n -> n.getDisplayName().equalsIgnoreCase(imagePath)).findAny().orElseThrow(
                () -> new NotFoundException("could not find image with name %s in project with id %d".formatted(imagePath, projectId)));
        ResultEntity result = image.getResult();

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }

        if (!imagePath.startsWith(imageName)) {
            throw new ValidationException("image name %s does not match image path %s".formatted(imageName, imagePath));
        }

        if (result == null) {
            throw new ValidationException("image %s has no result".formatted(imagePath));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", imageName);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(result.getTextRecognitionImageData());
    }

}