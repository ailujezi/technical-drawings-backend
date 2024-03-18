package com.ritzjucy.technicaldrawingsbackend.model;

import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.model.response.ImageResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ProjectResponseModel;
import com.ritzjucy.technicaldrawingsbackend.util.ImageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Mapper
{
    @Value("${app.host}")
    private String appHost;

    public ImageResponseModel map(ImageEntity image)
    {
        Long projectId = image.getProject().getId();

        return ImageResponseModel.builder()
                .id(image.getId())
                .projectId(projectId)
                .name(image.getName() + "." + image.getType())
                .oldName(image.getDisplayOldName())
                .type(image.getType())
                .imageUrl(ImageUtil.buildInputImageUrl(appHost, image))
                .hasResult(image.getResult() != null)
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }

    public ProjectResponseModel map(ProjectEntity project)
    {
        return ProjectResponseModel.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .aiModelId(project.getAiModelId())
                .status(project.getStatus())
                .images(project.getImages().stream()
                        .map(this::map)
                        .toList())
                .imagesNr((long)project.getImages().size())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

}
