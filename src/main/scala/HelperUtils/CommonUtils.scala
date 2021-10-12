package HelperUtils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.lib.input.FileSplit

class CommonUtils
object CommonUtils {
  val logger = CreateLogger(classOf[CommonUtils])

  val config = ObtainConfigReference("LogProcessing") match {
    case Some(value) => value.getConfig("LogProcessing")
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }
  val userConfig = ObtainConfigReference("userInput") match {
    case Some(value) => value.getConfig("userInput")
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  def getConfiguration(args: Array[String]): Configuration = {
    val conf = new Configuration()

    // Configuration params for longest-log for a pattern
    if(args(2) == Constants.LONGEST_PATTERN_REGEX_MR || args(2) == Constants.LOG_FREQUENCY_DIST_MR) {
      val logSearchConfig = userConfig.getConfig("logSearch")
      logger.info("Args: ", args(3))
      val patternOption =
        if(args.length > 3 && logSearchConfig.hasPath(args(3))) {

          logger.info(s"Search Pattern: ${logSearchConfig.getString(args(3))}")
          conf.set("pattern", logSearchConfig.getString(args(3)))
        } else {
          logger.info("Setting default Pattern: --> .*")
          conf.set("pattern", ".*")
        }
    }

    conf
  }

  def getDateFromFileName(context: Mapper[Object, Text, Text, IntWritable]#Context): String = {
    val fileSplit: FileSplit = context.getInputSplit().asInstanceOf[FileSplit]
    val fileName = fileSplit.getPath().getName();
    val fileNamePattern = config.getString(Constants.FILE_NAME_PATTERN).r
    val patternMatch = fileNamePattern.findFirstMatchIn(fileName)
    val dateStr = RegExUtil.getDataFromPattern(patternMatch, Constants.DATE_GROUP)
    dateStr
  }
}
