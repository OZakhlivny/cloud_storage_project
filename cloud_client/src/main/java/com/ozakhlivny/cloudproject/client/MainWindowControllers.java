package com.ozakhlivny.cloudproject.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.ResourceBundle;

public class MainWindowControllers implements Initializable {
    public static final String USER_LOCAL_DIRECTORY = "/Users/oleg/Desktop/GeekBrains";

    @FXML
    ListView<String> localDirectory;

    @FXML
    TextField localDirectoryPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateLocalDirectory(Paths.get(USER_LOCAL_DIRECTORY));

        localDirectory.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(localDirectoryPath.getText()).resolve(localDirectory.getSelectionModel().getSelectedItem());
                    if (Files.isDirectory(path)) {
                        updateLocalDirectory(path);
                    }
                }
            }
        });
    }

    public void updateLocalDirectory(Path path) {
        Platform.runLater(() -> {
            try {
                localDirectory.getItems().clear();
                localDirectoryPath.setText(path.normalize().toAbsolutePath().toString());
                Files.list(path)
                        .filter(p -> Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(file -> localDirectory.getItems().add(file));

                Files.list(path)
                        .filter(p -> !Files.isDirectory(p))
                        .filter(p -> !p.getFileName().toString().equals(".DS_Store"))
                        .map(p -> p.getFileName().toString())
                        .forEach(file ->  localDirectory.getItems().add(file));

                Collections.sort(localDirectory.getItems());

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
}
