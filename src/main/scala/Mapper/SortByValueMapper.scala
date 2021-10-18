package com.laxmena.Mapper

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class SortByValueMapper extends Mapper[Object, Text, IntWritable, Text]  {
  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, IntWritable, Text]#Context): Unit = {
    val pair = value.toString().split('\t')
    val negative = new IntWritable((pair(1).toInt) * -1)
    val text = new Text(pair(0))
    context.write(negative, text)
  }
}
