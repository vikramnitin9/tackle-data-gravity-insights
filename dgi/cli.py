################################################################################
# Copyright IBM Corporation 2021, 2022
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
################################################################################

"""
Tackle Data Gravity Insights

Command Line Interface (CLI) for Tackle Data Gravity Insights
"""
import sys
import json
import click
import logging
import importlib.resources

import random
import numpy as np
import pandas as pd
from tabulate import tabulate
from pathlib import Path
import os
import re

from neomodel import config
from simple_ddl_parser import parse_from_file

# Import our packages
from .schema2graph import schema_loader
from .code2graph import ClassGraphBuilder, MethodGraphBuilder
from .tx2graph import ClassTransactionLoader, MethodTransactionLoader
from .code2graph.utils.parse_config import Config
from .cargo import Cargo


######################################################################
# cli - Grouping for sub commands
######################################################################
@click.group()
@click.option("--neo4j-bolt", "-n", envvar="NEO4J_BOLT_URL",
              default="bolt://neo4j:tackle@localhost:7687", help="Neo4j Bolt URL")
@click.option("--quiet", "-q", required=False, help="Be more quiet", default=False, is_flag=True, show_default=True)
@click.option("--validate", "-v", help="Validate but don't populate graph", default=False, is_flag=True)
@click.option("--clear", "-c", help="Clear graph before loading", default=False, is_flag=True, show_default=True)
@click.pass_context
def cli(ctx, validate, quiet, clear, neo4j_bolt):
    """Tackle Data Gravity Insights"""
    ctx.ensure_object(dict)
    ctx.obj['validate'] = validate
    ctx.obj['verbose'] = not quiet
    ctx.obj['clear'] = clear
    ctx.obj['bolt'] = neo4j_bolt

    # Configure Neo4J
    config.DATABASE_URL = ctx.obj["bolt"]
    config.ENCRYPTED_CONNECTION = False

    # Set logging configuration
    loglevel = logging.WARNING
    if (ctx.obj["verbose"]):
        loglevel = logging.INFO
    logging.basicConfig(level=loglevel, format="[%(levelname)s] %(message)s")


@cli.command()
@click.option("--dataset", "-d", type=str, default="daytrader", help="Name of the dataset")
@click.pass_context
def cargo(ctx, dataset):

    algorithms      = ['Mono2Micro', 'CoGCN', 'FoSCI', 'MEM', 'CARGO_unique']
    metric_names    = ['Coupling', 'Cohesion', 'ICP', 'BCP', 'DB']
    seeds           = [42, 43, 44, 45, 46]
    
    algo_cols       = []
    for algo in algorithms:
        if algo == 'CARGO_unique':
            algo_cols += [algo]
        else:
            algo_cols += [algo, algo + '_CARGO']

    all_results = {metric_name : {col : [] for col in algo_cols} for metric_name in metric_names}
    dir_path = os.path.dirname(os.path.realpath(__file__))

    cargo = Cargo(dataset)

    for algo_name in algorithms:

        for seed in seeds:
            print("Running with seed {}".format(seed))

            random.seed(seed)
            np.random.seed(seed)

            if algo_name == 'CARGO_unique':
                for K in [3, 5, 7, 9, 11, 13]:
                    metrics = cargo.run('unique', K, None)
                    for metric_name in metric_names:
                        all_results[metric_name][algo_name] += [metrics[metric_name]]
            else:
                for fname in Path(os.path.join(dir_path, 'cargo/resources/{}/partitions/{}'.format(dataset, algo_name))).glob("*.json"):
                    fname_proc  = fname.stem
                    fname = str(fname)

                    if '_CARGO' in fname_proc:
                        continue
                    if 'repeat' in fname_proc:
                        fname_proc = re.sub('_repeat_[0-9]', '', fname_proc)
                    try:
                        K = re.findall(r'\d+', fname_proc)[0]
                        if int(K) > 13:
                            continue
                    except:
                        print("Warning - skipping file {}".format(fname))
                        continue

                    cargo_metrics   = cargo.run('file', -1, fname)
                    orig_metrics    = cargo.get_metrics_from_file(fname)

                    for metric_name in metric_names:
                        all_results[metric_name][algo_name]             += [orig_metrics[metric_name]]
                        all_results[metric_name][algo_name + '_CARGO']  += [cargo_metrics[metric_name]]

    results_df = pd.DataFrame(columns=algo_cols, index=metric_names)

    for m in metric_names:
        for algo_name in algo_cols:
            results_df.loc[m][algo_name] = np.round(np.mean(all_results[m][algo_name]), 3)

    db_df = pd.DataFrame(columns=algorithms, index=['Original', 'With CARGO'])
    
    for algo in algorithms:
        if algo != 'CARGO_unique':
            db_df.loc['Original'][algo]     = 1.0 - results_df.loc['DB'][algo]
            db_df.loc['With CARGO'][algo]   = 1.0 - results_df.loc['DB'][algo + '_CARGO']
        else:
            db_df.loc['Original'][algo]     = '--'
            db_df.loc['With CARGO'][algo]   = 1.0 - results_df.loc['DB'][algo]

    results_df = results_df.drop(index='DB')

    print('\n\n')
    print("---------- Dataset : {} ----------".format(dataset))
    print('\n\n')

    if dataset == 'daytrader':
        print("RQ1 - Database Transactional Purity")
        print(tabulate(db_df, headers=db_df.columns, tablefmt='grid'))

    print("RQ3 - Architectural Metrics")
    print(tabulate(results_df, headers=results_df.columns, tablefmt='grid'))

