package com.devbliss.gpullr.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * backend wide constants. very wide.
 */

public class Constants {

  public static final String KEY_DTO_ERROR_FORBIDDEN = "FORBIDDEN";

  public static final String KEY_DTO_ERROR_NOT_FOUND = "NOT_FOUND";

  public static final String KEY_DTO_ERROR_INTERNAL = "INTERNAL";

  public static final String KEY_DTO_ERROR_BAD_REQUEST = "BAD_REQUEST";

  public static final String QUALIFIER_TASK_SCHEDULER = "prtTaskScheduler";

  public static final List<String> ALLOWED_LANGUAGES = Arrays.asList(
      Locale.GERMAN.getLanguage(),
      Locale.ENGLISH.getLanguage());
}
