#ifndef __IMAGEHELP_H__
#define __IMAGEHELP_H__

#include <stdio.h>

typedef unsigned int uint;

#define MISSING_ARGUMENTS 0b1
#define INVALID_OPERATION 0b10
#define INPUT_FILE_ERROR  0b100
#define OUTPUT_FILE_ERROR 0b1000
#define INVALID_BMP_FILE  0b10000

// TODO: determine the proper offsets from the BMP specs
#define HEADER_SIZE 0
#define DATAOFFSET_OFFSET 0
#define WIDTH_OFFSET 0
#define HEIGHT_OFFSET 0
#define BITS_PER_PX_OFFSET 0

typedef struct {
    uint dataOffset; // Offset of the image data
    uint pxWidth;    // Width of the image in pixels
    uint pxHeight;   // Height of the image in pixels
    uint bitDepth;   // Number of bits used to represent a single pixel
    uint byteWidth;  // Width of a row in bytes
} ImageInfo;

FILE *openFileForReading(const char *filename);

FILE *openFileForWriting(const char *filename);

uint readBytesFromFile(FILE *fp, uint byteCount, char *buffer);

uint writeBytesToFile(FILE *fp, uint byteCount, char *buffer);

ImageInfo extractDataFromHeader(const char *header);

void swapBytes(char *left, char *right, uint count);

#endif
