#include "imagehelper.h"
#include <stdlib.h>

// TODO: implement imagehelper functions

FILE *openFileForReading(const char *filename){
    FILE *fp;
    fp = fopen(filename, "rb");

    return fp;
}

FILE *openFileForWriting(const char *filename){
    FILE *fp;
    fp = fopen(filename, "wb");
    return fp;
}

uint readBytesFromFile(FILE *fp, uint byteCount, char *buffer){
   return fread(buffer, byteCount, 1, fp);
}

uint writeBytesToFile(FILE *fp, uint byteCount, char *buffer){
    return fwrite(buffer,byteCount, 1, fp );
}

ImageInfo extractDataFromHeader(const char *header){
    ImageInfo * info = malloc(sizeof(ImageInfo));
    info->dataOffset = (header[10]&0xff)+(header[11]&0xff)*256+(header[12]&0xff)*256*256+(header[13]&0xff)*256*256*256;
    info->pxWidth = (header[18]&0xff)+(header[19]&0xff)*256+(header[20]&0xff)*256*256+(header[21]&0xff)*256*256*256;
    info->pxHeight = (header[22]&0xff)+(header[23]&0xff)*256+(header[24]&0xff)*256*256+(header[25]&0xff)*256*256*256;

    info->bitDepth = (header[28]&0xff)+(header[29]&0xff)*256;
    int extraBytes = (info->bitDepth*(info->pxWidth)/8)%4;
    if(extraBytes != 0){
        extraBytes = 4-extraBytes;
    }
    info->byteWidth = (((info->bitDepth)/8)*(info->pxWidth))+extraBytes; //total size/the amount of rows
    /*
    uint dataOffset; // Offset of the image data
    uint pxWidth;    // Width of the image in pixels
    uint pxHeight;   // Height of the image in pixels
    uint bitDepth;   // Number of bits used to represent a single pixel
    uint byteWidth;  // Width of a row in bytes
    */

    //printf(info->dataOffset);
    return *info;

}

void swapBytes(char *left, char *right, uint count){

}