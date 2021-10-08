package com.laxmena

import HelperUtils.RegExUtil.{getLogLevelFromLogString, getLogLevelFromText}
import HelperUtils.{CreateLogger, ObtainConfigReference}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text, WritableComparator}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}

import java.lang.Iterable
import java.util.StringTokenizer
import scala.collection.JavaConverters.*
import scala.util.matching.Regex

class LogLevelFrequency
object LogLevelFrequency {
  val logger = CreateLogger(classOf[LogLevelFrequency])
  val config = ObtainConfigReference("LogProcessing") match {
    case Some(value) => value.getConfig("LogProcessing")
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }

  class LogFrequencyMapper extends Mapper[Object, Text, Text, IntWritable] {
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

  class LogFrequencyReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: Iterable[IntWritable],
                        context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
      logger.info("Executing reduce method")
      var sum = values.asScala.foldLeft(0)(_ + _.get)
      context.write(key, new IntWritable(sum))
    }
  }

  def main(args: Array[String]): Unit = {
    logger.info("Starting LogLevelFrequency Execution...")
    val configuration = new Configuration()
    val job = Job.getInstance(configuration, "Log Level Frequency")

    job.setJarByClass(this.getClass)
    job.setMapperClass(classOf[LogFrequencyMapper])
    job.setCombinerClass(classOf[LogFrequencyReducer])
    job.setReducerClass(classOf[LogFrequencyReducer])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])

    logger.debug(s"HDFS Input Path: ${args(0)}")
    FileInputFormat.addInputPath(job, new Path(args(0)))

    logger.debug(s"HDFS Output Path: ${args(1)}")
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    logger.info("Submitting Job for Execution")
    System.exit(if(job.waitForCompletion(true)) 0 else 1)
  }
}