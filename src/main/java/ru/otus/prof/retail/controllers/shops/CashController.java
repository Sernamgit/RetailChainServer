package ru.otus.prof.retail.controllers.shops;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.error.ErrorResponse;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
import ru.otus.prof.retail.services.shops.CashService;

import java.util.List;

@Tag(name = "Кассы", description = "API для управления кассовыми аппаратами")
@Validated
@RestController
@RequestMapping("/api/v1/cash")
public class CashController {

    private static final Logger logger = LoggerFactory.getLogger(CashController.class);
    private final CashService cashService;

    @Autowired
    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @Operation(
            summary = "Создать новую кассу", description = "Создает новую кассу в указанном магазине. Номер кассы должен быть уникальным в рамках магазина."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Касса успешно создана",
                    content = @Content(schema = @Schema(implementation = CashDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные входные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт: касса с таким номером уже существует в магазине",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<CashDTO> createCash(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные кассы для создания",
            required = true,
            content = @Content(schema = @Schema(implementation = CashDTO.class))
    ) @RequestBody @Valid CashDTO cashDTO) {
        logger.info("Запрос на создание новой кассы");
        return ResponseEntity.ok(cashService.createCash(cashDTO));
    }

    @Operation(
            summary = "Получить кассу по ID", description = "Возвращает кассовый аппарат по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Касса найдена",
                    content = @Content(schema = @Schema(implementation = CashDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Касса не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CashDTO> getCashById(@Parameter(description = "ID кассы", required = true, example = "1")
                                               @PathVariable @NotNull Long id) {
        logger.info("Запрос на получение кассы по ID: {}", id);
        return ResponseEntity.ok(cashService.getCashById(id).orElseThrow());
    }

    @Operation(
            summary = "Получить кассу по номеру и магазину", description = "Возвращает кассовый аппарат по его номеру и номеру магазина"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Касса найдена",
                    content = @Content(schema = @Schema(implementation = CashDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Касса не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/number/{shopNumber}/{number}")
    public ResponseEntity<CashDTO> getCashByNumberAndShopNumber(
            @Parameter(description = "Номер магазина", required = true, example = "101") @PathVariable @NotNull Long shopNumber,
            @Parameter(description = "Номер кассы", required = true, example = "1") @PathVariable @NotNull Long number) {
        logger.info("Запрос на получение кассы по номеру {} в магазине {}", number, shopNumber);
        return ResponseEntity.ok(cashService.getCashByNumberAndShopNumber(number, shopNumber).orElseThrow());
    }

    @Operation(
            summary = "Получить все кассы магазина", description = "Возвращает список всех кассовых аппаратов в указанном магазине"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список касс получен",
                    content = @Content(schema = @Schema(implementation = CashDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин не найден или не имеет касс",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/shop/{shopNumber}")
    public ResponseEntity<List<CashDTO>> getCashByShopNumber(
            @Parameter(description = "Номер магазина", required = true, example = "101") @PathVariable @NotNull Long shopNumber) {
        logger.info("Запрос на получение всех касс магазина {}", shopNumber);
        return ResponseEntity.ok(cashService.getCashByShopNumber(shopNumber));
    }

    @Operation(
            summary = "Обновить статус кассы", description = "Изменяет статус кассового аппарата. Можно указать либо ID кассы, либо номер кассы и номер магазина."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные входные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Касса не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/status")
    public ResponseEntity<Void> updateCashStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления статуса кассы",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CashStatusRequestDTO.class))
            ) @RequestBody @Valid CashStatusRequestDTO request) {
        logger.info("Запрос на изменение статуса кассы");

        if (request.id() != null) {
            cashService.updateCashStatus(request.id(), request.status());
        } else if (request.number() != null && request.shopNumber() != null) {
            CashDTO cash = cashService.getCashByNumberAndShopNumber(request.number(), request.shopNumber()).orElseThrow();
            cashService.updateCashStatus(cash.id(), request.status());
        } else {
            throw new IllegalArgumentException("Необходимо указать либо ID кассы, либо номер и номер магазина");
        }

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить кассу по ID", description = "Помечает кассовый аппарат как удаленный (изменяет статус на DELETED)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Касса успешно помечена как удаленная"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Касса не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashById(
            @Parameter(description = "ID кассы", required = true, example = "1") @PathVariable @NotNull Long id) {
        logger.info("Запрос на удаление кассы по ID: {}", id);
        cashService.updateCashStatus(id, STATUS.DELETED);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удалить кассу по номеру и магазину", description = "Помечает кассовый аппарат как удаленный по номеру кассы и номеру магазина"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Касса успешно помечена как удаленная"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Касса не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/number/{shopNumber}/{number}")
    public ResponseEntity<Void> deleteCashByNumber(
            @Parameter(description = "Номер магазина", required = true, example = "101") @PathVariable @NotNull Long shopNumber,
            @Parameter(description = "Номер кассы", required = true, example = "1") @PathVariable @NotNull Long number) {
        logger.info("Запрос на удаление кассы по номеру {} в магазине {}", number, shopNumber);
        CashDTO cash = cashService.getCashByNumberAndShopNumber(number, shopNumber).orElseThrow();
        cashService.updateCashStatus(cash.id(), STATUS.DELETED);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удалить кассу",
            description = "Помечает кассу как удаленную. Можно указать либо ID кассы, либо номер кассы и номер магазина."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Касса успешно помечена как удаленная"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные входные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Касса не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteCash(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные кассы для удаления (должен быть указан ID или номер и номер магазина)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CashDeleteRequestDTO.class))
            ) @RequestBody @Valid CashDeleteRequestDTO request) {
        logger.info("Запрос на удаление кассы");

        if (request.id() != null) {
            cashService.updateCashStatus(request.id(), STATUS.DELETED);
            return ResponseEntity.noContent().build();
        }

        if (request.number() != null && request.shopNumber() != null) {
            CashDTO cash = cashService.getCashByNumberAndShopNumber(request.number(), request.shopNumber()).orElseThrow();
            cashService.updateCashStatus(cash.id(), STATUS.DELETED);
            return ResponseEntity.noContent().build();
        }

        throw new IllegalArgumentException("Необходимо указать либо ID кассы, либо номер и номер магазина");
    }
}