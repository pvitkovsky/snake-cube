
# Snake-cube solver

Brute-force solver for snake-cube puzzle ([see video here](https://www.youtube.com/watch?v=iTzVPgFjE9c)).

Input format consists of a "snake drawing" in plain ascii file. You unwind the whole snake and position it so that it begins
in the top left corner and continues just down or to the right. See snake_cube.input for example.

Then you invoke solver like this:

```
cat snake_cube.input | python process_input.py | python snake_cube.py
```

Output will contain all solutions that algorithm found, however majority of them will be 'mirrored' or 'turned around' versions
of the same solution. Single solution looks like this:

```
Found solution:
18 13 12 
19 14 11 
20 9  10 

17 16 3  
26 15 4  
21 8  7  

0  1  2  
25 24 5  
22 23 6  
```

This is a representation of the final cube (3 x 3 in 3 layers). Numbers represent individual pieces starting from the 
top left corner of the input - i.e. 0 is the top-left piece, 1 is the second one...

This can be quite easily used to solve the real 'physical' puzzle.


