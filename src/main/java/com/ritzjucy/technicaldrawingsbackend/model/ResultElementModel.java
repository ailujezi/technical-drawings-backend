package com.ritzjucy.technicaldrawingsbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultElementModel
{
    @JsonProperty("guid")
    @Builder.Default
    private String guid = UUID.randomUUID().toString();

    @JsonProperty("class_id")
    private Long classId;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("bbox_xyxy_abs")
    private List<Integer> bbox;

    @JsonProperty("text")
    private String text;

    public static ResultElementModel from(AIDetection aiDetection)
    {
        return ResultElementModel.builder()
                .text(aiDetection.text())
                .bbox(List.of(
                    aiDetection.x(),
                    aiDetection.y(),
                    aiDetection.x() + aiDetection.width(),
                    aiDetection.y() + aiDetection.height()))
                .confidence(aiDetection.confidence())
                .classId(0L)
                .build();
    }

}