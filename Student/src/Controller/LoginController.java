package Controller;

import Request.LoginRequest;
import Response.LoginResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML
    public AnchorPane loginPane;
    @FXML
    public Button loginButton;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField usernameField;
    @FXML
    public Label signinLabel;
    @FXML
    public Hyperlink signupLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void login(ActionEvent actionEvent) {
        System.out.println("Creating a request object");
        LoginRequest request=new LoginRequest(usernameField.getText(),passwordField.getText());
        Main.SendRequest(request);
        System.out.println("Request.Request Sent");
        LoginResponse response= (LoginResponse) Main.GetResponse();
        if (response != null && response.getFirstName() == null) {
            System.out.println("Wrong Info");
        }
        else {
            assert response != null;
            System.out.println("Registration number is "+response.getRegistrationNo());
            FXMLLoader homepageLoader= new FXMLLoader(getClass().getResource("../FXML/HomePage.fxml"));
            Stage currentStage=(Stage)loginButton.getScene().getWindow();
            Scene scene=null;
            try {
                 scene=new Scene(homepageLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentStage.setScene(scene);
            currentStage.setTitle("Welcome");
            HomePageController homePageController=homepageLoader.getController();
            homePageController.initData(scene,response);
        }
    }

    public void switchToSignup(ActionEvent actionEvent) {
        FXMLLoader registerLoader=new FXMLLoader(getClass().getResource("../FXML/Register.fxml"));
        Scene scene=null;
        Stage stage=(Stage)signupLink.getScene().getWindow();
        try {
            scene=new Scene(registerLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.setTitle("Sign Up");

    }
}
