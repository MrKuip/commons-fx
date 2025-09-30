package org.kku.fx.iconify.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.kku.common.util.Project;
import org.kku.fx.scene.control.Filter;
import org.kku.fx.scene.control.FilterPane;
import org.kku.fx.scene.control.TabPaneNode;
import org.kku.iconify.data.IconSetData;
import org.kku.iconify.data.IconSetData.IconData;
import org.kku.iconify.data.IconSetData.IconData.Category;
import org.kku.iconify.data.IconSets;
import org.kku.iconify.ui.FxIcon;
import org.kku.iconify.ui.FxIcon.IconSize;
import org.tbee.javafx.scene.layout.MigPane;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.stage.Stage;

public class IconifyViewer
  extends Application
{
  private final static int NUMBER_OF_COLUMNS = 10;
  private TabPaneNode m_tabbedPaneNode;
  private TableView<IconRow> m_iconTableView;
  private TableView<Icon> m_iconView;
  private final ObservableList<Icon> m_iconList = FXCollections.observableArrayList();
  private FilteredList<Icon> m_filteredIconList = new FilteredList<Icon>(m_iconList);
  private MultipleSelectModel<IconSetData> m_selectedIconSets;
  private MultipleSelectModel<String> m_selectedLicenses;
  private MultipleSelectModel<String> m_selectedCategories;
  private MultipleSelectModel<Category> m_selectedCategories2;
  private ObservableMap<FilterType, Predicate<Icon>> m_filterByIdMap = FXCollections.observableHashMap();

  static
  {
    Project.init("IconifyViewer");
  }

  private enum FilterType
  {
    TEXT,
    ICONSET,
    LICENSE,
    CATEGORY,
    CATEGORY2;
  }

  @Override
  public void start(Stage stage) throws IOException
  {
    MigPane pane;

    m_iconList.setAll(getIconList());

    m_iconTableView = getIconTableView();
    m_iconView = getIconView();

    pane = new MigPane("", "[grow][]", "[][][][grow]");
    pane.add(getFilterPane(), "cell 0 0 2 1, grow");
    pane.add(m_iconTableView, "cell 0 1 1 3, grow");
    pane.add(getButtonPane(), "cell 1 1, grow");
    pane.add(getIconSetInfoNode(), "cell 1 2, width 30%, grow");
    pane.add(m_iconView, "cell 1 3, grow, width pref");

    m_iconView.getSelectionModel().select(0);

    m_tabbedPaneNode = new TabPaneNode(pane);

    Scene scene = new Scene(m_tabbedPaneNode, 1200, 600, false, SceneAntialiasing.DISABLED);
    stage.setTitle("Iconify icons");
    stage.setScene(scene);
    stage.show();
  }

  private Node getFilterPane()
  {
    MigPane pane;
    Node filterLabel;
    Node menuButton;
    TextField filterField;

    filterLabel = new FxIcon("mdi-filter").size(IconSize.MEDIUM).getNode();

    filterField = new TextField();
    filterField.setPromptText("Filter by name...");
    filterField.textProperty().addListener((obs, oldValue, newValue) -> {
      Icon selectedIcon;
      String[] filterValues;

      filterValues = newValue.toLowerCase().split(" ");

      selectedIcon = m_iconView.getSelectionModel().getSelectedItem();
      m_filterByIdMap.put(FilterType.TEXT, data -> {
        if (filterValues != null && filterValues.length > 0)
        {
          for (String filter : filterValues)
          {
            if (!data.getIdLowerCase().contains(filter))
            {
              boolean found = false;
              for (Category categorie : data.getIconData().getCategoryList())
              {
                if (categorie.getCategoryLowerCase().contains(filter))
                {
                  found = true;
                  break;
                }
              }
              return found;
            }
          }
        }
        return true;
      });
      m_iconView.getSelectionModel().select(selectedIcon);
    });

    m_selectedIconSets = new MultipleSelectModel<>(getIconSetDataList());
    m_selectedIconSets.addListener(c -> {
      m_filterByIdMap.put(FilterType.ICONSET,
          icon -> m_selectedIconSets.getSelectedItems().contains(icon.getIconData().getIconSetData()));
    });

    m_selectedLicenses = new MultipleSelectModel<>(getIconSetDataList().stream().map(IconSetData::getLicenseName)
        .distinct().sorted().collect(Collectors.toList()));
    m_selectedLicenses.addListener(c -> {
      m_filterByIdMap.put(FilterType.LICENSE,
          icon -> m_selectedLicenses.getSelectedItems().contains(icon.getLicense()));
    });

    m_selectedCategories = new MultipleSelectModel<>(
        getIconSetDataList().stream().map(IconSetData::getCategory).distinct().sorted().collect(Collectors.toList()));
    m_selectedCategories.addListener(c -> {
      m_filterByIdMap.put(FilterType.CATEGORY,
          icon -> m_selectedCategories.getSelectedItems().contains(icon.getIconData().getIconSetData().getCategory()));
    });

    m_selectedCategories2 = new MultipleSelectModel<>(
        getIconList().stream().map(icon -> icon.getIconData().getCategoryList()).flatMap(Collection::stream).distinct()
            .sorted().collect(Collectors.toList()));
    m_selectedCategories2.addListener(c -> {
      m_filterByIdMap.put(FilterType.CATEGORY2, icon -> !Collections.disjoint(icon.getIconData().getCategoryList(),
          m_selectedCategories2.getSelectedItems()));
    });

    menuButton = new Button(null, new FxIcon("mdi-menu").size(IconSize.MEDIUM).getNode());

    FilterPane<Icon> filterPane = new FilterPane<>(filter -> setFilter(filter));
    filterPane.addFilter(new Filter<>("Search ", "in", "mdi", icon -> icon.getName().contains("mdi")), false);

    pane = new MigPane("", "[][grow, fill][][][]", "[baseline]");
    pane.add(filterLabel, "aligny center");
    pane.add(filterField, "");
    pane.add(m_selectedIconSets.createFilterButton("Icon sets"));
    pane.add(m_selectedLicenses.createFilterButton("License"));
    pane.add(m_selectedCategories.createFilterButton("Category"));
    pane.add(m_selectedCategories2.createFilterButton("Category2"));
    pane.add(menuButton, "wrap");
    pane.add(filterPane.getNode(), "span");

    return pane;
  }

  public void setFilter(Predicate<Icon> filter)
  {
  }

  public class MultipleSelectModel<T>
  {
    private final List<T> mi_itemList;
    private final ObservableList<T> mi_selectedItemList = FXCollections.observableList(new ArrayList<>());

    public MultipleSelectModel(List<T> itemList)
    {
      mi_itemList = itemList;
      mi_selectedItemList.addAll(mi_itemList);
    }

    public ObservableList<T> getSelectedItems()
    {
      return mi_selectedItemList;
    }

    public void addListener(ListChangeListener<? super T> listener)
    {
      getSelectedItems().addListener(listener);
    }

    public boolean isSelected(T item)
    {
      return mi_selectedItemList.contains(item);
    }

    public void select(T item, boolean selected)
    {
      if (selected)
      {
        mi_selectedItemList.add(item);
      }
      else
      {
        mi_selectedItemList.remove(item);
      }
    }

    public MenuButton createFilterButton(String text)
    {
      CustomMenuItem menuItem;
      MenuButton menuButton;
      List<CheckBox> checkBoxList;

      checkBoxList = new ArrayList<>();

      menuButton = new MenuButton(text);
      if (true)
      {
        Button button;
        MigPane box;

        box = new MigPane("gap 1 0 0 0", "[][]push[]", "[][][]");
        box.setStyle("-fx-background-color: lightgray;");

        button = new Button("", new FxIcon("mdi-checkboxes-marked").size(IconSize.SMALL).getNode());
        button.setStyle("-fx-background-radius: 0;" + // Rounded corners
            "-fx-border-radius: 0;" + // Border radius to match
            "");
        button.setTooltip(new Tooltip("Select all"));
        button.setAlignment(Pos.BASELINE_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction((ae) -> {
          checkBoxList.forEach(cb -> cb.setSelected(true));
          mi_selectedItemList.setAll(mi_itemList);
        });
        box.add(button, "grow");

        button = new Button("", new FxIcon("mdi-checkboxes-blank-outline").size(IconSize.SMALL).getNode());
        button.setStyle("-fx-background-radius: 0;" + // Rounded corners
            "-fx-border-radius: 0;" + // Border radius to match
            "");
        button.setTooltip(new Tooltip("Deselect all"));
        button.setAlignment(Pos.BASELINE_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction((ae) -> {
          checkBoxList.forEach(cb -> cb.setSelected(false));
          mi_selectedItemList.clear();
        });
        box.add(button, "grow");

        button = new Button("", new FxIcon("mdi-cancel-box").size(IconSize.SMALL).getNode());
        button.setStyle("-fx-background-radius: 0;" + // Rounded corners
            "-fx-border-radius: 0;" + // Border radius to match
            "");
        button.setOnAction((me) -> {
          menuButton.hide();
        });
        box.add(button);

        menuItem = new SeparatorMenuItem();
        menuItem.setContent(box);
        menuItem.setHideOnClick(false);
        menuButton.getItems().add(menuItem);
        menuButton.getItems().add(new SeparatorMenuItem());
      }

      for (T item : mi_itemList)
      {
        CheckBox checkBox;

        checkBox = new CheckBox(item.toString());
        checkBox.setSelected(true);
        checkBoxList.add(checkBox);
        checkBox.setOnAction((ae) -> {
          select(item, checkBox.isSelected());
        });

        menuItem = new CustomMenuItem(checkBox);
        menuItem.setHideOnClick(false);
        menuButton.getItems().add(menuItem);
      }

      return menuButton;
    }

  }

  private Node getButtonPane()
  {
    MigPane pane;

    pane = new MigPane("", "[]push[]push[]push[]push[]", "[]");

    for (IconSize iconSize : FxIcon.IconSize.values())
    {
      Button button;

      button = new Button();
      m_iconView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selectedIcon) -> {
        if (selectedIcon != null)
        {
          Node iconLabel;

          iconLabel = selectedIcon.getFxIcon().size(iconSize).getNode();
          button.graphicProperty().set(iconLabel);
        }
      });

      pane.add(button, "");
    }

    return pane;
  }

  private Node getIconSetInfoNode()
  {
    MigPane pane;
    Label iconSetNameText;
    Label prefixText;
    Label versionText;
    Label authorText;
    Hyperlink licenseURLHyperlink;
    Hyperlink projectURLHyperlink;
    Label numberOfIconsText;
    Label glyphNameText;
    Button editSVGButton;

    pane = new MigPane("fill, wrap 2", "[shrink 0][grow, push]");

    iconSetNameText = new Label();
    pane.add(new Label("Icon set name"));
    pane.add(iconSetNameText);

    prefixText = new Label();
    pane.add(new Label("Prefix"));
    pane.add(prefixText);

    versionText = new Label();
    pane.add(new Label("Version"));
    pane.add(versionText);

    authorText = new Label();
    pane.add(new Label("Author"));
    pane.add(authorText);

    projectURLHyperlink = new Hyperlink();
    projectURLHyperlink.setFocusTraversable(false);
    pane.add(new Label("Website"));
    pane.add(projectURLHyperlink);

    licenseURLHyperlink = new Hyperlink();
    licenseURLHyperlink.setFocusTraversable(false);
    pane.add(new Label("License url"));
    pane.add(licenseURLHyperlink);

    numberOfIconsText = new Label();
    pane.add(new Label("number of icons"));
    pane.add(numberOfIconsText);

    glyphNameText = new Label();
    pane.add(new Label("Glyph name"));
    pane.add(glyphNameText);

    editSVGButton = new Button("Edit svg");
    pane.add(new Label("SVG icon"));
    pane.add(editSVGButton);

    m_iconView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      FxIcon fxIcon;
      IconData iconData;
      IconSetData iconSetData;

      fxIcon = newValue == null ? null : newValue.getFxIcon();
      iconData = newValue == null ? null : newValue.getIconData();
      iconSetData = iconData == null ? null : iconData.getIconSetData();

      iconSetNameText.setText(iconSetData != null ? newValue.getIconSetName() : "");
      prefixText.setText(iconSetData != null ? newValue.getPrefix() : "");
      versionText.setText(iconSetData != null ? iconSetData.getVersion() : "");
      authorText.setText(iconSetData != null ? iconSetData.getAuthor() : "");
      projectURLHyperlink.setText(iconSetData != null ? iconSetData.getProjectURL() : "");
      projectURLHyperlink.setOnAction((ae) -> { browse(projectURLHyperlink.getText()); });
      licenseURLHyperlink.setText(iconSetData != null ? iconSetData.getLicenseName() : "");
      licenseURLHyperlink.setOnAction((ae) -> { browse(iconSetData.getLicenseURL()); });
      numberOfIconsText.setText(iconSetData != null ? "" + iconSetData.getNumberOfIcons() : "");

      glyphNameText.setText(iconSetData != null ? newValue.getName() : "");
      editSVGButton.setOnAction((a) -> {
        SVGEditor svgEditor;
        svgEditor = new SVGEditor(fxIcon);
        m_tabbedPaneNode.addTab(iconData.getId(), svgEditor.getEditor(), true);
      });
    });

    return pane;
  }

  /**
   * Launches default browser to display a uri.
   * 
   * @param url
   */
  private void browse(String url)
  {
    // This MUST happen in a separate Thread otherwise the JavaFX Thread will be blocked.
    new Thread(() -> {
      try
      {
        Desktop.getDesktop().browse(new URI(url));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }).start();
  }

  @SuppressWarnings("rawtypes")
  private TableView<IconRow> getIconTableView()
  {
    TableView<IconRow> tableView;
    TableColumn<IconRow, Node> column;

    tableView = new TableView<>();
    tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    tableView.getSelectionModel().setCellSelectionEnabled(true);
    tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    tableView.getSelectionModel().getSelectedCells().addListener(((ListChangeListener<TablePosition>) change -> {
      tableView.getSelectionModel().getSelectedCells().forEach(selectedCell -> {
        int rowIndex;

        if (selectedCell.getColumn() > 0)
        {
          rowIndex = selectedCell.getRow() * NUMBER_OF_COLUMNS + selectedCell.getColumn() - 1;
          m_iconView.getSelectionModel().select(rowIndex);
          if (!isRowVisible(m_iconView, rowIndex))
          {
            m_iconView.scrollTo(rowIndex);
          }
        }
      });
    }));

    m_filterByIdMap.addListener((MapChangeListener<FilterType, Predicate<Icon>>) change -> {
      m_filteredIconList.setPredicate(m_filterByIdMap.values().stream().reduce(x -> true, Predicate::and));
    });
    m_filteredIconList.addListener((ListChangeListener<Icon>) change -> {
      m_iconTableView.setItems(getIconRowList());
    });

    tableView.setMaxWidth(Double.MAX_VALUE);

    addRankColumn(tableView, "Row", NUMBER_OF_COLUMNS);

    for (int columnIndex = 0; columnIndex < NUMBER_OF_COLUMNS; columnIndex++)
    {
      final int colIndex;

      colIndex = columnIndex;
      column = new TableColumn<>(String.valueOf(columnIndex + 1));
      column.setCellValueFactory(cb -> {
        List<Icon> list = cb.getValue().getIconList();
        if (colIndex < 0 || colIndex >= list.size())
        {
          return null;
        }

        return new SimpleObjectProperty<>(list.get(colIndex).getIconLabel());
      });
      tableView.getColumns().add(column);
    }

    tableView.setItems(getIconRowList());

    return tableView;
  }

  private TableView<Icon> getIconView()
  {
    TableView<Icon> tableView;
    TableColumn<Icon, String> nameColumn;
    TableColumn<Icon, Node> iconLabelColumn;

    tableView = new TableView<>();
    tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      int index;
      int rowIndex;
      int columnIndex;
      TableColumn<IconRow, ?> column;

      index = m_iconView.getSelectionModel().getSelectedIndex();
      if (index > -1)
      {
        rowIndex = index / NUMBER_OF_COLUMNS;
        columnIndex = index % NUMBER_OF_COLUMNS + 1;

        column = m_iconTableView.getColumns().get(columnIndex);

        m_iconTableView.getSelectionModel().select(rowIndex, column);
        if (!isRowVisible(m_iconTableView, rowIndex))
        {
          m_iconTableView.scrollTo(rowIndex);
        }
      }
    });
    tableView.setMaxWidth(Double.MAX_VALUE);
    nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(cb -> new SimpleStringProperty(cb.getValue().getId()));
    tableView.getColumns().add(nameColumn);
    iconLabelColumn = new TableColumn<>("Icon");
    iconLabelColumn.setCellValueFactory(cb -> new SimpleObjectProperty<>(cb.getValue().getIconLabel()));
    tableView.getColumns().add(iconLabelColumn);

    tableView.setItems(m_filteredIconList);

    return tableView;
  }

  private boolean isRowVisible(TableView<?> tableView, int rowIndex)
  {
    Object o;
    TableViewSkin<?> ts;
    VirtualFlow<?> vf;
    int firstIndex;
    int lastIndex;
    IndexedCell<?> firstCell;
    IndexedCell<?> lastCell;

    o = tableView.getSkin();
    if (!(o instanceof TableViewSkin<?>))
    {
      return false;
    }
    ts = (TableViewSkin<?>) o;

    o = ts.getChildren().stream().filter(node -> node instanceof VirtualFlow).findFirst().orElse(null);
    if (o == null || !(o instanceof VirtualFlow<?>))
    {
      return false;
    }
    vf = (VirtualFlow<?>) o;

    firstCell = vf.getFirstVisibleCell();
    lastCell = vf.getLastVisibleCell();

    if (firstCell != null && lastCell != null)
    {
      firstIndex = firstCell.getIndex();
      lastIndex = lastCell.getIndex();

      return rowIndex >= firstIndex && rowIndex <= lastIndex;
    }

    return false;
  }

  private ObservableList<IconRow> getIconRowList()
  {
    IconRow iconRow;
    List<IconRow> iconRowList;

    iconRowList = new ArrayList<>();
    iconRow = null;

    for (Icon icon : m_filteredIconList)
    {
      if (iconRow == null)
      {
        iconRow = new IconRow();
        iconRowList.add(iconRow);
      }

      iconRow.add(icon);
      if (iconRow.getIconList().size() >= NUMBER_OF_COLUMNS)
      {
        iconRow = null;
      }
    }

    return FXCollections.observableArrayList(iconRowList);
  }

  private List<IconSetData> getIconSetDataList()
  {
    return IconSets.get().getIconSetDataList();
  }

  private List<Icon> getIconList()
  {
    return getIconSetDataList().stream()
        .map(isd -> isd.getIconDataList().stream().sorted().collect(Collectors.toList())).flatMap(Collection::stream)
        .map(Icon::new).collect(Collectors.toList());
  }

  class IconRow
  {
    private List<Icon> m_iconList = new ArrayList<>();

    public void add(Icon icon)
    {
      m_iconList.add(icon);
    }

    public List<Icon> getIconList()
    {
      return m_iconList;
    }
  }

  class Icon
      implements Comparable<Icon>
  {
    private final IconData mi_iconData;
    private final String mi_idLowerCase;
    private final FxIcon mi_fxIcon;

    Icon(IconData iconData)
    {
      mi_iconData = iconData;
      mi_idLowerCase = getId().toLowerCase();
      mi_fxIcon = new FxIcon(iconData).size(IconSize.HUGE);
    }

    public IconData getIconData()
    {
      return mi_iconData;
    }

    public String getLicense()
    {
      return mi_iconData.getIconSetData().getLicenseName();
    }

    public String getId()
    {
      return mi_iconData.getId();
    }

    public String getName()
    {
      return mi_iconData.getName();
    }

    public FxIcon getFxIcon()
    {
      return mi_fxIcon;
    }

    public String getIconSetName()
    {
      return mi_iconData.getIconSetData().getName();
    }

    public String getIdLowerCase()
    {
      return mi_idLowerCase;
    }

    public Node getIconLabel()
    {
      return mi_fxIcon.getNode();
    }

    public String getPrefix()
    {
      return getIconData().getPrefix();
    }

    @Override
    public String toString()
    {
      return getIconData().getId();
    }

    @Override
    public int compareTo(Icon icon)
    {
      int result;

      result = getIconSetName().compareTo(icon.getIconSetName());
      if (result != 0)
      {
        return result;
      }

      return getName().compareTo(icon.getName());
    }
  }

  public <S> TableColumn<S, Number> addRankColumn(TableView<S> tableView, String columnName, int multiplier)
  {
    TableColumn<S, Number> rankColumn;
    RankColumnData rankColumnData;

    rankColumnData = new RankColumnData(tableView);

    rankColumn = new TableColumn<S, Number>(columnName);
    tableView.getColumns().add(rankColumn);
    rankColumn
        .setCellValueFactory(cb -> new SimpleIntegerProperty((rankColumnData.getRank(cb.getValue()) - 1) * multiplier));
    rankColumn.setCellFactory(cb -> new TableCell<S, Number>()
    {
      @Override
      protected void updateItem(Number item, boolean empty)
      {
        super.updateItem(item, empty);
        if (empty || item == null)
        {
          setText(null);
        }
        else
        {
          setText(item.toString());
          setStyle("-fx-font-weight: bold;");
        }
      }
    });
    return rankColumn;
  }

  private class RankColumnData
  {
    private final Map<Integer, Integer> m_lineNumberMap = new HashMap<>();

    private RankColumnData(TableView<?> tableView)
    {
      tableView.itemsProperty().addListener((obs, oldValue, newValue) -> {
        m_lineNumberMap.clear();
        for (int index = 0; index < newValue.size(); index++)
        {
          m_lineNumberMap.put(System.identityHashCode(newValue.get(index)), index + 1);
        }
      });
    }

    public Integer getRank(Object item)
    {
      return m_lineNumberMap.getOrDefault(System.identityHashCode(item), Integer.valueOf(0));
    }
  }

  public static void main(String args[])
  {
    launch(args);
  }
}
