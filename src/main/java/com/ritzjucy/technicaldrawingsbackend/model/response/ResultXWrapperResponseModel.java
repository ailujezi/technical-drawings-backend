package com.ritzjucy.technicaldrawingsbackend.model.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultXWrapperResponseModel <T>
{
    @Builder.Default
    @JsonIgnore
    public Map<String, T> data = new HashMap<>();

    @JsonAnyGetter
    public Map<String, T> dataFields() {
        return data;
    }

    @JsonAnySetter
    public void setDataField(String name, T value) {
        data.put(name, value);
    }

}
