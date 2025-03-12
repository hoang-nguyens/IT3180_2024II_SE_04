package controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Fee;
import models.enums.FeeStatus;
import models.enums.FeeType;
import models.enums.FeeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import services.FeeService;
import java.math.BigDecimal;
import java.util.List;

@Component
public class FeeViewController {
    @Autowired
    private FeeService feeService;

//    public FeeViewController(FeeService feeService) {
//        this.feeService = feeService;
//    }

    // sửa lại các cột phải map từ fxml qua controller (đủ cột, not null)
    @FXML
    private TableView<Fee> feeTable;
    @FXML
    private TableColumn<Fee, Integer> idColumn;
    @FXML
    private TableColumn<Fee, String> nameColumn;
    @FXML
    private TableColumn<Fee, String> categoryColumn;
    @FXML
    private TableColumn<Fee, BigDecimal> amountColumn;
    @FXML
    private TableColumn<Fee, FeeStatus> statusColumn;
    @FXML
    private TableColumn<Fee, FeeType> feeTypeColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<FeeUnit> unitComboBox;
    @FXML
    private ComboBox<FeeType> feeTypeComboBox;
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

    private ObservableList<Fee> feeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup table columns
//        System.out.println("OK !!!!");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        amountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAmount()));
        feeTypeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFeeType()));

        // Load fee data
        loadFees();

        // Setup combo boxes
        unitComboBox.setItems(FXCollections.observableArrayList(FeeUnit.values()));
        feeTypeComboBox.setItems(FXCollections.observableArrayList(FeeType.values()));

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

    private void populateForm(Fee fee) {
        nameField.setText(fee.getName());
        categoryField.setText(fee.getCategory());
        amountField.setText(fee.getAmount().toString());
        unitComboBox.setValue(fee.getUnit());
        feeTypeComboBox.setValue(fee.getFeeType());
        descriptionArea.setText(fee.getDescription());
    }

    @FXML
    private void handleAdd() {
        try {
            Fee newFee = new Fee();
            newFee.setName(nameField.getText());
            newFee.setCategory(categoryField.getText());
            newFee.setAmount(new BigDecimal(amountField.getText()));
            newFee.setUnit(unitComboBox.getValue());
            newFee.setFeeType(feeTypeComboBox.getValue());
            newFee.setDescription(descriptionArea.getText());
            newFee.setStatus(FeeStatus.ACTIVE);

            Fee savedFee = feeService.createFee(
                    newFee.getName(), newFee.getCategory(), newFee.getDescription(),
                    newFee.getAmount(), newFee.getUnit(), newFee.getFeeType()
            );

            feeList.add(savedFee);
            feeTable.refresh();
            statusLabel.setText("Thêm khoản thu thành công!");
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
            selectedFee.setName(nameField.getText());
            selectedFee.setCategory(categoryField.getText());
            selectedFee.setAmount(new BigDecimal(amountField.getText()));
            selectedFee.setUnit(unitComboBox.getValue());
            selectedFee.setFeeType(feeTypeComboBox.getValue());
            selectedFee.setDescription(descriptionArea.getText());

            Fee updatedFee = feeService.updateFee(
                    selectedFee.getId(), selectedFee.getName(), selectedFee.getCategory(),
                    selectedFee.getDescription(), selectedFee.getAmount(),
                    selectedFee.getUnit(), selectedFee.getFeeType()
            );

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

}
