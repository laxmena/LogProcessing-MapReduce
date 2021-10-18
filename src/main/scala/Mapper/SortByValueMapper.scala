package com.laxmena.Mapper

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

/**
 * SortByValueMapper - Secondary Mapper Class that helps sorting the result of another mapper by values in 
 * descending order. This mapper leverages Hadoop Combiner class's sorting mechanism to sort in descending order.
 * 
 * By default, Combiner class combines the intermediates results of mapper into key value pairs, and sorts the keys in 
 * ascending order. Here in the mapper, we switch the keys and values, and multiply the value by -1. So, when the 
 * combiner sorts the keys, we have the results in our desired format, but in negative. 
 * 
 * Negative value will be converted back in the Reducer class, and key value pair will be swapped.
 */
class SortByValueMapper extends Mapper[Object, Text, IntWritable, Text]  {
  override def map(key: Object, value: Text,
                   context: Mapper[Object, Text, IntWritable, Text]#Context): Unit = {
    val pair = value.toString().split('\t')
    // Make the value negative, as combiner sorts in ascending order, and we want highest value first.
    val negative = new IntWritable((pair(1).toInt) * -1)
    val text = new Text(pair(0))
    // Switch key and value, as combiner will sort based on key  
    context.write(negative, text)
  }
}
