package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public interface AuthController {

  @Operation(
      summary = "User authentication",
      description = "Returns JWT token for authentication requests"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Authentication successful",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Invalid credentials"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request format"
      )
  })
  ResponseEntity<ApiResponseDto<JwtResponseDto>> login(
      @Valid @RequestBody LoginRequestDto request
  );

  @Operation(
      summary = "New user registration",
      description = "Creates a new user account in the system"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "User registration successful",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid user data or user already exists"
      ),
      @ApiResponse(
          responseCode = "409",
          description = "Conflict: user with email/username already exists"
      )
  })
  ResponseEntity<ApiResponseDto<UserResponseDto>> register(
      @Valid @RequestBody RegisterRequestDto request
  );

  @Operation(
      summary = "Get user profile",
      description = "Returns information about authenticated user. JWT token requires."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User profile received",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Insufficient access rights"
      )
  })
  ResponseEntity<ApiResponseDto<UserResponseDto>> getProfile(Principal principal);
}
