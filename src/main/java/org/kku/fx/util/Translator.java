package org.kku.fx.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.kku.common.conf.Language;
import org.kku.common.util.AppPreferences;
import org.kku.common.util.Log;
import org.kku.common.util.ResourceLoader;
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

  private Translator()
  {
    m_languageProperty.addListener((_, _, newValue) -> changeLanguage(newValue));
    m_languageProperty.bind(FxProperty.property(AppPreferences.languagePreference));
  }

  private void changeLanguage(Language language)
  {
    Locale.setDefault(language.getLocale());
    reload();
  }

  public static StringProperty translatedTextProperty(String text)
  {
    StringProperty stringProperty;

    stringProperty = m_instance.m_translationPropertyByIdMap.computeIfAbsent(text, (_) -> new SimpleStringProperty());
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
        Log.log.error(e, "Problem saving untranslated texts");
      }
    }
  }

  private void reload()
  {
    m_translationByIdMap.clear();

    // First choice is translations from the current language setting.
    reload(m_languageProperty.get().getLocale());
    // Second choice is the translations in English.
    reload(Locale.ENGLISH);
    // Last choice is the untranslated key (as programmed in the software)

    m_translationPropertyByIdMap.entrySet().forEach(entry -> {
      entry.getValue().set(getTranslatedText(entry.getKey()));
    });
  }

  private void reload(Locale locale)
  {
    getBundleNameList(locale).forEach(bundleName -> {
      String resourceName;

      resourceName = "translations/messages" + bundleName + ".properties";
      Log.log.fine("try to load %s", resourceName);
      try
      {
        ResourceLoader.getInstance().getResources(resourceName).forEach(resource -> {
          try (InputStream is = resource.openStream())
          {
            Properties properties;
            Log.log.info("Translator: Load translations %s from %s", locale, resource);

            properties = new Properties();
            try
            {
              properties.load(is);
              properties.entrySet().forEach(entry -> {
                m_translationByIdMap.putIfAbsent(toResourceKey(entry.getKey().toString()), entry.getValue().toString());
              });
            }
            catch (IOException e)
            {
              Log.log.error("Failed to load properties from resource: " + resource);
            }
          }
          catch (IOException e)
          {
            Log.log.error("Failed to load resource: " + resource);
          }
        });
      }
      catch (IOException e)
      {
        Log.log.error("Failed to load resource: " + resourceName);
      }
    });

  }

  private List<String> getBundleNameList(Locale locale)
  {
    List<String> bundleNameList;

    bundleNameList = new ArrayList<>();
    if (!StringUtils.isEmpty(locale.getLanguage()))
    {
      if (!StringUtils.isEmpty(locale.getCountry()))
      {
        bundleNameList.add("_" + locale.getLanguage() + "_" + locale.getCountry());
      }
      bundleNameList.add("_" + locale.getLanguage());
    }
    bundleNameList.add("");

    return bundleNameList;
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
