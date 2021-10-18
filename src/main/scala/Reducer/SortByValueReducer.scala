package com.laxmena.Reducer

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.lang.Iterable
import scala.collection.JavaConverters.*

/**
 * SortByBalueReducer - Reducer class which takes input from SortByValueMapper.
 * Switches the key and value, and converts the negative value back to its original value.
 *
 * The result will be output sorted in Descending order.
 */
class SortByValueReducer extends Reducer[IntWritable, Text, Text, IntWritable]  {
  override def reduce(key: IntWritable, values: Iterable[Text],
                      context: Reducer[IntWritable, Text, Text, IntWritable]#Context): Unit = {
    // Convert negative values to positive values
    val positive = new IntWritable(key.get() * -1)
    // Swap key and Value
    values.asScala.foreach(value => {
      context.write(value, positive)
    })
  }
}
