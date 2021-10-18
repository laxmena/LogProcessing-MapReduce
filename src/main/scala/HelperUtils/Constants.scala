package HelperUtils

import HelperUtils.RegExUtil.config

import scala.util.matching.Regex

/**
 * Encompasses all the constant values used across the project.
 */
class Constants
object Constants {
  val config = ObtainConfigReference("LogProcessing") match {
    case Some(value) => value.getConfig("LogProcessing")
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }
  val userConfig = ObtainConfigReference("userInput") match {
    case Some(value) => value.getConfig("userInput")
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  val LOG_FREQUENCY_MR = "log-frequency"
  val MOST_ERROR_MR = "most-error"
  val LONGEST_PATTERN_REGEX_MR = "longest-regex"
  val LOG_FREQUENCY_DIST_MR = "log-freq-dist"

  val TIMESTAMP = "timestamp_group"
  val LEVEL = "level_group"
  val CONTEXT = "context_group"
  val CLASSNAME = "className_group"
  val LOGSTR = "logString_group"
  val UNKNOWN = "unknown"
  val HOUR_FROM_TIMESTAMP = "hour_group"
  val MINUTE_FROM_TIMESTAMP = "minute_group"
  val SECONDS_FROM_TIMESTAMP = "seconds_group"
  val DEFAULT = "default_group"
  val DATE_GROUP = "date_group"
  val FILE_NAME_PATTERN = "file_name_pattern"
  
  val LOG_REGEX_PATTERN = "logRegexPattern"
  val HOUR_WINDOW_PATTRN = "hourWindowPattern"
  val TIMESTAMP_PATTERN = "timeStampPattern"
  val logPattern: Regex = config.getString(LOG_REGEX_PATTERN).r
  val hourWindowPattern: Regex = config.getString(HOUR_WINDOW_PATTRN).r
  val timeStampPattern: Regex = config.getString(TIMESTAMP_PATTERN).r

  val PATTERN_CONF_PREFIX = "pattern"
  val TIMEINTERVAL = "timeInterval"
  val LOGSEARCH = "logSearch"
  val USER_INPUT = "userInput"
  val PATTERN = "pattern"
  val LOG_PROCESSING = "LogProcessing"
  val ERROR = "ERROR"
  val WARN = "WARN"
  val INFO = "INFO"
  val DEBUG = "DEBUG"
  val DEFAULT_PATTERN = ".*"
}
