# Guide to Running LogProcessing MapReduce program in AWS and Hortonworks

Compile the project and generate the JAR file (See [Cloning and Compiling](./CloningAndCompiling.md) for more information).

## Hortonworks

1. Download and Install Hortonworks Sandbox from [Hortonworks Downloads]() on VMWare or VirtualBox.
2. Once installed, and the Sandbox started running, follow the instructions to connect to that machine using SSH.
3. After connecting to the machine using SSH, lets create required HDFS directories.
    ```shell
    hadoop fs -mkdir logprocess
    hadoop fs -mkdir logprocess/input
    ```
4. Copy files(input log files, and jar) from local machine to the VM by running the following command.
    ```shell
    scp -p 2222 <path/input_file> root@<hortonworks_ssh_ip>:<Destination Path>
    ```
5. Transfer the files to the HDFS file-system using the following command.
   ```shell
   hadoop fs -put <input_file> logprocess/input
   ```
6. Execute the Map-Reduce Jobs, See documentations of [Job1](./Job1_LogFrequency.md), [Job2](./Job2_MostError.md), [Job3](./Job3_LongestRegex.md) and [Job4](./Job4_LogFreqDist.md) for more information on how to execute each tasks.
    This is the skeleton to execute different map-reduce jobs.
    ```shell
   hadoop jar LogProcessing-MapReduce-assembly [input-path] [output-path] [job-key] [pattern-key]
    ```
7. Read output of the executed MapReduce jobs    
    ```shell
   hadoop fs -cat logprocess/<output_dir>/*
    ```
8. To Export the output from HDFS File System to Normal file System,
     ```shell
     hadoop fs -cat logprocess/<output_dir>/* > output.csv
     ```
9. Transferring output files from Remote machine to local machine.
    ```shell
    scp -p 2222 root@<hortonworks_ssh_ip>:output.csv <dest_path_on_local_machine>
    ```
   
## AWS

1. Create a Cluster in [AWS EMR](https://us-east-2.console.aws.amazon.com/elasticmapreduce). (Here's a Guide: [How to Create and Run EMR Cluster](https://towardsdatascience.com/how-to-create-and-run-an-emr-cluster-using-aws-cli-3a78977dc7f0))
2. Make sure SSH port is enabled in Security settings of the Master node.
3. Connect to the Cluster from local machine using Putty, and the amazon key-pair.
4. Create HDFS directories required to run this project.
   ```shell
    # Commands to be run on Putty Terminal
    hadoop fs -mkdir logprocess
    hadoop fs -mkdir logprocess/input
   ```
5. In another terminal, connect to master node using SSH. (Note: You will need a .pem key to establish this OpenSSH connection).
    ```shell
    # Command to be run on Local Terminal    
    sftp -i <aws_key_file.pem> hadoop@<aws_cluster_public_dns_name>
    ```
6. Once SFTP is connected, transfer the files from your local machine to the master node using the following commands
   ```shell
    # Command to be run on Local Terminal    
    put <src_path>/*.logs <dest_path>
   ```
7. Transfer the Jar file to the master node as well.
    ```shell 
    # Command to be run on Local Terminal    
    put <src_path>/*.jar <dest_path>
    ```
8. Store the files in HDFS Directory. Navigate to the directory, where the log files are located.
   ```shell
   # PuTTy Terminal
   hadoop fs -put *.log logprocess/input    
   ```
9. Execute the Map-Reduce Program. See documentations of [Job1](./Job1_LogFrequency.md), [Job2](./Job2_MostError.md), [Job3](./Job3_LongestRegex.md) and [Job4](./Job4_LogFreqDist.md) for more information on how to execute each tasks.
   ```shell
   # PuTTy Terminal
   hadoop jar LogProcessing-MapReduce-assembly [input-path] [output-path] [job-key] [pattern-key]
    ```
   This is the pattern for running LogProcessing MapReduce programs.
10. Once the output is generated, we can view the results of the jobs directly in the terminal using the following command.
    ```shell
    # PuTTy Terminal
    hadoop fs -cat logprocess/<output_dir>/*
    ```
11. To Export the output from HDFS File System to Normal file System, 
    ```shell
    # PuTTy Terminal
    hadoop fs -cat logprocess/output_dir/* > output.csv
    ```
12. To transfer output files from remote machine to the local machine, 
    ```shell
    # Command to be run on Local Terminal with SFTP Connection established
    get <output_file_name> <local_directory_path>
    # To transfer directories
    get -r <output_directory_name> <local_directory_path>
    ```

<hr/>

[<< Back to Index](README.md)
