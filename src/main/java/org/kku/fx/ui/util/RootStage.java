package org.kku.fx.ui.util;

import javafx.stage.Window;

public class RootStage 
{
  private static Window m_rootStage;
  
  static public void set(Window rootStage)
  {
    m_rootStage = rootStage;
  }

  static public Window get()
  {
    return m_rootStage;
  }
}
