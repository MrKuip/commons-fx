module org.kku.fx
{
  requires transitive javafx.controls;
  requires transitive javafx.graphics;

  requires transitive org.kku.fonticons;
  requires transitive org.kku.common;

  exports org.kku.fx.util;
  exports org.kku.fx.ui.util;
  exports org.kku.fx.scene.control;
}
