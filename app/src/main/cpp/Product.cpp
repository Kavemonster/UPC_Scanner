#include "Product.h"

Product::Product(std::string upc, std::string name){
    this->upc = upc;
    this->name = name;
    this->quantity = 1;
}