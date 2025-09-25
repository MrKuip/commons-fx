package org.kku.iconify.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class TestModule
{
  private static final Pattern MODULE_INFO_CLASS_MRJAR_PATH = Pattern
      .compile("META-INF/versions/\\d+/module-info.class");
  private static final String MODULE_INFO_CLASS_FILE = "module-info.class";
  private static final String MULTI_RELEASE_ATTRIBUTE = "Multi-Release";
  private static final String AUTOMATIC_MODULE_NAME_ATTRIBUTE = "Automatic-Module-Name";

  public TestModule()
  {
  }

  private void start()
  {
    String name;

    name = "/mnt/MrCubesLocal/kees/projecten/own/Iconify4J/Iconify4J/lib/jsvg-2.0.1-SNAPSHOT.jar";
    System.out.println(name + " = " + isModuleJar(new File(name)));
    name = "/mnt/MrCubesLocal/kees/projecten/own/Iconify4J/Iconify4J/lib/jsvg-javafx-2.0.1-SNAPSHOT.jar";
    System.out.println(name + " = " + isModuleJar(new File(name)));
  }

  private static boolean isModuleJar(File jarFile)
  {
    try (JarInputStream jarStream = new JarInputStream(new FileInputStream(jarFile)))
    {
      if (containsAutomaticModuleName(jarStream))
      {
        return true;
      }
      boolean isMultiReleaseJar = containsMultiReleaseJarEntry(jarStream);
      ZipEntry next = jarStream.getNextEntry();
      while (next != null)
      {
        if (MODULE_INFO_CLASS_FILE.equals(next.getName()))
        {
          return true;
        }
        if (isMultiReleaseJar && MODULE_INFO_CLASS_MRJAR_PATH.matcher(next.getName()).matches())
        {
          return true;
        }
        next = jarStream.getNextEntry();
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    return false;
  }

  private static boolean containsMultiReleaseJarEntry(JarInputStream jarStream)
  {
    Manifest manifest = jarStream.getManifest();
    return manifest != null && Boolean.parseBoolean(manifest.getMainAttributes().getValue(MULTI_RELEASE_ATTRIBUTE));
  }

  private static boolean containsAutomaticModuleName(JarInputStream jarStream)
  {
    return getAutomaticModuleName(jarStream.getManifest()) != null;
  }

  private static String getAutomaticModuleName(Manifest manifest)
  {
    if (manifest == null)
    {
      return null;
    }
    return manifest.getMainAttributes().getValue(AUTOMATIC_MODULE_NAME_ATTRIBUTE);
  }

  public static void main(String[] args)
  {
    new TestModule().start();
  }
}
