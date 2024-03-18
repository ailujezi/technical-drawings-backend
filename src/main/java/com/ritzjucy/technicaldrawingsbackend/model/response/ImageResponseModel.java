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
public class ImageResponseModel
{
    @JsonProperty("id")
    public Long id;

    @JsonProperty("project_id")
    public Long projectId;

    @JsonProperty("name")
    public String name;

    @JsonProperty("old_name")
    public String oldName;

    @JsonProperty("type")
    public String type;

    @JsonProperty("image_url")
    public String imageUrl;

    @JsonProperty("has_result")
    public Boolean hasResult;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public OffsetDateTime updatedAt;

}
