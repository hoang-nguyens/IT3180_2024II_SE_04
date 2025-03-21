package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import models.enums.BillPeriod;
import models.enums.FeeUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "fees2")
public class Fee extends BaseModel{
    @Column(nullable = false, length=50)
    private String category;

    @Column(nullable = false, length = 50)
    private String subCategory;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FeeUnit unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BillPeriod billPeriod;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    public Fee(){};

    public Fee(String category, String subCategory, BigDecimal amount, FeeUnit unit, BillPeriod billPeriod, String description, LocalDate startDate, LocalDate endDate) {
        this.category = category;
        this.subCategory = subCategory;
        this.amount = amount;
        this.unit = unit;
        this.billPeriod = billPeriod;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }}