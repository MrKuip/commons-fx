package org.kku.fx.iconify.main;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.kku.iconify.ui.FxIcon;
import org.kku.iconify.ui.FxIcons;
import org.tbee.javafx.scene.layout.MigPane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class SVGEditor
{
  static final private int ICON_SIZE = 640;
  private FxIcon m_icon;
  private Node m_editor;

  public SVGEditor(FxIcon fxIcon)
  {
    m_icon = FxIcons.create(fxIcon).size(ICON_SIZE);
    m_editor = createEditor();
  }

  public Node getEditor()
  {
    return m_editor;
  }

  private Node createEditor()
  {
    MigPane pane;
    Button runButton;
    String svgText;
    TextArea svgTextArea;
    VBox svgImageBox;

    svgImageBox = new VBox();

    svgText = getPrettyPrint(m_icon.getParsedSVGText());

    svgTextArea = new TextArea();
    svgTextArea.setStyle("-fx-font-family: monospace;");
    svgTextArea.setText(svgText);

    runButton = new Button("Run");
    runButton.setOnAction((a) -> {
      m_icon.setParsedSVG(svgTextArea.getText());
      svgImageBox.getChildren().setAll(m_icon.getNode());
    });

    svgImageBox.getChildren().add(m_icon.getNode());

    pane = new MigPane("", "[grow][pref]", "[][grow]");
    pane.add(runButton, "span, wrap");
    pane.add(svgTextArea, "grow");
    pane.add(svgImageBox, "");

    return pane;
  }

  private String getPrettyPrint(String xmlText)
  {
    try
    {
      TransformerFactory transformerFactory;
      Transformer transformer;
      StringReader reader;
      StringWriter writer;

      transformerFactory = TransformerFactory.newInstance();
      transformer = transformerFactory.newTransformer();

      // Set output properties for pretty-printing
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      reader = new StringReader(xmlText);
      writer = new StringWriter(xmlText.length() + 1000);
      transformer.transform(new StreamSource(reader), new StreamResult(writer));

      return writer.toString();
    }
    catch (Exception ex)
    {
      return xmlText;
    }
  }
}
