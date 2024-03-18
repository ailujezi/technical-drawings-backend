package com.ritzjucy.technicaldrawingsbackend.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResultResponseModel
{
    @JsonProperty("project_id")
    public Long projectId;

    @JsonProperty("image_id")
    public Long imageId;

    @JsonProperty("ai_model_id")
    public Long aiModelId;

    @JsonProperty("text_recognition_image_url")
    public String textRecognitionImageUrl;

    @JsonProperty("result_recognition")
    public ResultXWrapperResponseModel<ResultRecognitionResponseModel> resultRecognition;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public OffsetDateTime updatedAt;

}
