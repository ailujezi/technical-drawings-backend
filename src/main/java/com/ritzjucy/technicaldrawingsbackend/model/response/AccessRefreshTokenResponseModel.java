package com.ritzjucy.technicaldrawingsbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessRefreshTokenResponseModel
{
    @JsonProperty("refresh")
    public String refresh;

    @JsonProperty("access")
    public String access;

}
