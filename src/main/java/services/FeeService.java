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
import java.util.Set;

@Service
public class FeeService {
    @Autowired
    private FeeRepository feeRepository;

//    @Autowired
//    public FeeService(FeeRepository feeRepository) {
//        this.feeRepository = feeRepository;
//    }

    public Fee createFee(String feeCategory,
                         String feeSubCategory,
                         BigDecimal amount,
                         FeeUnit feeUnit,
                         BillPeriod billPeriod,
                         String description,
                         LocalDate start_date,
                         @Nullable LocalDate end_date) {
        Fee fee = new Fee(feeCategory,feeSubCategory, amount, feeUnit, billPeriod, description, start_date, end_date);
        validateFee(fee);
        return feeRepository.save(fee);
    }

    public Fee createFee(Fee fee){
        validateFee(fee);
        System.out.println("Fee created");
        return feeRepository.save(fee);
    }

    public Fee updateFee(Long feeId,
                         BigDecimal amount,
                         FeeUnit unit,
                         BillPeriod billPeriod,
                         String description,
                         LocalDate end_date){
        Fee fee = feeRepository.findById(feeId).orElse(null);
        if (fee==null) {
            throw new EntityNotFoundException("Fee not found with id " + feeId);
        }
        fee.setAmount(amount);
        fee.setUnit(unit);
        fee.setBillPeriod(billPeriod);
        fee.setDescription(description);
        fee.setEndDate(end_date);
        return feeRepository.save(fee);
    }

    public void deleteFee(Long feeId) {
        if (!feeRepository.existsById(feeId)) {
            throw new EntityNotFoundException("Fee not found with ID: " + feeId);
        }
        feeRepository.deleteById(feeId);
    }

    public void validateFee(Fee fee) {
        boolean exists = feeRepository.existsByCategoryAndSubCategoryAndTimeOverlap(
                fee.getCategory(),fee.getSubCategory(),fee.getStartDate(),fee.getEndDate()
        );
        if (exists) {
            throw new IllegalArgumentException("Khoảng thời gian của khoản phí mới bị giao với khoản phí đã tồn tại");
        }
    }

    public Fee getFeeById(Long feeId) {
        return feeRepository.findById(feeId)
                .orElseThrow(() -> new EntityNotFoundException("Fee not found with ID: " + feeId));
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public List<Fee> getAllActiveFees() {
        return feeRepository.findAllActiveFees();
    }

    public List<Fee> getAllFeesByCategoryAndSubCategory(String category, String subCategory) {
        return feeRepository.findByCategoryAndSubCategory(category, subCategory);
    }

    public List<Fee> getAllActiveFeesByCategoryAndSubCategory(String category, String subCategory) {
        return feeRepository.findByCategoryAndSubCategoryAndIsActive(category, subCategory);
    }

    public List<Fee> getAllActiveForcedFees(){
        return feeRepository.findByCategoryNot("Đóng góp");
    }
}