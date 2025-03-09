package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import models.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class PaymentModel extends BaseModel{

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceModel invoice;

    @ManyToOne
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate = new Date();

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(unique = true)
    private String transactionId;
}
