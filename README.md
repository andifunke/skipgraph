# Skipgraph

A skipgraph is a robust, ordered data structure for distributed self-organising systems such as
peer-to-peer networks.
It allows access in logarithmic time via probabilistic assignment of its nodes to higher order
'shortcut'-levels.
A skip-graph is based on a network of doubly linked, disjoint skip-lists.

This skipgraph-implementation is a sketch for PacketSkip, an ordered index structure for efficient
storage and retrieval of capacity-information from network nodes.

#### Skiplist

![img/skiplist.png](img/skiplist.png)

#### Skipgraph

![img/skipgraph.png](img/skipgraph.png)

#### Actual implementation

![img/final.png](img/final.png)




### Requirement

- Java 8+
- graphviz (e.g.: `sudo apt install graphviz`)

### Compile

```
$ mkdir out
$ javac -d out -sourcepath src src/de/skipgraph/Main.java
```


### Run

```
$ cd out
$ java de.skipgraph.Main
```


### Results

The `main` class adds random capacity values to the skipgraph and performs search and delete
operations on the graph.
With each insert/leave operation (i.e. add or remove a node to/from the data structure) a dot-file
will be created in the graph/ folder in order to log the state of the skip graph.
