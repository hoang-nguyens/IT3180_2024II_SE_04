package controllers;

import app.MainApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Fee;
import models.FeeCategory;
import models.User;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class FeeViewController {
    private final FeeService feeService;
    private final FeeCategoryService feeCategoryService;
    private FeeInsertController feeInsertController;

    private final UserService userService;
    @Autowired
    public FeeViewController(FeeService feeService, FeeCategoryService feeCategoryService, UserService userService, FeeInsertController feeInsertController) {
        this.feeService = feeService;
        this.feeCategoryService = feeCategoryService;
        this.userService = userService;
        this.feeInsertController = feeInsertController;
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
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> subCategoryComboBox;

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
    @FXML
    private Button resetButton;

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
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Fee fee = getTableView().getItems().get(getIndex());
                    handleEdit(fee);
                });

                deleteButton.setOnAction(event -> {
                    Fee fee = getTableView().getItems().get(getIndex());
                    handleDelete(fee);
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
        categoryComboBox.setOnAction(event -> loadSubCategories());

//        categoryComboBox.getEditor().setOnKeyPressed(event -> {
//            if (event.getCode().toString().equals("ENTER")) {
//                addCategory();
//            }
//        });
//        subCategoryComboBox.getEditor().setOnKeyPressed(event -> {
//            if (event.getCode().toString().equals("ENTER")) {
//                addSubCategory();
//            }
//        });

        // Table selection listener
//        feeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//            if (newSelection != null) {
//                populateForm(newSelection);
//            }
//        });
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

//
//    private void populateForm(Fee fee) {
//        categoryComboBox.setValue(fee.getCategory());
//        subCategoryComboBox.setValue(fee.getSubCategory());
//        amountField.setText(fee.getAmount().toString());
//        unitComboBox.setValue(fee.getUnit());
//        billPeriodComboBox.setValue(fee.getBillPeriod());
//        descriptionArea.setText(fee.getDescription());
//        startDatePicker.setValue(fee.getStartDate());
//        endDatePicker.setValue(fee.getEndDate());
//    }

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

    private void openFeeStage(Fee fee){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/fee_insert.fxml"));
            fxmlLoader.setControllerFactory(MainApplication.springContext::getBean);
            Scene feeScene = new Scene(fxmlLoader.load(), 875, 415);
            Stage feeInsertStage = new Stage();
            feeInsertStage.initModality(Modality.APPLICATION_MODAL);
            feeInsertStage.setTitle(fee==null ? "Thêm mới khoản thu" : "Cập nhật khoản thu");
            feeInsertStage.setScene(feeScene);
            feeInsertController.setStage(feeInsertStage);
            feeInsertController.setFee(fee);
            feeInsertStage.showAndWait();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        try {

//            User currentUser = userService.getCurrentUser();
//            System.out.println(currentUser.toString());
//            Fee savedFee = feeService.createFee(
//                    selectedCategory, selectedSubCategory, new BigDecimal(amountField.getText()),
//                    unitComboBox.getValue(), billPeriodComboBox.getValue(), descriptionArea.getText(),
//                    startDatePicker.getValue(), endDatePicker.getValue());

            openFeeStage(null);
            Fee newFee = feeInsertController.getFee();
            if (newFee != null) {
                feeList.add(newFee);
                feeTable.refresh();
                statusLabel.setText("Thêm khoản thu mới thành công");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit(Fee selectedFee) {
        statusLabel.setText("Đang cập nhật");

//        Fee selectedFee = feeTable.getSelectionModel().getSelectedItem();
        if (selectedFee == null) {
            statusLabel.setText("Chọn khoản thu để cập nhật!");
            return;
        }
        openFeeStage(selectedFee);
        Fee updatedFee = feeInsertController.getFee();
        if (!updatedFee.equals(selectedFee)) {
            for (int i = 0; i < feeList.size(); i++) {
                if (feeList.get(i).getId().equals(updatedFee.getId())) {
                    feeList.set(i, updatedFee); // Cập nhật phần tử
                    break;
                }
            }
            feeTable.refresh();
            statusLabel.setText("Cập nhật khoản thu thành công!");
        }
    }

    @FXML
    private void handleDelete(Fee selectedFee) {
        statusLabel.setText("đang xóa");
        if (selectedFee == null) {
            statusLabel.setText("Chọn khoản thu để xóa!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc muốn xóa khoản thu này?");
        alert.setContentText("Loại khoản thu: " + selectedFee.getCategory());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            feeList.remove(selectedFee);
        }
//
//        try {
//            feeService.deleteFee(selectedFee.getId());
//            feeList.remove(selectedFee);
//            feeTable.refresh();
//            statusLabel.setText("Xóa khoản thu thành công!");
//        } catch (Exception e) {
//            statusLabel.setText("Lỗi: " + e.getMessage());
//        }
    }


    @FXML
    private void handleSearch(){
//        System.out.println("searching !!!");
        String filterCategory = categoryComboBox.getValue();
        String filterSubCategory = subCategoryComboBox.getValue();
        System.out.println(filterCategory + " " + filterSubCategory);
        if (filterCategory.isEmpty()){
            filterCategory = null;
        }
        if (filterSubCategory.isEmpty()){
            filterSubCategory = null;
        }
        System.out.println(filterCategory + " " + filterSubCategory);
        List<Fee> filteredFeeList = feeService.getAllFeesByCategoryAndSubCategory(filterCategory, filterSubCategory);
        feeList.setAll(filteredFeeList);
        System.out.println(filteredFeeList);
        System.out.println(feeList);
        feeTable.setItems(feeList);
    }

    @FXML
    private void handleReset(){
        loadFees();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}