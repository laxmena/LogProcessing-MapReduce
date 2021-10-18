package com.laxmena.Reducer
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.lang.Iterable
import scala.collection.JavaConverters.*

class LongestLogStringReducer extends Reducer[Text, IntWritable, Text, IntWritable]  {
  override def reduce(key: Text, values: Iterable[IntWritable],
                      context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {

    val initial = 0

    val max = values.asScala.foldLeft(initial){(max, x) => {
      if (x.get > max) x.get else max
    }}

    context.write(key, new IntWritable(max))
  }
}
