package lk.ijse.dep8.control;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep8.util.CustomerTM;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainInterfaceControl {
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public Button btnSave;
    public Button btnReset;
    public TableView<CustomerTM> tblCustomer;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colOpt;

    private CustomerTM customerObj;

    public void initialize() throws IOException {
        readCustomerObject();


        // TODO: 3/11/22 create load to object from table

       tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
       tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
       tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<CustomerTM, Button> colOpt =
                (TableColumn<CustomerTM, Button>) tblCustomer.getColumns().get(3);

        colOpt.setCellValueFactory(param -> {
            Button btnDel = new Button("Delete");

            btnDel.setOnAction((event -> tblCustomer.getItems().remove(param.getValue())));
            return new ReadOnlyObjectWrapper<>(btnDel);
        });

    }

    private void readCustomerObject() throws IOException {

        Path path = Paths.get("/home/thanura/Desktop/CustomerData/CustomerData.txt");

        if(!Files.exists(path)){
            System.err.println("No Such File Found");
            return;
        }

        try {
            InputStream is = Files.newInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(is);


            CustomerTM c  = (CustomerTM) ois.readObject();
            c.printData();


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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

        CustomerTM customerObj = new CustomerTM(
                txtId.getText(),
                txtName.getText(),
                txtAddress.getText()
        );
        tblCustomer.getItems().add(customerObj);

        writeData(customerObj);
    }

    private void writeData(CustomerTM customerObj) throws IOException {
        String homePath = System.getProperty("user.home");
        Path dirPath = Paths.get(homePath, "Desktop", "CustomerData");

        if(!Files.isDirectory(dirPath)){
            Files.createDirectory(dirPath);
        }
        Path filePath = Paths.get(dirPath.toString(), "CustomerData.txt");

        if(!Files.exists(filePath)){
            Files.createFile(filePath);
        }

        OutputStream fos = Files.newOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(customerObj);


    }

    public void btnReset_OnAction(ActionEvent actionEvent) {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        tblCustomer.getItems().clear();
        }

   }


