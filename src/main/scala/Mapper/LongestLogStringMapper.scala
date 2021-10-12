package com.laxmena.Mapper

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import HelperUtils.{CreateLogger, ObtainConfigReference, RegExUtil, Constants}

import scala.collection.JavaConverters.*
import scala.util.matching.Regex

class LongestLogStringMapper extends Mapper[Object, Text, Text, IntWritable] {
  val logger = CreateLogger(classOf[LongestLogStringMapper])

  val userConfig = ObtainConfigReference("userInput") match {
    case Some(value) => value.getConfig("userInput")
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {

    // Reference: https://stackoverflow.com/a/8246522
    val conf = context.getConfiguration()

    val logSearchPatternConf = userConfig.getConfig("logSearch")
    val userPattern: Regex = conf.get("pattern").r

    val word = Text()
    val level = RegExUtil.getLogLevelFromText(value)
    word.set(level)

    val logStr = RegExUtil.getLogMessageFromText(value)

    val allMatchedStrings = RegExUtil.getAllMatchedStringsWithPattern(logStr, userPattern)
    allMatchedStrings.map((matchedString) => {
      val stringLength = new IntWritable(matchedString.length())
      context.write(word, stringLength)
    })

  }
}
