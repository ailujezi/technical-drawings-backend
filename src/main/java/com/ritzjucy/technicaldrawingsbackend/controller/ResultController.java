package com.ritzjucy.technicaldrawingsbackend.controller;

import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ResultEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.ProjectRepo;
import com.ritzjucy.technicaldrawingsbackend.exception.NotFoundException;
import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ProjectResultResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ResultRecognitionResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ResultXWrapperResponseModel;
import com.ritzjucy.technicaldrawingsbackend.service.TokenService;
import com.ritzjucy.technicaldrawingsbackend.util.ImageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Controller
@RequestMapping("store/projects/{projectId}/results")
public class ResultController
{
    @Value("${app.host}")
    private String appHost;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private TokenService tokenService;

    @GetMapping(
            path = "",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "result",
            description = "get all results for a single project id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectResultResponseModel.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody List<ProjectResultResponseModel> getAllResults(
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

        Function<ImageEntity, String> urlBuilder = image -> ImageUtil.buildRecognitionOutputImageUrl(
                appHost,
                image);

        List<ProjectResultResponseModel> models = new ArrayList<>();

        for (ImageEntity image : project.getImages()) {
            if (image.getResult() == null) {
                continue;
            }

            ResultEntity result = image.getResult();

            ResultRecognitionResponseModel recognition = ResultRecognitionResponseModel.builder()
                    .visualResultPath(ImageUtil.buildImageVisualPath(image))
                    .elements(result.getElements())
                    .build();

            ResultXWrapperResponseModel<ResultRecognitionResponseModel> recognitionWrapper
                    = ResultXWrapperResponseModel.<ResultRecognitionResponseModel>builder()
                    .data(Map.of(image.getDisplayOldName(), recognition))
                    .build();

            ProjectResultResponseModel model = ProjectResultResponseModel.builder()
                    .aiModelId(project.getAiModelId())
                    .projectId(project.getId())
                    .imageId(image.getId())
                    .textRecognitionImageUrl(urlBuilder.apply(image))
                    .resultRecognition(recognitionWrapper)
                    .createdAt(result.getCreatedAt())
                    .updatedAt(result.getUpdatedAt())
                    .build();

            models.add(model);
        }

        return models;
    }

}