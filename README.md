# Lattice_Measurements
Lattice Based Measurements

For that measurements run the main class LatticeMeasurements.java in src/gr/forth/ics/isl/lattice

For example, for coverage an indicative output is:

Select the Measurement Type: Commonalities or Coverage
Coverage
Select the Dataset: EntitiesDesc, LiteralsDesc, TriplesDesc
EntitiesDesc
Select the Desired Approach for computing Coverage: STRAIGHTFORWARD,LATTICE_BASED_PRUNING,LATTICE_BASED_NO_PRUNING
LBDC
Select the minimum Number of Subsets:
10
Select the maximum Number of Subsets:
15
Print the coverage of all the subsets? true or false
false
10 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.229
11 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.116
12 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.133
13 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.244
14 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.701
15 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.378




For example, for commonalities an indicative output is

Select the Measurement Type: Commonalities or Coverage
Commonalities
Select the Dataset: Entities, Literals, Triples
Entities
Select the Desired Approach for computing Commonalities: STRAIGHTFORWARD,LATTICE_BASED_PRUNING,LATTICE_BASED_NO_PRUNING,TOP-DOWN
LATTICE_BASED_PRUNING
Select the minimum Number of Subsets:
10
Select the maximum Number of Subsets:
15
Print the commonalities of all the subsets? true or false
false
10 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.305
11 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.228
12 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.273
13 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.283
14 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.318
15 Subsets	Approach:LATTICE_BASED_PRUNING	Time (Seconds):0.303




