package com.example.demo5;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HelloController {
    private List<ToDOItem> toDoItemList;
    @FXML
    private ListView<ToDOItem> toDoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadLineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ToDOItem> filteredList;

    private Predicate<ToDOItem> wantAllItems;
    private Predicate<ToDOItem> wantTodaysItems;


    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDOItem item = toDoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
//        ToDOItem item1 = new ToDOItem("Mail birthday card", "Buy a 30th birthday card for John",
//                LocalDate.of(2016, Month.APRIL, 25));
//        ToDOItem item2 = new ToDOItem("Doctor's Appointment", "See Dr. Smith at 123 Main Street.  Bring paperwork",
//                LocalDate.of(2016, Month.MAY, 23));
//        ToDOItem item3 = new ToDOItem("Finish design proposal for client", "I promised Mike I'd email website mockups by Friday 22nd April",
//                LocalDate.of(2016, Month.APRIL, 22));
//        ToDOItem item4 = new ToDOItem("Pickup Doug at the train station", "Doug's arriving on March 23 on the 5:00 train",
//                LocalDate.of(2016, Month.MARCH, 23));
//        ToDOItem item5 = new ToDOItem("Pick up dry cleaning", "The clothes should be ready by Wednesday",
//                LocalDate.of(2016, Month.APRIL, 20));
//
//        toDoItemList = new ArrayList<ToDOItem>();
//        toDoItemList.add(item1);
//        toDoItemList.add(item2);
//        toDoItemList.add(item3);
//        toDoItemList.add(item4);
//        toDoItemList.add(item5);
//
//        ToDoData.getInstance().setToDOItems(toDoItemList);
        toDoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDOItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDOItem> observable, ToDOItem oldValue, ToDOItem newValue) {
                if (newValue != null) {
                    ToDOItem item = toDoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("d MMMM yyyy");
                    deadLineLabel.setText(df.format(item.getDeadLine()));
                }
            }
        });
        filteredList = new FilteredList<ToDOItem>(ToDoData.getInstance().getToDoItems(), wantAllItems);
        wantAllItems = new Predicate<ToDOItem>() {
            @Override
            public boolean test(ToDOItem toDOItem) {
                return true;
            }
        };
        wantTodaysItems = new Predicate<ToDOItem>() {
            @Override
            public boolean test(ToDOItem toDOItem) {
                return (toDOItem.getDeadLine().equals(LocalDate.now()));
            }
        };


        SortedList<ToDOItem> sortedList = new SortedList<ToDOItem>(filteredList,
                new Comparator<ToDOItem>() {
                    @Override
                    public int compare(ToDOItem o1, ToDOItem o2) {
                        return o1.getDeadLine().compareTo(o2.getDeadLine());
                    }
                });

        toDoListView.setItems(sortedList);
//        toDoListView.setItems(ToDoData.getInstance().getToDoItems());
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();

        toDoListView.setCellFactory(new Callback<ListView<ToDOItem>, ListCell<ToDOItem>>() {
            @Override
            public ListCell<ToDOItem> call(ListView<ToDOItem> toDOItemListView) {
                ListCell<ToDOItem> cell = new ListCell<ToDOItem>() {
                    @Override
                    protected void updateItem(ToDOItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());
                            if (item.getDeadLine().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (item.getDeadLine().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.YELLOW);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;

            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new todo item");
        dialog.setHeaderText("Use the dialog to create a new todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ToDOItem newItem = controller.processResults();
            toDoListView.getSelectionModel().select(newItem);
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDOItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }

    @FXML
    public void handleClickListView() {
        ToDOItem item = toDoListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDetails());
        deadLineLabel.setText(item.getDeadLine().toString());

    }

    public void deleteItem(ToDOItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete todo item");
        alert.setHeaderText("Delete item " + item.getShortDescription());
        alert.setContentText("Are you sure ? Press ok to confirm , or cancel to back out. ");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            ToDoData.getInstance().deleteTodoItem(item);
        }

    }

    public void handleFilterButton() {
        ToDOItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if(filteredList.isEmpty()){
                itemDetailsTextArea.clear();
                deadLineLabel.setText("");
            }else if(filteredList.contains(selectedItem)){
                toDoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantAllItems);
            toDoListView.getSelectionModel().select(selectedItem);
        }
    }
    @FXML
    public void handleExit(){
        Platform.exit();
    }
}



