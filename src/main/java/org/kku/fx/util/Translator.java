package org.kku.fx.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import org.kku.common.conf.Language;
import org.kku.common.util.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Translator
{
  // Singleton
  private static Translator m_instance = new Translator();
  private static boolean debugUntranslatedTexts = true;

  private Map<String, String> m_translationByIdMap = new HashMap<>();
  private Map<String, StringProperty> m_translationPropertyByIdMap = new HashMap<>();
  private ObjectProperty<Language> m_languageProperty = new SimpleObjectProperty<>();

  private String bundleName = "translations/messages";

  private Translator()
  {
    m_languageProperty.addListener((o, oldValue, newValue) -> changeLanguage(newValue));
    m_languageProperty.bind(AppPreferences.languagePreference.property());
  }

  private void changeLanguage(Language language)
  {
    Locale.setDefault(language.getLocale());
    reload();
  }

  public static StringProperty translatedTextProperty(String text)
  {
    StringProperty stringProperty;

    stringProperty = m_instance.m_translationPropertyByIdMap.computeIfAbsent(text, (k) -> new SimpleStringProperty());
    stringProperty.set(getTranslatedText(text));

    return stringProperty;
  }

  public static String getTranslatedText(String text)
  {
    String translatedText;
    String resourceKey;

    if (StringUtils.isEmpty(text))
    {
      return text;
    }

    resourceKey = toResourceKey(text);

    translatedText = m_instance.m_translationByIdMap.get(resourceKey);
    if (StringUtils.isEmpty(translatedText))
    {
      translatedText = text;
      debugUntranslatedText(translatedText);
    }
    return translatedText;
  }

  private static void debugUntranslatedText(String unTranslatedText)
  {
    if (debugUntranslatedTexts)
    {
      try
      {
        Path path;

        path = Paths.get("untranslated.txt");
        if (Files.notExists(path) || Files.readAllLines(Paths.get("untranslated.txt")).stream()
            .noneMatch(txt -> Objects.equals(txt, unTranslatedText)))
        {
          Files.write(path, Collections.singletonList(unTranslatedText), StandardOpenOption.APPEND,
              StandardOpenOption.CREATE);
        }
      }
      catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private void reload()
  {
    m_translationByIdMap.clear();

    // First choice is translations from the current language setting.
    reload(m_languageProperty.get().getLocale());
    // Second choice is the translations in english.
    reload(Locale.ENGLISH);
    // Last choice is the untranslated key (as programmed in the software)

    m_translationPropertyByIdMap.entrySet().forEach(entry -> {
      entry.getValue().set(getTranslatedText(entry.getKey()));
    });
  }

  private void reload(Locale locale)
  {
    ResourceBundle bundle;

    bundle = ResourceBundle.getBundle(bundleName, locale);
    if (bundle != null)
    {
      bundle.keySet().forEach(key -> {
        String resourceKey;
        String value;

        resourceKey = toResourceKey(key);
        value = bundle.getString(key);

        m_translationByIdMap.putIfAbsent(resourceKey, value);
      });
    }
  }

  private static String toResourceKey(String text)
  {
    if (text == null)
    {
      return "";
    }

    return text.toLowerCase().replace(' ', '-').replace('\n', '-');
  }

}
