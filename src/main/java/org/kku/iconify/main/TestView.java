package org.kku.iconify.main;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.kku.iconify.data.IconSetData;
import org.kku.iconify.data.IconSets;
import org.kku.iconify.ui.AbstractIcon.IconSize;
import org.kku.iconify.ui.FxIcons;
import org.tbee.javafx.scene.layout.MigPane;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TestView
  extends Application
{
  @Override
  public void start(Stage stage) throws IOException
  {
    HBox hbox;
    ToggleButton button;
    List<String> iconSetNameList;
    ListView<String> listView;
    ContextMenu contextMenu;

    button = new ToggleButton("License");

    hbox = new HBox();
    hbox.getChildren().add(button);

    iconSetNameList = IconSets.get().getIconSetDataList().stream().map(IconSetData::getName).distinct().sorted()
        .collect(Collectors.toList());

    listView = new ListView<>();
    listView.getItems().addAll(iconSetNameList);

    CustomMenuItem topNode = getTopNode();

    contextMenu = new ContextMenu();
    contextMenu.widthProperty().addListener((ae) -> {
      System.out.println(contextMenu.getWidth());
      ((MigPane) topNode.getContent()).setPrefWidth(contextMenu.getWidth());
      ((MigPane) topNode.getContent()).requestLayout();
    });
    contextMenu.getItems().add(getTopNode());
    contextMenu.getItems().add(new SeparatorMenuItem());
    contextMenu.getItems().add(new MenuItem("Hele lange menu item is dit"));
    /*
    contextMenu.getItems()
        .addAll(iconSetNameList.stream().map(l -> new CustomMenuItem(getNode(l))).collect(Collectors.toList()));
        */
    System.out.println(contextMenu.getWidth());

    button.setContextMenu(contextMenu);

    Scene scene = new Scene(hbox, 1200, 600);
    stage.setTitle("Iconify");
    stage.setScene(scene);
    stage.show();
  }

  private CustomMenuItem getTopNode()
  {
    Button button;
    MigPane box;
    CustomMenuItem menuItem;

    box = new MigPane("debug, insets 0, gap 0, ", "[fill]push[]", "[][][]");
    box.setStyle("-fx-background-color: lightblue; -fx-padding: 0;"); // For visibility
    button = new Button("Select all", FxIcons.create("mdi-checkboxes-marked").size(IconSize.MEDIUM).getNode());
    button.setAlignment(Pos.BASELINE_LEFT);
    button.setMaxWidth(Double.MAX_VALUE);
    box.add(button, "grow");

    Label label = new Label("", FxIcons.create("mdi-cancel-box").size(IconSize.MEDIUM).getNode());
    box.add(label, "align right, wrap");

    button = new Button("Deselect all", FxIcons.create("mdi-checkboxes-blank-outline").size(IconSize.MEDIUM).getNode());
    button.setAlignment(Pos.BASELINE_LEFT);
    button.setMaxWidth(Double.MAX_VALUE);
    box.add(button, "grow");

    menuItem = new SeparatorMenuItem();
    menuItem.setContent(box);
    menuItem.setHideOnClick(false);

    return menuItem;
  }

  private Node getNode(String text)
  {
    Label label;

    label = new Label(text);
    label.setStyle("-fx-background-color: lightblue;");
    return label;
  }

  public static void main(String args[])
  {
    launch(args);
  }
}