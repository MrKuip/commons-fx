package org.kku.iconify.main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestCombobox
  extends Application
{

  @Override
  public void start(Stage stage)
  {
    VBox vbox;
    Scene scene;

    ObservableList<String> itemList = FXCollections.observableArrayList();
    itemList.add("one");
    itemList.add("two");
    itemList.add("three");
    itemList.add("four");
    vbox = new VBox();
    MenuButton button = new MenuButton("Choice");
    for (String item : itemList)
    {
      CustomMenuItem cmi;
      cmi = new CustomMenuItem(new CheckBox(item));
      cmi.setHideOnClick(false);
      button.getItems().add(cmi);
    }

    vbox.getChildren().add(button);

    scene = new Scene(vbox, 450, 250);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}