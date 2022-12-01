#include "imagehelper.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/***************************************************
Expect two arguments in the following order:
Operation
Input file namepath

If less than two arguments were specified
Print: "Missing arguments"
Print: "Usage: imageflip <H|V|HV> <filename>"
Do not continue processing and return an error code of of MISSING_ARGUMENTS

If more than two arguments were specified:
Print: "Warning: Ignoring extra ? arguments"
(Continue processing)

If the operations were not one of {H, V, HV}:
Print: "Invalid operation: ?, must be one of {H, V, HV}"
Return an error code of INVALID_OPERATION
(Note, handle both upper- and lowercase letters)

If the input file could not be found/opened:
Print: "Could not open input file: filename"
Return an error code of INPUT_FILE_ERROR

 If the input file's header is incorrect
 (cannot read HEADER_SIZE byes, or signature is not BM):
 Print: "File is not a valid BMP file"
 Return an error code of INVALID_BMP_FILE

Assume an output file name of "out.bmp"
If the output file could not be opened:
Print: "Could not open output file for writing: out.bmp"
Return an error code of OUTPUT_FILE_ERROR

If multiple errors exist (except in the case of MISSING_ARGUMENTS)
Your return code should incorporate all of the relevant codes
e.g., INVALID_OPERATION, INVALID_BMP_FILE
e.g., INVALID_OPERATION, INVALID_BMP_FILE, OUTPUT_FILE_ERROR
e.g., INPUT_FILE_ERROR, OUTPUT_FILE_ERROR
****************************************************/

