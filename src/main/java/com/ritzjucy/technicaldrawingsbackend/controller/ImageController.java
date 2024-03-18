package com.ritzjucy.technicaldrawingsbackend.controller;

import com.google.common.io.Files;
import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import com.ritzjucy.technicaldrawingsbackend.exception.NotFoundException;
import com.ritzjucy.technicaldrawingsbackend.exception.Validation;
import com.ritzjucy.technicaldrawingsbackend.model.Mapper;
import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ImageResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ImagesUploadOperationResultResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.*;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ImageRepo;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("store/projects/{projectId}/images")
public class ImageController
{
    private static final List<String> validFileTypes = List.of("png", "jpg");

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Mapper mapper;

    @GetMapping(
            path = "",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "image",
            description = "get all images attached to the given project id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ImageResponseModel.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody List<ImageResponseModel> getImagesOfProject(
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

        return project.getImages().stream()
                    .map(image -> mapper.map(image))
                    .toList();
    }

    @GetMapping(
            path = "{imageId}",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "image",
            description = "get a single image by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ImageResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody ImageResponseModel getImageById(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @PathVariable("imageId") Long imageId)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));
        ImageEntity image = imageRepo.findById(imageId).orElseThrow(
                () -> new NotFoundException("could not find image with id %d".formatted(imageId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }
        if(!Objects.equals(image.getProject().getId(), project.getId())) {
            throw new NotFoundException("Could not find image with id %d in project with id %d".formatted(imageId, projectId));
        }

        return mapper.map(image);
    }


    @PostMapping(
            path = "",
            produces = "application/json",
            consumes = "multipart/form-data")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "image",
            description = "upload an image and attach it to the given project id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ImagesUploadOperationResultResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public @ResponseBody ImagesUploadOperationResultResponseModel uploadImages(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @RequestParam("file") MultipartFile file) throws IOException {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);
        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(projectId));
        }

        Validation.validateNonNull(file, "file");

        String oldFileName = Files.getNameWithoutExtension(file.getOriginalFilename());
        String fileType = Files.getFileExtension(file.getOriginalFilename());

        if (!validFileTypes.contains(fileType.toLowerCase())) {
            return ImagesUploadOperationResultResponseModel.builder()
                    .badImages(List.of(file.getOriginalFilename()))
                    .error(true)
                    .errorMessage("invalid file type: %s".formatted(fileType))
                    .build();
        }

        String fileName = UUID.randomUUID().toString();

        byte[] bytes = file.getBytes();

        ImageEntity image = ImageEntity.builder()
                .name(fileName)
                .oldName(oldFileName)
                .project(project)
                .type(fileType)
                .data(bytes)
                .build();

        project.getImages().add(image);

        image = imageRepo.save(image);

        projectRepo.save(project);

        var model = (ImageResponseModel) getImageById(request, projectId, image.getId());

        return ImagesUploadOperationResultResponseModel.builder()
                .data(List.of(model))
                .build();
    }

    @DeleteMapping(
            path = "{imageId}",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "image",
            description = "delete a single image by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public ResponseEntity<Void> deleteImage(
            HttpServletRequest request,
            @PathVariable("projectId") Long projectId,
            @PathVariable("imageId") Long imageId)
    {
        tokenService.validateToken(request);

        UserEntity user = tokenService.getUserFromToken(request);

        ProjectEntity project = projectRepo.findById(projectId).orElseThrow(
                () -> new NotFoundException("could not find project with id %d".formatted(projectId)));

        if(!Objects.equals(project.getUser().getId(), user.getId())) {
            throw new NotFoundException("could not find project with id %d".formatted(imageId));
        }

        ImageEntity image = project.getImages().stream()
                .filter(i -> i.getId().equals(imageId))
                .findAny()
                .orElseThrow(
                        () -> new NotFoundException("could not find image with id %d in project with id %d".formatted(imageId, projectId)));

        project.getImages().remove(image);

        projectRepo.save(project);

        return ResponseEntity.ok().build();
    }

}