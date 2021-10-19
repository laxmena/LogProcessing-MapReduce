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

[<< Back to Index](README.md)