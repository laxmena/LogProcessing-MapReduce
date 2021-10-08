package com.laxmena

import scala.util.matching.Regex

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

  @main def runRegEx =
    val logPattern: Regex = "(?m)(^\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s\\[([^\\]]*)\\]\\s(WARN|INFO|DEBUG|ERROR)\\s+([A-Z][A-Za-z\\.]+)\\$\\s-\\s(.*)".r
    val value =
      """17:59:51.582 [main] INFO  GenerateLogData$ - Log data generator started...
        |17:59:52.107 [main] WARN  HelperUtils.Parameters$ - Max count 100 is used to create records instead of timeouts
        |17:59:52.334 [scala-execution-context-global-15] WARN  HelperUtils.Parameters$ - s%]s,+2k|D}K7b/XCwG&@7HDPR8z
        |17:59:52.384 [scala-execution-context-global-15] INFO  HelperUtils.Parameters$ - ;kNI&V%v<c#eSDK@lPY(""".stripMargin

    val patternMatch = logPattern.findFirstMatchIn(value.toString)
    patternMatch match {
      case Some(pattern) => println(s"Pattern ${pattern.group(1)}")
      case None => println("No match found")
    }


}
