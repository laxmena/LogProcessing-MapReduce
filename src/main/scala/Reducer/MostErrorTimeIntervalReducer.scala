package com.laxmena.Reducer

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.lang.Iterable
import scala.collection.JavaConverters.*
import scala.collection.immutable.TreeMap

class MostErrorTimeIntervalReducer extends Reducer[Text, IntWritable, Text, IntWritable] {

  case class resultList(result: List[(Text, Int)]) {
    def appendValue(newValue: (Text, Int)) = resultList(result.toList :+ newValue)
  }
  object resultList
  override def reduce(key: Text, values: Iterable[IntWritable],
                      context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    val sum = values.asScala.foldLeft(0)(_ + _.get)
    context.write(key, new IntWritable(sum))
  }

  override def cleanup(context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    super.cleanup(context)
  }
}
