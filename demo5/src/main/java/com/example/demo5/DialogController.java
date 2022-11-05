package com.example.demo5;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadLinePicker;

    public ToDOItem processResults(){
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadLineValue = deadLinePicker.getValue();

        ToDOItem newItem = new ToDOItem(shortDescription,details,deadLineValue);

//        ToDoData.getInstance().addTodoItem(new ToDOItem(shortDescription,details,deadLineValue));
        ToDoData.getInstance().addTodoItem(newItem);
        return newItem;
    }


}
