package com.monprojet.boutiquejeux.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.monprojet.boutiquejeux.dto.PageDto;
import com.monprojet.boutiquejeux.dto.planning.PlanningRowDto;
import com.monprojet.boutiquejeux.service.ApiClient;
import com.monprojet.boutiquejeux.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class PlanningController {

    @FXML private DatePicker datePicker;
    @FXML private TableView<PlanningRowDto> tablePlanning;
    @FXML private TableColumn<PlanningRowDto, String> colEmploye;
    @FXML private TableColumn<PlanningRowDto, String> colRole;
    @FXML private TableColumn<PlanningRowDto, String> colLundi;
    @FXML private TableColumn<PlanningRowDto, String> colMardi;
    @FXML private TableColumn<PlanningRowDto, String> colMercredi;
    @FXML private TableColumn<PlanningRowDto, String> colJeudi;
    @FXML private TableColumn<PlanningRowDto, String> colVendredi;
    @FXML private ProgressIndicator spinner;

    @FXML
    public void initialize() {
        spinner.setVisible(false);
        datePicker.setValue(LocalDate.now());

        colEmploye.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().nomEmploye != null ? c.getValue().nomEmploye : ""));
        colRole.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().role != null ? c.getValue().role : ""));
        colLundi.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().lundi != null ? c.getValue().lundi : ""));
        colMardi.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().mardi != null ? c.getValue().mardi : ""));
        colMercredi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().mercredi != null ? c.getValue().mercredi : ""));
        colJeudi.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().jeudi != null ? c.getValue().jeudi : ""));
        colVendredi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().vendredi != null ? c.getValue().vendredi : ""));

        handleCharger();
    }

    @FXML
    private void handleCharger() {
        LocalDate date  = datePicker.getValue();
        Long      magId = SessionManager.getInstance().getMagasinId();
        spinner.setVisible(true);

        Task<PageDto<PlanningRowDto>> task = new Task<>() {
            @Override protected PageDto<PlanningRowDto> call() throws Exception {
                return ApiClient.getInstance().get(
                    "/plannings?magasinId=" + magId + "&semaine=" + date,
                    new TypeReference<PageDto<PlanningRowDto>>() {}
                );
            }
        };
        task.setOnSucceeded(e -> {
            PageDto<PlanningRowDto> res = task.getValue();
            List<PlanningRowDto> rows = res != null && res.content != null ? res.content : List.of();
            Platform.runLater(() -> {
                tablePlanning.setItems(FXCollections.observableArrayList(rows));
                spinner.setVisible(false);
            });
        });
        task.setOnFailed(e -> Platform.runLater(() -> spinner.setVisible(false)));
        new Thread(task).start();
    }
}
