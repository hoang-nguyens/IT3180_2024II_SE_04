package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import models.Fee;
import models.FeeCategory;
import models.enums.BillPeriod;
import models.enums.FeeStatus;
import models.enums.FeeType;
import models.enums.FeeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import services.FeeCategoryService;
import services.FeeService;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class FeeViewController {
    private final FeeService feeService;
    private final FeeCategoryService feeCategoryService;
    @Autowired
    public FeeViewController(FeeService feeService, FeeCategoryService feeCategoryService) {
        this.feeService = feeService;
        this.feeCategoryService = feeCategoryService;
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());



    // sửa lại các cột phải map từ fxml qua controller (đủ cột, not null)
    @FXML
    private TableView<Fee> feeTable;
    @FXML
    private TableColumn<Fee, Long> idColumn;
    @FXML
    private TableColumn<Fee, String> categoryColumn;
    @FXML
    private TableColumn<Fee, String> subCategoryColumn;
    @FXML
    private TableColumn<Fee, BigDecimal> amountColumn;
    @FXML
    private TableColumn<Fee, FeeUnit> unitColumn;
    @FXML
    private TableColumn<Fee, BillPeriod> billPeriodColumn;
    @FXML
    private TableColumn<Fee, String> descriptionColumn;
    @FXML
    private TableColumn<Fee, LocalDate> startDateColumn;
    @FXML
    private TableColumn<Fee, LocalDate> endDateColumn;
    @FXML
    private TableColumn<Fee, Void> actionColumn;
    //    @FXML
//    private TextField categoryField;
//    @FXML
//    private TextField subCategoryField;
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
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button insertButton;
    @FXML
    private Button searchButton;

    private ObservableList<Fee> feeList = FXCollections.observableArrayList();
    private ObservableList<String> categoryList = FXCollections.observableArrayList();
    private ObservableList<String> subCategoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup table columns
//        System.out.println("OK !!!!");
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        subCategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubCategory()));
        amountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        unitColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUnit()));
        billPeriodColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBillPeriod()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        startDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndDate()));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("❌");
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Fee fee = getTableView().getItems().get(getIndex());
                    handleEdit();
                });

                deleteButton.setOnAction(event -> {
                    Fee fee = getTableView().getItems().get(getIndex());
                    handleDelete();
                });

                pane.setSpacing(5);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        // Load fee data
        loadFees();
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

        // Table selection listener
        feeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });


    }

    private void loadFees() {
        List<Fee> fees = feeService.getAllFees();
        feeList.setAll(fees);
        feeTable.setItems(feeList);
    }

    private void loadCategories() {
        List<String> categories = feeCategoryService.getParentCategoryNames();
        categoryList.setAll(categories);
    }

    private void loadSubCategories() {
        String selectedCategory = categoryComboBox.getValue();
        System.out.println(selectedCategory);
        if (!selectedCategory.isEmpty()) {
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
    }

    private void addCategory() {
        String newCategory = categoryComboBox.getEditor().getText().trim();
        if (newCategory.isEmpty()) {
            showAlert("Vui lòng chọn danh mục cha trước!");
            return;
        }
        if (!categoryList.contains(newCategory)) {
            FeeCategory newFeeCategory = feeCategoryService.createFeeCategory(newCategory, null);;
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
            FeeCategory subCategory = feeCategoryService.createFeeCategory(newSubCategory, selectedFeeCategory);
            subCategoryList.add(newSubCategory);
            subCategoryComboBox.setItems(FXCollections.observableArrayList(subCategoryList));
            subCategoryComboBox.setValue(newSubCategory);
        }
    }

    @FXML
    private void handleInsert(){
        // chuyến stage ở đây
        System.out.println("switching !!!");
    }

    @FXML
    private void handleSearch(){
        System.out.println("searching !!!");
    }

    @FXML
    private void handleAdd() {
        try {
            String selectedCategory = categoryComboBox.getValue();
            String selectedSubCategory = subCategoryComboBox.getValue();

//            Fee savedFee = feeService.createFee(
//                    selectedCategory, selectedSubCategory, new BigDecimal(amountField.getText()),
//                    unitComboBox.getValue(), billPeriodComboBox.getValue(), descriptionArea.getText(),
//                    startDatePicker.getValue(), endDatePicker.getValue());
            Fee newFee = new Fee();
            newFee.setCategory(selectedCategory);
            newFee.setSubCategory(selectedSubCategory);

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
            newFee.setAmount(amount);

            newFee.setUnit(unitComboBox.getValue());
            newFee.setBillPeriod(billPeriodComboBox.getValue());
            newFee.setDescription(descriptionArea.getText());
            newFee.setStartDate(startDatePicker.getValue());
            newFee.setEndDate(endDatePicker.getValue());

            String requestBody = objectMapper.writeValueAsString(newFee);
            System.out.println(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/fee"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            System.out.println(request.toString());

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (response.statusCode() == 200) {
                feeList.add(newFee);
                feeTable.refresh();
                statusLabel.setText("Thêm khoản thu thành công!");
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
            statusLabel.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        Fee selectedFee = feeTable.getSelectionModel().getSelectedItem();
        if (selectedFee == null) {
            statusLabel.setText("Chọn khoản thu để cập nhật!");
            return;
        }

        try {
            String selectedCategory = categoryComboBox.getValue();
            String selectedSubCategory = subCategoryComboBox.getValue();

//            Fee savedFee = feeService.createFee(
//                    selectedCategory, selectedSubCategory, new BigDecimal(amountField.getText()),
//                    unitComboBox.getValue(), billPeriodComboBox.getValue(), descriptionArea.getText(),
//                    startDatePicker.getValue(), endDatePicker.getValue());
            selectedFee.setCategory(selectedCategory);
            selectedFee.setSubCategory(selectedSubCategory);

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
            selectedFee.setAmount(amount);

            selectedFee.setUnit(unitComboBox.getValue());
            selectedFee.setBillPeriod(billPeriodComboBox.getValue());
            selectedFee.setDescription(descriptionArea.getText());
            selectedFee.setStartDate(startDatePicker.getValue());
            selectedFee.setEndDate(endDatePicker.getValue());
            Fee updatedFee = feeService.updateFee(selectedFee.getId(), new BigDecimal(amountField.getText()),
                    unitComboBox.getValue(), billPeriodComboBox.getValue(),
                    descriptionArea.getText(), endDatePicker.getValue());

            // Lấy danh sách hiện tại của TableView
            ObservableList<Fee> feeList = feeTable.getItems();

            // Tìm phần tử cần cập nhật
            for (int i = 0; i < feeList.size(); i++) {
                if (feeList.get(i).getId().equals(updatedFee.getId())) {
                    feeList.set(i, updatedFee); // Cập nhật phần tử
                    break;
                }
            }
            feeTable.refresh();
            statusLabel.setText("Cập nhật khoản thu thành công!");
        } catch (Exception e) {
            statusLabel.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Fee selectedFee = feeTable.getSelectionModel().getSelectedItem();
        if (selectedFee == null) {
            statusLabel.setText("Chọn khoản thu để xóa!");
            return;
        }

        try {
            feeService.deleteFee(selectedFee.getId());
            feeList.remove(selectedFee);
            feeTable.refresh();
            statusLabel.setText("Xóa khoản thu thành công!");
        } catch (Exception e) {
            statusLabel.setText("Lỗi: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}