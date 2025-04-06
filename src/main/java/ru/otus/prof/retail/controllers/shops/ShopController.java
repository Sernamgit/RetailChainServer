package ru.otus.prof.retail.controllers.shops;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.otus.prof.retail.dto.shop.ShopBasicDTO;
import ru.otus.prof.retail.dto.shop.ShopDTO;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;
import ru.otus.prof.retail.exception.shop.ShopValidationException;
import ru.otus.prof.retail.services.shops.ShopService;

import java.util.Optional;

@Tag(name = "Магазины", description = "Операции с магазинами")
@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {

    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);
    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @Operation(
            summary = "Создать новый магазин",
            description = "Создает новый магазин с указанными параметрами. Номер магазина должен быть уникальным."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Магазин успешно создан",
                    content = @Content(schema = @Schema(implementation = ShopDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные входные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт: магазин с таким номером уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ShopDTO> createShop(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные магазина для создания",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ShopDTO.class)))@RequestBody @Valid ShopDTO shopDTO) {
        logger.info("Получен запрос на создание магазина: {}", shopDTO);
        ShopDTO createdShop = shopService.createShop(shopDTO);
        logger.info("Магазин успешно создан с ID: {}", createdShop.id());
        return ResponseEntity.ok(createdShop);
    }

    @Operation(
            summary = "Получить магазин по ID",
            description = "Возвращает магазин по его идентификатору. Можно получить как базовую информацию, так и полную с кассами."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Магазин найден",
                    content = @Content(schema = @Schema(oneOf = {ShopBasicDTO.class, ShopDTO.class}))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getShopById(
            @Parameter(description = "ID магазина", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Флаг получения информации о кассах", example = "false")
            @RequestParam(required = false) Boolean withCash) {

        logger.debug("Получен запрос на получение магазина по ID: {}, withCash: {}", id, withCash);
        if (Boolean.TRUE.equals(withCash)) {
            ShopDTO shop = shopService.getShopByIdWithCash(id)
                    .orElseThrow(() -> new ShopNotFoundException("Магазин с ID: " + id + " не найден"));
            return ResponseEntity.ok(shop);
        } else {
            ShopBasicDTO shop = shopService.getShopById(id)
                    .orElseThrow(() -> new ShopNotFoundException("Магазин с ID: " + id + " не найден"));
            return ResponseEntity.ok(shop);
        }
    }

    @Operation(
            summary = "Получить магазин по номеру",
            description = "Возвращает магазин по его номеру. Можно получить как базовую информацию, так и полную с кассами."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Магазин найден",
                    content = @Content(schema = @Schema(oneOf = {ShopBasicDTO.class, ShopDTO.class}))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/number/{number}")
    public ResponseEntity<?> getShopByNumber(
            @Parameter(description = "Номер магазина", example = "101")
            @PathVariable Long number,

            @Parameter(description = "Флаг получения информации о кассах", example = "false")
            @RequestParam(required = false) Boolean withCash) {

        logger.debug("Получен запрос на получение магазина по номеру: {}, withCash: {}", number, withCash);
        if (Boolean.TRUE.equals(withCash)) {
            ShopDTO shop = shopService.getShopByNumberWithCash(number)
                    .orElseThrow(() -> new ShopNotFoundException("Магазин с номером: " + number + " не найден"));
            return ResponseEntity.ok(shop);
        } else {
            ShopBasicDTO shop = shopService.getShopByNumber(number)
                    .orElseThrow(() -> new ShopNotFoundException("Магазин с номером: " + number + " не найден"));
            return ResponseEntity.ok(shop);
        }
    }

    @Operation(
            summary = "Обновить информацию о магазине",
            description = "Обновляет информацию о существующем магазине. Можно обновлять по ID или номеру магазина."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Магазин успешно обновлен",
                    content = @Content(schema = @Schema(implementation = ShopDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные входные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт: магазин с таким номером уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping
    public ResponseEntity<ShopDTO> updateShop(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления магазина",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ShopDTO.class))
            )
            @RequestBody @Valid ShopDTO shopDTO) {

        logger.info("Получен запрос на обновление магазина: {}", shopDTO);
        ShopDTO updatedShop = shopService.updateShop(shopDTO);
        return ResponseEntity.ok(updatedShop);
    }


    @Operation(
            summary = "Удалить магазин по ID",
            description = "Удаляет магазин по его идентификатору. Все связанные кассы также будут удалены."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Магазин успешно удален"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShopById(
            @Parameter(description = "ID магазина", example = "1")
            @PathVariable Long id) {

        logger.info("Получен запрос на удаление магазина с ID: {}", id);
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удалить магазин по номеру",
            description = "Удаляет магазин по его номеру. Все связанные кассы также будут удалены."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Магазин успешно удален"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/number/{number}")
    public ResponseEntity<Void> deleteShopByNumber(
            @Parameter(description = "Номер магазина", example = "101")
            @PathVariable Long number) {

        logger.info("Получен запрос на удаление магазина с номером: {}", number);
        Optional<ShopBasicDTO> shopOpt = shopService.getShopByNumber(number);
        if (shopOpt.isEmpty()) {
            logger.warn("Магазин с номером: {} не найден для удаления", number);
            throw new ShopNotFoundException("Магазин с номером " + number + " не найден");
        }

        shopService.deleteShop(shopOpt.get().id());
        logger.info("Магазин успешно удален с номером: {}", number);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удалить магазин по данным из тела запроса",
            description = "Удаляет магазин по ID или номеру из тела запроса. Все связанные кассы также будут удалены."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Магазин успешно удален"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Не указан ID или номер магазина",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteShop(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные магазина для удаления (должен быть указан ID или номер)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ShopDTO.class))
            )
            @RequestBody ShopDTO shopDTO) {

        logger.info("Получен запрос на удаление магазина: {}", shopDTO);
        if (shopDTO.id() != null) {
            shopService.deleteShop(shopDTO.id());
            logger.info("Магазин успешно удален с ID: {}", shopDTO.id());
            return ResponseEntity.noContent().build();
        }

        if (shopDTO.number() != null) {
            ShopBasicDTO shop = shopService.getShopByNumber(shopDTO.number())
                    .orElseThrow(() -> new ShopNotFoundException("Магазин с номером " + shopDTO.number() + " не найден"));

            shopService.deleteShop(shop.id());
            logger.info("Магазин успешно удален с номером: {}", shopDTO.number());
            return ResponseEntity.noContent().build();
        }

        logger.warn("Не удалось удалить магазин - не указан ID или номер");
        throw new ShopValidationException("Для удаления магазина необходимо указать ID или номер");
    }
}