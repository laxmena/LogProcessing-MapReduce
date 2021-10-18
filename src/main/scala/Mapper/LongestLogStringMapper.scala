package com.laxmena.Mapper

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import HelperUtils.{CreateLogger, ObtainConfigReference, RegExUtil, Constants}

import scala.collection.JavaConverters.*
import scala.util.matching.Regex

/**
 * <b>LongestLogStringMapper</b>: Extracts the Longest Log that matches the given Regular Expression Pattern.
 *
 * <ul>
 *   <li>Gets the User Specified Regular Expressions from Configuration</li>
 *   <li>Checks if there is a match with the LogStrings</li>
 *   <li>If there is a match, then the mapper will write the level and length of the matched output to the
 *   intermediate output file</li>
 * </ul>
 */
class LongestLogStringMapper extends Mapper[Object, Text, Text, IntWritable] {
  val logger = CreateLogger(classOf[LongestLogStringMapper])

  val userConfig = ObtainConfigReference("userInput") match {
    case Some(value) => value.getConfig("userInput")
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  /**
   * Mapper Implementation: If the input string matches Regular Expression, LogLevel and Mathced Substring length
   * are written as outputs.
   *
   * @param key
   * @param value
   * @param context
   */
  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    // Reference: https://stackoverflow.com/a/8246522
    val conf = context.getConfiguration()
    val logSearchPatternConf = userConfig.getConfig("logSearch")
    // Get the Regular Expression from Configuration
    val userPattern: Regex = conf.get("pattern").r

    // Initialize Output writables
    val word = Text()
    val level = RegExUtil.getLogLevelFromText(value)
    word.set(level)

    // Extract LogMessage from the LogString
    val logStr = RegExUtil.getLogMessageFromText(value)
    // Find all matches in the LogMessage, find the longest substring that matches a pattern.
    val allMatchedStrings = RegExUtil.getAllMatchedStringsWithPattern(logStr, userPattern)
    allMatchedStrings.map((matchedString) => {
      // compute the length of the substring
      val stringLength = new IntWritable(matchedString.length())
      context.write(word, stringLength)
    })
  }
}
