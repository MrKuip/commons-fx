module org.kku.fx
{
  requires transitive javafx.controls;
  requires transitive javafx.graphics;

  requires transitive org.kku.iconify4j;
  requires transitive org.kku.common;
  requires transitive com.miglayout.javafx;
  requires java.xml;
  requires java.desktop;

  exports org.kku.fx.util;
  exports org.kku.fx.ui.util;
  exports org.kku.fx.scene.control;
  exports org.kku.fx.ui.dialog;

  exports org.kku.fx.iconify.main to javafx.graphics;
}
