package com.mri.uiapp;

import com.mri.uiapp.ApiConnector.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class HistoryController {

    @FXML
    private TableView<History> tableViewHistory;

    @FXML
    private TableColumn<History, String> tableViewHistory1stName;

    @FXML
    private TableColumn<History, String> tableViewHistory2ndName;

    @FXML
    private TableColumn<History, String> tableViewHistoryDate;

    @FXML
    private TableColumn<History, Integer> tableViewHistoryId;

    @FXML
    private TextField textFieldHistorySearch;

    @FXML
    private void initialize() {
        prepTableViewHistory();
        populateTableViewHistory();
    }



    private void prepTableViewHistory() {
        tableViewHistoryId.setCellValueFactory(new PropertyValueFactory<History, Integer>("id"));
        tableViewHistory1stName.setCellValueFactory(new PropertyValueFactory<History,String>("userFirstName"));
        tableViewHistory2ndName.setCellValueFactory(new PropertyValueFactory<History,String>("userSecondName"));
        tableViewHistoryDate.setCellValueFactory(new PropertyValueFactory<History,String>("dateTime"));
    }

    private ObservableList<History> getDataHistory() {
        ObservableList<History> historyCollectionsFX = FXCollections.observableArrayList();
        List<History> historyList = ApiClient.getHistory();
        historyCollectionsFX.addAll(historyList);
        return historyCollectionsFX;
    }

    private void populateTableViewHistory() {
        tableViewHistory.setItems(getDataHistory());
    }
}
