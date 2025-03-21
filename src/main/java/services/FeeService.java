package services;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import models.Fee;
import models.enums.BillPeriod;
import models.enums.FeeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FeeService {
    @Autowired
    private FeeRepository feeRepository;

    //    @Autowired
//    private FeeCategoryRepository feeCategoryRepository;
    @Autowired
    private FeeRepository fee2Repository;

    public Fee createFee(String feeCategory,
                         String feeSubCategory,
                         BigDecimal amount,
                         FeeUnit feeUnit,
                         BillPeriod billPeriod,
                         String description,
                         LocalDate start_date,
                         @Nullable LocalDate end_date) {

        Fee fee = new Fee(feeCategory,feeSubCategory, amount, feeUnit, billPeriod, description, start_date, end_date);
        return fee2Repository.save(fee);
    }

    public Fee updateFee(Long feeId,
                         BigDecimal amount,
                         FeeUnit unit,
                         BillPeriod billPeriod,
                         String description,
                         LocalDate end_date){
        Fee fee = fee2Repository.findById(feeId).orElse(null);
        if (fee==null) {
            throw new EntityNotFoundException("Fee not found with id " + feeId);
        }
        fee.setAmount(amount);
        fee.setUnit(unit);
        fee.setBillPeriod(billPeriod);
        fee.setDescription(description);
        fee.setEndDate(end_date);
        return fee2Repository.save(fee);
    }

    public void deleteFee(Long feeId) {
        if (!fee2Repository.existsById(feeId)) {
            throw new EntityNotFoundException("Fee not found with ID: " + feeId);
        }
        fee2Repository.deleteById(feeId);
    }

    public Fee getFeeById(Long feeId) {
        return fee2Repository.findById(feeId)
                .orElseThrow(() -> new EntityNotFoundException("Fee not found with ID: " + feeId));
    }

    public List<Fee> getAllFees() {
        return fee2Repository.findAll();
    }

}