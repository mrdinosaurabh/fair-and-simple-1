package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.GuiUtil;
import main.TeacherApplication;
import request.*;
import response.*;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

public class TeacherHomeController {
    @FXML
    public ImageView profilePicImageView;
    @FXML
    public Button changePasswordButton;
    @FXML
    public Label heyNameLabel;
    @FXML
    public TextField courseSearchTextField;
    @FXML
    public TableView<Course> coursesTableView;
    @FXML
    public TableColumn<Course, String> courseTableColumn;
    @FXML
    public TextField courseNameTextField;
    @FXML
    public TextArea courseDescriptionTextArea;
    @FXML
    public TextField courseCodeTextField;
    @FXML
    public Button createCourseButton;
    @FXML
    public TextField examHistorySearchBar;
    @FXML
    public TableView<Exam> resultExamsTableView;
    @FXML
    public TableColumn<Exam, String> examResultCourseTableColumn;
    @FXML
    public TableColumn<Exam, String> examsResultTitleTableColumn;
    @FXML
    public TableColumn<Exam, Date> examsResultDateTableColumn;
    @FXML
    public TableView particularResultTableView;
    @FXML
    public TableColumn particularResultRankTableColumn;
    @FXML
    public TableColumn particularResultRegistrationNumberTableColumn;
    @FXML
    public TableColumn particularResultNameTableColumn;
    @FXML
    public TableColumn particularResultMarksTableColumn;
    @FXML
    public TextField upcomingExamsSearchBar;
    @FXML
    public TableView<Exam> upcomingExamsTableView;
    @FXML
    public TableColumn<Exam, String> upcomingCourseNameTableColumn;
    @FXML
    public TableColumn<Exam, String> upcomingTitleTableColumn;
    @FXML
    public TableColumn<Exam, Date> upcomingTimeTableColumn;
    @FXML
    public TableColumn<Exam, Integer> upcomingMaxMarksTableColumn;
    @FXML
    public PasswordField oldPasswordTextField;
    @FXML
    public PasswordField newPasswordTextField;
    @FXML
    public PasswordField confirmNewPasswordTextField;

    @FXML
    public void createExamResponse(ActionEvent actionEvent) {
    }

    @FXML
    public void upcomingExamsResponse(ActionEvent actionEvent) {
    }

    @FXML
    public void changePasswordResponse(ActionEvent actionEvent) {
        if(!newPasswordTextField.getText().equals(confirmNewPasswordTextField.getText())) {
            GuiUtil.alert(Alert.AlertType.WARNING, "New password and confirm password don't match.");
            return;
        }
        Platform.runLater(() -> {
            changePasswordButton.setDisable(true);
            TeacherChangePasswordRequest request = new TeacherChangePasswordRequest(TeacherApplication.getTeacherId(),
                    oldPasswordTextField.getText(), newPasswordTextField.getText());
            TeacherApplication.sendRequest(request);
            TeacherChangePasswordResponse response = (TeacherChangePasswordResponse) TeacherApplication.receiveResponse();
            if(response == null || response.getStatus() == -1) {
                GuiUtil.alert(Alert.AlertType.ERROR, "Could not change the password");
            } else {
                GuiUtil.alert(Alert.AlertType.INFORMATION, "Password changed successfully!");
                newPasswordTextField.clear();
                oldPasswordTextField.clear();
                confirmNewPasswordTextField.clear();
                changePasswordButton.setDisable(false);
            }
        });
    }

    @FXML
    public void changeProfilePictureResponse(ActionEvent actionEvent) {
    }

    @FXML
    public void logOutResponse(ActionEvent actionEvent) {
        TeacherApplication.sendRequest(new LogoutRequest());
        Platform.exit();
    }

