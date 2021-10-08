package com.laxmena

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}

import java.lang.Iterable
import java.util.StringTokenizer

import scala.collection.JavaConverters.*
import scala.util.matching.Regex

class LogLevelFrequency
object LogLevelFrequency {

  class LogFrequencyMapper extends Mapper[Object, Text, Text, IntWritable] {
      override def map(key: Object, value: Text,
                       context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
        val logPattern: Regex = "^(^\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s\\[([^\\]]*)\\]\\s(WARN|INFO|DEBUG|ERROR)\\s+([A-Z][A-Za-z\\.]+)\\$\\s-\\s(.*)$".r
        val word = Text()
        val one = new IntWritable(1)

        val patternMatch = logPattern.findFirstMatchIn(value.toString)

        patternMatch match {
          case Some(pattern) => {
            word.set(pattern.group(3))
            context.write(word, one)
          }
          case None => {}
        }
      }
  }

  class LogFrequencyReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: Iterable[IntWritable],
                        context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
      var sum = values.asScala.foldLeft(0)(_ + _.get)
      context.write(key, new IntWritable(sum))
    }
  }

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    val job = Job.getInstance(configuration, "Log Level Frequency")

    job.setJarByClass(this.getClass)
    job.setMapperClass(classOf[LogFrequencyMapper])
    job.setCombinerClass(classOf[LogFrequencyReducer])
    job.setReducerClass(classOf[LogFrequencyReducer])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])

    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    System.exit(if(job.waitForCompletion(true)) 0 else 1)

  }
}