# Bounding Box

Write a program that takes input from stdin with the following properties:
- Input is split into lines delimited by newline characters.
- Every line has the same length.
- Every line consists of an arbitrary sequence of hyphens ("-") and asterisks ("\*").
- The final line of input is terminated by a newline character.

In this challenge, each character in the input will have coordinates defined by `(line number, character number)`, starting at the top and left. So the first character on the first line will have the coordinates `(1,1)` and the fifth character on line 3 will have the coordinates `(3,5)`.

The program should find a box (or boxes) in the input with the following properties:
- The box must be defined by two pairs of coordinates corresponding to its top left and bottom right corners.
- It must be the **minimum bounding box** for some contiguous group of asterisks, with each asterisk being horizontally or vertically (but not diagonally) adjacent to at least one other asterisk in the group. The box should not _strictly_ bound the group, so the coordinates for the box in the following input should be `(2,2)(3,3)` not `(1,1)(4,4)`
    ```
    ----
    -**-
    -**-
    ----
    ```
- It should not overlap (i.e. share any characters with) any other minimum bounding boxes.
- Of all the non-overlapping, minimum bounding boxes in the input, _it should be the largest_.

If any boxes satisfying the conditions can be found in the input, the program should return an exit code of 0 and, for each box, print a line to stdout with the two pairs of coordinates.

So, given the file “groups.txt” with the following content:
```
**-------***
-*--**--***-
-----***--**
-------***--
```

Running your program with this input would look something like this:
```
> cat groups.txt | bounding-box
(1,1)(2,2)
```

This is because the larger groups on the right of the input have overlapping bounding boxes, so the returned coordinates bound the smaller group on the top left.
