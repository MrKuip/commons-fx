package org.kku.fx.ui.util;

import org.kku.common.util.AppProperties.AppProperty;
import org.kku.common.util.StringUtils;
import javafx.application.Platform;
import javafx.scene.control.TabPane;

public class FxSettingsUtil
{
  /**
   * Select the tab that was selected in a previous run of the app.
   * 
   * @param selectedIdProperty The property that persists the id of the selected
   *                           tab
   * @param tabPane            The tabPane
   */
  public static void initSelectedTabSetting(AppProperty<String> selectedIdProperty, TabPane tabPane)
  {
    // runLater because this code must run AFTER all the tab's have been created!
    Platform.runLater(() -> {
      // Select the tab that was previously selected (in a previous run of the jvm)
      if (!StringUtils.isEmpty(selectedIdProperty.get()))
      {
        tabPane.getTabs().stream().filter(t -> t.getId().equals(selectedIdProperty.get())).findFirst()
            .ifPresentOrElse(t -> {
              tabPane.getSelectionModel().select(t);
            }, () -> {
              tabPane.getSelectionModel().select(0);
            });
      }

      // Remember the selected tab in a setting.
      tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
        selectedIdProperty.set(newValue.getId());
      });
    });
  }
}
