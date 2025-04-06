package ru.otus.prof.retail.controllers.product;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.services.product.BarcodeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product/barcode")
@Validated
@Tag(name = "Управление штрих-кодами", description = "API для работы со штрих-кодами товаров")
public class BarcodeController {
    private static final Logger logger = LoggerFactory.getLogger(BarcodeController.class);

    private final BarcodeService barcodeService;

    @Autowired
    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @Operation(summary = "Получение информации о штрих-коде", description = "Возвращает информацию о штрих-коде по его значению")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Штрих-код найден", content = @Content(schema = @Schema(implementation = BarcodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат штрих-кода"),
            @ApiResponse(responseCode = "404", description = "Штрих-код не найден")
    })
    @GetMapping("/{barcode}")
    public ResponseEntity<BarcodeDTO> getBarcode(
            @PathVariable
            @NotBlank(message = "Штрих-код не может быть пустым")
            @Size(min = 8, max = 50, message = "Длина штрих-кода должна быть от 8 до 50 символов")
            @Parameter(description = "Значение штрих-кода", required = true, example = "123456789012")
            String barcode) {
        logger.info("Запрос на получение штрих-кода: {}", barcode);
        BarcodeDTO barcodeDTO = barcodeService.getBarcode(barcode);
        logger.debug("Получен штрих-код: {} для товара с артикулом: {}", barcodeDTO.barcode(), barcodeDTO.article());
        return ResponseEntity.ok(barcodeDTO);
    }

    @Operation(summary = "Удаление штрих-кода", description = "Удаляет штрих-код по его значению")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Штрих-код успешно удален"),
            @ApiResponse(responseCode = "400", description = "Неверный формат штрих-кода"),
            @ApiResponse(responseCode = "404", description = "Штрих-код не найден")
    })
    @DeleteMapping("/{barcode}")
    public ResponseEntity<Void> deleteBarcode(
            @PathVariable
            @NotBlank(message = "Штрих-код не может быть пустым")
            @Size(min = 8, max = 50, message = "Длина штрих-кода должна быть от 8 до 50 символов")
            @Parameter(description = "Значение штрих-кода для удаления", required = true, example = "123456789012")
            String barcode) {
        logger.info("Запрос на удаление штрих-кода: {}", barcode);
        barcodeService.deleteBarcode(barcode);
        logger.info("Штрих-код успешно удален: {}", barcode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получение штрих-кодов по артикулу товара", description = "Возвращает список штрих-кодов для указанного товара")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Штрих-коды найдены", content = @Content(schema = @Schema(implementation = BarcodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат артикула"),
            @ApiResponse(responseCode = "404", description = "Товар не найден или не имеет штрих-кодов")
    })
    @GetMapping
    public ResponseEntity<List<BarcodeDTO>> getBarcodesByArticle(
            @RequestParam
            @NotNull(message = "Артикул товара не может быть пустым")
            @Positive(message = "Артикул товара должен быть положительным числом")
            @Parameter(description = "Артикул товара", required = true, example = "12345")
            Long article) {
        logger.info("Запрос на получение штрих-кодов для товара с артикулом: {}", article);
        List<BarcodeDTO> barcodes = barcodeService.getBarcodesByItemArticle(article);
        logger.debug("Найдено {} штрих-кодов для товара с артикулом {}", barcodes.size(), article);
        return ResponseEntity.ok(barcodes);
    }

    @Operation(summary = "Создание штрих-кода", description = "Создает штрих-код для товара")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Штрих-код успешно создан", content = @Content(schema = @Schema(implementation = BarcodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "409", description = "Штрих-код уже существует")
    })
    @PostMapping("/single")
    public ResponseEntity<BarcodeDTO> createBarcode(
            @Parameter(description = "Данные штрих-кода в формате JSON", required = true, example = "{\"barcode\": \"123456789012\", \"article\": 123456}")
            @RequestBody @Valid BarcodeDTO barcodeDTO) {
        logger.info("Запрос на создание штрих-кода: {} для товара с артикулом: {}",
                barcodeDTO.barcode(), barcodeDTO.article());
        BarcodeDTO createdBarcode = barcodeService.createBarcode(barcodeDTO);
        logger.info("Создан новый штрих-код: {} для товара с артикулом: {}",
                createdBarcode.barcode(), createdBarcode.article());
        return ResponseEntity.ok(createdBarcode);
    }

    @Operation(summary = "Пакетное создание штрих-кодов", description = "Создает несколько штрих-кодов для товаров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Штрих-коды успешно созданы", content = @Content(schema = @Schema(implementation = BarcodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "409", description = "Некоторые штрих-коды уже существуют")
    })
    @PostMapping("/batch")
    public ResponseEntity<List<BarcodeDTO>> createBarcodesBatch(
            @Parameter(description = "Список данных штрих-кодов в формате JSON", required = true,
                    example = "[{\"barcode\": \"123456789012\", \"article\": 123456}, {\"barcode\": \"987654321098\", \"article\": 654321}]")
            @RequestBody @Valid @NotEmpty List<BarcodeDTO> barcodeDTOs) {
        logger.info("Запрос на пакетное создание {} штрих-кодов", barcodeDTOs.size());
        List<BarcodeDTO> createdBarcodes = barcodeService.createBarcodes(barcodeDTOs);
        logger.info("Успешно создано {} штрих-кодов", createdBarcodes.size());
        return ResponseEntity.ok(createdBarcodes);
    }

    @Operation(summary = "Удаление всех штрих-кодов по артикулу товара", description = "Удаляет все штрих-коды, связанные с указанным товаром")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Штрих-коды успешно удалены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат артикула"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteBarcodesByArticle(
            @RequestParam
            @NotNull(message = "Артикул товара не может быть пустым")
            @Positive(message = "Артикул товара должен быть положительным числом")
            @Parameter(description = "Артикул товара", required = true, example = "12345")
            Long article) {
        logger.info("Запрос на удаление всех штрих-кодов для товара с артикулом: {}", article);
        barcodeService.deleteAllBarcodesByItemArticle(article);
        logger.info("Все штрих-коды для товара с артикулом {} успешно удалены", article);
        return ResponseEntity.noContent().build();
    }
}