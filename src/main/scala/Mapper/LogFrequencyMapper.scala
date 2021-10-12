package com.laxmena.Mapper

import HelperUtils.CreateLogger
import HelperUtils.RegExUtil.getLogLevelFromText
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class LogFrequencyMapper extends Mapper[Object, Text, Text, IntWritable] {

  val logger = CreateLogger(classOf[LogFrequencyMapper])

  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    logger.info("Executing map method")
    val word = Text()
    val one = new IntWritable(1)

    val level = getLogLevelFromText(value)
    word.set(level)
    context.write(word, one)
  }
}
