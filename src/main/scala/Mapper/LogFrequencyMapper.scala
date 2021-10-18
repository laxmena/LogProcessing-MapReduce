package com.laxmena.Mapper

import HelperUtils.CreateLogger
import HelperUtils.RegExUtil.getLogLevelFromText
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

/**
 * <b>LogFrequencyMapper:</b> Extracts Log Level from LogStrings and writes them to intermediate output files.
 * <p>Intermediate Output Format: key: "LOG_LEVEL" value: 1</p>
 * <p> Example: WARN 1</p>
 */
class LogFrequencyMapper extends Mapper[Object, Text, Text, IntWritable] {
  val logger = CreateLogger(classOf[LogFrequencyMapper])

  /**
   * Map method implementation. Extracts Log Level from the LogString.
   *
   * @param key Object
   * @param value Text
   * @param context
   */
  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    logger.info("Executing LogFrequencyMapper::map method")
    // Initialize output writables
    val word = Text()
    val one = new IntWritable(1)
    // Extract Log Level from the LogString
    val level = getLogLevelFromText(value)
    word.set(level)
    // Write output to Intermediate Output File
    context.write(word, one)
  }
}
