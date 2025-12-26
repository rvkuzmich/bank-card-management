package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.CardResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Card Management", description = "Operations with bank cards")
public interface CardController {

  @Operation(
      summary = "Create new card",
      description = "Create a new bank card. Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Card created successfully",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid card data"
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
  ResponseEntity<ApiResponseDto<CardResponseDto>> createCard(
      @Valid @RequestBody CardRequestDto request,
      Principal principal
  );

  @Operation(
      summary = "Get user's cards",
      description = "Get paginated list of user's cards with filtering options"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Cards retrieved successfully",
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
          description = "Forbidden"
      )
  })
  ResponseEntity<ApiResponseDto<Page<CardResponseDto>>> getMyCards(
      @Valid CardFilterRequestDto filter,
      @Parameter(description = "Pagination parameters")
      Pageable pageable,
      Principal principal
  );

  @Operation(
      summary = "Block card",
      description = "Block a card. For users - creates block request, for admins - immediate block."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Card blocked or block request created",
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
          description = "Forbidden"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Card not found"
      )
  })
  ResponseEntity<ApiResponseDto<?>> blockCard(
      @Parameter(description = "Card ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String cardId,
      Principal principal
  );

  @Operation(
      summary = "Approve card block request",
      description = "Admin approval for card block request. Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Card block approved",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request"
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
          description = "Card or block request not found"
      )
  })
  ResponseEntity<ApiResponseDto<CardResponseDto>> approveBlockCard(
      @Parameter(description = "Card ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String cardId,
      @Parameter(description = "Username for block request (optional)", required = false)
      @RequestParam(required = false) String username
  );

  @Operation(
      summary = "Reject card block request",
      description = "Admin rejection for card block request. Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Card block rejected",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request"
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
          description = "Card or block request not found"
      )
  })
  ResponseEntity<ApiResponseDto<CardResponseDto>> rejectBlockCard(
      @Parameter(description = "Card ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String cardId,
      @Parameter(description = "Username for block request (optional)", required = false)
      @RequestParam(required = false) String username
  );

  @Operation(
      summary = "Activate card",
      description = "Activate a previously blocked card. Admin only."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Card activated",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Card is not blocked"
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
          description = "Card not found"
      )
  })
  ResponseEntity<ApiResponseDto<CardResponseDto>> activateCard(
      @Parameter(description = "Card ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String cardId,
      Principal principal
  );

  @Operation(
      summary = "Get card balance",
      description = "Get current balance of a specific card"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Balance retrieved",
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
          description = "Forbidden - user doesn't have access to this card"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Card not found"
      )
  })
  ResponseEntity<ApiResponseDto<BigDecimal>> getBalance(
      @Parameter(description = "Card ID", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String cardId,
      Principal principal
  );
}
