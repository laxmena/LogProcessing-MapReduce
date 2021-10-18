package com.laxmena

import HelperUtils.RegExUtil.{getLogLevelFromLogString, getLogLevelFromText}
import HelperUtils.{CommonUtils, Constants, CreateLogger, ObtainConfigReference}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text, WritableComparator}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import com.laxmena.Mapper.{LogFrequencyDistributionMapper, LogFrequencyMapper, LongestLogStringMapper, MostErrorTimeIntervalMapper, SortByValueMapper}
import com.laxmena.Reducer.{LogFrequencyDistributionReducer, LogFrequencyReducer, LongestLogStringReducer, MostErrorTimeIntervalReducer, SortByValueReducer}

import java.lang.Iterable
import scala.collection.JavaConverters.*
import scala.util.matching.Regex

/**
 * LogProcessor contains Main Class. This invokes
 */
class LogProcessor
object LogProcessor {
  val logger = CreateLogger(classOf[LogProcessor])

  def main(args: Array[String]): Unit = {
    logger.info("Starting LogProcessor Execution...")

    val configuration: Configuration = CommonUtils.getConfiguration(args)
    val conf2 = CommonUtils.getConfiguration(args)


    val job  = Job.getInstance(configuration, "LogProcessor")
    val job2 = Job.getInstance(conf2, "Sorting")

    job.setJarByClass(this.getClass)
    job2.setJarByClass(this.getClass)

    args(2) match {
      case Constants.LOG_FREQUENCY_MR => {
        logger.info("Selected Map-Reduce Program: Log Frequency Calculator")
        job.setMapperClass(classOf[LogFrequencyMapper])
        job.setCombinerClass(classOf[LogFrequencyReducer])
        job.setReducerClass(classOf[LogFrequencyReducer])

        job.setOutputKeyClass(classOf[Text])
        job.setOutputValueClass(classOf[IntWritable])
      }
      case Constants.MOST_ERROR_MR => {
        logger.info("Selected Map-Reduce Program: Most Error in a Time Window")
        job.setMapperClass(classOf[MostErrorTimeIntervalMapper])
        job.setCombinerClass(classOf[MostErrorTimeIntervalReducer])
        job.setReducerClass(classOf[MostErrorTimeIntervalReducer])
        job.setOutputKeyClass(classOf[Text])
        job.setOutputValueClass(classOf[IntWritable])

        // Sorting
        job2.setMapperClass(classOf[SortByValueMapper])
        job2.setReducerClass(classOf[SortByValueReducer])
        job2.setMapOutputKeyClass(classOf[IntWritable])
        job2.setMapOutputValueClass(classOf[Text])
        job2.setOutputKeyClass(classOf[Text])
        job2.setOutputValueClass(classOf[IntWritable])
      }
      case Constants.LONGEST_PATTERN_REGEX_MR => {
        logger.info("Selected Map-Reduce Program: Longest Log String in each Logging Level")
        job.setMapperClass(classOf[LongestLogStringMapper])
        job.setCombinerClass(classOf[LongestLogStringReducer])
        job.setReducerClass(classOf[LongestLogStringReducer])

        job.setOutputKeyClass(classOf[Text])
        job.setOutputValueClass(classOf[IntWritable])
      }
      case Constants.LOG_FREQUENCY_DIST_MR => {
        logger.info("Selected Map-Redice Program: Distribution of Log String that Mataches Regex as blocks of time intervals")
        job.setMapperClass(classOf[LogFrequencyDistributionMapper])
        job.setCombinerClass(classOf[LogFrequencyDistributionReducer])
        job.setReducerClass(classOf[LogFrequencyDistributionReducer])

        job.setOutputKeyClass(classOf[Text])
        job.setOutputValueClass(classOf[IntWritable])
      }
      case _ => {
        logger.info("Selected Map-Reduce Program: DEFAULT - Log Frequency Calculator")
        job.setMapperClass(classOf[LogFrequencyMapper])
        job.setCombinerClass(classOf[LogFrequencyReducer])
        job.setReducerClass(classOf[LogFrequencyReducer])

        job.setOutputKeyClass(classOf[Text])
        job.setOutputValueClass(classOf[IntWritable])
      }
    }




    logger.info("Submitting Job for Execution")
    args(2) match {
      case Constants.MOST_ERROR_MR => {
        FileInputFormat.addInputPath(job, new Path(args(0)))
        FileOutputFormat.setOutputPath(job, new Path(args(1)+"_interm"))
        job.waitForCompletion(true)
        FileInputFormat.addInputPath(job2, new Path(args(1)+"_interm"))
        FileOutputFormat.setOutputPath(job2, new Path(args(1)))
        conf2.set("mapred.textoutputformat.separator", ",")
        System.exit(if(job2.waitForCompletion(true)) 0 else 1)
      }
      case _ => {
        logger.debug(s"HDFS Input Path: ${args(0)}")
        FileInputFormat.addInputPath(job, new Path(args(0)))
        logger.debug(s"HDFS Output Path: ${args(1)}")
        FileOutputFormat.setOutputPath(job, new Path(args(1)))
        configuration.set("mapred.textoutputformat.separator", ",")
        System.exit(if(job.waitForCompletion(true)) 0 else 1)
      }
    }
  }
}


