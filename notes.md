KJ's AGREEMENT

# KJ's Notes

## ImageSummaryApp
- Takes in a target image, hex color, and a threshold (for what?)
- Parses over arguments in input command
- Binarize, buffer, write out image.

## ColorDistanceFinder
"Defines an interface for computing the distance between two colors."
 - Uses hex codes
 - How would we calculate the distance between colors?

 ## ImageBinarizer
 "An interface for converting between RBG.. and binary (B&W)"
- Binary image represented as int[][].
- 1 = white, 0 = black
- Contains two methods:
**toBinaryArray(BufferedImage image)**
BufferedImage -> int[][]

**toBufferedImage(int[][] image)**
int[][] -> BufferedImage
