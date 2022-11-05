package com.example.demo5;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {
    private static ToDoData instance = new ToDoData();
    private static String fileName = "TodoListItems.txt";

    private ObservableList<ToDOItem> toDOItems;
    private DateTimeFormatter formatter;

    public static ToDoData getInstance() {
        return instance;
    }
//    public void setToDOItems(List<ToDOItem> toDOItems ){
//        this.toDOItems=toDOItems;
//    }

    private ToDoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<ToDOItem> getToDoItems() {
        return toDOItems;
    }

    public void addTodoItem(ToDOItem item) {

        toDOItems.add(item);
    }


    public void loadToDoItems() throws IOException {
        toDOItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        BufferedReader br = Files.newBufferedReader(path);

        String input;

        try {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);
                ToDOItem toDOItem = new ToDOItem(shortDescription, details, date);
                toDOItems.add(toDOItem);

            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public void storeToDoItems() throws IOException {
        Path path = Paths.get(fileName);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<ToDOItem> iter = toDOItems.iterator();
            while (iter.hasNext()) {
                ToDOItem item = iter.next();
                bw.write(String.format("%S\t%S\t%S",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadLine().format(formatter)));
                bw.newLine();
                bw.flush();
            }
        } finally {
            if (bw != null) {

            }
        }
    }
    public void deleteTodoItem(ToDOItem item){
        toDOItems.remove(item);
    }

}
