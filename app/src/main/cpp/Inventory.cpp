#include "Inventory.h"
#include <fstream>

void Inventory::addProduct(std::string upc, std::string name) {
    //adds a product object to the products vector, increments quantity if it's already there
    for (Product& product : products){
        if (product.upc == upc) {
            product.quantity++;
            return;
        }
    }

    Product product(upc, name);
    products.push_back(product);
}

int Inventory::getSize() {
    return products.size();
}

Product Inventory::getProduct(int index){
    return products[index];
}

void Inventory::save(const std::string& filename){
    std::ofstream file(filename);

    if (!file.is_open()){
        return;
    }

    for (auto& product : products) {

        file << product.upc << "|"
             << product.name << "|"
             << product.quantity << "\n";
    }
    file.close();
}

void Inventory::load(const std::string& filename){
    std::ifstream file(filename);

    if (!file.is_open()){
        return;
    }

    products.clear();

    std::string upc;
    std::string name;
    int quantity;

    while (std::getline(file, upc, '|')) {

        std::getline(file, name, '|');

        file >> quantity;
        file.ignore();

        Product product(upc, name);
        product.quantity = quantity;

        products.push_back(product);
    }
    file.close();
}

void Inventory::changeQuantity(int index, int amount) {
    if (index < 0 || index >= static_cast<int>(products.size())){
        return;
    }
    products[index].quantity += amount;
}

void Inventory::removeProduct(int index) {
    if (index < 0 || index >= static_cast<int>(products.size())){
        return;
    }
    products.erase(products.begin() + index);
}

void Inventory::editProductUPC(int index, std::string upc) {
    if (index < 0 || index >= static_cast<int>(products.size())){return;}

    products[index].upc = upc;
}

void Inventory::editProductName(int index, std::string name) {
    if (index < 0 || index >= static_cast<int>(products.size())){return;}

    products[index].name = name;
}

void Inventory::setCustomName(std::string upc, std::string name) {
    customNames[upc] = name;
}

std::string Inventory::getCustomName(std::string upc){
    auto it = customNames.find(upc);

    if (it != customNames.end()) {
        return it->second;
    }

    return "";
}

void Inventory::saveCustomNames(const std::string &filename) {
    std::ofstream file(filename);

    if(!file.is_open()) {
        return;
    }

    for (const auto& entry : customNames) {
        file <<entry.first << "|"
             <<entry.second << "\n";
    }

    file.close();
}

void Inventory::loadCustomNames(const std::string &filename) {
    std::ifstream file(filename);

    if(!file.is_open()){
        return;
    }

    customNames.clear();

    std::string upc;
    std::string name;

    while (std::getline(file, upc, '|')) {
        std::getline(file, name);

        customNames[upc] = name;
    }

    file.close();
}

void Inventory::removeCustomName(std::string upc) {
    customNames.erase(upc);
}