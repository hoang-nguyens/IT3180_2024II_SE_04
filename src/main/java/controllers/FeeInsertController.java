package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Fee;
import models.FeeCategory;
import models.enums.BillPeriod;
import models.enums.FeeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import services.FeeCategoryService;
import services.FeeService;
import services.UserService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Controller
public class FeeInsertController {
    //    private final FeeService feeService;
    private final FeeCategoryService feeCategoryService;
    private Stage stage;
    private Fee fee;

    @Autowired
    public FeeInsertController(FeeCategoryService feeCategoryService) {
        this.feeCategoryService = feeCategoryService;
    }
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> subCategoryComboBox;
    @FXML
    private ComboBox<FeeUnit> unitComboBox;
    @FXML
    private ComboBox<BillPeriod> billPeriodComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label statusLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private ObservableList<String> categoryList = FXCollections.observableArrayList();
    private ObservableList<String> subCategoryList = FXCollections.observableArrayList();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
//        System.out.println("OK !!!!");

        loadCategories();

        // Setup combo boxes
        categoryComboBox.setItems(categoryList);
        unitComboBox.setItems(FXCollections.observableArrayList(FeeUnit.values()));
        billPeriodComboBox.setItems(FXCollections.observableArrayList(BillPeriod.values()));

        categoryComboBox.setOnAction(event -> loadSubCategories());
        categoryComboBox.setEditable(true);
        subCategoryComboBox.setEditable(true);

        categoryComboBox.getEditor().setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                addCategory();
            }
        });
        subCategoryComboBox.getEditor().setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                addSubCategory();
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setFee(Fee fee){
        this.fee = fee;
        if (fee != null) {
            populateForm(fee);
        }
    }
    public Fee getFee() {
        return fee;
    }

    private void loadCategories() {
        List<String> categories = feeCategoryService.getParentCategoryNames();
        categoryList.setAll(categories);
    }

    private void loadSubCategories() {
        String selectedCategory = categoryComboBox.getValue();
        System.out.println(selectedCategory);
        if (selectedCategory!=null && !selectedCategory.isEmpty()) {
            List<String> subCategories = feeCategoryService.getSubCategoriesNames(selectedCategory);
            System.out.println(subCategories);
            subCategoryList.setAll(subCategories);
            subCategoryComboBox.setItems(subCategoryList);
        }
    }


    private void populateForm(Fee fee) {
        categoryComboBox.setValue(fee.getCategory());
        subCategoryComboBox.setValue(fee.getSubCategory());
        amountField.setText(fee.getAmount().toString());
        unitComboBox.setValue(fee.getUnit());
        billPeriodComboBox.setValue(fee.getBillPeriod());
        descriptionArea.setText(fee.getDescription());
        startDatePicker.setValue(fee.getStartDate());
        endDatePicker.setValue(fee.getEndDate());
        categoryComboBox.setDisable(false);
        subCategoryComboBox.setDisable(false);
        startDatePicker.setDisable(true);
    }

    private void addCategory() {
        String newCategory = categoryComboBox.getEditor().getText().trim();
        if (newCategory.isEmpty()) {
            showAlert("Vui lòng chọn danh mục cha trước!");
            return;
        }
        if (!categoryList.contains(newCategory)) {
//            FeeCategory newFeeCategory = feeCategoryService.createFeeCategory(newCategory, null);;
            categoryList.add(newCategory);
            categoryComboBox.setItems(FXCollections.observableArrayList(categoryList));
            categoryComboBox.setValue(newCategory);
        }
    }

    private void addSubCategory() {
        String selectedCategory = categoryComboBox.getValue(); // Lấy danh mục cha
        if (selectedCategory.isEmpty()) {
            showAlert("Vui lòng chọn danh mục cha trước!");
            return;
        }
        String newSubCategory = subCategoryComboBox.getEditor().getText().trim();
        if (newSubCategory.isEmpty()) {
            showAlert("Vui lòng nhập danh mục con!");
            return;
        }
        FeeCategory selectedFeeCategory = feeCategoryService.findTopLevelCategoryByName(selectedCategory);
        if (!subCategoryList.contains(newSubCategory)) {
//            FeeCategory subCategory = feeCategoryService.createFeeCategory(newSubCategory, selectedFeeCategory);
            subCategoryList.add(newSubCategory);
            subCategoryComboBox.setItems(FXCollections.observableArrayList(subCategoryList));
            subCategoryComboBox.setValue(newSubCategory);
        }
    }

    @FXML
    private void handleSave() {
        try {
            String selectedCategory = categoryComboBox.getValue();
            String selectedSubCategory = subCategoryComboBox.getValue();
//            User currentUser = userService.getCurrentUser();
//            System.out.println(currentUser.toString());
//            Fee savedFee = feeService.createFee(
//                    selectedCategory, selectedSubCategory, new BigDecimal(amountField.getText()),
//                    unitComboBox.getValue(), billPeriodComboBox.getValue(), descriptionArea.getText(),
//                    startDatePicker.getValue(), endDatePicker.getValue());
//            Fee newFee = new Fee();
            boolean inserted = true;
            if (fee == null) {
                fee = new Fee();
                fee.setCategory(selectedCategory);
                fee.setSubCategory(selectedSubCategory);
                fee.setStartDate(startDatePicker.getValue());
            } else {
                inserted = false;
            }

            String amountText = amountField.getText();
            BigDecimal amount = null;
            if (!selectedCategory.equals("Đóng Góp")) {
                if (amountText == null || amountText.trim().isEmpty()) {
                    statusLabel.setText("Vui lòng nhập số tiền!");
                    return;
                }
                amount = new BigDecimal(amountText);
            } else if (amountText != null && !amountText.trim().isEmpty()) {
                amount = new BigDecimal(amountText);
            }
            fee.setAmount(amount);

            fee.setUnit(unitComboBox.getValue());
            fee.setBillPeriod(billPeriodComboBox.getValue());
            fee.setDescription(descriptionArea.getText());
            fee.setEndDate(endDatePicker.getValue());

            String requestBody = objectMapper.writeValueAsString(fee);
            System.out.println(requestBody);
            HttpRequest request = null;
            if (inserted) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/fee"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/fee/" + fee.getId()))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
            }

            System.out.println(request.toString());

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (response.statusCode() == 200) {
//                feeList.add(newFee);
//                feeTable.refresh();
                statusLabel.setText("Thao tác thành công!");
                stage.close();
            } else {
                System.out.println(response.body());
                statusLabel.setText("Lỗi request: " + response.body());
            }

//            feeList.add(newFee);
//            feeTable.refresh();
//            statusLabel.setText("Thêm khoản thu thành công!");
        } catch (NumberFormatException e) {
            statusLabel.setText("Lỗi nhập liệu: Số tiền không hợp lệ!");
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Lỗi nhập liệu: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            statusLabel.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}