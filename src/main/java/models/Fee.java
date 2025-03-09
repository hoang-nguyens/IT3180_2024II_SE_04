package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import models.enums.FeeStatus;
import models.enums.FeeType;
import models.enums.FeeUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "fees")
public class Fee extends BaseModel{

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FeeUnit unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType; // Loại phí (Cố định hay phát sinh)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeStatus status = FeeStatus.ACTIVE;
}
