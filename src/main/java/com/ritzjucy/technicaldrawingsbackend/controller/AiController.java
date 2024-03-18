package com.ritzjucy.technicaldrawingsbackend.controller;

import com.ritzjucy.technicaldrawingsbackend.Ai;
import com.ritzjucy.technicaldrawingsbackend.model.response.AiResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import com.ritzjucy.technicaldrawingsbackend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class AiController
{
    @Autowired
    private TokenService tokenService;

    @GetMapping(
            path = "store/ais",
            produces = "application/json")
    @Operation(
            security = @SecurityRequirement(name = "jwtAuth"),
            tags = "ai",
            description = "Lists all available ai models")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AiResponseModel.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody List<AiResponseModel> getAis(
            HttpServletRequest request)
    {
        tokenService.validateToken(request);

        return Ai.all.stream()
                .map(ai -> (AiResponseModel) AiResponseModel.builder()
                        .id(ai.id)
                        .name(ai.name)
                        .description(ai.description)
                        .build())
                .toList();
    }

}