package com.ozakhlivny.cloudproject.client.forms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthDialog implements Initializable {
    @FXML
    TextField tfLogin;
    @FXML
    PasswordField tfPassword;

    @FXML
    Button btnOk, btnCancel;

    String login, password;


    public void btOK(ActionEvent actionEvent) {
        login = tfLogin.getText().trim();
        password = tfPassword.getText().trim();
        btnOk.getScene().getWindow().hide();
    }

    public void btCancel(ActionEvent actionEvent) {
        login = null;
        password = null;
        btnCancel.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        login = null;
        password = null;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
