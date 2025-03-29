package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Invoice;
import models.User;
import models.enums.InvoiceStatus;
import models.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import utils.UserUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

@Controller
public class InvoiceViewController {
    private final InvoiceController invoiceController;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final Set<Role> adminRoles = Set.of(Role.ADMIN, Role.ADMIN_ROOT);

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TableColumn<Invoice, Integer> invoiceIdColumn;

    @FXML
    private TableColumn<Invoice, String> issueDateColumn;

    @FXML
    private TableColumn<Invoice, String> dueDateColumn;
    @FXML
    private TableColumn<Invoice, String> categoryColumn;
    @FXML
    private TableColumn<Invoice, BigDecimal> amountColumn;

    @FXML
    private TableColumn<Invoice, InvoiceStatus> statusColumn;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button payButton;
    @FXML
    private Label statusLabel;

    private ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    @Autowired
    public InvoiceViewController(InvoiceController invoiceController) {
        this.invoiceController = invoiceController;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadInvoices();
        updateTotalAmount();

        payButton.setOnAction(event -> handlePayment());
    }

    private void setupTableColumns() {
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        issueDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

//        invoiceTable.setItems(invoiceList);
    }

    private void loadInvoices() {
        User currentUser = UserUtils.getCurrentUser();
        if (currentUser == null) {
            statusLabel.setText("Vui lòng đăng nhập !!!");
            return;
        }
        String url = "http://localhost:8080/api/invoices";
        if (!adminRoles.contains(currentUser.getRole())) {
            url += "?userId=" + currentUser.getId();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Invoice[] invoices = objectMapper.readValue(response.body(), Invoice[].class);
                invoiceList.setAll(invoices);
                invoiceTable.setItems(invoiceList);
                statusLabel.setText("Tải lên dữ liệu thành công.");
            } else {
                statusLabel.setText("Tải lên dữ liệu thất bại." + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotalAmount() {
        BigDecimal total = invoiceList.stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.UNPAID)
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmountLabel.setText(total + " VNĐ");
    }

    private void handlePayment() {
        System.out.println("Tính năng thanh toán đang hoàn thiện.");
//        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
//                "Xác nhận thanh toán các hóa đơn?", ButtonType.YES, ButtonType.NO);
//        confirmation.showAndWait().ifPresent(response -> {
//            if (response == ButtonType.YES) {
//                for (Invoice invoice : invoiceList) {
//                    if (invoice.getStatus() == InvoiceStatus.UNPAID) {
//                        invoice.setStatus(InvoiceStatus.PAID);
//                        // Gọi API để cập nhật hóa đơn đã thanh toán
//                        invoiceController.updateInvoice(invoice.getId(), invoice);
//                    }
//                }
//                loadInvoices();
//                updateTotalAmount();
//            }
//        });
    }
}