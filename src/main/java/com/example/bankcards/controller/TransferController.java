package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Transfer Management", description = "Money transfer operations between cards")
public interface TransferController {

  @Operation(
      summary = "Transfer money between cards",
      description = "Transfer money between user's own cards. Both cards must belong to the authenticated user."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Transfer completed successfully",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid transfer request (insufficient funds, invalid cards, etc.)"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden - cards don't belong to user"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "One or both cards not found"
      )
  })
  ResponseEntity<ApiResponseDto<TransferResponseDto>> transfer(
      @Valid @RequestBody TransferRequestDto request,
      @Parameter(description = "Authenticated user principal", hidden = true)
      Principal principal
  );

  @Operation(
      summary = "Get transfer history",
      description = "Get paginated history of user's transfers"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Transfer history retrieved",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      )
  })
  ResponseEntity<ApiResponseDto<Page<TransferResponseDto>>> getTransferHistory(
      @Parameter(description = "Pagination parameters (page, size, sort)")
      Pageable pageable,
      @Parameter(description = "Authenticated user principal", hidden = true)
      Principal principal
  );
}
