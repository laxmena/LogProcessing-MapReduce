# 1. Project Structure
```shell
LOGPROCESSING-MAPREDUCE\SRC
├───main
│   ├───resources
│   └───scala
│       ├───Experiments
│       ├───HelperUtils
│       ├───Mapper
│       └───Reducer
└───test
    ├───resources
    └───scala
        └───LogProcessor
```

**main/**:

1. **resources**: Contains `applications.conf` and `logback.xml`. 
2. **scala/Experiments**: (Unmaintained) Used for experimenting and testing different programming implementation logics.
3. **scala/HelperUtils**: Contains Helper Classes and Util functions, that are used across the project. 
4. **scala/Mapper**: Hosts all the Mapper class implementations for this project.
5. **scala/Reducer**: Hosts all the Reducer implementations for this project.
6. **scala/LogProcessor.scala**: Primary class, that contains the definition of Main method. 

**test/**:
1. **resources**: Repository to hold resources for tests.
2. **scala/LogProcessor**: Contains Test suite for LogProcessor module.

## Input LogFiles Structure

Log Files used for testing this Project can be found here: [Input Files](./input).

There are four log files. Input LogFile file name contains the date.

Here is an example LogString found inside one of the input files.
```
17:59:52.334 [scala-execution-context-global-15] WARN  HelperUtils.Parameters$ - s%]s,+2k|D}K7b/XCwG&@7HDPR8z
```

Following terminologies is used in the documentation of this projects:

1. LogString - The entire log string in the codeblock above, is referred to as LogString.
2. TimeStamp - `17:59:52.334`, refers to the time the log message has been generated.
3. Context - ``, this is refers to the context of the log message.
4. LogLevel - `WARN`, There are four types of LogLevels found in this project: ERROR, WARN, INFO and DEBUG.
5. ClassName - `HelperUtils.Parameters$`, this in general is referred to as ClassName.
6. LogMessage - `s%]s,+2k|D}K7b/XCwG&@7HDPR8z`, this is the actual Log Message. Note that LogString and LogMessage are different.

**More Information:**

Of the four Log files in the input directory, log files on `2021-10-04`, `2021-10-07` and `2021-10-10` have 103 LogStrings each, and the distribution of LogLevels in these files are same.

Fourth Log input file, dated `2021-10-17` have 15000+3 LogMessages, with similar distribution to the other log files.

Note: When these files are served to the Mappers, One mapper task for each file is created. Each datafile here is a shard. And the shards are not equally split, the file dated `2021-10-17` is comparatively pretty big than other input files.

<hr/>

[<< Back to Index](README.md)