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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.dto.purchases.ShiftSearchRequest;
import ru.otus.prof.retail.services.purchases.ShiftService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases/shift")
@Validated
@Tag(name = "Управление сменами", description = "API для работы с кассовыми сменами")
public class ShiftController {
    private static final Logger logger = LoggerFactory.getLogger(ShiftController.class);

    @Autowired
    private ShiftService shiftService;

    //все смены
    @Operation(summary = "Получить смены по дате закрытия", description = "Возвращает список смен, закрытых в указанную дату")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты"),
            @ApiResponse(responseCode = "404", description = "Смены не найдены")
    })
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ShiftDTO>> getAllShiftsByCloseDate(
            @Parameter(description = "Дата закрытия смены в формате YYYY-MM-DD", required = true, example = "2023-01-01")
            @PathVariable @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Включить информацию о покупках", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean withPurchases
    ) {
        logger.info("Получен запрос всех смен по дате закрытия: {}, с покупками: {}", date, withPurchases);
        List<ShiftDTO> result = shiftService.getAllShiftsByCloseDate(date, withPurchases);
        logger.debug("Возвращаем {} смен за дату {}", result.size(), date);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить смены по диапазону дат", description = "Возвращает список смен, закрытых в указанном диапазоне дат")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат дат"),
            @ApiResponse(responseCode = "404", description = "Смены не найдены")
    })
    @GetMapping("/range")
    public ResponseEntity<List<ShiftDTO>> getAllShiftsByCloseDateRange(
            @Parameter(description = "Начальная дата диапазона в формате YYYY-MM-DD", required = true, example = "2023-01-01")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Конечная дата диапазона в формате YYYY-MM-DD", required = true, example = "2023-01-31")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Включить информацию о покупках", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean withPurchases
    ) {
        logger.info("Получен запрос всех смен по диапазону дат: {} - {}, с покупками: {}", startDate, endDate, withPurchases);
        List<ShiftDTO> result = shiftService.getAllShiftsByCloseDateRange(startDate, endDate, withPurchases);
        logger.debug("Возвращаем {} смен в диапазоне {} - {}", result.size(), startDate, endDate);
        return ResponseEntity.ok(result);
    }

    //по магазину
    @Operation(summary = "Получить смены по номеру магазина и дате", description = "Возвращает список смен для указанного магазина и даты закрытия")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Магазин или смены не найдены")
    })
    @GetMapping("/shop/{shopNumber}/date/{date}")
    public ResponseEntity<List<ShiftDTO>> getShiftsByShopNumberAndCloseDate(
            @Parameter(description = "Номер магазина", required = true, example = "1")
            @PathVariable @NotNull Long shopNumber,
            @Parameter(description = "Дата закрытия смены в формате YYYY-MM-DD", required = true, example = "2023-01-01")
            @PathVariable @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Включить информацию о покупках", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean withPurchases
    ) {
        logger.info("Получен запрос смен для магазина {} по дате закрытия: {}, с покупками: {}", shopNumber, date, withPurchases);
        List<ShiftDTO> result = shiftService.getShiftsByShopNumberAndCloseDate(shopNumber, date, withPurchases);
        logger.debug("Возвращаем {} смен для магазина {} за дату {}", result.size(), shopNumber, date);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить смены по номеру магазина и диапазону дат", description = "Возвращает список смен для указанного магазина в диапазоне дат")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Магазин или смены не найдены")
    })
    @GetMapping("/shop/{shopNumber}/range")
    public ResponseEntity<List<ShiftDTO>> getShiftsByShopNumberAndCloseDateRange(
            @Parameter(description = "Номер магазина", required = true, example = "1")
            @PathVariable @NotNull Long shopNumber,
            @Parameter(description = "Начальная дата диапазона в формате YYYY-MM-DD", required = true, example = "2023-01-01")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Конечная дата диапазона в формате YYYY-MM-DD", required = true, example = "2023-01-31")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Включить информацию о покупках", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean withPurchases
    ) {
        logger.info("Получен запрос смен для магазина {} по диапазону дат: {} - {}, с покупками: {}",
                shopNumber, startDate, endDate, withPurchases);
        List<ShiftDTO> result = shiftService.getShiftsByShopNumberAndCloseRange(
                shopNumber, startDate, endDate, withPurchases
        );
        logger.debug("Возвращаем {} смен для магазина {} в диапазоне {} - {}",
                result.size(), shopNumber, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    //по кассе
    @Operation(summary = "Получить смены по номеру магазина, кассы и дате", description = "Возвращает список смен для указанного магазина, кассы и даты закрытия")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Магазин, касса или смены не найдены")
    })
    @GetMapping("/shop/{shopNumber}/cash/{cashNumber}/date/{date}")
    public ResponseEntity<List<ShiftDTO>> getShiftsByShopNumberAndCashNumberAndCloseDate(
            @Parameter(description = "Номер магазина", required = true, example = "1")
            @PathVariable @NotNull Long shopNumber,
            @Parameter(description = "Номер кассы", required = true, example = "1")
            @PathVariable @NotNull Long cashNumber,
            @Parameter(description = "Дата закрытия смены в формате YYYY-MM-DD", required = true, example = "2023-01-01")
            @PathVariable @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Включить информацию о покупках", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean withPurchases
    ) {
        logger.info("Получен запрос смен для магазина {} и кассы {} по дате закрытия: {}, с покупками: {}",
                shopNumber, cashNumber, date, withPurchases);
        List<ShiftDTO> result = shiftService.getShiftsByShopNumberAndCashNumberAndCloseDate(
                shopNumber, cashNumber, date, withPurchases
        );
        logger.debug("Возвращаем {} смен для магазина {} и кассы {} за дату {}",
                result.size(), shopNumber, cashNumber, date);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить смены по номеру магазина, кассы и диапазону дат", description = "Возвращает список смен для указанного магазина, кассы в диапазоне дат")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Магазин, касса или смены не найдены")
    })
    @GetMapping("/shop/{shopNumber}/cash/{cashNumber}/range")
    public ResponseEntity<List<ShiftDTO>> getShiftsByShopNumberAndCashNumberAndCloseDateRange(
            @Parameter(description = "Номер магазина", required = true, example = "1")
            @PathVariable @NotNull Long shopNumber,
            @Parameter(description = "Номер кассы", required = true, example = "1")
            @PathVariable @NotNull Long cashNumber,
            @Parameter(description = "Начальная дата диапазона в формате YYYY-MM-DD", required = true, example = "2023-01-01")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Конечная дата диапазона в формате YYYY-MM-DD", required = true, example = "2023-01-31")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Включить информацию о покупках", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean withPurchases
    ) {
        logger.info("Получен запрос смен для магазина {} и кассы {} по диапазону дат: {} - {}, с покупками: {}",
                shopNumber, cashNumber, startDate, endDate, withPurchases);
        List<ShiftDTO> result = shiftService.getShiftsByShopNumberAndCashNumberAndCloseDateRange(
                shopNumber, cashNumber, startDate, endDate, withPurchases
        );
        logger.debug("Возвращаем {} смен для магазина {} и кассы {} в диапазоне {} - {}",
                result.size(), shopNumber, cashNumber, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Поиск смен по параметрам", description = "Возвращает список смен, соответствующих критериям поиска")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Смены не найдены")
    })
    @PostMapping("/search")
    public ResponseEntity<List<ShiftDTO>> searchShifts(
            @Parameter(description = "Параметры поиска смен", required = true)
            @RequestBody @Valid ShiftSearchRequest request) {
        logger.info("Получен запрос поиска смен по параметрам: {}", request);
        List<ShiftDTO> result = shiftService.searchShifts(request);
        logger.debug("Возвращаем {} смен по критериям поиска", result.size());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Пакетный поиск смен", description = "Возвращает список смен по нескольким критериям поиска")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Смены найдены",
                    content = @Content(schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Смены не найдены")
    })
    @PostMapping("/search/batch")
    public ResponseEntity<List<ShiftDTO>> searchShiftsBatch(
            @Parameter(description = "Список параметров поиска смен", required = true)
            @RequestBody @Valid @NotEmpty List<@Valid ShiftSearchRequest> requests) {
        logger.info("Получен запрос пакетного поиска смен по {} запросам", requests.size());
        List<ShiftDTO> result = shiftService.searchShiftsBatch(requests);
        logger.debug("Пакетный поиск завершен. Возвращаем {} уникальных смен", result.size());
        return ResponseEntity.ok(result);
    }
}