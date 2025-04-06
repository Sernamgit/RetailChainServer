package ru.otus.prof.retail.controllers.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.services.product.PriceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product/price")
@Validated
@Tag(name = "Управление ценами", description = "API для работы с ценами товаров")
public class PriceController {
    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

    private final PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @Operation(summary = "Получение информации о цене", description = "Возвращает информацию о цене по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цена найдена", content = @Content(schema = @Schema(implementation = PriceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора"),
            @ApiResponse(responseCode = "404", description = "Цена не найдена")
    })
    @GetMapping("{id}")
    public ResponseEntity<PriceDTO> getPrice(
            @Parameter(description = "Идентификатор цены", required = true, example = "1")
            @PathVariable @NotNull @Positive Long id) {
        logger.info("Запрос на получение цены с ID: {}", id);
        PriceDTO price = priceService.getPrice(id);
        logger.debug("Получена цена с ID: {} для товара с артикулом: {}", price.id(), price.article());
        return ResponseEntity.ok(price);
    }

    @Operation(summary = "Удаление цены", description = "Удаляет цену по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Цена успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора"),
            @ApiResponse(responseCode = "404", description = "Цена не найдена")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePrice(
            @Parameter(description = "Идентификатор цены", required = true, example = "1")
            @PathVariable @NotNull @Positive Long id) {
        logger.info("Запрос на удаление цены с ID: {}", id);
        priceService.deletePrice(id);
        logger.info("Цена с ID {} успешно удалена", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получение цен по артикулу товара", description = "Возвращает список цен для указанного товара")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цены найдены", content = @Content(schema = @Schema(implementation = PriceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат артикула"),
            @ApiResponse(responseCode = "404", description = "Товар не найден или не имеет цен")
    })
    @GetMapping
    public ResponseEntity<List<PriceDTO>> getPriceByArticle(
            @Parameter(description = "Артикул товара", required = true, example = "123456")
            @RequestParam @NotNull @Positive Long article) {
        logger.info("Запрос на получение цен для товара с артикулом: {}", article);
        List<PriceDTO> prices = priceService.getPricesByItemArticle(article);
        logger.debug("Найдено {} цен для товара с артикулом {}", prices.size(), article);
        return ResponseEntity.ok(prices);
    }

    @Operation(summary = "Создание цены", description = "Создает цену для товара")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цена успешно создана", content = @Content(schema = @Schema(implementation = PriceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @PostMapping("/single")
    public ResponseEntity<PriceDTO> createPrice(
            @Parameter(description = "Данные цены в формате JSON", required = true, example = "{\"price\": 10000, \"article\": 123456}")
            @RequestBody @Valid PriceDTO priceDTO) {
        logger.info("Запрос на создание цены для товара с артикулом: {}", priceDTO.article());
        PriceDTO createdPrice = priceService.createPrice(priceDTO);
        logger.info("Создана новая цена с ID: {} для товара с артикулом: {}",
                createdPrice.id(), createdPrice.article());
        return ResponseEntity.ok(createdPrice);
    }

    @Operation(summary = "Пакетное создание цен", description = "Создает несколько цен для товаров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Цены успешно созданы", content = @Content(schema = @Schema(implementation = PriceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @PostMapping("/batch")
    public ResponseEntity<List<PriceDTO>> createPricesBatch(
            @Parameter(description = "Список данных цен в формате JSON", required = true,
                    example = "[{\"price\": 10000, \"article\": 123456}, {\"price\": 15000, \"article\": 654321}]")
            @RequestBody @Valid @NotEmpty List<PriceDTO> priceDTOs) {
        logger.info("Запрос на пакетное создание {} цен", priceDTOs.size());
        List<PriceDTO> createdPrices = priceService.createPrices(priceDTOs);
        logger.info("Успешно создано {} новых цен", createdPrices.size());
        return ResponseEntity.ok(createdPrices);
    }

    @Operation(summary = "Удаление всех цен по артикулу товара", description = "Удаляет все цены, связанные с указанным товаром")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Цены успешно удалены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат артикула"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteByArticle(
            @Parameter(description = "Артикул товара", required = true, example = "123456")
            @RequestParam @NotNull @Positive Long article) {
        logger.info("Запрос на удаление всех цен для товара с артикулом: {}", article);
        priceService.deleteAllPricesByItemArticle(article);
        logger.info("Все цены для товара с артикулом {} успешно удалены", article);
        return ResponseEntity.noContent().build();
    }
}