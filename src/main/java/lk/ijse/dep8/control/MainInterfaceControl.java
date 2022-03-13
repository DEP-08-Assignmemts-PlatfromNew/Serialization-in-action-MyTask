package lk.ijse.dep8.control;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import lk.ijse.dep8.util.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class MainInterfaceControl {

    private final Path sourcePath = Paths.get("dataSource/customerData.dep8");
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public Button btnSave;
    public Button btnReset;
    public TableView<Customer> tblCustomer;
    public TextField txtProPic;
    public Button btnBrowse;
    public ImageView imgProfile;
    public RadioButton rbtMale;
    public RadioButton rbtFemale;
    // Path imagePath;
    byte[] profilePicture;
    Customer customer;
    private Path defaultImage = Paths.get("assets/MaleIcon.png");
    private Button btnDelete;

    public void initialize() {

        initDataSource();
        rbtMale.requestFocus();
        getCustomerId();
        txtId.setText(getCustomerId());


        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, ImageView> colProPic =
                (TableColumn<Customer, ImageView>) tblCustomer.getColumns().get(3);
        colProPic.setCellValueFactory(param -> {
            profilePicture = param.getValue().getProfilePic();
            ByteArrayInputStream bis = new ByteArrayInputStream(profilePicture);
            ImageView imgVw = new ImageView(new Image(bis));
            imgVw.setFitWidth(80);
            imgVw.setFitHeight(80);
            return new ReadOnlyObjectWrapper<>(imgVw);
        });

        TableColumn<Customer, Button> colOpt =
                (TableColumn<Customer, Button>) tblCustomer.getColumns().get(4);

        colOpt.setCellValueFactory(param -> {
            btnDelete = new Button("Delete");

            btnDelete.setOnAction((event -> tblCustomer.getItems().remove(param.getValue())));
            return new ReadOnlyObjectWrapper<>(btnDelete);
        });

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedValue) -> {
            btnDelete.setDisable(selectedValue == null);
            btnSave.setText(selectedValue == null ? "Save" : "Update");

            txtId.setText(selectedValue.getId());
            txtName.setText(selectedValue.getName());
            txtAddress.setText(selectedValue.getAddress());
            txtProPic.setText("Edit your profile picture");

        });
    }

    private String getCustomerId() {
        if (tblCustomer.getItems().isEmpty()) {
            return "C001";
        } else {
            ObservableList<Customer> customers = tblCustomer.getItems();
            int lastCustomerId = Integer.parseInt(customers.get(customers.size() - 1).getId().replace("C", ""));
            return String.format("C%03d", (lastCustomerId + 1));
        }

    }

    private void initDataSource() {
        try {
            if (!Files.exists(sourcePath)) {
                Files.createDirectory(sourcePath.getParent()); //create parent folder
                Files.createFile(sourcePath); //create files in parent folder
            }

            readSavedData();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to create source file").showAndWait();
            Platform.exit();
        }
    }

    public void rbtMale_OnAction(ActionEvent event) {
        imgProfile.setImage(new Image("assets/MaleIcon.png"));
        defaultImage = Paths.get(String.valueOf(defaultImage));
    }

    public void rbtFemale_OnAction(ActionEvent event) {
        imgProfile.setImage(new Image("assets/femaleIcon.png"));
        defaultImage = Paths.get("assets/femaleIcon.png");

    }

    public void btnBrowse_OnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Images", "*jpeg", "*.jpg", "*.png", "*.bmp"
        ));
        fileChooser.setTitle("Select your profile Image");
        File file = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        txtProPic.setText(file != null ? file.getAbsolutePath() : "No Image Data");
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

        if (btnSave.getText().equals("Save")) {

            if (!txtProPic.getText().trim().isEmpty()) {
                Path selectedImgPath = Paths.get(txtProPic.getText());
                profilePicture = Files.readAllBytes(selectedImgPath);

            } else {
                profilePicture = Files.readAllBytes(defaultImage);
            }

             if (txtName.getText().trim().isEmpty()) {
                txtName.requestFocus();
                txtName.selectAll();
                return;

            } else if (txtAddress.getText().trim().isEmpty()) {
                txtAddress.requestFocus();
                txtAddress.selectAll();
                return;
            }

            customer = new Customer(
                    txtId.getText(),
                    txtName.getText(),
                    txtAddress.getText(),
                    profilePicture

            );

            tblCustomer.getItems().add(customer);
            boolean result = storeData();

            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Failed to Save").show();
                tblCustomer.getItems().remove(customer);

            } else {
                txtId.clear();
                txtName.clear();
                txtAddress.clear();
                txtProPic.clear();

            }
        } else if (btnSave.getText().equals("Update")) {
            profilePicture = Files.readAllBytes(Paths.get(txtProPic.getText()));

            //update method run

            customer = tblCustomer.getSelectionModel().getSelectedItem();
            customer.setProfilePic(profilePicture);
            customer.setId(txtId.getText());
            customer.setName(txtName.getText());
            customer.setAddress(txtAddress.getText());
            tblCustomer.refresh();
            storeData();
        }
    }

    private boolean storeData() {
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(Files.newOutputStream(sourcePath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));
            oos.writeObject(new ArrayList<>(tblCustomer.getItems()));
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void btnReset_OnAction(ActionEvent actionEvent) {
        txtProPic.clear();
        txtId.clear();
        txtName.clear();
        txtAddress.clear();

    }


}


