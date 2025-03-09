package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import models.enums.InvoiceStatus;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class InvoiceModel extends BaseModel{
    @ManyToOne
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;

    @ManyToOne
    @JoinColumn(name = "fee_id", nullable = false)
    private FeeModel fee;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    private LocalDate paidDate;
}
