package com.laxmena.Mapper

import HelperUtils.RegExUtil.{getHourWindow, getLogLevelFromText, getTimeStampFromText, getTimeWindow}
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import HelperUtils.{CreateLogger, ObtainConfigReference}

class MostErrorTimeIntervalMapper extends Mapper[Object, Text, Text, IntWritable] {
  val logger = CreateLogger(classOf[MostErrorTimeIntervalMapper])

  val userConfig = ObtainConfigReference("userInput") match {
    case Some(value) => value.getConfig("userInput")
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    val fileSplit: FileSplit = context.getInputSplit().asInstanceOf[FileSplit]
    val fileName = Text(fileSplit.getPath().getName());

    val word = Text()
    val one = new IntWritable(1)

    val windowSize  = userConfig.getInt("timeInterval")
    val timeStamp = getTimeStampFromText(value)
    val timeBucket = getTimeWindow(timeStamp, windowSize)
    val level = getLogLevelFromText(value)


    if(level == "ERROR") {
      word.set(s"$fileName $timeBucket")
      context.write(word, one)
    }
  }
}
