package lk.ijse.dep8.control;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep8.util.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class MainInterfaceControl {
    private final Path sourcePath = Paths.get("dataSource/customers.dep8");
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public Button btnSave;
    public Button btnReset;
    public TableView<Customer> tblCustomer;

    public void initialize() throws IOException {

        initDataSource();

        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, Button> colOpt =
                (TableColumn<Customer, Button>) tblCustomer.getColumns().get(3);

        colOpt.setCellValueFactory(param -> {
            Button btnDel = new Button("Delete");

            btnDel.setOnAction((event -> tblCustomer.getItems().remove(param.getValue())));
            return new ReadOnlyObjectWrapper<>(btnDel);
        });

    }

    private void initDataSource() {
        try {
            if (!Files.exists(sourcePath)) {
                Files.createDirectory(sourcePath.getParent()); //create parent folder
                Files.createFile(sourcePath); ////create files in parent folder
            }

            readSavedData();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to create source file").showAndWait();
            Platform.exit();
        }
    }

    private void readSavedData() {
        try {
            InputStream is = Files.newInputStream(sourcePath, StandardOpenOption.READ);
            ObjectInputStream ois = new ObjectInputStream(is);
            tblCustomer.getItems().clear();
            tblCustomer.setItems(FXCollections.observableArrayList((ArrayList<Customer>) ois.readObject()));


        } catch (IOException | ClassNotFoundException e) {
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to Load Data").show();

            }
        }
    }


    public void btnSave_OnAction(ActionEvent actionEvent) throws IOException {
        if (!txtId.getText().matches("C\\d{3}") || tblCustomer.getItems().stream().anyMatch(c -> c.getId().equalsIgnoreCase(txtId.getText()))) {
            txtId.requestFocus();
            txtId.selectAll();
            return;

        } else if (txtName.getText().trim().isEmpty()) {
            txtName.requestFocus();
            txtName.selectAll();
            return;

        } else if (txtAddress.getText().trim().isEmpty()) {
            txtAddress.requestFocus();
            txtAddress.selectAll();
            return;
        }


        Customer customerObj = new Customer(
                txtId.getText(),
                txtName.getText(),
                txtAddress.getText()
        );
        tblCustomer.getItems().add(customerObj);
        boolean result = storeData();

        if (!result) {
            new Alert(Alert.AlertType.ERROR, "Failed to Save").show();
            tblCustomer.getItems().remove(customerObj);

        } else {
            new Alert(Alert.AlertType.INFORMATION, "Saved Data Successfully").show();
            txtId.clear();
            txtName.clear();
            txtAddress.clear();

        }

    }

    private boolean storeData() {
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(Files.newOutputStream(sourcePath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));
            oos.writeObject(new ArrayList<Customer>(tblCustomer.getItems()));
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

    public void btnReset_OnAction(ActionEvent actionEvent) {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        tblCustomer.getItems().clear();
    }

}


