# CARGO Artifact Evaluation (ASE 2022)

```
CARGO: AI-Guided Dependency Analysis for Migrating Monolithic Applications to Microservices Architecture
Vikram Nitin, Shubhi Asthana, Baishakhi Ray, Rahul Krishna
```

CARGO (short for Context-sensitive lAbel pRopaGatiOn) is a novel un-/semi-supervised partition refinement technique that uses a comprehensive system dependence graph built using context and flow-sensitive static analysis of a monolithic application to refine and thereby enrich the partitioning quality of the current state-of-the-art algorithms.

## System Requirements

1. [Docker](docker.io)
1. Python 3 (tested with Python >= 3.8), Pip

## Generating Program Graphs

In the below commands, replace `daytrader` with the name of the application. Possible choices are `daytrader, plants, jpetstore, acmeair`.

We first need to run [DOOP](https://bitbucket.org/yanniss/doop/src/master/). For ease of use, DOOP has been pre-compiled and hosted as a docker image at [quay.io/rkrsn/doop-main](quay.io/rkrsn/doop-main). We'll use that for this demo.

From the root folder of the project, run the following command :
```
docker run -it --rm -v $(pwd)/jars/daytrader:/root/doop-data/input -v $(pwd)/doop-data/daytrader:/root/doop-data/output/ quay.io/rkrsn/doop-main:latest rundoop
```
_Note : running DOOP may take 5-6 minutes_

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

Now we will use DGI to load information about our application into the graph database. From the root folder, run the following command to install DGI:
```
pip install -e .
```

Now we can use the `dgi` command to populate a Neo4j graph database.
```

dgi c2g -c -i doop-data/daytrader
dgi tx2g -c -i doop-data/daytrader
```

## Running CARGO

Once we have created the Neo4j graphs by following the above steps, we can run the following command to reproduce RQ1 (Figure 7) and RQ3 (Table 1) from the paper.
```
dgi cargo --dataset daytrader
```
