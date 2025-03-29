package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import models.enums.BillPeriod;
import models.enums.FeeUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "fees")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fee extends BaseModel{

    @NotBlank(message = "Danh mục không được để trống")
    @Size(max = 50, message = "Danh mục không được quá 50 ký tự")
    @Column(nullable = false, length=50)
    private String category;

    @NotBlank(message = "Danh mục con không được để trống")
    @Size(max = 50, message = "Danh mục con không được quá 50 ký tự")
    @Column(nullable = false, length = 50)
    private String subCategory;

    //    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    @DecimalMax(value = "1000000", message = "Số tiền không được vượt quá 1 triệu")
    @Column(nullable = true)
    private BigDecimal amount;

    @NotNull(message = "Đơn vị không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FeeUnit unit;

    @NotNull(message = "Chu kỳ thanh toán không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BillPeriod billPeriod;

    @Size(max = 255, message = "Mô tả không được quá 255 ký tự")
    @Lob
    private String description;

    @NotNull(message = "Ngày bắt đầu không được để trống")
//    @FutureOrPresent(message = "Ngày bắt đầu phải từ hôm nay trở đi")
    @Column(nullable = false)
    private LocalDate startDate;

    @Future(message = "Ngày kết thúc phải sau ngày bắt đầu")
    @Column(nullable = true)
    private LocalDate endDate;

    public Fee(){};

//    public void setCategory(String category) {
//        if (category == null || category.isEmpty()) {
//            throw new IllegalArgumentException("Loa khoản thu không được để trống");
//        }
//        this.category = category;
//    }

//    public void setSubCategory(String subCategory) {
//        if (subCategory == null || subCategory.isEmpty()) {
//            throw new IllegalArgumentException("Loaị khoản thu không được để trống.");
//        }
//        this.subCategory = subCategory;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        if (!category.equals("Đóng Góp")) {
//            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
//                throw new IllegalArgumentException("Số tiền phải > 0.");
//            }
//        } else {
//            if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
//                throw new IllegalArgumentException("Số tiền phải > 0.");
//            }
//        }
//        this.amount = amount;
//    }
//
//    public void setUnit(FeeUnit unit) {
//        if (unit == null) {
//            throw new IllegalArgumentException("Đơn vị không được để trống.");
//        }
//        this.unit = unit;
//    }
//
//    public void setBillPeriod(BillPeriod billPeriod) {
//        if (billPeriod == null) {
//            throw new IllegalArgumentException("Kỳ thanh toán không được để trống.");
//        }
//        this.billPeriod = billPeriod;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public void setStartDate(LocalDate startDate) {
//        if (startDate == null || startDate.isBefore(LocalDate.now())) {
//            throw new IllegalArgumentException("Ngày bắt đầu phải từ hôm nay trở đi");
//        }
//        this.startDate = startDate;
//    }
//
//    public void setEndDate(LocalDate endDate) {
//        if (endDate != null && this.startDate != null && endDate.isBefore(this.startDate)) {
//            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
//        }
//        this.endDate = endDate;
//    }

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