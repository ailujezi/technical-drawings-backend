package com.ritzjucy.technicaldrawingsbackend.model.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTokenRequestModel
{
    @JsonProperty("username")
    public String username;

    @JsonProperty("password")
    public String password;

}
