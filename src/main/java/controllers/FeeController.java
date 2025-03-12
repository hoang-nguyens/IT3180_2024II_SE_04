package controllers;

import models.Fee;
import models.enums.FeeType;
import models.enums.FeeUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.FeeService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fee")
public class FeeController {
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
    public ResponseEntity<Fee> createFee(@RequestParam String name,
                                         @RequestParam String category,
                                         @RequestParam String description,
                                         @RequestParam BigDecimal amount,
                                         @RequestParam FeeUnit feeUnit,
                                         @RequestParam FeeType feeType) {
        return ResponseEntity.ok(feeService.createFee(name, category, description, amount, feeUnit, feeType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fee> updateFee(@PathVariable Long id,
                                         @RequestParam String name,
                                         @RequestParam String category,
                                         @RequestParam String description,
                                         @RequestParam BigDecimal amount,
                                         @RequestParam FeeUnit feeUnit,
                                         @RequestParam FeeType feeType){
        return ResponseEntity.ok(feeService.updateFee(id, name, category, description, amount, feeUnit, feeType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
        return ResponseEntity.ok("Đã xóa thành công khoản thu với id " + id);
    }
}
