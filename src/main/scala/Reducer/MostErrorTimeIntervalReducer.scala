package com.laxmena.Reducer

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.lang.Iterable
import scala.collection.JavaConverters.*
import scala.collection.immutable.TreeMap

/**
 * <b>MostErrorTimeIntervalReducer: </b> Aggregates the total number of Error messages in each time bucket.
 */
class MostErrorTimeIntervalReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
  /**
   * Reducer Implementation: Counts the number of Error messages in each time interval(time bucket) and outputs the
   * timebucket start timestamp and number of errors in the interval.
   * @param key
   * @param values
   * @param context
   */
  override def reduce(key: Text, values: Iterable[IntWritable],
                      context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    // Count number of instances of Errors for each TimeBucket
    val sum = values.asScala.foldLeft(0)(_ + _.get)
    context.write(key, new IntWritable(sum))
  }

}
