cmake_minimum_required(VERSION 3.23)
project(release3)

set(CMAKE_CXX_STANDARD 14)

add_executable(release3 main.cpp Duck.cpp Duck.hpp AllMyDucks.cpp AllMyDucks.hpp Behaviors.hpp Behaviors.cpp)
