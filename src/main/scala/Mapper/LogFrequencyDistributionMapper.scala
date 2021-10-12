package com.laxmena.Mapper

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import HelperUtils.{CommonUtils, Constants, CreateLogger, ObtainConfigReference, RegExUtil}
import org.apache.hadoop.mapreduce.lib.input.FileSplit

import scala.collection.JavaConverters.*
import scala.util.matching.Regex

class LogFrequencyDistributionMapper extends Mapper[Object, Text, Text, IntWritable] {
  val logger = CreateLogger(classOf[LogFrequencyDistributionMapper])

  val userConfig = ObtainConfigReference("userInput") match {
    case Some(value) => value.getConfig("userInput")
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    val conf = context.getConfiguration()
    val logSearchPatternConf = userConfig.getConfig("logSearch")
    val userPattern: Regex = conf.get("pattern").r

    val date = CommonUtils.getDateFromFileName(context)

    val word = Text()
    val one = new IntWritable(1)

    val level = RegExUtil.getLogLevelFromText(value)

    val logStr = RegExUtil.getLogMessageFromText(value)

    val windowSize = userConfig.getInt("timeInterval")
    val timeStamp = RegExUtil.getTimeStampFromText(value)
    val timeBucket = RegExUtil.getTimeWindow(timeStamp, windowSize)

    val matchedString = RegExUtil.getMatchedStringWithPattern(logStr, userPattern)
    if(matchedString != Constants.UNKNOWN) {
      word.set(s"$date $timeBucket $level")
      context.write(word, one)
    }
  }
}
