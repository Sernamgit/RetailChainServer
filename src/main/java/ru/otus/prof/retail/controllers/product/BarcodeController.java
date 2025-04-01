package ru.otus.prof.retail.controllers.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.exception.BarcodeAlreadyExistsException;
import ru.otus.prof.retail.exception.BarcodeValidationException;
import ru.otus.prof.retail.services.product.BarcodeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product/barcode")
@Tag(name = "Управление штрих-кодами", description = "API для работы со штрих-кодами товаров")
@Validated
public class BarcodeController {
    private static final Logger logger = LoggerFactory.getLogger(BarcodeController.class);

    private final BarcodeService barcodeService;
    private final ObjectMapper objectMapper;

    public BarcodeController(BarcodeService barcodeService, ObjectMapper objectMapper) {
        this.barcodeService = barcodeService;
        this.objectMapper = objectMapper;
    }

    @Operation(
            summary = "Получение информации о штрих-коде", description = "Возвращает информацию о штрих-коде по его значению"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Штрих-код найден",
                    content = @Content(schema = @Schema(implementation = BarcodeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Штрих-код не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат штрих-кода"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{barcode}")
    public ResponseEntity<BarcodeDTO> getBarcode(
            @PathVariable
            @NotBlank(message = "Штрих-код не может быть пустым")
            @Size(min = 8, max = 50, message = "Длина штрих-кода должна быть от 8 до 50 символов")
            @Parameter(description = "Значение штрих-кода", required = true, example = "123456789012")
            String barcode) {

        logger.debug("Получение запроса на информацию о штрих-коде: {}", barcode);
        BarcodeDTO barcodeDTO = barcodeService.getBarcode(barcode);
        if (barcodeDTO != null) {
            logger.debug("Успешно найдена информация о штрих-коде: {}", barcode);
            return ResponseEntity.ok(barcodeDTO);
        } else {
            logger.warn("Штрих-код не найден в системе: {}", barcode);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Удаление штрих-кода", description = "Удаляет штрих-код по его значению"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Штрих-код успешно удален"),
            @ApiResponse(responseCode = "404", description = "Штрих-код не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат штрих-кода"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{barcode}")
    public ResponseEntity<Void> deleteBarcode(
            @PathVariable
            @NotBlank(message = "Штрих-код не может быть пустым")
            @Size(min = 8, max = 50, message = "Длина штрих-кода должна быть от 8 до 50 символов")
            @Parameter(description = "Значение штрих-кода для удаления", required = true, example = "123456789012")
            String barcode) {

        logger.info("Получен запрос на удаление штрих-кода: {}", barcode);
        barcodeService.deleteBarcode(barcode);
        logger.info("Штрих-код успешно удален: {}", barcode);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получение штрих-кодов по артикулу товара", description = "Возвращает список штрих-кодов для указанного артикула товара"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список штрих-кодов успешно получен",
                    content = @Content(schema = @Schema(implementation = BarcodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Не указан или неверный артикул товара"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<BarcodeDTO>> getBarcodesByArticle(
            @RequestParam(required = false)
            @NotNull(message = "Артикул товара не может быть пустым")
            @Positive(message = "Артикул товара должен быть положительным числом")
            @Parameter(description = "Артикул товара", required = true, example = "12345")
            Long article) {

        logger.debug("Получен запрос на получение штрих-кодов для товара с артикулом: {}", article);
        List<BarcodeDTO> barcodes = barcodeService.getBarcodesByItemArticle(article);
        logger.debug("Найдено {} штрих-кодов для товара с артикулом {}", barcodes.size(), article);
        return ResponseEntity.ok(barcodes);
    }

    @Operation(
            summary = "Создание штрих-кода(ов)", description = "Позволяет создать один или несколько штрих-кодов. Поддерживает как одиночные объекты, так и списки."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Штрих-код(ы) успешно создан(ы)",
                    content = @Content(schema = @Schema(implementation = BarcodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса"),
            @ApiResponse(responseCode = "409", description = "Штрих-код уже существует"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<?> createBarcodes(
            @RequestBody
            @NotBlank(message = "Тело запроса не может быть пустым")
            @Parameter(description = "Данные штрих-кода (объект или массив)", required = true,
                    content = @Content(schema = @Schema(oneOf = {BarcodeDTO.class, List.class})))
            String requestBody) {

        logger.info("Получен запрос на создание штрих-кода(ов)");

        try {
            try {
                BarcodeDTO singleBarcode = objectMapper.readValue(requestBody, BarcodeDTO.class);
                logger.debug("Создание одиночного штрих-кода: {}", singleBarcode.barcode());
                BarcodeDTO createdBarcode = barcodeService.createBarcode(singleBarcode);
                return ResponseEntity.ok(List.of(createdBarcode));
            } catch (JsonProcessingException e) {
                try {
                    List<BarcodeDTO> barcodeDTOs = objectMapper.readValue(
                            requestBody,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, BarcodeDTO.class)
                    );
                    if (barcodeDTOs.isEmpty()) {
                        throw new BarcodeValidationException("Список штрих-кодов не может быть пустым");
                    }
                    logger.debug("Создание списка из {} штрих-кодов", barcodeDTOs.size());
                    List<BarcodeDTO> createdBarcodes = barcodeService.createBarcodes(barcodeDTOs);
                    return ResponseEntity.ok(createdBarcodes);
                } catch (JsonProcessingException ex) {
                    logger.error("Не удалось обработать тело запроса: {}. Ошибка: {}", requestBody, ex.getMessage());
                    throw new BarcodeValidationException("Неверный формат запроса. Ожидается BarcodeDTO или List<BarcodeDTO>");
                }
            }
        } catch (BarcodeValidationException | BarcodeAlreadyExistsException e) {
            throw e; // Пробрасываем дальше для обработки в GlobalExceptionHandler
        } catch (RuntimeException e) {
            logger.error("Ошибка при создании штрих-кода(ов): {}", e.getMessage());
            throw new BarcodeValidationException("Ошибка при обработке запроса: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Удаление всех штрих-кодов по артикулу товара", description = "Удаляет все штрих-коды, связанные с указанным артикулом товара"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Штрих-коды успешно удалены"),
            @ApiResponse(responseCode = "400", description = "Неверный артикул товара"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteBarcodesByArticle(
            @RequestParam
            @NotNull(message = "Артикул товара не может быть пустым")
            @Positive(message = "Артикул товара должен быть положительным числом")
            @Parameter(description = "Артикул товара", required = true, example = "12345")
            Long article) {

        logger.info("Получен запрос на удаление всех штрих-кодов для товара с артикулом: {}", article);
        barcodeService.deleteAllBarcodesByItemArticle(article);
        logger.info("Все штрих-коды для товара с артикулом {} успешно удалены", article);
        return ResponseEntity.noContent().build();
    }
}