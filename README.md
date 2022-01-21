# Summary

This project was done for the CG:SHOP (Computational Geometry: Solving Hard Optimization Problems Geometric Optimization Challenges). The problem is described here : https://cgshop.ibr.cs.tu-bs.de/. You will find a explanation of the data structure.

The goal is to partition a geometrical graph (the representation is important, the definition of planar here differs from the usual one) in a minimal number of without edge-crossing sub-graphs.

We transform the graph the folowwing way : each edges of the initial graph G becomes a node of the transformed graph G', and two nodes of G' are connected if and only if the two corresponding edges in G cross. Then we color G'.

In the principal file you will find a main.java file that runs the partitionning on each instance of the instances file and writes the solution in the solutions file. All files are JSON files.

We have implemented our DSATUR coloring algorithm optimizing as much as possible the data structure (using primitive arrays of optimal size) so that all instances can be computed on a 16GB RAM PC.
