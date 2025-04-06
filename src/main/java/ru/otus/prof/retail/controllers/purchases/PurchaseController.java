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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.dto.error.ErrorResponse;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.services.purchases.PurchaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/purchases/check")
@Validated
@Tag(name = "Purchase Controller", description = "API для работы с чеками покупок")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);
    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Operation(summary = "Получить чеки по ID смены")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Чеки найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Смена не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/shift/{shiftId}")
    public List<PurchaseDTO> getPurchasesByShift(
            @Parameter(description = "ID смены", required = true, example = "1")
            @PathVariable @NotNull(message = "ID смены не может быть null")
            @Positive(message = "ID смены должен быть положительным числом") Long shiftId) {

        logger.info("Запрос всех чеков по смене с ID: {}", shiftId);
        List<PurchaseDTO> result = purchaseService.getPurchaseByShiftId(shiftId);
        logger.info("Найдено {} чеков для смены с ID: {}", result.size(), shiftId);
        return result;
    }

    @Operation(summary = "Пакетное получение чеков по списку ID смен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Чеки найдены",
                    content = @Content(schema = @Schema(implementation = PurchaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Одна или несколько смен не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/shift/batch")
    public Map<Long, List<PurchaseDTO>> getPurchasesByShiftIds(
            @Parameter(description = "Список ID смен", required = true)
            @RequestBody @Valid @NotEmpty List<@Positive Long> shiftIds) {

        logger.info("Пакетный запрос чеков для {} смен", shiftIds.size());
        Map<Long, List<PurchaseDTO>> result = purchaseService.getPurchasesByShiftIds(shiftIds);

        if (result.size() < shiftIds.size()) {
            logger.warn("Найдены чеки только для {} из {} запрошенных смен",
                    result.size(), shiftIds.size());
        }

        logger.info("Успешно возвращены чеки для {} смен", result.size());
        return result;
    }

    @Operation(summary = "Получить чеки по номеру магазина и дате")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Чеки найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/shop/{shopNumber}/date/{date}")
    public List<PurchaseDTO> getPurchasesByShopAndDate(
            @Parameter(description = "Номер магазина", required = true, example = "15")
            @PathVariable @NotNull(message = "Номер магазина не может быть null")
            @Positive(message = "Номер магазина должен быть положительным числом") Long shopNumber,

            @Parameter(description = "Дата в формате YYYY-MM-DD", required = true, example = "2023-05-15")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        logger.info("Запрос чеков для магазина №{} за дату {}", shopNumber, date);
        List<PurchaseDTO> result = purchaseService.getPurchaseByShoNumberAndDate(shopNumber, date);
        logger.info("Найдено {} чеков для магазина №{} за дату {}", result.size(), shopNumber, date);
        return result;
    }

    @Operation(summary = "Получить чеки по номеру магазина, номеру кассы и дате")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Чеки найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные параметры",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Магазин или касса не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/shop/{shopNumber}/cash/{cashNumber}/date/{date}")
    public List<PurchaseDTO> getPurchasesByShopCashAndDate(
            @Parameter(description = "Номер магазина", required = true, example = "15")
            @PathVariable @NotNull(message = "Номер магазина не может быть null")
            @Positive(message = "Номер магазина должен быть положительным числом") Long shopNumber,

            @Parameter(description = "Номер кассы", required = true, example = "3")
            @PathVariable @NotNull(message = "Номер кассы не может быть null")
            @Positive(message = "Номер кассы должен быть положительным числом") Long cashNumber,

            @Parameter(description = "Дата в формате YYYY-MM-DD", required = true, example = "2023-05-15")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        logger.info("Запрос чеков для магазина №{}, кассы №{} за дату {}", shopNumber, cashNumber, date);
        List<PurchaseDTO> result = purchaseService.getPurchaseByShopNumberAndCashNumberAndDate(shopNumber, cashNumber, date);
        logger.info("Найдено {} чеков для магазина №{}, кассы №{} за дату {}",
                result.size(), shopNumber, cashNumber, date);
        return result;
    }
}