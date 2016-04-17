
# Snake-cube solver

Brute-force solver for snake-cube puzzle. More info
[here](http://webhome.cs.uvic.ca/~mweston/snakes.html) or
[see video here](https://www.youtube.com/watch?v=iTzVPgFjE9c).


## Input specification

Tool consumes an input string from stdin. Alphabet consists of two characters:

 * **R** - joint going from right to left
 * **D** - joint going from up to down

Input has to contain exactly 26 characters (that's number of cubes minus one), no whitespaces inside the
sequence.

## How to represent your snake-cube puzzle

Unwind whole snake puzzle and put it flat on the table so that it goes from top-left to
bottom-down. You should see something like this:

```
XXX
  XX
   XX
    XX
     XX
      X
      XX
       XX
        XX
         XXX
           X
           XX
            XX
             X
```

Starting from top-left for each joint, write use R or D depending on whether
it is horizontal or vertical. So you end up with this:

```
RRDRDRDRDRDDRDRDRDRRDDRDRD
```

This is the input string for a solver.

There is a helper python script `drawing2rd.py` that transforms the 'drawing'
format to the R/D format. Have a look at `example.drawing` and `example.rd`
files. You can produce latter from the former by invoking:

```
cat example.drawing | python drawing2rd.py
```

## Implementation and usage

There are two implementations currently:

 * **snake_cube.py** - Simple python implementation. It is slow and does not de-duplicate results.
 * **snake_cube.scala** - Scala script, better optimized, prints out all unique solutions.

Example of invoking the solver:

```
echo RRDRDRDRDRDDRDRDRDRRDDRDRD | python snake_cube.py
echo RRDRDRDRDRDDRDRDRDRRDDRDRD | scala snake_cube.scala
```

Output will look like this:

```
Found solution:
  0  1  2
 23  4  3
 22  5  6

 25 26 13
 24 15 14
 21  8  7

 18 17 12
 19 16 11
 20  9 10
```

This is a representation of the final cube (3 x 3 in 3 layers). Numbers represent individual
pieces starting from the top left corner of the input - i.e. 0 is the top-left piece, 1 is
the second one...

This can be quite easily used to solve the real 'physical' puzzle.
