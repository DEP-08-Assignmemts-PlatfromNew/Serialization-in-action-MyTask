package lk.ijse.dep8.control;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
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

    private final Path sourcePath = Paths.get("dataSource/customers.dep8");
    private Path defaultImage = Paths.get("assets/MaleIcon.png");


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

    private Button btnDelete;
    Path imagePath;


    public void initialize(){

        initDataSource();

        tblCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, ImageView> colProPic =
                (TableColumn<Customer, ImageView>) tblCustomer.getColumns().get(3);
        colProPic.setCellValueFactory(param -> {
            byte[] profilePic = param.getValue().getProfilePic();
            ByteArrayInputStream bis = new ByteArrayInputStream(profilePic);
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
          "Images","*jpeg","*.jpg","*.png","*.bmp"
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

        byte[] profilePicture = new byte[0];
        if (btnSave.getText().equals("Save")) {

            if(!txtProPic.getText().trim().isEmpty()){
                profilePicture = Files.readAllBytes(Paths.get(txtProPic.getText()));
            }else {
                profilePicture = Files.readAllBytes(defaultImage);
            }


            if (!txtId.getText().matches("C\\d{5}") || tblCustomer.getItems().stream().anyMatch(c -> c.getId().equalsIgnoreCase(txtId.getText()))) {
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
                    txtAddress.getText(),
                    profilePicture

            );

            tblCustomer.getItems().add(customerObj);
            boolean result = storeData();

            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Failed to Save").show();
                tblCustomer.getItems().remove(customerObj);

            } else {
                txtId.clear();
                txtName.clear();
                txtAddress.clear();

            }
        } else if (btnSave.getText().equals("Update")) {


            //update method run
            Customer selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
            selectedCustomer.setProfilePic(profilePicture);
            selectedCustomer.setId(txtId.getText());
            selectedCustomer.setName(txtName.getText());
            selectedCustomer.setAddress(txtAddress.getText());
            tblCustomer.refresh();
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


