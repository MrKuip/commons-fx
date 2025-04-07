package org.kku.fx.scene.control;

import java.util.stream.Collectors;
import org.kku.common.util.SuppressFBWarnings;
import org.kku.fonticons.ui.FxIcon.IconSize;
import org.kku.fx.ui.util.FxIconUtil;
import org.kku.fx.util.AppProperties.AppProperty;
import org.kku.fx.util.AppSettings;
import org.tbee.javafx.scene.layout.MigPane;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DraggableTabPane
  extends TabPane
{
  @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  private static int NEXT_TAB_SEQUENCE_NUMBER = 0;
  private static String TAB_SEQUENCE_NUMBER = "TAB_SEQUENCE_NUMBER";
  private static DataFormat DATA_FORMAT = new DataFormat("draggable/tab");

  private Tab m_draggedTab;
  private Node m_draggedNode;

  public DraggableTabPane()
  {
    setOnDragDetected();
  }

  public Tab createTab(String iconName, StringExpression nameExpression)
  {
    Tab tab;
    MigPane iconPane;

    tab = new Tab();
    tab.textProperty().bind(nameExpression);
    tab.getStyleClass().add("draggable-tab");
    tab.setUserData(this);
    tab.getProperties().put(TAB_SEQUENCE_NUMBER, ++NEXT_TAB_SEQUENCE_NUMBER);
    tab.setClosable(false);

    iconPane = new MigPane("", "0[]0[]0");
    iconPane.getChildren().add(FxIconUtil.createFxIcon("drag", IconSize.SMALL).fillColor(Color.GREY).getIconLabel());
    if (iconName != null)
    {
      Node icon;

      icon = FxIconUtil.createIconNode(iconName, IconSize.SMALL);
      icon.prefHeight(300);
      iconPane.getChildren().add(icon);
    }
    tab.setGraphic(iconPane);

    getTabs().add(tab);

    return tab;
  }

  //source events handlers
  public void setOnDragDetected()
  {
    setOnDragDetected(me -> {
      Image image;
      Dragboard dragboard;
      ClipboardContent clipboardContent;

      /* drag was detected, start drag-and-drop gesture*/
      m_draggedTab = getSelectionModel().getSelectedItem();
      m_draggedNode = m_draggedTab.getContent();

      image = m_draggedNode.snapshot(null, null);

      /* allow any transfer mode */
      dragboard = m_draggedNode.startDragAndDrop(TransferMode.ANY);

      clipboardContent = new ClipboardContent();
      // Create a DataFormat unknown to everybody so it won't get accepted anywhere
      clipboardContent.put(DATA_FORMAT, m_draggedTab.getText());

      dragboard.setContent(clipboardContent);
      dragboard.setDragView(image);

      me.consume();
    });

    setOnDragDone(de -> {
      Stage draggedDialog;
      StackPane dialogContent;
      Point2D mousePosition;
      Node draggedNode;
      Tab draggedTab;
      Double dialogWidth;
      Double dialogHeight;
      String subjectId;
      AppProperty<Double> widthProperty;
      AppProperty<Double> heightProperty;
      TabPane tabPane;

      mousePosition = new Robot().getMousePosition();

      de.getDragboard().setDragView(null);

      draggedNode = m_draggedNode;
      draggedTab = m_draggedTab;

      dialogWidth = draggedNode.getLayoutBounds().getWidth();
      dialogHeight = draggedNode.getLayoutBounds().getHeight();

      subjectId = "DraggedTab:" + draggedTab.getText();
      widthProperty = AppSettings.WIDTH.forSubject(subjectId);
      heightProperty = AppSettings.HEIGHT.forSubject(subjectId);

      draggedDialog = new Stage();
      draggedDialog.setTitle(draggedTab.getText());
      draggedDialog.initStyle(StageStyle.UTILITY);
      dialogContent = new StackPane();
      dialogContent.getChildren().add(draggedNode);
      Scene dialogScene = new Scene(dialogContent);
      draggedDialog.setScene(dialogScene);
      draggedDialog.setX(mousePosition.getX());
      draggedDialog.setY(mousePosition.getY());
      draggedDialog.setWidth(widthProperty.get(dialogWidth));
      draggedDialog.setHeight(heightProperty.get(dialogHeight));
      draggedDialog.widthProperty().addListener(widthProperty.getChangeListener());
      draggedDialog.heightProperty().addListener(heightProperty.getChangeListener());

      // Replace the dialog with the original tab
      draggedDialog.setOnCloseRequest((cr) -> {
        Long index;

        draggedTab.setContent(draggedNode);

        // Calculate the original index of the tab in the tabpane
        index = getTabs().stream().filter(t -> getSequenceNumber(t) < getSequenceNumber(draggedTab))
            .collect(Collectors.counting());

        getTabs().add(index.intValue(), draggedTab);
      });
      draggedDialog.show();

      // Remove tab from tabPane
      tabPane = draggedTab.getTabPane();
      tabPane.getTabs().remove(draggedTab);

      de.consume();
    });
  }

  private int getSequenceNumber(Tab tab)
  {
    Object tabSequenceNumber;

    tabSequenceNumber = tab.getProperties().get(TAB_SEQUENCE_NUMBER);
    if (tabSequenceNumber == null)
    {
      return Integer.MAX_VALUE;
    }

    return (Integer) tabSequenceNumber;
  }

}