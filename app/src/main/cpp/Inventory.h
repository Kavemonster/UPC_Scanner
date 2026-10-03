#ifndef INVENTORY_H
#define INVENTORY_H

#include "Product.h"
#include <vector>
#include <unordered_map>

class Inventory{
private:
    std::vector<Product> products;
    std::unordered_map<std::string, std::string> customNames;

public:
    void addProduct(std::string upc, std::string name);
    int getSize();
    void save(const std::string& filename);
    void load(const std::string& filename);
    Product getProduct(int index);
    void changeQuantity(int index, int amount);
    void removeProduct(int index);
    void editProductName(int index, std::string name);
    void editProductUPC(int index, std::string upc);
    void setCustomName(std::string upc, std::string name);
    std::string getCustomName(std::string upc);
    void saveCustomNames(const std::string& filename);
    void loadCustomNames(const std::string& filename);
    void removeCustomName(std::string upc);
};

#endif