package HelperUtils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.lib.input.FileSplit

/**
 * CommonUtil - Collection of reusable methods that can be used across the project.
 */
class CommonUtils
object CommonUtils {
  val logger = CreateLogger(classOf[CommonUtils])

  // Get Default Configurations necessary for Program execution
  val config = ObtainConfigReference(Constants.LOG_PROCESSING) match {
    case Some(value) => value.getConfig(Constants.LOG_PROCESSING)
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }
  // Get customizable configurations
  val userConfig = ObtainConfigReference(Constants.USER_INPUT) match {
    case Some(value) => value.getConfig(Constants.USER_INPUT)
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  /**
   * Helper method to create Configurations for the Map-Reduce jobs based
   * on input arguments.
   * Certain map reduce methods can specify which Regex pattern to use for
   * the job from the arguments. This method validates the user argument and
   * sets the configuration accordingly.
   *
   * @param args Array[String] Command line arguments passed while execution.
   * @return hadoop.conf.Configuration Hadoop Configuration object.
   */
  def getConfiguration(args: Array[String]): Configuration = {
    val conf = new Configuration()

    // If the request map-reduce job is to 'find longest pattern' or
    // 'frequency distribution' over a time interval, user has the ability
    // to select the regex pattern.
    if(args(2) == Constants.LONGEST_PATTERN_REGEX_MR
      || args(2) == Constants.LOG_FREQUENCY_DIST_MR) {
      val logSearchConfig = userConfig.getConfig(Constants.LOGSEARCH)
      // If input validation fails, set default search regex pattern.
      val patternOption =
        if(args.length > 3 && logSearchConfig.hasPath(args(3))) {
          logger.info(s"Search Pattern: --> ${logSearchConfig.getString(args(3))}")
          conf.set(Constants.PATTERN, logSearchConfig.getString(args(3)))
        } else {
          logger.info("Setting default Pattern: --> " + Constants.DEFAULT_PATTERN)
          conf.set(Constants.PATTERN, Constants.DEFAULT_PATTERN)
        }
    }
    conf
  }

  /**
   * Extract date from input LogFile name. Date is specified in the file name
   * in our input files. This method extracts the date from the file name.
   *
   * @param context Hadoop Mapper object
   * @return String Date value as String
   */
  def getDateFromFileName(context: Mapper[Object, Text, Text, IntWritable]#Context): String = {
    val fileSplit: FileSplit = context.getInputSplit().asInstanceOf[FileSplit]
    val fileName = fileSplit.getPath().getName();
    val fileNamePattern = config.getString(Constants.FILE_NAME_PATTERN).r
    val patternMatch = fileNamePattern.findFirstMatchIn(fileName)
    val dateStr = RegExUtil.getDataFromPattern(patternMatch, Constants.DATE_GROUP)
    dateStr
  }
}
