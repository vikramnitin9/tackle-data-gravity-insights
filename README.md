# CARGO Artifact Evaluation (ASE 2022)

```
CARGO: AI-Guided Dependency Analysis for Migrating Monolithic Applications to Microservices Architecture
Vikram Nitin, Shubhi Asthana, Baishakhi Ray, Rahul Krishna
```

![Overview of CARGO](./figures/overview.svg)

[Paper](https://arxiv.org/pdf/2207.11784.pdf)

CARGO (short for Context-sensitive lAbel pRopaGatiOn) is a novel un-/semi-supervised partition refinement technique that uses a comprehensive system dependence graph built using context and flow-sensitive static analysis of a monolithic application to refine and thereby enrich the partitioning quality of the current state-of-the-art algorithms.

## Kick-the-tires Instructions (~15 minutes)

### System Requirements

1. [Docker](docker.io)
1. Python 3 (tested with Python >= 3.8), Pip

### Creating a Neo4j Docker container

We will need an instance of Neo4j to store the graphs that `dgi` creates. We will start one up in a docker container and set an environment variable to let `dgi` know where to find it.

```bash
docker run -d --name neo4j \
    -p 7474:7474 \
    -p 7687:7687 \
    -e NEO4J_AUTH="neo4j/tackle" \
    -e NEO4J_apoc_export_file_enabled=true \
    -e NEO4J_apoc_import_file_enabled=true \
    -e NEO4J_apoc_import_file_use__neo4j__config=true \
    -e NEO4JLABS_PLUGINS=\["apoc"\] \
    neo4j

export NEO4J_BOLT_URL="bolt://neo4j:tackle@localhost:7687"
```

### Building Program Dependency Graphs with DGI

We first need to run [DOOP](https://bitbucket.org/yanniss/doop/src/master/). For ease of use, DOOP has been pre-compiled and hosted as a docker image at [quay.io/rkrsn/doop-main](quay.io/rkrsn/doop-main). We'll use that for this demo.

From the root folder of the project, run the following commands :
```
mkdir -p doop-data/daytrader
docker run -it --rm -v $(pwd)/jars/daytrader:/root/doop-data/input -v $(pwd)/doop-data/daytrader:/root/doop-data/output/ quay.io/rkrsn/doop-main:latest rundoop
```
_Note : running DOOP may take 5-6 minutes_

From the root folder, run the following command to install DGI:
```
pip install -e .
```

Now we can use the `dgi` command to populate a Neo4j graph database.
```
dgi -c c2g -i doop-data/daytrader
```
This will take 4-5 minutes. After successful completion, we should see something like this :
```
$ dgi -c c2g -i=doop-data/daytrader
    code2graph generator started...
    Verbose mode: ON
    Building Graph...
    [INFO] Populating heap carried dependencies edges
    100%|█████████████████████| 7138/7138 [01:37<00:00, 72.92it/s]
    [INFO] Populating dataflow edges
    100%|█████████████████████| 5022/5022 [01:31<00:00, 54.99it/s]
    [INFO] Populating call-return dependencies edges
    100%|█████████████████████| 7052/7052 [02:26<00:00, 48.30it/s]
    [INFO] Populating entrypoints
    code2graph build complete
```

### Extracting Database Transactions with Tackle-DiVA

Note that this step is only for applications with database transactions, like Daytrader. In particular, if you are running these steps for `plants`, `jpetstore` or `acmeair` sample applications as part of the "full" evaluation, **skip this step**.

Clone [Daytrader](https://github.com/WASdev/sample.daytrader7) :
```
git clone https://github.com/WASdev/sample.daytrader7
```
Now we will run [Tackle-DiVA](https://github.com/konveyor/tackle-diva) to extract transactions from Daytrader.
```
docker run --rm \
  -v sample.daytrader7:/app \
  -v $(pwd):/diva-distribution/output \
  quay.io/konveyor/tackle-diva
```
This should generate a file `transaction.json`. Finally, we run DGI to load these transaction edges into the program dependency graph.
```
dgi -c tx2g -i transaction.json
```

### Running CARGO

Once we have created the Neo4j graphs by following the above steps, we can run CARGO as follows :

```
dgi cargo --dataset daytrader
```

#### RQ1 - Distributed Database Transactions

The above command should produce a table similar to this :
```
+------------+--------------+---------+---------+-------+----------------+
|            |   Mono2Micro |   CoGCN |   FoSCI |   MEM | CARGO_unique   |
+============+==============+=========+=========+=======+================+
| Original   |        0.297 |   0.789 |   0.405 | 0.273 | --             |
+------------+--------------+---------+---------+-------+----------------+
| With CARGO |        0.949 |   1     |   0.99  | 1     | 0.971          |
+------------+--------------+---------+---------+-------+----------------+
```
If this is plotted as a bar graph, it will give Figure 7.

#### RQ3 - Performance on Architectural Metrics

The above command should also produce a table similar to this :

```
+----------+--------------+--------------------+---------+---------------+---------+---------------+-------+-------------+----------------+
|          |   Mono2Micro |   Mono2Micro_CARGO |   CoGCN |   CoGCN_CARGO |   FoSCI |   FoSCI_CARGO |   MEM |   MEM_CARGO |   CARGO_unique |
+==========+==============+====================+=========+===============+=========+===============+=======+=============+================+
| Coupling |        0.785 |              0.013 |   0.284 |         0     |   0.686 |         0.008 | 0.117 |       0.001 |          0.008 |
+----------+--------------+--------------------+---------+---------------+---------+---------------+-------+-------------+----------------+
| Cohesion |        0.281 |              0.588 |   0.357 |         0.619 |   0.204 |         0.457 | 0.256 |       0.53  |          0.894 |
+----------+--------------+--------------------+---------+---------------+---------+---------------+-------+-------------+----------------+
| ICP      |        0.596 |              0.053 |   0.157 |         0.047 |   0.481 |         0.077 | 0.041 |       0.074 |          0.004 |
+----------+--------------+--------------------+---------+---------------+---------+---------------+-------+-------------+----------------+
| BCP      |        2.308 |              2.498 |   1.399 |         1.824 |   2.545 |         2.192 | 2.227 |       2.496 |          1.382 |
+----------+--------------+--------------------+---------+---------------+---------+---------------+-------+-------------+----------------+
```

These four rows correspond to the first row of Table 1, for each of the four metrics.

#### RQ2 - Run-time Performance

RQ2 measures how the partitioned Daytrader application performs under real-world load testing. For instructions on how to set up the partitioned application and simulate load, look at the [full eval](#full-eval) instructions .

We have enclosed the results of running JMeter in the folders `RQ2/Aggregate-CARGO` and `RQ2/Aggregate-Mono2Micro`. To process these csv files and generate Figure 6, first install matplotlib :
```
pip install matplotlib
```
Then run :
```
cd RQ2
python make_plots.py
```
This will generate the file `latency_throughput.pdf` which will look something like this :

<img src="./figures/latency_throughput.svg" width="750">

## Full Eval

### RQ1

The evaluation of RQ1 above ([link](#rq1---distributed-database-transactions)) is complete, since Daytrader is the only application with database transactions.

### RQ2

#### Install Apache JMeter
https://jmeter.apache.org/download_jmeter.cgi

Please read the documentation at https://jmeter.apache.org/ to learn more about Apache Jmeter to load test functional behavior and measure performances.

#### Open the Apache Jmeter UI
From the root folder of the JMeter installation, run
```
cd bin
sh jmeter.sh 
```
Load the `.jmx` file `RQ2/JMeter_files/daytrader.jmx` and run the Jmeter tests to measure latency and throughput.
```
jmeter -n -t daytrader7.jmx -JHOST=localhost -JPORT=9082 -JPROTOCOL=http -JMAXTHINKTIME=100 -JDURATION=300
```
This will produce csv files corresponding to the various use cases, a sample of which we have provided in `RQ2/Aggregate_Mono2Micro` and `RQ2/Aggregate_CARGO`.

The `jmeter` script has the following options which can be configured as desired :
```
	-JHOST	    The name of the machine running the DayTrader Application. The default is localhost.
	-JPORT	    The HTTP port of the server running the DayTrader Application. The default is 9080.
	-JPROTOCOL  The transport either http or https
	-JTHREADS   The number of jmeter threads to start. The default is 50.
	-JRAMP		The ramp up time for starting the threads. Set this to the same value as -JTHREADS for a smoother startup. The default is 0.
	-JDURATION  The time (in seconds) to run jmeter.
	-JMAXTHINKTIME The time in milliseconds to wait between each call. The default is 0 ms
	-JSTOCKS    The total amount of stocks/quotes in the database, minus one. The default is 9999, which assumes there are 10,000 stocks in the database.
	-JBOTUID    The lowest user id. The default is 0.
	-JTOPUID    The highest user id. The default is 14999, which assumes there are 15,000 users in the database.
```

### RQ3

The evaluation of RQ3 above ([link](#rq3---performance-on-architectural-metrics)) is only for Daytrader. To evaluate the other sample applications, run the above instructions ([here](#building-program-dependency-graphs-with-dgi) and [here](#running-cargo)) replacing `daytrader` with the name of your application. Possible choices - `daytrader | plants | jpetstore | acmeair`.

Each of these applications corresponds to one row of Table 1.
