package com.example.bankcards.controller.impl;

import static com.example.bankcards.constants.TestConstants.AMOUNT_IS_REQUIRED_MESSAGE;
import static com.example.bankcards.constants.TestConstants.SUCCESSFUL_TRANSFER_MESSAGE;
import static com.example.bankcards.constants.TestConstants.BALANCE;
import static com.example.bankcards.constants.TestConstants.TRANSFER_DESCRIPTION;
import static com.example.bankcards.constants.TestConstants.PAGE_NUMBER;
import static com.example.bankcards.constants.TestConstants.PAGE_SIZE;
import static com.example.bankcards.constants.TestConstants.SOURCE_CARD_ID;
import static com.example.bankcards.constants.TestConstants.TARGET_CARD_ID;
import static com.example.bankcards.constants.TestConstants.TRANSFER_ID;
import static com.example.bankcards.constants.TestConstants.USERNAME_USER;
import static com.example.bankcards.constants.TestConstants.TRANSFER_HISTORY_URI;
import static com.example.bankcards.constants.TestConstants.TRANSFER_URI;
import static com.example.bankcards.constants.TestConstants.VALIDATION_FAILED_MESSAGE;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TransferControllerImplTest {

  @Mock
  private TransferService transferService;

  @Mock
  private Principal principal;

  @InjectMocks
  private TransferControllerImpl transferController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = createObjectMapper();
    mockMvc = MockMvcBuilders.standaloneSetup(transferController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
    principal = () -> USERNAME_USER;
  }

  @Test
  void transfer_ShouldReturnSuccess_WhenValidRequest() throws Exception {
    TransferRequestDto request = createValidTransferRequest();
    TransferResponseDto response = createTransferResponse();

    when(transferService.transferBetweenOwnCards(any(TransferRequestDto.class), eq(USERNAME_USER)))
        .thenReturn(response);

    mockMvc.perform(post(TRANSFER_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(TRANSFER_ID.toString()))
        .andExpect(jsonPath("$.data.amount").isNumber())
        .andExpect(jsonPath("$.data.amount").value(1500.5))
        .andExpect(jsonPath("$.message").value(SUCCESSFUL_TRANSFER_MESSAGE));
  }

  @Test
  void transfer_ShouldReturnBadRequest_WhenAmountIsNull() throws Exception {
    TransferRequestDto request = createValidTransferRequest();
    request.setAmount(null);

    mockMvc.perform(post(TRANSFER_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(containsString(VALIDATION_FAILED_MESSAGE)))
        .andExpect(jsonPath("$.message").value(containsString(AMOUNT_IS_REQUIRED_MESSAGE)));
  }

  @Test
  void transfer_ShouldReturnBadRequest_WhenAmountIsZero() throws Exception {
    TransferRequestDto request = createValidTransferRequest();
    request.setAmount(BigDecimal.ZERO);

    mockMvc.perform(post(TRANSFER_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  void transfer_ShouldReturnBadRequest_WhenAmountExceedsLimit() throws Exception {
    TransferRequestDto request = createValidTransferRequest();
    request.setAmount(new BigDecimal("1000001"));

    mockMvc.perform(post(TRANSFER_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  void transfer_ShouldReturnBadRequest_WhenToCardIdIsBlank() throws Exception {
    TransferRequestDto request = createValidTransferRequest();
    request.setToCardId("");

    mockMvc.perform(post(TRANSFER_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  void transfer_ShouldReturnBadRequest_WhenDescriptionExceedsMaxLength() throws Exception {
    TransferRequestDto request = createValidTransferRequest();
    String longDescription = "A".repeat(256);
    request.setDescription(longDescription);

    mockMvc.perform(post(TRANSFER_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  @Test
  void getTransferHistory_ShouldReturnPage_WhenValidRequest() throws Exception {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    List<TransferResponseDto> transfers = List.of(
        createTransferResponse(),
        createTransferResponse()
    );
    Page<TransferResponseDto> page = new PageImpl<>(transfers, pageable, transfers.size());

    when(transferService.getTransferHistory(eq(USERNAME_USER), any(Pageable.class)))
        .thenReturn(page);

    mockMvc.perform(get(TRANSFER_HISTORY_URI)
            .principal(principal)
            .param("page", PAGE_NUMBER.toString())
            .param("size", PAGE_SIZE.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.content.length()").value(2))
        .andExpect(jsonPath("$.data.totalElements").value(2))
        .andExpect(jsonPath("$.data.totalPages").value(1));
  }

  @Test
  void getTransferHistory_ShouldReturnEmptyPage_WhenNoTransfers() throws Exception {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    Page<TransferResponseDto> page = Page.empty(pageable);

    when(transferService.getTransferHistory(eq(USERNAME_USER), any(Pageable.class)))
        .thenReturn(page);

    mockMvc.perform(get(TRANSFER_HISTORY_URI)
            .principal(principal)
            .param("page", PAGE_NUMBER.toString())
            .param("size", PAGE_SIZE.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isEmpty())
        .andExpect(jsonPath("$.data.totalElements").value(0));
  }

  @Test
  void getTransferHistory_ShouldUseDefaultPagination_WhenNoParams() throws Exception {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    Page<TransferResponseDto> page = Page.empty(pageable);

    when(transferService.getTransferHistory(eq(USERNAME_USER), any(Pageable.class)))
        .thenReturn(page);

    mockMvc.perform(get(TRANSFER_HISTORY_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void transfer_ShouldThrowException_WhenPrincipalIsNull() throws Exception {
    TransferRequestDto request = createValidTransferRequest();

    mockMvc.perform(post(TRANSFER_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void transfer_ShouldHandleValidationException_WhenInvalidRequest() throws Exception {
    TransferRequestDto request = new TransferRequestDto();

    mockMvc.perform(post(TRANSFER_URI)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  private ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  private TransferRequestDto createValidTransferRequest() {
    TransferRequestDto request = new TransferRequestDto();
    request.setFromCardId(SOURCE_CARD_ID.toString());
    request.setToCardId(TARGET_CARD_ID.toString());
    request.setAmount(BALANCE);
    request.setDescription(TRANSFER_DESCRIPTION);
    return request;
  }

  private TransferResponseDto createTransferResponse() {
    TransferResponseDto response = new TransferResponseDto();
    response.setId(TRANSFER_ID);
    response.setFromCardId(SOURCE_CARD_ID);
    response.setFromCardMaskedNumber("****1234");
    response.setToCardId(TARGET_CARD_ID);
    response.setToCardMaskedNumber("****5678");
    response.setAmount(BALANCE);
    response.setDescription(TRANSFER_DESCRIPTION);
    response.setTimestamp(LocalDateTime.now());
    return response;
  }
}
