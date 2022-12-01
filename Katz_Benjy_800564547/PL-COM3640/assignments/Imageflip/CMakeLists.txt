cmake_minimum_required(VERSION 3.23)
project(imageflip C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(imageflip
        imagehelper.c
        imagehelper.h
        main.c)
