# CARGO Artifact Evaluation (ASE 2022)

```
CARGO: AI-Guided Dependency Analysis for Migrating Monolithic Applications to Microservices Architecture
Vikram Nitin, Shubhi Asthana, Baishakhi Ray, Rahul Krishna
```

CARGO (short for Context-sensitive lAbel pRopaGatiOn) is a novel un-/semi-supervised partition refinement technique that uses a comprehensive system dependence graph built using context and flow-sensitive static analysis of a monolithic application to refine and thereby enrich the partitioning quality of the current state-of-the-art algorithms.

## System Requirements

1. [Docker](docker.io)
1. Python 3 (tested with Python >= 3.8), Pip

## Kick-the-tires Instructions (~15 minutes)

### Generating Program Facts

We first need to run [DOOP](https://bitbucket.org/yanniss/doop/src/master/). For ease of use, DOOP has been pre-compiled and hosted as a docker image at [quay.io/rkrsn/doop-main](quay.io/rkrsn/doop-main). We'll use that for this demo.

From the root folder of the project, run the following commands :
```
mkdir -p doop-data/daytrader
docker run -it --rm -v $(pwd)/jars/daytrader:/root/doop-data/input -v $(pwd)/doop-data/daytrader:/root/doop-data/output/ quay.io/rkrsn/doop-main:latest rundoop
```
_Note : running DOOP may take 5-6 minutes_

### Building Program Dependency Graphs with DGI

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

From the root folder, run the following command to install DGI:
```
pip install -e .
```

Now we can use the `dgi` command to populate a Neo4j graph database.
```
dgi c2g -c -i doop-data/daytrader
dgi tx2g -c -i doop-data/daytrader
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

RQ2 measures how the partitioned Daytrader application performs under real-world load testing. For instructions on how to set up the partitioned application and simulate load, look at the full instructions in the next section.

We have enclosed the results of running JMeter in the folders `RQ2/Aggregate-CARGO` and `RQ2/Aggregate-Mono2Micro`. To process these csv files and generate Figure 6, run
```
cd RQ2
python make_plots.py
```
This will generate the file `latency_throughput.pdf` which will look something like this :
