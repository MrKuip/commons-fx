package org.kku.iconify.main;

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class TestList
{

  public static void main(String[] args)
  {
    // Create an ObservableList
    ObservableSet<String> fruits = FXCollections.observableSet();

    // Add a listener to the list
    fruits.addListener(new SetChangeListener<String>()
    {
      @Override
      public void onChanged(Change<? extends String> c)
      {
        // The 'c' object represents a single change event
        System.out.println("Change event detected!");
      }
    });

    // Add multiple elements using addAll(), which will trigger a single change event
    fruits.addAll(Arrays.asList("Apple", "Banana", "Cherry"));

    // Adding a single element, which also triggers a single change event
    fruits.add("Date");
  }
}