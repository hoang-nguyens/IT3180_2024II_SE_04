package controllers;

import jakarta.validation.Valid;
import models.Fee;
import models.enums.BillPeriod;
import models.enums.FeeType;
import models.enums.FeeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import services.FeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fee")
public class FeeController {
    @Autowired
    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    // Lấy danh sách tất cả khoản thu
    @GetMapping
    public ResponseEntity<List<Fee>> getAllFees() {
        return ResponseEntity.ok(feeService.getAllFees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fee> getFeeById(@PathVariable Long id) {
        return ResponseEntity.ok(feeService.getFeeById(id));
    }

    @PostMapping
    public ResponseEntity<?> createFee(@RequestBody Fee fee) {
        try {
            System.out.println(fee.toString());
            feeService.createFee(fee);
            return ResponseEntity.ok(feeService.getFeeById(fee.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
//        return ResponseEntity.ok(feeService.createFee(category, subCategory, amount, unit, billPeriod, description, startDate, endDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fee> updateFee(@PathVariable Long id,
                                         @RequestParam BigDecimal amount,
                                         @RequestParam FeeUnit unit,
                                         @RequestParam BillPeriod billPeriod,
                                         @RequestParam String description,
                                         @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(feeService.updateFee(id, amount, unit, billPeriod, description, endDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
        return ResponseEntity.ok("Đã xóa thành công khoản thu với id " + id);
    }
}