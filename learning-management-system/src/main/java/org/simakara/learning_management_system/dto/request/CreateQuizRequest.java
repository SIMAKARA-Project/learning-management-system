package org.simakara.learning_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.simakara.learning_management_system.model.enums.QuizType;

import java.time.LocalDateTime;

public record CreateQuizRequest(
        @NotBlank(message = "Quiz name is required.")
        String name,

        String description,

        @NotBlank(message = "Quiz type is required.")
        QuizType type,

        LocalDateTime expiration
) {
}
