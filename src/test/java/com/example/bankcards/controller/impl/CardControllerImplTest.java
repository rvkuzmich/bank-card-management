package com.example.bankcards.controller.impl;

import static com.example.bankcards.constants.TestConstants.BLOCK_REQUEST_MESSAGE;
import static com.example.bankcards.constants.TestConstants.CARDS_ACTIVATE_URI;
import static com.example.bankcards.constants.TestConstants.CARDS_APPROVE_BLOCK_URI;
import static com.example.bankcards.constants.TestConstants.CARDS_BALANCE_URI;
import static com.example.bankcards.constants.TestConstants.CARDS_BLOCK_URI;
import static com.example.bankcards.constants.TestConstants.CARDS_URI;
import static com.example.bankcards.constants.TestConstants.CARD_NOT_FOUND_MESSAGE;
import static com.example.bankcards.constants.TestConstants.INVALID_JSON_FORMAT_MESSAGE;
import static com.example.bankcards.constants.TestConstants.INVALID_CARDHOLDER;
import static com.example.bankcards.constants.TestConstants.INVALID_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.BALANCE;
import static com.example.bankcards.constants.TestConstants.CARDHOLDER;
import static com.example.bankcards.constants.TestConstants.CARD_ID;
import static com.example.bankcards.constants.TestConstants.CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.CARD_STATUS_ACTIVE;
import static com.example.bankcards.constants.TestConstants.CARD_STATUS_BLOCKED;
import static com.example.bankcards.constants.TestConstants.EXPIRY_DATE;
import static com.example.bankcards.constants.TestConstants.MASKED_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.MAX_BALANCE;
import static com.example.bankcards.constants.TestConstants.MIN_BALANCE;
import static com.example.bankcards.constants.TestConstants.PAGE_NUMBER;
import static com.example.bankcards.constants.TestConstants.PAGE_SIZE;
import static com.example.bankcards.constants.TestConstants.USERNAME_USER;
import static com.example.bankcards.constants.TestConstants.USERNAME_ADMIN;
import static com.example.bankcards.constants.TestConstants.VALIDATION_FAILED_MESSAGE;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.USERNAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.BlockRequestResponseDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CardControllerImplTest {

  @Mock
  private CardService cardService;

  @InjectMocks
  private CardControllerImpl cardController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private Principal principal;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    mockMvc = MockMvcBuilders.standaloneSetup(cardController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();

    principal = () -> USERNAME;
  }

  @Test
  void createCard_ShouldReturnCreatedCard() throws Exception {
    CardRequestDto requestDto = CardRequestDto.builder()
        .cardholder(CARDHOLDER)
        .cardNumber(CARD_NUMBER)
        .expiryDate(EXPIRY_DATE)
        .build();

    CardResponseDto responseDto = CardResponseDto.builder()
        .id(CARD_ID)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .expiryDate(EXPIRY_DATE)
        .status(CardStatus.ACTIVE)
        .balance(BigDecimal.ZERO)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(cardService.createCard(any(CardRequestDto.class), eq(USERNAME_USER)))
        .thenReturn(responseDto);

    mockMvc.perform(post(CARDS_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto))
            .principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(CARD_ID))
        .andExpect(jsonPath("$.data.maskedNumber").value(MASKED_CARD_NUMBER))
        .andExpect(jsonPath("$.data.cardholder").value(CARDHOLDER))
        .andExpect(jsonPath("$.data.status").value(CARD_STATUS_ACTIVE));
  }

  @Test
  void createCard_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
    CardRequestDto requestDto = CardRequestDto.builder()
        .cardholder(INVALID_CARDHOLDER)
        .cardNumber(INVALID_CARD_NUMBER)
        .build();

    mockMvc.perform(post(CARDS_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto))
            .principal(principal))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)));
  }

  @Test
  void getMyCards_ShouldReturnPageOfCards() throws Exception {
    CardFilterRequestDto filter = CardFilterRequestDto.builder()
        .status(CardStatus.ACTIVE)
        .minBalance(MIN_BALANCE)
        .build();

    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    List<CardResponseDto> cards = List.of(
        createTestCardResponse("1"),
        createTestCardResponse("2")
    );
    Page<CardResponseDto> page = new PageImpl<>(cards, pageable, cards.size());

    doReturn(page).when(cardService)
        .getUserCards(eq(USERNAME), any(CardFilterRequestDto.class), any(Pageable.class));

    mockMvc.perform(get(CARDS_URI)
            .param("status", CARD_STATUS_ACTIVE)
            .param("minBalance", MIN_BALANCE.toString())
            .param("page", PAGE_NUMBER.toString())
            .param("size", PAGE_SIZE.toString())
            .principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.content[0].id").value("card-1"))
        .andExpect(jsonPath("$.data.totalElements").value(2))
        .andExpect(jsonPath("$.data.totalPages").value(1));
  }

  @Test
  void blockCard_AsAdmin_ShouldReturnCardResponse() throws Exception {
    CardResponseDto responseDto = CardResponseDto.builder()
        .id(CARD_ID)
        .status(CardStatus.BLOCKED)
        .maskedNumber(MASKED_CARD_NUMBER)
        .build();

    doReturn(responseDto).when(cardService)
        .blockCard(eq(CARD_ID), eq(USERNAME_ADMIN));

    Authentication auth = new TestingAuthenticationToken(
        USERNAME_ADMIN,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
    );
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);

    Principal adminPrincipal = () -> USERNAME_ADMIN;

    mockMvc.perform(post(CARDS_BLOCK_URI, CARD_ID)
            .principal(adminPrincipal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(CARD_ID))
        .andExpect(jsonPath("$.data.status").value(CARD_STATUS_BLOCKED));

    SecurityContextHolder.clearContext();
  }

  @Test
  void blockCard_AsRegularUser_ShouldReturnBlockRequestResponse() throws Exception {
    BlockRequestResponseDto responseDto = BlockRequestResponseDto.builder()
        .cardId(CARD_ID)
        .cardStatus(CardStatus.ACTIVE)
        .hasPendingRequest(true)
        .requestedAt(LocalDateTime.now())
        .requestedBy(USERNAME_USER)
        .message(BLOCK_REQUEST_MESSAGE)
        .build();

    doReturn(responseDto).when(cardService)
        .requestCardBlock(eq(CARD_ID), eq(USERNAME_USER));

    Authentication auth = new TestingAuthenticationToken(
        USERNAME_USER,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);

    mockMvc.perform(post(CARDS_BLOCK_URI, CARD_ID)
            .principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.cardId").value(CARD_ID))
        .andExpect(jsonPath("$.data.hasPendingRequest").value(true))
        .andExpect(jsonPath("$.data.message").exists());

    SecurityContextHolder.clearContext();
  }

  @Test
  void approveBlockCard_ShouldReturnCardResponse() throws Exception {
    CardResponseDto responseDto = CardResponseDto.builder()
        .id(CARD_ID)
        .status(CardStatus.BLOCKED)
        .maskedNumber(MASKED_CARD_NUMBER)
        .build();

    when(cardService.approveBlockCard(eq(CARD_ID), eq(USERNAME_USER)))
        .thenReturn(responseDto);

    mockMvc.perform(post(CARDS_APPROVE_BLOCK_URI, CARD_ID)
            .param("username", USERNAME_USER)
            .principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(CARD_ID))
        .andExpect(jsonPath("$.data.status").value(CARD_STATUS_BLOCKED));
  }

  @Test
  void activateCard_ShouldReturnActivatedCard() throws Exception {
    CardResponseDto responseDto = CardResponseDto.builder()
        .id(CARD_ID)
        .status(CardStatus.ACTIVE)
        .maskedNumber(MASKED_CARD_NUMBER)
        .build();

    when(cardService.activateCard(eq(CARD_ID), eq(USERNAME)))
        .thenReturn(responseDto);

    mockMvc.perform(post(CARDS_ACTIVATE_URI, CARD_ID)
            .principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(CARD_ID))
        .andExpect(jsonPath("$.data.status").value(CARD_STATUS_ACTIVE));
  }

  @Test
  void getBalance_ShouldReturnCardBalance() throws Exception {

    when(cardService.getCardBalance(eq(CARD_ID), eq(USERNAME)))
        .thenReturn(BALANCE);

    mockMvc.perform(get(CARDS_BALANCE_URI, CARD_ID)
            .principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").value(BALANCE.doubleValue()));
  }

  @Test
  void getBalance_WhenCardNotFound_ShouldReturnNotFound() throws Exception {
    when(cardService.getCardBalance(eq(CARD_ID), eq(USERNAME)))
        .thenThrow(new CardNotFoundException(CARD_NOT_FOUND_MESSAGE));

    mockMvc.perform(get(CARDS_BALANCE_URI, CARD_ID)
            .principal(principal))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(CARD_NOT_FOUND_MESSAGE)));
  }

  @Test
  void createCard_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(post(CARDS_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{invalid json}")
            .principal(principal))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(INVALID_JSON_FORMAT_MESSAGE)));
  }

  @Test
  void activateCard_WhenUnauthorized_ShouldReturnForbidden() throws Exception {
    when(cardService.activateCard(eq(CARD_ID), eq(USERNAME_USER)))
        .thenThrow(new AccessDeniedException("Access denied"));

    mockMvc.perform(post(CARDS_ACTIVATE_URI, CARD_ID)
            .principal(principal))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString("Access denied")));
  }

  @Test
  void getMyCards_WithMinBalanceGreaterThanMaxBalance_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get(CARDS_URI)
            .param("minBalance", MAX_BALANCE.toString())
            .param("maxBalance", MIN_BALANCE.toString())
            .principal(principal))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)));
  }

  @Test
  @WithAnonymousUser
  void getAllCards_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get(CARDS_URI))
        .andExpect(status().isUnauthorized());
  }

  private CardResponseDto createTestCardResponse(String suffix) {
    return CardResponseDto.builder()
        .id("card-" + suffix)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .expiryDate(EXPIRY_DATE)
        .status(CardStatus.ACTIVE)
        .balance(BALANCE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}