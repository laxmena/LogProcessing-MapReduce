package com.laxmena.Mapper

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import HelperUtils.{CommonUtils, Constants, CreateLogger, ObtainConfigReference, RegExUtil}
import org.apache.hadoop.mapreduce.lib.input.FileSplit

import scala.collection.JavaConverters.*
import scala.util.matching.Regex

/**
 * <b>LogFrequencyDistributionMapper:</b> Breaks logs into time buckets, and computes
 * Log Level Distributions for each time bucket.
 *
 * <ul>
 *  <li>Determines which Time Bucket the LogString belongs to</li>
 *  <li>Extracts Log Level from the LogString</li>
 *  <li>Searches if there is a match for the given regular expression in the log strings.</li>
 *  <li>If there is a match, then mapper outputs the following key-value to the intermediate storage file.
 *  key: "Date TimeBucket LogLevel", value: 1   </li>
 * </ul>
 */
class LogFrequencyDistributionMapper extends Mapper[Object, Text, Text, IntWritable] {
  val logger = CreateLogger(classOf[LogFrequencyDistributionMapper])

  val userConfig = ObtainConfigReference(Constants.USER_INPUT) match {
    case Some(value) => value.getConfig(Constants.USER_INPUT)
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  /**
   * Mapper Implementation: Breaks log files into time buckets, checks if LogString matches the regex pattern.
   * Outputs TimeBucket and LogLevel, for the LogMessages that matches the pattern.
   * 
   * @param key Object
   * @param value Text: LogString
   * @param context
   */
  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    val conf = context.getConfiguration()
    val logSearchPatternConf = userConfig.getConfig(Constants.LOGSEARCH)

    // Get Regular Expression from Configuration
    val userPattern: Regex = conf.get(Constants.PATTERN).r

    // Get Date from FileName
    val date = CommonUtils.getDateFromFileName(context)
    // Initialize writables
    val word = Text()
    val one = new IntWritable(1)
    // Find Log Level
    val level = RegExUtil.getLogLevelFromText(value)
    // Extract Log Message from the LogString
    val logStr = RegExUtil.getLogMessageFromText(value)

    // Get the TimeBucket based on the Time Interval specified in the config
    val windowSize = userConfig.getInt(Constants.TIMEINTERVAL)
    val timeStamp = RegExUtil.getTimeStampFromText(value)
    val timeBucket = RegExUtil.getTimeWindow(timeStamp, windowSize)

    // Search for the Regex Pattern in the String
    val matchedString = RegExUtil.getMatchedStringWithPattern(logStr, userPattern)
    if(matchedString != Constants.UNKNOWN) {
      word.set(s"$date $timeBucket $level")
      context.write(word, one)
    }
  }
}
