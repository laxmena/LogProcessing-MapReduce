package HelperUtils

import HelperUtils.Constants.{DEFAULT, HOUR_FROM_TIMESTAMP, LEVEL, MINUTE_FROM_TIMESTAMP, SECONDS_FROM_TIMESTAMP, TIMESTAMP, UNKNOWN, hourWindowPattern, logPattern, timeStampPattern, LOGSTR}

import scala.collection.JavaConverters.*
import scala.util.matching.Regex
import org.apache.hadoop.io.{IntWritable, Text}


class RegExUtil
object RegExUtil {
  val logger = CreateLogger(classOf[RegExUtil])

  val config = ObtainConfigReference("LogProcessing") match {
    case Some(value) => value.getConfig("LogProcessing")
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }

  def getDataFromPattern(patternMatch: Option[Regex.Match], data: String): String = {
    val pattern = patternMatch match {
      case Some(ptr) => ptr
      case None => return UNKNOWN
    }
    pattern.group(config.getInt(data))
  }

  def getDataListFromPattern(patternMatchList: List[Regex.Match], data: String): List[String] = {
    patternMatchList.map((pattern: Regex.Match) => {
      pattern.group(config.getInt(data))
    }).toList
  }

  def getLogLevelFromLogString(inputString: String): String = {
    val patternMatch = logPattern.findFirstMatchIn(inputString)
    getDataFromPattern(patternMatch, LEVEL)
  }

  def getLogLevelFromText(inp: Text): String = {
    getLogLevelFromLogString(inp.toString)
  }

  def getTimeStampFromLogString(inputString: String): String = {
    val patternMatch = logPattern.findFirstMatchIn(inputString)
    getDataFromPattern(patternMatch, TIMESTAMP)
  }

  def getTimeStampFromText(inp: Text): String = {
    getTimeStampFromLogString(inp.toString)
  }

  def getHourWindow(timeStamp: String): String = {
    val patternMatch = hourWindowPattern.findFirstMatchIn(timeStamp)
    getDataFromPattern(patternMatch, HOUR_FROM_TIMESTAMP)
  }

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

  def getMatchedStringWithPattern(inputStr: String, pattern: Regex): String = {
    val patternMatch = pattern.findFirstMatchIn(inputStr)
    getDataFromPattern(patternMatch, DEFAULT)
  }

  def getAllMatchedStringsWithPattern(inputStr: String, pattern: Regex): List[String] = {
    val patternMatch = pattern.findAllMatchIn(inputStr)
    getDataListFromPattern(patternMatch.toList, DEFAULT)
  }

  def getMatchTextWithPattern(inputText: Text, pattern: Regex): String = {
    getMatchedStringWithPattern(inputText.toString, pattern)
  }

  def getLogMessageFromText(inputText: Text): String = {
    val patternMatch = logPattern.findFirstMatchIn(inputText.toString)
    getDataFromPattern(patternMatch, LOGSTR)
  }
}
