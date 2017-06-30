package com.clinacuity.acv.controllers;

import com.clinacuity.acv.context.AcvContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppMainController implements Initializable {
    private static final Logger logger = LogManager.getLogger();

    @FXML private GridPane masterGrid;
    @FXML private GridPane targetGridContent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AcvContext.getInstance().setMainController(this);

        try {
            MenuBar menuBar = FXMLLoader.load(getClass().getResource("/pages/MenuBar.fxml"));
            masterGrid.getChildren().addAll(menuBar);

            reloadContent("/pages/LoadScreenView.fxml");
        } catch (IOException e) {
            logger.throwing(e);
        }
    }

    void reloadContent(String page) {
        try {
            masterGrid.getChildren().remove(targetGridContent);
            targetGridContent = FXMLLoader.load(getClass().getResource(page));
            targetGridContent.setPadding(new Insets(5.0f, 10.f, 10.0f, 10.f));
            masterGrid.addRow(1, targetGridContent);

//            targetGridContent.prefHeightProperty().bind(targetGridContent.getScene().heightProperty());
//            targetGridContent.prefWidthProperty().bind(targetGridContent.getScene().widthProperty());
        } catch (IOException e) {
            logger.throwing(e);
        }
    }
}
