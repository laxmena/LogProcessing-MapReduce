package HelperUtils

import HelperUtils.Constants.{DEFAULT, HOUR_FROM_TIMESTAMP, LEVEL, MINUTE_FROM_TIMESTAMP, SECONDS_FROM_TIMESTAMP, TIMESTAMP, UNKNOWN, hourWindowPattern, logPattern, timeStampPattern, LOGSTR}

import scala.collection.JavaConverters.*
import scala.util.matching.Regex
import org.apache.hadoop.io.{IntWritable, Text}

/**
 * RegExUtil: Collection of reusable methods dealing with Regular Expressions in this project.
 */
class RegExUtil
object RegExUtil {
  val logger = CreateLogger(classOf[RegExUtil])

  // Get LogProcessing Config object
  val config = ObtainConfigReference("LogProcessing") match {
    case Some(value) => value.getConfig("LogProcessing")
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }

  /**
   * Helper method to extract data for the requested key from Regex.Match option object.
   *
   * @param patternMatch Option[Regex.Match] obtained by running findFirstMatchIn method.
   * @param data value from Constants class that specifies a maps to config key.
   * @return String result of the extracted data from the Regex.Match object.(Default: Constants.UNKNOWN)
   */
  def getDataFromPattern(patternMatch: Option[Regex.Match], data: String): String = {
    val pattern = patternMatch match {
      case Some(ptr) => ptr
      case None => return UNKNOWN
    }
    pattern.group(config.getInt(data))
  }

  /**
   * Helper method to extract requested data from list of Regex.Match objects.
   * Returns List of extracted data. This method used to parse data from result of
   * findAllMatchIn regex operation.
   *
   * @param patternMatchList List[Regex.Match] result from findAllMatchIn regex operation.
   * @param data value from Constants file that maps to a config key.
   * @return List[String] List of Strings extracted from the patternMatchList objects.
   */
  def getDataListFromPattern(patternMatchList: List[Regex.Match], data: String): List[String] = {
    patternMatchList.map((pattern: Regex.Match) => {
      pattern.group(config.getInt(data))
    }).toList
  }

  /**
   * Extracts the Log Level from a String. Log Levels can be ERROR/INFO/DEBUG/WARN
   * If these Log levels are not present, then UNKNOWN is returned.
   *
   * @param inputString Log String
   * @return String Log Level of the input log string(ERROR/INFO/DEBUG/WARN).
   *         Default: Constants.UNKNOWN
   */
  def getLogLevelFromLogString(inputString: String): String = {
    val patternMatch = logPattern.findFirstMatchIn(inputString)
    getDataFromPattern(patternMatch, LEVEL)
  }

  /**
   * Get Log Level from hadoop.io.Text data type.
   *
   * @param inp Log String as Text.
   * @return String Log Level of the input log string(ERROR/INFO/DEBUG/WARN).
   *         Default: Constants.UNKNOWN
   */
  def getLogLevelFromText(inp: Text): String = {
    getLogLevelFromLogString(inp.toString)
  }

  /**
   * Extracts the TimeStamp from a Log String.
   *
   * @param inputString Log String
   * @return String TimeStamp that matches HH:MM:SS.sss pattern. Default: Constants.UNKNOWN
   */
  def getTimeStampFromLogString(inputString: String): String = {
    val patternMatch = logPattern.findFirstMatchIn(inputString)
    getDataFromPattern(patternMatch, TIMESTAMP)
  }

  /**
   * Get TimeStamp from hadoop.io.Text data type.
   *
   * @param inp Log String as Text.
   * @return String TimeStamp that matches HH:MM:SS.sss pattern. Default: Constants.UNKNOWN
   */
  def getTimeStampFromText(inp: Text): String = {
    getTimeStampFromLogString(inp.toString)
  }

  /**
   * Get Hour window from the input timestamp. It returns only the current hour,
   * without minutes and seconds.
   * @param timeStamp TimeStamp HH:MM:SS.sss
   * @return String (HH)
   */
  def getHourWindow(timeStamp: String): String = {
    val patternMatch = hourWindowPattern.findFirstMatchIn(timeStamp)
    getDataFromPattern(patternMatch, HOUR_FROM_TIMESTAMP)
  }

  /**
   * Given minutes as window size, it returns which time block
   * the current timestamp belongs to.
   *
   * Eg. If the time window is 5 minutes, and input timestamp is 12:13:12.334
   * This timestamp belongs to 12:10:00-12:15:00 interval.
   * So the method returns 12:10:00 as time block.
   *
   * @param timeStamp TimeStamp HH:MM:SS.sss
   * @param minutesWindow Integer that represents the size of the time block.
   * @return String TimeInterval (Pattern: HH:MM:00)
   */
  def getTimeWindow(timeStamp: String, minutesWindow: Int): String = {
    // Minimum window Size is 1 minute, and Maximum window size is 60
    val window = 60.min(minutesWindow.max(1))
    if(window == 60) {
      return s"${getHourWindow(timeStamp)}:00:00"
    }
    val patternMatch = timeStampPattern.findFirstMatchIn(timeStamp)
    val hour = getDataFromPattern(patternMatch, HOUR_FROM_TIMESTAMP)
    val minute = getDataFromPattern(patternMatch, MINUTE_FROM_TIMESTAMP).toInt
    val minuteWindow = window * (minute/window)
    s"$hour:$minuteWindow:00"
  }

  /**
   * Given an input string and regex pattern, this method checks
   * if the pattern exist in the string. If it is present, it returns the **first match**,
   * else it returns default value(Constants.UNKNOWN).
   * @param inputStr Input String
   * @param pattern Regular Expression
   * @return Returns First match of the regular expression.
   */
  def getMatchedStringWithPattern(inputStr: String, pattern: Regex): String = {
    val patternMatch = pattern.findFirstMatchIn(inputStr)
    getDataFromPattern(patternMatch, DEFAULT)
  }

  /**
   * Given an input string and regex pattern, this method checks if the pattern exists.
   * If it exits, it returns all the instances in the String that matches this attern.
   *
   * @param inputStr Input String
   * @param pattern Regular Expression
   * @return Returns all matches in the inputString that matches the pattern.
   */
  def getAllMatchedStringsWithPattern(inputStr: String, pattern: Regex): List[String] = {
    val patternMatch = pattern.findAllMatchIn(inputStr)
    getDataListFromPattern(patternMatch.toList, DEFAULT)
  }

  /**
   * Given hadoop.io.Text object and regex pattern, method checks if pattern exists.
   * If exists, it returns the first match.
   *
   * @param inputText hadoop.io.Text object - Log Text
   * @param pattern regular expression pattern
   * @return Returns first match in inputText that matches the pattern
   */
  def getMatchTextWithPattern(inputText: Text, pattern: Regex): String = {
    getMatchedStringWithPattern(inputText.toString, pattern)
  }

  /**
   * Given hadoop.io.Text object, method extracts only the log message.
   *
   * @param inputText hadoop.io.Text object - Log Text
   * @return Returns log message
   */
  def getLogMessageFromText(inputText: Text): String = {
    val patternMatch = logPattern.findFirstMatchIn(inputText.toString)
    getDataFromPattern(patternMatch, LOGSTR)
  }
}
