package services;

import models.Apartment;
import models.Fee;
import models.Invoice;
import models.User;
import models.enums.InvoiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.InvoiceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final UserService userService;
    private final FeeService feeService;
    private final ApartmentService apartmentService;

    LocalDate today = LocalDate.now();
    YearMonth yearMonth = YearMonth.from(today);
    LocalDate monthlyIssueDate = yearMonth.atDay(1);
    LocalDate monthyDueDate = yearMonth.atDay(10);

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, UserService userService, FeeService feeService, ApartmentService apartmentService) {
        this.invoiceRepository = invoiceRepository;
        this.userService = userService;
        this.feeService = feeService;
        this.apartmentService = apartmentService;
    }

    public void createInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    public void createMonthlyInvoices() {
        List<String> categories = List.of("Quản Lý", "Dịch Vụ");

//        List<Fee> monthlyFees = feeService.getAllActiveForcedFees();
//        for (Fee fee : monthlyFees) {
//            List<User> users = userService.getAllUsersWithUserRole();
//            for (User user : users) {
//                BigDecimal amount = calculateAmount(user, fee);
//                if (amount.compareTo(BigDecimal.ZERO) > 0) {
//                    System.out.println(amount.toPlainString());
//                    // goi invoice service them vao db
//
//                }
//            }
//        }
        List<User> users = userService.getAllUsersWithUserRole();
        for (User user : users) {
            for (String category : categories) {
                if (checkExistedInvoice(user, category)) {
                    continue;
                }
                BigDecimal totalFee = BigDecimal.ZERO;
                List<Fee> feeList = feeService.getAllActiveFeesByCategoryAndSubCategory(category, null);
                for (Fee fee : feeList) {
                    BigDecimal amount = calculateAmount(user, fee);

                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        System.out.println(amount.toPlainString());
                        totalFee = totalFee.add(amount);
                        // goi invoice service them vao db

                    }
                }
                if (totalFee.compareTo(BigDecimal.ZERO) > 0) {
                    Invoice invoice = new Invoice(user, monthlyIssueDate, monthyDueDate, category, totalFee);
                    createInvoice(invoice);
                }
            }
        }
    }

    private boolean checkExistedInvoice(User user, String category){
        return invoiceRepository.existsByUserAndCategoryAndIssueDate(user, category, monthlyIssueDate);
    }

    private BigDecimal calculateAmount(User user, Fee fee) {
//        if (checkExistedInvoice(user, fee)) {
//            return BigDecimal.ZERO;
//        }
        switch (fee.getUnit()) {
            case M2:
                List<Apartment> apartmentList = apartmentService.getAllApartmentsByOwner(user);
                BigDecimal totalAmount = BigDecimal.ZERO;
                if (!apartmentList.isEmpty()) {
                    for (Apartment apartment : apartmentList) {
                        float area = apartment.getArea();
                        BigDecimal amount = BigDecimal.valueOf(area).multiply(fee.getAmount());
                        totalAmount = totalAmount.add(amount).setScale(0, RoundingMode.HALF_UP);;
                    }
                    return totalAmount;
                }
            default:
                return BigDecimal.ZERO;
        }
    }

    public void updateOverdueInvoice() {
        List<Invoice> overdueInvoices = invoiceRepository.findByDueDateBeforeAndStatusNot(today, InvoiceStatus.OVERDUE);
        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);
        }
    }


    public List<Invoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoiceByUser(User user) {
        return invoiceRepository.findByUser(user);
    }

    public List<Invoice> getInvoiceByUserId(long userId) {
        return invoiceRepository.findByUserId(userId);
    }
}