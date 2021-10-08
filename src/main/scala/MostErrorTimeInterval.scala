package com.laxmena

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import HelperUtils.RegExUtil.{getHourWindow, getLogLevelFromText, getTimeStampFromText}
import HelperUtils.{CreateLogger, ObtainConfigReference}
import org.apache.hadoop.mapreduce.lib.input.FileSplit

import java.lang.Iterable
import scala.collection.JavaConverters.*

class MostErrorTimeInterval

object MostErrorTimeInterval {
  val logger = CreateLogger(classOf[MostErrorTimeInterval])
  val config = ObtainConfigReference("LogProcessing") match {
    case Some(value) => value.getConfig("LogProcessing")
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }

  class MostErrorTimeIntervalMapper extends Mapper[Object, Text, Text, IntWritable] {
    override def map(key: Object, value: Text,
                     context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
      logger.info("Executing MostErrorTimeIntervalMapper method")
      val fileSplit: FileSplit = context.getInputSplit().asInstanceOf[FileSplit]
      val fileName = Text(fileSplit.getPath().getName());

      val word = Text()
      val one = new IntWritable(1)

      val timeStamp = getTimeStampFromText(value)
      val hourWindow = getHourWindow(timeStamp)
      val level = getLogLevelFromText(value)

      if(level == "ERROR") {
        word.set(s"$fileName $hourWindow")
        context.write(word, one)
      }
    }
  }

  class MostErrorTimeIntervalReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
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
    job.setMapperClass(classOf[MostErrorTimeIntervalMapper])
    job.setCombinerClass(classOf[MostErrorTimeIntervalReducer])
    job.setReducerClass(classOf[MostErrorTimeIntervalReducer])

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