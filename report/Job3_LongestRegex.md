# Job3: Longest Regular Expression matching Substring

## Description
Compute the length of the longest string that matches the given injected Regular Expression. Length is computed for each logging level.

## Functionality:
### Mapper
Mapper Implementation Class: [LongestLogStringMapper.scala](../src/main/scala/Mapper/LongestLogStringMapper.scala)

1. Using Regular Expression parse the input LogString into following groups - G1: TimeStamp, G2: Context, G3: LogLevel, G4: ClassName, G5: LogMessage.
2. Load the user selected Regular Expression Pattern to match.
3. Check the Log Messages if there is a match for the pattern.
4. If there is pattern, then write the Log Level and length of the Log Message to the mapper output file.

### Reducer
Reducer Implementation Class: [LongestLogStringReducer.scala](../src/main/scala/Reducer/LongestLogStringReducer.scala)

1. Input to Reducer will be of the format, example: `key: INFO, values: [23, 56, 24, 12, 34, 44]` 
2. Compute the Max of all the integers in values array.
3. Write key as key, and the maximum integeer as final output value, to the reducer output file.

### Input Files:

Please find the input log files used for testing this Map-Reduce Job here: [InputFiles](./input)

### Result:
Output after running the Map-Reduce job on the above mentioned input files.

**Pattern1:**

```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/longest-regex-1 longest-regex pattern1
```

```text
DEBUG,90
ERROR,78
INFO,102
WARN,95
```

**Explanation:**: 
pattern1 is wildcard pattern, it matches any characters, which essentially means we are computing the max length of the log message, for each log level.

**Pattern2:**
```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/longest-regex-2 longest-regex pattern2
```

```
DEBUG,48
ERROR,34
INFO,73
WARN,72
```

**Explanation:**: 
For pattern2, we search if a substring exist with '(' and ')', with any number of characters in between. We get the length of those matched patterns, and compute maximum for each logging level.

**Pattern3:**
```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/longest-regex-3 longest-regex pattern3
```

```
DEBUG,90
ERROR,78
INFO,102
WARN,95
```

**Explanation:**: 
For pattern3, we search for sub-strings with any characters but spaces. We compute the length of each such pattern, and determine the maximum length in each logging levels. Interestingly, we see that the maximum length that we computed for Pattern1 and Pattern2 are same. So, the strings that matched Pattern3, also matched Pattern1.


**Pattern4:**
```
hadoop jar LogProcessing-MapReduce-assembly-0.1.jar logprocess/input logprocess/longest-regex-4 longest-regex pattern4
```

```
DEBUG,5
ERROR,3
INFO,8
WARN,5
```

**Explanation:**: 
For pattern4, we search for sub-strings with consequetive numbers. We compute the length of each such pattern, and determine the maximum length in each logging levels. 

In the result, we see that one log message with `INFO` level, has a substring with 8 consequtive numbers.


Check all the output file here: [Ouptut Files](./results)

<hr/>

[<< Back to Index](README.md)
