package ru.otus.prof.retail.controllers.purchases;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.dto.error.ErrorResponse;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.services.purchases.PositionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/purchases/position")
@Validated
@Tag(name = "Position Controller", description = "API для работы с позициями в чеках")
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @Operation(summary = "Получить позиции по ID чека")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Позиции найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Чек не найден или не содержит позиций",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/purchase/{purchaseId}")
    public List<PositionDTO> getPositionsByPurchase(
            @Parameter(description = "ID чека", required = true, example = "123")
            @PathVariable @NotNull(message = "ID чека не может быть null")
            @Positive(message = "ID чека должен быть положительным числом") Long purchaseId) {

        logger.info("Запрос всех позиций для чека с ID: {}", purchaseId);
        List<PositionDTO> result = positionService.getPositionsByPurchaseId(purchaseId);
        logger.info("Найдено {} позиций для чека с ID: {}", result.size(), purchaseId);
        return result;
    }

    @Operation(summary = "Пакетное получение позиций по списку ID чеков")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Позиции найдены",
                    content = @Content(schema = @Schema(implementation = PositionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Один или несколько чеков не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/batch")
    public Map<Long, List<PositionDTO>> getPositionsByPurchaseIds(
            @Parameter(description = "Список ID чеков", required = true)
            @RequestBody @Valid @NotEmpty List<@Positive Long> purchaseIds) {

        logger.info("Пакетный запрос позиций для {} чеков", purchaseIds.size());
        Map<Long, List<PositionDTO>> result = positionService.getPositionsByPurchaseIds(purchaseIds);

        if (result.size() < purchaseIds.size()) {
            logger.warn("Найдены позиции только для {} из {} запрошенных чеков",
                    result.size(), purchaseIds.size());
        }

        logger.info("Успешно возвращены позиции для {} чеков", result.size());
        return result;
    }
}