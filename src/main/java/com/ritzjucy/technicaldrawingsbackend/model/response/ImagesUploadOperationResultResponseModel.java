package com.ritzjucy.technicaldrawingsbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImagesUploadOperationResultResponseModel
{
    @JsonProperty("data")
    @Builder.Default
    public List<ImageResponseModel> data = new ArrayList<>();

    @JsonProperty("error")
    @Builder.Default
    public boolean error = false;

    @JsonProperty("error_msg")
    @Builder.Default
    public String errorMessage = "";

    @JsonProperty("bad_images")
    @Builder.Default
    public List<String> badImages = new ArrayList<>();

}
