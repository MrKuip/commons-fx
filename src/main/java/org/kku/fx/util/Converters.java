package org.kku.fx.util;

import org.kku.fx.ui.util.ColorPalette;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Converters
  extends org.kku.common.util.Converters
{
  public static Converter<Color> getColorConverter()
  {
    return new Converter<Color>(Color::web, ColorPalette::toHexString);
  }

  public static Converter<Font> getFontConverter()
  {
    return new Converter<Font>(fontName -> null, font -> font.getName() + "-" + font.getSize());
  }

}
