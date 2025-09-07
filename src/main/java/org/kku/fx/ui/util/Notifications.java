package org.kku.fx.ui.util;

import org.kku.iconify.ui.FxIcon;
import org.kku.iconify.ui.FxIcon.IconSize;
import org.tbee.javafx.scene.layout.MigPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.StringExpression;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Notifications
{
  private static Notifications m_instance = new Notifications();
  private static int MESSAGE_SHOW_DURATION_IN_SECONDS = 10;

  private VBox m_view = new VBox();

  private Notifications()
  {
  }

  public static Notifications getInstance()
  {
    return m_instance;
  }

  public VBox getView()
  {
    return m_view;
  }

  public static void showMessage(StringExpression titleExpression, StringExpression textExpression)
  {
    m_instance._showMessage(titleExpression, textExpression);
  }

  private void _showMessage(StringExpression titleExpression, StringExpression textExpression)
  {
    Node node;

    node = new MessageItemView(new FxIcon("mdi-information").size(IconSize.LARGE).getNode(), titleExpression,
        textExpression, MESSAGE_SHOW_DURATION_IN_SECONDS);

    m_instance.getView().getChildren().add(0, node);
  }

  public static void showTask(Task<?> task)
  {
    m_instance._showTask(task);
  }

  private void _showTask(Task<?> task)
  {
    m_view.getChildren().add(new TaskItemView<>(task));
  }

  class TaskItemView<T>
    extends MigPane
  {
    private final Node graphic;
    private final ProgressBar progressBar;
    private final Label titleText;
    private final Label messageText;
    private final Button cancelButton;
    private final Task<?> mi_task;

    TaskItemView(Task<?> task)
    {
      super("wrap3", "[][grow][]", "");

      mi_task = task;
      mi_task.stateProperty().addListener((_, _, newValue) -> {
        if (newValue == Worker.State.CANCELLED || newValue == Worker.State.FAILED || newValue == Worker.State.READY
            || newValue == Worker.State.SUCCEEDED)
        {
          m_view.getChildren().remove(this);
        }
      });

      getStyleClass().add("task-item");

      graphic = FxIconUtil.createIconNode("mdi-magnify", IconSize.LARGE);
      titleText = new Label();
      titleText.textProperty().bind(task.titleProperty());
      titleText.setStyle("-fx-font-weight: bold");
      messageText = new Label();
      messageText.textProperty().bind(task.messageProperty());
      progressBar = new ProgressBar();
      progressBar.setMaxWidth(Double.MAX_VALUE);
      progressBar.setMaxHeight(8);
      progressBar.progressProperty().bind(task.progressProperty());
      cancelButton = new Button("Cancel", FxIconUtil.createIconNode("mdi-cancel", IconSize.REGULAR));
      cancelButton.setTooltip(new Tooltip("Cancel Task"));
      cancelButton.setOnAction((_) -> {
        task.cancel();
      });

      add(graphic, "cell 0 0, span 1 3, aligny center");
      add(titleText, "cell 1 0");
      add(cancelButton, "cell 2 0, span 1 3, aligny center");
      add(messageText, "cell 1 1");
      add(progressBar, "cell 1 2, span 3, grow");
    }
  }

  class MessageItemView
    extends MigPane
  {
    MessageItemView(Node graphicNode, StringExpression titleExpression, StringExpression textExpression,
        int durationInSeconds)
    {
      super("wrap3", "[][grow]", "");

      Timeline timeline;
      Label titleLabel;
      Label messageLabel;

      timeline = new Timeline(new KeyFrame(Duration.seconds(0), new KeyValue(opacityProperty(), 0.1)),
          new KeyFrame(Duration.seconds(2), new KeyValue(opacityProperty(), 1.0)),
          new KeyFrame(Duration.seconds(durationInSeconds - 2), new KeyValue(opacityProperty(), 1.0)),
          new KeyFrame(Duration.seconds(durationInSeconds), new KeyValue(opacityProperty(), 0.1)));
      timeline.setOnFinished((_) -> m_view.getChildren().remove(MessageItemView.this));
      timeline.play();

      getStyleClass().add("notification-item");

      titleLabel = new Label();
      titleLabel.textProperty().bind(titleExpression);
      titleLabel.getStyleClass().add("title");
      //titleLabel.setStyle("-fx-font-weight: bold");

      messageLabel = new Label();
      messageLabel.getStyleClass().add("message");
      messageLabel.textProperty().bind(textExpression);

      add(graphicNode, "cell 0 0, span 1 2, aligny center");
      add(titleLabel, "cell 1 0");
      add(messageLabel, "cell 1 1");
    }
  }
}