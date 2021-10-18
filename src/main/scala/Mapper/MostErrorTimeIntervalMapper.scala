package com.laxmena.Mapper

import HelperUtils.RegExUtil.{getHourWindow, getLogLevelFromText, getTimeStampFromText, getTimeWindow}
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import HelperUtils.{CommonUtils, CreateLogger, ObtainConfigReference, Constants}

/**
 * <b>MostErrorTimeIntervalMapper:</b> Breaks the input files into timebuckets, and counts the number of error messages
 * in each bucket. Length of the TimeBucket is configured in the Configuration file.
 * <ul>
 *   <li></li>
 * </ul>
 */
class MostErrorTimeIntervalMapper extends Mapper[Object, Text, Text, IntWritable] {
  val logger = CreateLogger(classOf[MostErrorTimeIntervalMapper])

  val userConfig = ObtainConfigReference(Constants.USER_INPUT) match {
    case Some(value) => value.getConfig(Constants.USER_INPUT)
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  /**
   * Checks if the LogLevel is ERROR, and writes TimeBucket and value 1 to the mapper output.
   * 
   * @param key
   * @param value
   * @param context
   */
  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    val date = CommonUtils.getDateFromFileName(context)
    
    // Initialize the output writables
    val word = Text()
    val one = new IntWritable(1)
    
    // Get TimeBucket that the current LogString belongs to
    val windowSize  = userConfig.getInt(Constants.TIMEINTERVAL)
    val timeStamp = getTimeStampFromText(value)
    val timeBucket = getTimeWindow(timeStamp, windowSize)

    // Extract LogLevel
    val level = getLogLevelFromText(value)
    // Check if the LogLevel is ERROR
    if(level == Constants.ERROR) {
      word.set(s"$date $timeBucket")
      context.write(word, one)
    }
  }
}
