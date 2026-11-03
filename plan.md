## GOALS
(necessary)
- Process mp4 videos
- Use old centroid code
- Output a csv file
- first col: number of seconds
- second and third: x and y (-1, -1) if no centroid

## Focus on:
Object oriented, multiple classes
Take in arguments (that error when not correct)

**No reason to output a new video**

1. Deconstruct the video (VideoDeconstructor)
2. Frame by frame finding centroids (call GroupFinder)
3. Put them in a list
4. Build the csv looping through centroids

another class pull frames

biggestframe = null;
for (frame in frames) {
    if !biggestframe = frame;
    else if (biggestframe < frame) biggestframe = frame;
}

csv.output(biggestframe);

java -jar videoprocesser.jar inputPath outputCsv targetColor threshold