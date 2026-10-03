cmake_minimum_required(VERSION 3.22.1)

project("upcscanner")

add_library(
        native-lib
        SHARED
        native-lib.cpp
        Product.cpp
        Inventory.cpp
)