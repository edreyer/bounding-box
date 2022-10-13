# Bounding Box submission

This application fulfills the requirements as outlined in the BoundingBox.md document.

The gist of the algorithm is to:
1) read all input, looking for `*` characters, noting the coordinates of that 
asterisk (`Star`), and adding information to a `Set<Star>` to be processed later.
2) This approach allows us to toss the `-` characters into the garbage, so for 
very large inputs, we can keep only the relevant data.
3) We peel `Star` elements off the `Set<Star>` collection and start turning them
into `BoundingBox` instances by using a basic graph navigation algorithm. Each time
we find that a `Star` is part of a box, we remove it from the `Set`.
4) At the end of this process we have a `Set<BoundingBox>` collection and have
tossed the individual `Star` instances into the garbage, since they have served their 
purpose.
5) At this point, we cull the overlappong `BoundingBox` instances, then 
6) Determine which of the remaining boxes is/are the largest.

Notes:
* I wrote a unit test that checks for a number of configurations that I could think of.
Hopefully I didn't miss any cases that would cause the app to fail.
* This app uses a `State` object to help track data as I move through. It has mutable
state, which isn't ideal, but is performant.
* If I had more time I'd follow functional programming tenets and make State immutable.
* In that case I could probably safely parallelize the graph navigation and possibly 
the bit where I find overlapping boxes.
