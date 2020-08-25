package com.ozakhlivny.cloudproject.client.forms;

import com.ozakhlivny.cloudproject.client.controller.ClientController;
import com.ozakhlivny.cloudproject.common.command.FileInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainWindowControllers implements Initializable {

    private ClientController clientController;

    @FXML
    TableView<FileInfo> localDirectory, cloudDirectory;

    @FXML
    TextField localDirectoryPath, cloudDirectoryPath;

    @FXML
    Button btLogin, btCloudList, btUpload, btDownload;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clientController = new ClientController(this);

        TableColumn<FileInfo, Byte> fileType = new TableColumn<>("");
        fileType.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getIsFile()));
        fileType.setPrefWidth(0); fileType.setVisible(false);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Name");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(280);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "DIR";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);

        localDirectory.getColumns().addAll(fileType, filenameColumn, fileSizeColumn);
        localDirectory.getSortOrder().addAll(fileType, filenameColumn);

        updateLocalDirectory(Paths.get(clientController.getLocalUserDirectory()));

        localDirectory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(localDirectoryPath.getText()).resolve(localDirectory.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        updateLocalDirectory(path);
                    }
                }
            }
        });

        TableColumn<FileInfo, Byte> cloudFileType = new TableColumn<>("");
        cloudFileType.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getIsFile()));
        cloudFileType.setPrefWidth(0); cloudFileType.setVisible(false);

        TableColumn<FileInfo, String> cloudFilenameColumn = new TableColumn<>("Name");
        cloudFilenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        cloudFilenameColumn.setPrefWidth(280);

        TableColumn<FileInfo, Long> cloudFileSizeColumn = new TableColumn<>("Size");
        cloudFileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        cloudFileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "DIR";
                        }
                        setText(text);
                    }
                }
            };
        });
        cloudFileSizeColumn.setPrefWidth(120);

        cloudDirectory.getColumns().addAll(cloudFileType, cloudFilenameColumn, cloudFileSizeColumn);
        cloudDirectory.getSortOrder().addAll(cloudFileType, cloudFilenameColumn);

        btCloudList.setDisable(true);
        btUpload.setDisable(true);
        btDownload.setDisable(true);

    }

    public void updateLocalDirectory(Path path) {
        Platform.runLater(() -> {
            try {
                localDirectory.getItems().clear();
                localDirectoryPath.setText(path.normalize().toAbsolutePath().toString());
                localDirectory.getItems().addAll(Files.list(path)
                        .filter(p -> !p.getFileName().toString().equals(".DS_Store"))
                        .map(FileInfo::new).collect(Collectors.toList()));
                localDirectory.sort();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void btnUpLocalDirectory(ActionEvent actionEvent) {
        Path parentPath = Paths.get(localDirectoryPath.getText()).getParent();
        if (parentPath != null) {
            updateLocalDirectory(parentPath);
        }
    }

    public void btnLogin(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/auth_dialog.fxml"));
        try {
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btLogin.getParent().getScene().getWindow());
            AuthDialog controller = (AuthDialog) fxmlLoader.getController();
            stage.showAndWait();
            clientController.runAuthProcess(controller.getLogin(), controller.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientIsAuthorized() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login to cloud");
            alert.setHeaderText(null);
            if (clientController.isAuthorized()) {
                alert.setContentText("Login is successful!");
                btCloudList.setDisable(false);
                btUpload.setDisable(false);
                btDownload.setDisable(false);
                clientController.getCloudList();
            } else {
                alert.setContentText("Error in login or password!");
                btCloudList.setDisable(true);
                btUpload.setDisable(true);
                btDownload.setDisable(true);
            }
            alert.showAndWait();
        });
    }

    public void btnCloudList(ActionEvent actionEvent) {
        clientController.getCloudList();
    }

    public void updateCloudDirectory(List<FileInfo> cloudFileList) {
        Platform.runLater(() -> {
            cloudDirectory.getItems().clear();
            cloudDirectoryPath.setText(clientController.getUserName());
            cloudDirectory.getItems().addAll(cloudFileList);
            cloudDirectory.sort();
        });
    }

    public void btnUpload(ActionEvent actionEvent) {
        if(localDirectory.getSelectionModel().getSelectedItem() != null) {
            if(localDirectory.getSelectionModel().getSelectedItem().getIsFile() == 0) {
                showErrorAlert("Select file not directory!");
                return;
            }
            clientController.uploadFile(localDirectory.getSelectionModel().getSelectedItem().getFilename(),
                    localDirectoryPath.getText(), cloudDirectoryPath.getText());
        }
    }

    public void btnDownload(ActionEvent actionEvent) {
        if(cloudDirectory.getSelectionModel().getSelectedItem() != null){
            if(cloudDirectory.getSelectionModel().getSelectedItem().getIsFile() == 0) {
                showErrorAlert("Select file not directory!");
                return;
            }
            clientController.downloadFile(cloudDirectory.getSelectionModel().getSelectedItem().getFilename(),
                    cloudDirectoryPath.getText(), localDirectoryPath.getText());
        }
    }

    public void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
