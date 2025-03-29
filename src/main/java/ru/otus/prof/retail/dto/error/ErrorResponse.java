package ru.otus.prof.retail.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация об ошибке")
public class ErrorResponse {
    @Schema(description = "Время возникновения ошибки")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP статус код")
    private int status;

    @Schema(description = "Сообщение об ошибке")
    private String message;

    @Schema(description = "Путь, по которому возникла ошибка")
    private String path;
}