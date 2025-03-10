package services;

import jakarta.persistence.EntityNotFoundException;
import models.Fee;
import models.enums.FeeType;
import models.enums.FeeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.FeeRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FeeService {
    @Autowired
    private FeeRepository feeRepository;

    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    public Fee createFee(String name, String category, String description, BigDecimal amount, FeeUnit unit, FeeType feeType) {
        Fee fee = new Fee();
        fee.setName(name);
        fee.setCategory(category);
        fee.setDescription(description);
        fee.setAmount(amount);
        fee.setUnit(unit);
        fee.setFeeType(feeType);
        return feeRepository.save(fee);
    }

    public Fee updateFee(Long feeId, String name, String category, String description, BigDecimal amount, FeeUnit unit, FeeType feeType) {
        Fee fee = feeRepository.findById(feeId).orElse(null);
        if (fee == null) {
            throw new EntityNotFoundException("Fee not found with id " + feeId);
        }
        fee.setName(name);
        fee.setCategory(category);
        fee.setDescription(description);
        fee.setAmount(amount);
        fee.setUnit(unit);
        fee.setFeeType(feeType);
        return feeRepository.save(fee);
    }

    public void deleteFee(Long feeId) {
        if (!feeRepository.existsById(feeId)) {
            throw new EntityNotFoundException("Fee not found with ID: " + feeId);
        }
        feeRepository.deleteById(feeId);
    }

    public Fee getFeeById(Long feeId) {
        return feeRepository.findById(feeId)
                .orElseThrow(() -> new EntityNotFoundException("Fee not found with ID: " + feeId));
    }

    public List<Fee> getFeeByType(FeeType feeType) {
        return feeRepository.findByFeeType(feeType);
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }
}
