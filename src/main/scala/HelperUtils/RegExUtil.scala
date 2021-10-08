package HelperUtils

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
  val TIMESTAMP = "timestamp_group"
  val LEVEL = "level_group"
  val CONTEXT = "context_group"
  val CLASSNAME = "className_group"
  val LOGSTR = "logString_group"
  val UNKNOWN = "unknown"
  val HOUR_FROM_TIMESTAMP = "hour_group"

  val logPattern: Regex = config.getString("logRegexPattern").r
//  val logPattern: Regex = "(^\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s\\[([^\\]]*)\\]\\s(WARN|INFO|DEBUG|ERROR)\\s+([A-Z][A-Za-z\\.]+)\\$\\s-\\s(.*)".r
  val hourWindowPattern: Regex = config.getString("hourWindowPattern").r
//  val hourWindowPattern: Regex = "^(\\d{2}).*".r
  def getDataFromPattern(patternMatch: Option[Regex.Match], data: String): String = {
    val pattern = patternMatch match {
      case Some(ptr) => ptr
      case None => return UNKNOWN
    }
    pattern.group(config.getInt(data))
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
}