######################################################################
# schema2graph - Populates the graph from an SQL schema DDL
######################################################################
@cli.command()
@click.option("--input", "-i", type=click.Path(exists=True), required=True,
              help="The SQL/DDL file to load into the graph")
@click.option("--output", "-o", required=False, help="The JSON file to write the schema to")
@click.pass_context
def s2g(ctx, input, output):
    """This command parses SQL schema DDL into a graph"""

    # Read the DDL file
    click.echo(f"Reading: {input}")
    result = None
    try:
        result = parse_from_file(input, group_by_type=True)
    except FileNotFoundError as error:
        raise click.ClickException(error)

    # Optionally write it output to json
    if output:
        click.echo(f"Writing: {output}")
        with open(output, "w") as f:
            contents = json.dumps(result, indent=4)
            f.write(contents)

    if ctx.obj['validate']:
        click.echo(f"File [{input}] validated.")
        exit(0)

    if ctx.obj['clear']:
        click.echo("Clearing graph...")
        schema_loader.remove_all_nodes()

    click.echo("Building Graph...")
    schema_loader.load_graph(result)
    click.echo("Graph build complete")


######################################################################
#  tx2graph - Loads output from DiVA into graph
######################################################################
@cli.command()
@click.option("--input", "-i", type=click.Path(exists=True), required=True,
              help="DiVA Transaction JSON file")
@click.option("--abstraction", "-a", type=click.Choice(["class", "method", "full"]),
              default="class", help="The level of abstraction to use when building the graph", show_default=True)
@click.pass_context
def tx2g(ctx, input, abstraction):
    """This command loads DiVA database transactions into a graph"""

    if ctx.obj["verbose"]:
        click.echo("Verbose mode: ON")

    class_transaction_loader = ClassTransactionLoader()
    method_transaction_loader = MethodTransactionLoader()

    if abstraction.lower() == "full":
        if ctx.obj['validate']:
            click.echo("Validate mode: abstraction level is {}".format(
                abstraction.lower()))
            sys.exit()

        class_transaction_loader.load_transactions(
            input, clear=ctx.obj['clear'])
        # We don't want to clear the table node twice.
        # Otherwise, we'll use the table nodes created above
        method_transaction_loader.load_transactions(input, clear=False)

    elif abstraction.lower() == "class":
        if ctx.obj['validate']:
            click.echo("Validate mode: abstraction level is {}".format(
                abstraction.lower()))
            sys.exit()
        class_transaction_loader.load_transactions(
            input, clear=ctx.obj['clear'])

    elif abstraction.lower() == "method":
        if ctx.obj['validate']:
            click.echo("Validate mode: abstraction level is {}".format(
                abstraction.lower()))
            sys.exit()

        method_transaction_loader.load_transactions(
            input, clear=ctx.obj['clear'])

    else:
        raise click.BadArgumentUsage(
            "Not a valid abstraction level. Valid options are 'class', 'method', 'full'.")

    click.echo("Transactions populated")


######################################################################
#  code2graph - Imports code dependencies into the graph
######################################################################
@cli.command()
@click.option("--input", "-i", type=click.Path(exists=True, resolve_path=True,
              file_okay=False), required=True, help="DOOP output facts directory.")
@click.option("--abstraction", "-a", type=click.Choice(["class", "method", "full"]),
              default="class", help="The level of abstraction to use when building the graph", show_default=True)
@click.pass_context
def c2g(ctx, input, abstraction):
    """This command loads Code dependencies into the graph"""

    click.echo("code2graph generator started...")

    if ctx.obj["verbose"]:
        click.echo("Verbose mode: ON")

    # -------------------------
    # Initialize configurations
    # -------------------------
    proj_root = importlib.resources.files('dgi.code2graph')
    usr_cfg = Config(config_file=proj_root.joinpath("etc", "config.yml"))
    usr_cfg.load_config()

    # Add the input dir to configuration.
    usr_cfg.set_config(key="GRAPH_FACTS_DIR", val=input)

    # ---------------
    # Build the graph
    # ---------------

    click.echo("Building Graph...")

    class_g_builder = ClassGraphBuilder(usr_cfg)
    method_g_builder = MethodGraphBuilder(usr_cfg)

    if abstraction.lower() == "full":
        if ctx.obj['validate']:
            click.echo("Validate mode: abstraction level is {}".format(
                abstraction.lower()))
            sys.exit()
        class_g_builder.build_ddg(clear=ctx.obj['clear'])
        # We don't want to clear the table node twice.
        # Otherwise, we'll use the table nodes created above
        method_g_builder.build_ddg(clear=ctx.obj['clear'])

    elif abstraction.lower() == "class":
        if ctx.obj['validate']:
            click.echo("Validate mode: abstraction level is {}".format(
                abstraction.lower()))
            sys.exit()
        class_g_builder.build_ddg(clear=ctx.obj['clear'])

    elif abstraction.lower() == "method":
        if ctx.obj['validate']:
            click.echo("Validate mode: abstraction level is {}".format(
                abstraction.lower()))
            sys.exit()
        method_g_builder.build_ddg(clear=ctx.obj['clear'])

    else:
        raise click.BadArgumentUsage(
            "Not a valid abstraction level. Valid options are 'class', 'method', 'full'.")

    click.echo("code2graph build complete")
