# Job4: Log Frequency Distribution in TimeIntervals

## Description
Compute the distribution of Log messages across different time intervals, where the log message matches the given regular expression.

## Functionality:
### Mapper
Mapper Implementation Class: [LogFrequencyDistributionMapper.scala](../src/main/scala/Mapper/LogFrequencyDistributionMapper.scala)

1. Using Regular Expression parse the input LogString into following groups - G1: TimeStamp, G2: Context, G3: LogLevel, G4: ClassName, G5: LogMessage.
2. Based on the Time Interval configured in `applications.config` find which time bucket does the log string belong to.
    By default, timeInterval is configured as 5 minutes. So, total time interval is split into 5 minute blocks (Eg. If the logstring has timestamp 12:33:00.342, then it belongs to the time bucket 12:30:00 - starting time of the timebucket)
3. Check for the presence of injected regular expression in the logstring.
4. If there is pattern, then write the "TimeBucket + LogLevel" and length of the Log Message to the mapper output file.

### Reducer
Reducer Implementation Class: [LogFrequencyDistributionReducer.scala](../src/main/scala/Reducer/LogFrequencyDistributionReducer.scala)

1. Input to Reducer will be of the format, example: `key: 2021-10-04 17:55:00, values: [1, 1, 1, 1, 1]` 
2. Compute the Sum of all the integers in values array.
3. Write key as key, and the maximum integeer as final output value, to the reducer output file.

### Input Files:

Please find the input log files used for testing this Map-Reduce Job here: [InputFiles](./input)

### Result:
Output after running the Map-Reduce job on the above mentioned input files.

**Pattern1:**

```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/log-freq-dist-1 log-freq-dist pattern1
```

**Output:**
```text
2021-10-04 17:55:00 DEBUG,7
2021-10-04 17:55:00 ERROR,2
2021-10-04 17:55:00 INFO,66
2021-10-04 17:55:00 WARN,28
2021-10-07 19:40:00 DEBUG,7
2021-10-07 19:40:00 ERROR,2
2021-10-07 19:40:00 INFO,66
2021-10-07 19:40:00 WARN,28
2021-10-10 19:40:00 DEBUG,7
2021-10-10 19:40:00 ERROR,3
2021-10-10 19:40:00 INFO,66
2021-10-10 19:40:00 WARN,27
2021-10-17 21:45:00 DEBUG,1546
2021-10-17 21:45:00 ERROR,144
2021-10-17 21:45:00 INFO,10504
2021-10-17 21:45:00 WARN,2809

```

**Explanation:**: 
pattern1 is wildcard pattern, it matches any characters, which essentially means we are computing the distribution of log levels in each time interval.

**Pattern2:**
```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/log-freq-dist-2 log-freq-dist pattern2
```
**Output:**
```
2021-10-04 17:55:00 WARN,2
2021-10-07 19:40:00 INFO,1
2021-10-07 19:40:00 WARN,1
2021-10-10 19:40:00 INFO,1
2021-10-10 19:40:00 WARN,1
2021-10-17 21:45:00 DEBUG,102
2021-10-17 21:45:00 ERROR,8
2021-10-17 21:45:00 INFO,636
2021-10-17 21:45:00 WARN,169
```

**Explanation:**: 
For pattern2, we search if a substring exist with '(' and ')', with any number of characters in between. We get the log level distribution for the time inervals that matches the injected patterns.

**Pattern3:**
```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/log-freq-dist-3 log-freq-dist pattern3
```
**Output:**
```
2021-10-04 17:55:00 DEBUG,7
2021-10-04 17:55:00 ERROR,2
2021-10-04 17:55:00 INFO,66
2021-10-04 17:55:00 WARN,28
2021-10-07 19:40:00 DEBUG,7
2021-10-07 19:40:00 ERROR,2
2021-10-07 19:40:00 INFO,66
2021-10-07 19:40:00 WARN,28
2021-10-10 19:40:00 DEBUG,7
2021-10-10 19:40:00 ERROR,3
2021-10-10 19:40:00 INFO,66
2021-10-10 19:40:00 WARN,27
2021-10-17 21:45:00 DEBUG,1546
2021-10-17 21:45:00 ERROR,144
2021-10-17 21:45:00 INFO,10504
2021-10-17 21:45:00 WARN,2809
```

**Explanation:**: 
For pattern3, we search for sub-strings with any characters but spaces. We determine the distribution of log levels for each time interval blocks.


**Pattern4:**
```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/log-freq-dist-4 log-freq-dist pattern4
```

**Output:**
```
2021-10-04 17:55:00 DEBUG,7
2021-10-04 17:55:00 ERROR,2
2021-10-04 17:55:00 INFO,61
2021-10-04 17:55:00 WARN,25
2021-10-07 19:40:00 DEBUG,7
2021-10-07 19:40:00 ERROR,2
2021-10-07 19:40:00 INFO,64
2021-10-07 19:40:00 WARN,27
2021-10-10 19:40:00 DEBUG,7
2021-10-10 19:40:00 ERROR,3
2021-10-10 19:40:00 INFO,64
2021-10-10 19:40:00 WARN,26
2021-10-17 21:45:00 DEBUG,1476
2021-10-17 21:45:00 ERROR,136
2021-10-17 21:45:00 INFO,9985
2021-10-17 21:45:00 WARN,2679
```

**Explanation:**: 
For pattern4, we search for sub-strings with consequetive numbers. We compute the distribution of Log Levels in each time interval. 



Check all the output file here: [Ouptut Files](./results)

[<< Back to Index](README.md)