    @FXML
    public void onCourseClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2)
        {
            Course selectedCourse = coursesTableView.getSelectionModel().getSelectedItem();
            String courseTitle = selectedCourse.getCourseTitle();
            String courseId = selectedCourse.getCourseId();
            System.out.println("Clicked = " + courseId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/CourseView.fxml"));
            Stage stage = (Stage) changePasswordButton.getScene().getWindow();
            Scene scene = null;
            try {
                scene = new Scene(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(scene);
            stage.setTitle(courseTitle);
            CourseController controller = loader.getController();
            controller.callFirst(selectedCourse.getCourseId());
        }
    }

    public void callFirst() {
        populateTeacherCourses();
        populateExamTables();
    }

    @FXML
    public void createCourseButtonResponse(ActionEvent actionEvent) {
        if(courseNameTextField.getText() == null || courseNameTextField.getText().length() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Enter a non-empty course name.");
            alert.showAndWait();
        } else {
            courseNameTextField.setEditable(false);
            courseDescriptionTextArea.setEditable(false);
            createCourseButton.setDisable(true);
            Platform.runLater(() -> {
                CreateCourseRequest request = new CreateCourseRequest(TeacherApplication.getTeacherId(), courseDescriptionTextArea.getText(), courseNameTextField.getText());
                TeacherApplication.sendRequest(request);
                CreateCourseResponse response = (CreateCourseResponse) TeacherApplication.receiveResponse();
                System.out.println("Response = " + response);
                if(response == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Could Not create a course. Please try again.");
                    alert.showAndWait();
                    courseNameTextField.setEditable(true);
                    courseDescriptionTextArea.setEditable(true);
                    createCourseButton.setDisable(false);
                } else {
                    courseCodeTextField.setText(response.getTeamCode());
                }
            });
        }
    }

    private void populateTeacherCourses() {
        System.out.println("The thread is " + Thread.currentThread());
        System.out.println("Called first");
        courseTableColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        System.out.println("Values set");
        Platform.runLater(() -> {
            System.out.println("Inside courses request thread" + Thread.currentThread());
            TeacherCoursesRequest request = new TeacherCoursesRequest(TeacherApplication.getTeacherId());
            TeacherApplication.sendRequest(request);
            TeacherCoursesResponse response = (TeacherCoursesResponse) TeacherApplication.receiveResponse();
            System.out.println("Fetched courses response");
            if(response == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not fetch your courses. Click OK to exit the application");
                alert.showAndWait();
                System.exit(0);
            } else {
                System.out.println("Creating courses list");
                ObservableList<Course> courseList = FXCollections.observableList(response.getCourses());
                System.out.println("List created. Now setting into table.");
                coursesTableView.setItems(courseList);
                System.out.println("List set into table.");
            }
        });
    }

    private void populateExamTables() {
        upcomingCourseNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        upcomingTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        upcomingTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        upcomingMaxMarksTableColumn.setCellValueFactory(new PropertyValueFactory<>("maximumMarks"));

        examsResultTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        examsResultDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        examResultCourseTableColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));

        Platform.runLater(() -> {
            TeacherExamRequest request = new TeacherExamRequest(TeacherApplication.getTeacherId(), false);
            TeacherApplication.sendRequest(request);
            TeacherExamResponse response = (TeacherExamResponse) TeacherApplication.receiveResponse();
            if(response == null) GuiUtil.alert(Alert.AlertType.ERROR, "Could not fetch your exams. Might be a server error.");
            else {
                ObservableList<Exam> upcomingExams = FXCollections.observableList(response.getExams().stream()
                        .filter((Exam e) -> e.getDate().after(new Date()))
                        .collect(Collectors.toList()));
                ObservableList<Exam> pastExams = FXCollections.observableList(response.getExams().stream()
                        .filter((Exam e) -> e.getDate().before(new Date()))
                        .collect(Collectors.toList()));
                upcomingExamsTableView.setItems(upcomingExams);
                resultExamsTableView.setItems(pastExams);
            }
        });
    }

    public void changePasswordButtonResponse(ActionEvent actionEvent) {
    }

    public void selectImageButtonResponse(ActionEvent actionEvent) {
    }

    public void confirmPicChangeButtonResponse(ActionEvent actionEvent) {
    }

    public void logOutButtonResponse(ActionEvent actionEvent) {
    }

    public void refreshButtonResponse(ActionEvent actionEvent) {
    }

    public void clearResponse(ActionEvent actionEvent) {
        courseNameTextField.clear();
        courseDescriptionTextArea.clear();
        courseCodeTextField.clear();
    }
}
