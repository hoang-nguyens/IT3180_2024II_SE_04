package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import models.enums.InvoiceStatus;

@Getter
@Setter
@Entity
@Table(name = "invoices")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Invoice extends BaseModel{
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    //    @ManyToOne
//    @JoinColumn(name = "fee_id", referencedColumnName = "id", nullable = false)
//    private Fee fee;
    @Column(nullable = false)
    private String category;

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

    public Invoice(User user, LocalDate issueDate, LocalDate dueDate, String category, BigDecimal amount) {
        this.user = user;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.category = category;
        this.amount = amount;
    }

    public Invoice() {

    }
}