package Experiments

import scala.util.matching.Regex
import HelperUtils.RegExUtil.{getHourWindow, getLogLevelFromText, getTimeStampFromLogString, getTimeStampFromText}

import org.apache.hadoop.io.{IntWritable, Text}

class RegExExperiment
object RegExExperiment {

  def printPattern(patternList: List[Regex.Match]): Unit = {
    patternList match {
      case pattern :: tail => {
        println(s"Timestamp: ${pattern.group(1)}")
        println(s"Context: ${pattern.group(2)}")
        println(s"LogLevel: ${pattern.group(3)}")
        println(s"ClassName: ${pattern.group(4)}")
        println(s"Log: ${pattern.group(5)}")

        printPattern(tail)
      }
      case _ => {}
    }
  }

  def runRegEx =
    val logPattern: Regex = "(?m)(^\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s\\[([^\\]]*)\\]\\s(WARN|INFO|DEBUG|ERROR)\\s+([A-Z][A-Za-z\\.]+)\\$\\s-\\s(.*)".r
    val value =
      "17:59:51.582 [main] INFO  GenerateLogData$ - Log data generator started..."

    val patternMatch = logPattern.findFirstMatchIn(value.toString)
    patternMatch match {
      case Some(pattern) => println(s"Pattern ${pattern.group(1)}")
      case None => println("No match found")
    }

    val patternMatch1 = logPattern.findFirstMatchIn(value.toString)

    val timeStamp = getTimeStampFromText(Text(value))
    println("timeStamp: " + timeStamp)

    val hourWindow = getHourWindow(timeStamp)
    println("Hourwindo: " + hourWindow)

}
