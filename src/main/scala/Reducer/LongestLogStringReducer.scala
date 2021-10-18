package com.laxmena.Reducer
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.lang.Iterable
import scala.collection.JavaConverters.*

/**
 * <b>LongestLogStringReducer:</b> Reads all the intermediate outputs from the Mapper, and finds the
 * maximum length of the substring for each LogLevel.
 *
 */
class LongestLogStringReducer extends Reducer[Text, IntWritable, Text, IntWritable]  {
  /**
   * Reducer Implementation: Finds the Maximum value for given keys, and outputs key and maximum value
   * 
   * @param key
   * @param values
   * @param context
   */
  override def reduce(key: Text, values: Iterable[IntWritable],
                      context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    // Compute the Max value for Each Key
    val max = values.asScala.foldLeft(0){(max, x) => {
      if (x.get > max) x.get else max
    }}
    // Output: "LogLevel MaxSubStringLength"
    context.write(key, new IntWritable(max))
  }
}
