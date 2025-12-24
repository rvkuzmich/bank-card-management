package com.example.bankcards.controller;

import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User Management", description = "Admin user management operations")
public interface UserController {

  @Operation(
      summary = "Get all users",
      description = "Returns paginated list of all users. Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Users retrieved successfully",
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
          description = "Forbidden - admin role required"
      )
  })
  ResponseEntity<ApiResponseDto<Page<UserResponseDto>>> getAllUsers(
      @Parameter(description = "Pagination parameters")
      Pageable pageable
  );

  @Operation(
      summary = "Update user role",
      description = "Change user role (USER/ADMIN). Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User role updated successfully",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid role or cannot modify own role"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden - admin role required"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserRole(
      @Parameter(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String userId,
      @Parameter(
          description = "New role",
          example = "ADMIN",
          required = true,
          schema = @Schema(implementation = Role.class)
      )
      @RequestParam Role role
  );

  @Operation(
      summary = "Disable user account",
      description = "Disable user account. User will not be able to login. Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User disabled successfully",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Cannot disable own account or already disabled"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden - admin role required"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  ResponseEntity<ApiResponseDto<Void>> disableUser(
      @Parameter(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String userId
  );

  @Operation(
      summary = "Enable user account",
      description = "Enable previously disabled user account. Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User enabled successfully",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "User already enabled"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden - admin role required"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  ResponseEntity<ApiResponseDto<Void>> enableUser(
      @Parameter(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String userId
  );
}
