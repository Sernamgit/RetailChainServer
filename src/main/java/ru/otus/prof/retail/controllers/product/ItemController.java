package ru.otus.prof.retail.controllers.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.dto.product.*;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.services.product.ItemService;

@RestController
@RequestMapping("/api/v1/product/item")
@Tag(name = "Товары", description = "API для работы с товарами")
@Validated
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "Создание нового товара", description = "Позволяет создать новый товар с указанными параметрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно создан", content = @Content(schema = @Schema(implementation = ItemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(
            @Valid @RequestBody @Parameter(description = "Данные для создания товара", required = true, schema = @Schema(implementation = CreateItemDTO.class))
            CreateItemDTO createItemDTO) {
        logger.info("Запрос на создание товара с артикулом: {}", createItemDTO.article());
        ItemDTO createdItem = itemService.createItem(createItemDTO);
        logger.info("Товар успешно создан. Артикул: {}, название: {}",
                createdItem.article(), createdItem.name());
        return ResponseEntity.ok(createdItem);
    }

    @Operation(summary = "Получение информации о товаре", description = "Возвращает информацию о товаре по его артикулу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар найден", content = @Content(schema = @Schema(implementation = ItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный артикул"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{article}")
    public ResponseEntity<ItemDTO> getItem(
            @PathVariable @NotNull(message = "Артикул не может быть пустым")
            @Min(value = 1, message = "Артикул должен быть положительным числом")
            @Parameter(description = "Артикул товара", required = true, example = "12345")
            Long article) {
        logger.info("Запрос на получение товара с артикулом: {}", article);
        ItemDTO item = itemService.getItem(article)
                .orElseThrow(() -> {
                    logger.warn("Товар с артикулом {} не найден", article);
                    return new ItemNotFoundException("Товар с артикулом " + article + " не найден");
                });
        logger.debug("Получен товар: {} с артикулом: {}", item.name(), item.article());
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "Обновление информации о товаре", description = "Позволяет обновить информацию о существующем товаре")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно обновлен", content = @Content(schema = @Schema(implementation = ItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping
    public ResponseEntity<ItemDTO> updateItem(
            @Valid @RequestBody @Parameter(description = "Данные для обновления товара", required = true, schema = @Schema(implementation = UpdateItemDTO.class))
            UpdateItemDTO updateItemDTO) {
        logger.info("Запрос на обновление товара с артикулом: {}", updateItemDTO.article());
        ItemDTO updatedItem = itemService.updateItem(updateItemDTO);
        logger.info("Товар успешно обновлен. Артикул: {}, новое название: {}",
                updatedItem.article(), updatedItem.name());
        return ResponseEntity.ok(updatedItem);
    }

    @Operation(summary = "Удаление товара", description = "Удаляет товар по указанному артикулу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Товар успешно удален"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный артикул"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{article}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable @NotNull(message = "Артикул не может быть пустым")
            @Min(value = 1, message = "Артикул должен быть положительным числом")
            @Parameter(description = "Артикул товара для удаления", required = true, example = "12345")
            Long article) {
        logger.info("Запрос на удаление товара с артикулом: {}", article);
        itemService.deleteItem(article);
        logger.info("Товар с артикулом {} успешно удален", article);
        return ResponseEntity.noContent().build();
    }
}