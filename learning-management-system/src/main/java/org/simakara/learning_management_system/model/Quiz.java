package org.simakara.learning_management_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.simakara.learning_management_system.audit.CreatedAndUpdatedAt;
import org.simakara.learning_management_system.model.enums.QuizType;

import java.time.LocalDateTime;
import java.time.Year;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quizzes")
public class Quiz extends CreatedAndUpdatedAt {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "quiz-generator"
    )
    @SequenceGenerator(
            name = "quiz-generator",
            sequenceName = "quiz-sequence-generator",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private QuizType type;

    private LocalDateTime expiredAt;

    @PrePersist
    public void generateCode() {
        String yearCode = String.format("%02d", Year.now().getValue() % 100);
        this.code = String.format(
                "%s-%s-%d",
                this.type.name(),
                yearCode,
                this.id
        );
    }
}
