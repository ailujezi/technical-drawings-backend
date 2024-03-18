package com.ritzjucy.technicaldrawingsbackend.controller;

import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.repository.UserRepo;
import com.ritzjucy.technicaldrawingsbackend.exception.AuthException;
import com.ritzjucy.technicaldrawingsbackend.exception.NotFoundException;
import com.ritzjucy.technicaldrawingsbackend.exception.Validation;
import com.ritzjucy.technicaldrawingsbackend.model.request.CreateTokenRequestModel;
import com.ritzjucy.technicaldrawingsbackend.model.request.CreateUserRequestModel;
import com.ritzjucy.technicaldrawingsbackend.model.request.RefreshTokenRequestModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.AccessRefreshTokenResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.AccessTokenResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import com.ritzjucy.technicaldrawingsbackend.model.response.UserResponseModel;
import com.ritzjucy.technicaldrawingsbackend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("auth")
public class AuthController
{
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TokenService tokenService;

    @PostMapping(
            path = "users",
            produces = "application/json")
    @Operation(
            security = {},
            tags = "auth",
            description = "create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional
    public @ResponseBody UserResponseModel createUser(@RequestBody CreateUserRequestModel createUserRequestModel)
    {
        Validation.validateNonNull(createUserRequestModel.username, "username");
        Validation.validateNonNull(createUserRequestModel.password, "password");
        Validation.validateNonNull(createUserRequestModel.email, "email");
        Validation.validateNonNull(createUserRequestModel.firstName, "first_name");
        Validation.validateNonNull(createUserRequestModel.lastName, "last_name");

        UserEntity userEntity = UserEntity.builder()
                .username(createUserRequestModel.username)
                .password(createUserRequestModel.password)
                .email(createUserRequestModel.email)
                .firstName(createUserRequestModel.firstName)
                .lastName(createUserRequestModel.lastName)
                .build();

        userEntity = userRepo.save(userEntity);

        return UserResponseModel.builder()
                .id(userEntity.getId())
                .username(createUserRequestModel.username)
                .email(createUserRequestModel.email)
                .firstName(createUserRequestModel.firstName)
                .lastName(createUserRequestModel.lastName)
                .build();
    }

    @PostMapping(
            path = "jwt/create",
            produces = "application/json")
    @Operation(
            security = {},
            tags = "auth",
            description = "create access/refresh tokens for an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AccessRefreshTokenResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody AccessRefreshTokenResponseModel createToken(@RequestBody CreateTokenRequestModel createTokenRequestModel)
    {
        Validation.validateNonNull(createTokenRequestModel.username, "username");
        Validation.validateNonNull(createTokenRequestModel.password, "password");

        UserEntity userEntity = userRepo.findByUsername(createTokenRequestModel.username).orElseThrow(
                () -> new NotFoundException("user with username %s not found".formatted(createTokenRequestModel.username)));
        if (!userEntity.getPassword().equals(createTokenRequestModel.password)) {
            throw new AuthException("invalid username or password");
        }

        String access = tokenService.createAccessToken(userEntity);
        String refresh = tokenService.createRefreshToken(userEntity);

        return AccessRefreshTokenResponseModel.builder()
                .access(access)
                .refresh(refresh)
                .build();
    }

    @PostMapping(
            path = "jwt/refresh",
            produces = "application/json")
    @Operation(
            security = {},
            tags = "auth",
            description = "create access/refresh tokens using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AccessTokenResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Auth error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Not found error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseModel.class)))})
    @Transactional(readOnly = true)
    public @ResponseBody AccessTokenResponseModel refreshToken(@RequestBody RefreshTokenRequestModel refreshTokenRequestModel)
    {
        Validation.validateNonNull(refreshTokenRequestModel.refresh, "refresh");

        Long userId = tokenService.getUserId(refreshTokenRequestModel.refresh);
        UserEntity userEntity = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("user not found for refresh token"));

        String access = tokenService.createAccessToken(userEntity);

        return AccessTokenResponseModel.builder()
                .access(access)
                .build();
    }

}