int main(int argc, char *argv[]) {
    int totalErrors = 0;
    // TODO: read and check program arguments
    if(argc<2){
        printf("Missing arguments\n");
        printf("Usage: imageflip <H|V|HV> <filename>\n");

        return MISSING_ARGUMENTS;
    }
    if(argc>3){
        printf("Warning: Ignoring extra %d arguments\n",argc-3);
    }
    /*
     * If the operations were not one of {H, V, HV}:
        Print: "Invalid operation: ?, must be one of {H, V, HV}"
        Return an error code of INVALID_OPERATION
     */
    if(strcmp(argv[1], "H") == 0 || strcmp(argv[1], "h") == 0) {
    }

    else if(strcmp(argv[1], "V") == 0 || strcmp(argv[1], "v") == 0) {
    }

    else if(strcmp(argv[1], "HV") == 0 || strcmp(argv[1], "hv") == 0) {
    }
    else{
        printf("Invalid operation: %s, must be one of {H, V, HV}\n", argv[1]);
        totalErrors+= INVALID_OPERATION;
    }

    // TODO: open the input file for reading

    FILE * fp = openFileForReading(argv[2]);
    if(fp == NULL){
        printf( "Could not open input file: %s\n",argv[2]);
        totalErrors += INPUT_FILE_ERROR;
        return totalErrors;
    }
    // TODO: open the output file for writing

    // TODO: read the header information from the input file

    char *header = (char*)malloc(54);


    readBytesFromFile(fp, 54, header);

    // TODO: validate the header by checking the signature
    if(!(header[0]=='B'&&header[1]=='M')){
        printf("File is not a valid BMP file\n");
        totalErrors+= INVALID_BMP_FILE;
    }
    // TODO: if at this point you have errors, go no further and return the errorcode
    // (if operation, input file & output file are good, then continue)
    if(totalErrors>0){
        return totalErrors;
    }

    // TODO: extract the data we need from the header
    ImageInfo headerData =  extractDataFromHeader(header);


    // TODO: print the image information to the stdout as below
    printf("px width: %d\n", headerData.pxWidth);
    printf("px height: %d\n", headerData.pxHeight);
    printf("bit depth: %d\n", headerData.bitDepth);
    printf("byte width: %d\n", headerData.byteWidth);
    printf("data offset: 0x%x\n", headerData.dataOffset);


    // TODO: print what you are going to do to stdout
    //  (one of the below, based on the operation)

    int horizontal = 0;
    if(strcmp(argv[1], "H") == 0 || strcmp(argv[1], "h") == 0) {
        printf("Flipping the image horizontally\n");
        horizontal = 1;
    }
    int vertical = 0;
    if(strcmp(argv[1], "V") == 0 || strcmp(argv[1], "v") == 0) {
        printf("Flipping the image vertically\n");
        vertical = 1;
    }
    int both = 0;
    if(strcmp(argv[1], "HV") == 0 || strcmp(argv[1], "hv") == 0) {
        printf("Flipping the image horizontally and vertically\n");
        both = 1;
    }

    // TODO: write header information to the output file
    FILE *outfp = fopen("out.bmp", "wb");
    if(outfp == NULL){
        printf( "Could not open output file for writing: out.bmp\n");
        totalErrors+=OUTPUT_FILE_ERROR;
        return totalErrors;
    }
    fwrite(header, 1, 54, outfp);

    char *preDataRegion = (char*)malloc(headerData.dataOffset-54);

    readBytesFromFile(fp, headerData.dataOffset-54, preDataRegion);
    fwrite(preDataRegion, 1, headerData.dataOffset-54, outfp);

    // TODO: Read in the image data from the input file
    if(horizontal||both) {
        for (int i = 0; i < headerData.pxHeight; i++) {
            //for every row bring into memory and create new row but in reverse
            int rowSize = headerData.pxWidth * headerData.bitDepth / 8;
            int paddingBytes = 0;
            if(rowSize % 4 != 0){
                paddingBytes = 4 - (rowSize % 4);
            }

            rowSize = rowSize + paddingBytes;
            char *oldRow = (char *) malloc(rowSize);
            char *newRow = (char *) malloc(rowSize);
            readBytesFromFile(fp, rowSize, oldRow);
            //padding is at the end of the row
            int oldStart = 0;
            for (int j = (rowSize - paddingBytes - (headerData.bitDepth / 8)); j >= 0; j -= (headerData.bitDepth / 8)) {
                for (int p = 0; p < headerData.bitDepth / 8; p++) {
                    newRow[oldStart] = oldRow[j + p];
                    oldStart++;
                }
            }
            for (int j = 0; j < paddingBytes; j++) {
                newRow[rowSize - 1 - j] = oldRow[rowSize - 1 - j];
            }
            writeBytesToFile(outfp, rowSize, newRow);
            free(newRow);
            free(oldRow);
            //padding?
        }
    }
    if(both){
        fseek(fp,headerData.dataOffset, SEEK_SET);
        fseek(outfp, headerData.dataOffset, SEEK_SET);

    }
    if(vertical||both) {

        for (int i = 0; i < headerData.pxHeight; i++) {
            int rowSize = headerData.pxWidth * headerData.bitDepth / 8;
            int paddingBytes = 0;
            if(rowSize % 4 != 0){
                paddingBytes = 4 - (rowSize % 4);
            }

            rowSize = rowSize + paddingBytes;
            char *oldRow = (char *) malloc(rowSize);
            char *newRow = (char *) malloc(rowSize);
            fseek(fp, headerData.dataOffset + headerData.pxHeight * rowSize - i * rowSize - rowSize, SEEK_SET);
            readBytesFromFile(fp, rowSize, oldRow);
            //padding is at the end of the row
            for (int j = 0; j < rowSize; j++) {
                newRow[j] = oldRow[j];
            }
            writeBytesToFile(outfp, rowSize, newRow);
            free(newRow);
            free(oldRow);

        }
    }
    // TODO: Transform the image data in memory
    // Iterate over the pixel data and transform the image data
    // such that the image is horizontally and/or vertically flipped

    // TODO: Write the transformed data to the output file

    // TODO: Release any memory
    free(header);
    free(preDataRegion);
    // TODO: Close the file handlers
    fclose(fp);
    fclose(outfp);
    return 0;
}

