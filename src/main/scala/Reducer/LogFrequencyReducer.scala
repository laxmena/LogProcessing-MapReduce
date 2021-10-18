package com.laxmena.Reducer

import HelperUtils.CreateLogger
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.lang.Iterable
import scala.collection.JavaConverters.*

/**
 * <b>LogFrequencyReducer:</b> Aggregates the intermediate results by the LogFrequencyMapper.
 * Final output is the number of log strings associated with each LogLevels (ERROR/WARN/DEBUG/INFO)
 */
class LogFrequencyReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
  val logger = CreateLogger(classOf[LogFrequencyReducer])

  /**
   * Reduce Implementation: Aggregates mapper results and generates total number of ERROR, WARN, DEBUG and INFO logs.
   *
   * @param key
   * @param values
   * @param context
   */
  override def reduce(key: Text, values: Iterable[IntWritable],
                      context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    logger.info("Executing LogFrequencyReducer Reducer")
    // Aggregate all the values for a given key. Add the values.
    val sum = values.asScala.foldLeft(0)(_ + _.get)
    // Write to Output. Example: "ERROR 89"
    context.write(key, new IntWritable(sum))
  }
}