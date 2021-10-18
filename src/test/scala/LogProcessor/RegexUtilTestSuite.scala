package LogProcessor

import HelperUtils.{Constants, ObtainConfigReference, RegExUtil}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.apache.hadoop.io.{IntWritable, Text}

class RegexUtilTestSuite extends AnyFlatSpec with Matchers {
  behavior of "RegexUtil Test-Suite"
  val config = ObtainConfigReference(Constants.LOG_PROCESSING) match {
    case Some(value) => value.getConfig(Constants.LOG_PROCESSING)
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }
  // Get customizable configurations
  val userConfig = ObtainConfigReference("userInput") match {
    case Some(value) => value.getConfig("userInput")
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  it should "getLogLevelFromLogString() - Extract LogLevel from String" in {
    val pattern = ".*"
    val logString = "21:46:55.058 [scala-execution-context-global-23] WARN  HelperUtils.Parameters$ - s%]s,+2k|D}K7b/XCwG&@7HDPR8z"
    val level = RegExUtil.getLogLevelFromLogString(logString)
    assert(level == Constants.WARN)
  }

  it should "getLogLevelFromText() - Extract LogLevel from Hadoop Text" in {
    val pattern = ".*"
    val logString = "21:46:55.058 [scala-execution-context-global-23] WARN  HelperUtils.Parameters$ - s%]s,+2k|D}K7b/XCwG&@7HDPR8z"
    val level = RegExUtil.getLogLevelFromText(new Text(logString))
    assert(level == Constants.WARN)
  }

  it should "get timestamp from Text" in {
    val logText = new Text("21:46:55.058 [scala-execution-context-global-23] WARN  HelperUtils.Parameters$ - s%]s,+2k|D}K7b/XCwG&@7HDPR8z")
    val timeStamp = RegExUtil.getTimeStampFromText(logText)
    assert(timeStamp == "21:46:55.058")
  }

  it should "get Time window from TimeStamp string" in {
    val timeStamp = "21:46:55.058"
    val timeWindow = RegExUtil.getTimeWindow(timeStamp, 5)
    assert(timeWindow == "21:45:00")
  }

  it should "get log message from text" in  {
    val logString = new Text("21:46:55.058 [scala-execution-context-global-23] WARN  HelperUtils.Parameters$ - s%]s,+2k|D}K7b/XCwG&@7HDPR8z")
    val logMessage = RegExUtil.getLogMessageFromText(logString)
    assert(logMessage == "s%]s,+2k|D}K7b/XCwG&@7HDPR8z")
  }

  it should "Match Text with Pattern" in {
    val logString = "21:46:55.058 [scala-execution-context-global-23] WARN  HelperUtils.Parameters$ - s%]s,+2k|D}K7b/XCwG&@7HDPR8z"
    val logText = new Text(logString)
    val pattern = Constants.DEFAULT_PATTERN
    val patternMatch = RegExUtil.getMatchTextWithPattern(logText, pattern.r)
    assert(patternMatch == logString)
  }
}
