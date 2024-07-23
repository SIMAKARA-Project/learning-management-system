package org.simakara.learning_management_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.simakara.learning_management_system.audit.CreatedAndUpdatedAt;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course extends CreatedAndUpdatedAt {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "courses-generator"
    )
    @SequenceGenerator(
            name = "courses-generator",
            sequenceName = "courses-sequence-generator",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, unique = true)
    private String code;

    @PrePersist
    public void generateCode() {
        this.code = "SMKR-"+String.format("%03d", this.id);
    }
}


