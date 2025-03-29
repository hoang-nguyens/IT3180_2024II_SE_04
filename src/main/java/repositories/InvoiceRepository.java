package repositories;

import models.Fee;
import models.Invoice;
import models.User;
import models.enums.InvoiceStatus;
import models.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>{
    List<Invoice> findAll();
    List<Invoice> findByUser(User user);
    List<Invoice> findByUserId(Long id);

    boolean existsByUserAndCategoryAndIssueDate(User user, String category, LocalDate issueDate);
    List<Invoice> findByDueDateBeforeAndStatusNot(LocalDate dueDate, InvoiceStatus status);
}