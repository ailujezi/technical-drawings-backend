package com.ritzjucy.technicaldrawingsbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ritzjucy.technicaldrawingsbackend.model.ResultElementModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultRecognitionResponseModel
{
    @JsonProperty("visual_result_path")
    public String visualResultPath;

    @JsonProperty("elements")
    @Builder.Default
    public List<ResultElementModel> elements = new ArrayList<>();

}
