package com.ozakhlivny.cloudproject.client.forms;

import com.ozakhlivny.cloudproject.common.files.FileInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainWindowControllers implements Initializable {
    public static final String USER_LOCAL_DIRECTORY = "/Users/oleg/Desktop/GeekBrains";

    @FXML
    TableView<FileInfo> localDirectory;

    @FXML
    TextField localDirectoryPath;

    @FXML
    Button btLogin, btCloudList, btUpload, btDownload;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        updateLocalDirectory(Paths.get(USER_LOCAL_DIRECTORY));

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
            Parent dialog = fxmlLoader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(dialog);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btLogin.getParent().getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
