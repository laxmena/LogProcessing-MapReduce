package com.laxmena

import HelperUtils.RegExUtil.{getLogLevelFromLogString, getLogLevelFromText}
import HelperUtils.{CommonUtils, Constants, CreateLogger, ObtainConfigReference}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text, WritableComparator}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import com.laxmena.Mapper.{LogFrequencyDistributionMapper, LogFrequencyMapper, LongestLogStringMapper}
import com.laxmena.Mapper.{MostErrorTimeIntervalMapper, SortByValueMapper}
import com.laxmena.Reducer.{LogFrequencyDistributionReducer, LogFrequencyReducer, LongestLogStringReducer}
import com.laxmena.Reducer.{MostErrorTimeIntervalReducer, SortByValueReducer}

import java.lang.Iterable
import scala.collection.JavaConverters.*
import scala.util.matching.Regex

/**
 * <h1>LogProcessor</h1>
 * <p>
 *   LogProcessor is the Primary class for Map-Reduce programs. LogProcessor contains 4 different Map-Reduce programs
 *   which can be invoked by passing different arguments.
 * </p>
 * <h3>List of Map-Reduce Programs implemented:</h3>
 * <ol>
 *   <li><b>LogLevel Frequency<b>: Compute LogLevels Distribution in the input log files</li>
 *   <li><b>Most Error in TimeInterval</b>: Find Time Intervals with most errors, sorted descending order</li>
 *   <li><b>Longest Substring matching Regex</b>: Find the Longest Substring that matches a Regular Expression</li>
 *   <li><b>LogLevel Frequency Distribution in TimeIntervals</b>: LogLevel distributions in specified TimeIntervals</li>
 * </ol>
 * <br/>
 * <code>hadoop jar LogProcessing-MapReduce-assembly-0.1.jar [args(0)] [args(1)] [args(2)] [args(3)] </code>
 * <ol>
 *   <li><b>args(0)</b>: Input Path. (Example: logprocess/input) </li>
 *   <li><b>args(1)</b>: Output Path. (Example: logprocess/output) </li>
 *   <li><b>args(2)</b>: Map-Reduce Key. Must be one of the following:
 *      <ul>
 *        <li>log-frequency</li>
 *        <li>most-error</li>
 *        <li>longest-regex (Takes Pattern as args(3))</li>
 *        <li>log-freq-dist (Takes Pattern as args(3))</li>
 *      </ul>
 *   </li>
 *  <li><b>args(3)[Optional]</b>: Regular Expression Pattern key. Must be one of the following text in the bold face:
 *    <ul>
 *      <li><b>pattern1</b>: Any. Matches Complete String.</li>
 *      <li><b>pattern2</b>: Substring enclosed within Parantheses().</li>
 *      <li><b>pattern3</b>: Substring with any alphabets, numbers and special characters</li>
 *      <li><b>pattern4</b>: Substring with consecutive Numbers</li>
 *    </ul>
 *  </li>
 * </ol>
 * <br/>
 * <p><b>Example command to run LogProcessor in Hadoop:</b> <br/>
 * <code>hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/output longest-regex pattern2</code></p>
 */
class LogProcessor
object LogProcessor {
  val logger = CreateLogger(classOf[LogProcessor])

  def main(args: Array[String]): Unit = {
    logger.info("Starting LogProcessor Execution...")

    // Get Configurations based on Arguments
    val configuration: Configuration = CommonUtils.getConfiguration(args)
    val conf2 = CommonUtils.getConfiguration(args)
    conf2.set("mapred.textoutputformat.separator", ",")

    // Create Map-Reduce Jobs
    val job  = Job.getInstance(configuration, "LogProcessor")
    val job2 = Job.getInstance(conf2, "Sorting")

    // Set Jars for the Jobs
    job.setJarByClass(this.getClass)
    job2.setJarByClass(this.getClass)

    // Invoke Map-Reduce jobs based on user inputs in command line
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
        logger.info("Selected Map-Reduce Program: Longest Log String that matches a Substring in each Logging Level")
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
        conf2.set("mapred.textoutputformat.separator", ",")
        FileInputFormat.addInputPath(job2, new Path(args(1)+"_interm"))
        FileOutputFormat.setOutputPath(job2, new Path(args(1)))
        System.exit(if(job2.waitForCompletion(true)) 0 else 1)
      }
      case _ => {

        logger.debug(s"HDFS Input Path: ${args(0)}")
        FileInputFormat.addInputPath(job, new Path(args(0)))
        logger.debug(s"HDFS Output Path: ${args(1)}")
        FileOutputFormat.setOutputPath(job, new Path(args(1)))
        System.exit(if(job.waitForCompletion(true)) 0 else 1)
      }
    }
  }
}


