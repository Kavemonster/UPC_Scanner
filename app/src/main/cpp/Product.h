#ifndef PRODUCT_H
#define PRODUCT_H

#include <string>

class Product {
public:
    std::string upc;
    std::string name;
    int quantity;

    Product(std::string, std::string name);
};

#endif