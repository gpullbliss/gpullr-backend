package com.devbliss.gpullr.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * backend wide constants. very wide.
 */

public class Constants {

  public static String KEY_DTO_ERROR_FORBIDDEN = "FORBIDDEN";

  public static String KEY_DTO_ERROR_NOT_FOUND = "NOT_FOUND";

  public static String KEY_DTO_ERROR_INTERNAL = "INTERNAL";

  public static String KEY_DTO_ERROR_BAD_REQUEST = "BAD_REQUEST";

  public static final Map<String, String> ALLOWED_LANGUAGES = new HashMap<>();

  static {
    ALLOWED_LANGUAGES.put(Locale.ENGLISH.getLanguage(), "English");
    ALLOWED_LANGUAGES.put(Locale.GERMAN.getLanguage(), "Deutsch");
    ALLOWED_LANGUAGES.put(Locale.ITALIAN.getLanguage(), "Italiano");
  }
}
