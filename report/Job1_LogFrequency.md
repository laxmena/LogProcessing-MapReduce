# Job1: LogLevel Frequency

## Description
Computes count for each Log Level(WARN/DEBUG/INFO/ERROR) across all input files.

## Functionality:
### Mapper
Mapper Implementation Class: [LogFrequencyMapper.scala](../src/main/scala/Mapper/LogFrequencyMapper.scala)

1. Using Regular Expression parse the input LogString into following groups - G1: TimeStamp, G2: Context, G3: LogLevel, G4: ClassName, G5: LogMessage.
2. Extract LogLevel from the LogString (Group3)
3. Write the LogLevel as key, and value as 1 to the Mapper output file.

### Reducer
Reducer Implementation Class: [LogFrequencyReducer.scala](../src/main/scala/Reducer/LogFrequencyReducer.scala)

1. Input to Reducer will be of the format, example: `key: INFO, values: [1, 1, 1, 1, 1, 1, 1]` 
2. Compute the Sum of all the integers in values array.
3. Write key as key, and the sum of values as final output value, to the reducer output file.

### Input Files:

Please find the input log files used for testing this Map-Reduce Job here: [InputFiles](./input)

### Result:
This is the output after running the Map-Reduce job on the above mentioned input files.

```text
DEBUG,1567
ERROR,151
INFO,10702
WARN,2892
```

Check the output file here: [logfrequency.csv](./results/logfrequency.csv)

[<< Back to Index](README.md)
