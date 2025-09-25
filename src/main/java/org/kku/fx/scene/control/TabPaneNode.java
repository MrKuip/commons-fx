package org.kku.fx.scene.control;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * A TabPane that will only show tabs when there is more than 1 tab. Otherwise the tabs are not visible.
 */
public class TabPaneNode
  extends BorderPane
{
  private TabInfo mi_mainTabInfo;
  private Tab mi_mainTab;
  private TabPane mi_tabbedPane;

  private class TabInfo
  {
    private final String mi_id;
    private final Node mi_node;
    private final boolean mi_closeable;

    private TabInfo(String id, Node node, boolean closeable)
    {
      mi_id = id;
      mi_node = node;
      mi_closeable = closeable;
    }

    String id()
    {
      return mi_id;
    }

    Node node()
    {
      return mi_node;
    }

    boolean closeable()
    {
      return mi_closeable;
    }
  }

  public TabPaneNode(Node mainNode)
  {
    mi_mainTabInfo = new TabInfo("Icons", mainNode, false);
    mi_tabbedPane = new TabPane();
    mi_mainTab = addTab(mi_mainTabInfo);
  }

  public void addTab(String id, Node node, boolean closeable)
  {
    addTab(new TabInfo(id, node, closeable));
  }

  private Tab addTab(TabInfo tabInfo)
  {
    Tab tab;

    tab = new Tab();
    tab.setUserData(tabInfo);
    tab.setText(tabInfo.id());
    tab.setClosable(tabInfo.closeable());
    tab.setContent(tabInfo.node());
    tab.setOnClosed((a) -> {
      update();
    });

    mi_tabbedPane.getTabs().add(tab);
    mi_tabbedPane.getSelectionModel().select(tab);

    update();

    return tab;
  }

  private void update()
  {
    Node mainNode;

    mainNode = mi_mainTabInfo.node();
    // Only main node is in the list
    if (mi_tabbedPane.getTabs().size() <= 1)
    {
      if (getCenter() != mi_mainTabInfo.node())
      {
        setCenter(mainNode);
        if (mi_mainTab != null)
        {
          mi_mainTab.setContent(null);
        }
      }
    }
    else
    {
      if (getCenter() != mi_tabbedPane)
      {
        mi_mainTab.setContent(mi_mainTabInfo.node());
        setCenter(mi_tabbedPane);
      }
    }
  }
}
