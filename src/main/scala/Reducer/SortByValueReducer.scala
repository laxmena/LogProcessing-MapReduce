package com.laxmena.Reducer

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.lang.Iterable
import scala.collection.JavaConverters.*

class SortByValueReducer extends Reducer[IntWritable, Text, Text, IntWritable]  {
  override def reduce(key: IntWritable, values: Iterable[Text],
                      context: Reducer[IntWritable, Text, Text, IntWritable]#Context): Unit = {
    val positive = new IntWritable(key.get() * -1)
    values.asScala.foreach(value => {
      context.write(value, positive)
    })
  }
}
