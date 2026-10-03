#include <jni.h>
#include "Product.h"
#include "Inventory.h"

Inventory inventory;

extern "C"
JNIEXPORT jstring JNICALL

Java_com_example_upcscanner_NativeBridge_getMessage(
        JNIEnv* env,
        jobject){

    std::string message =
            "Inventory size: " +
            std::to_string(inventory.getSize());

    return env->NewStringUTF(message.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_upcscanner_NativeBridge_getProduct(
        JNIEnv* env,
        jobject,
        jint index) {

    Product product = inventory.getProduct(index);

    std::string message =
            "Product: " + product.name +
            "\nUPC: " + product.upc +
            "\nQuantity: " + std::to_string(product.quantity);

    return env->NewStringUTF(message.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_addProduct(
        JNIEnv* env,
        jobject,
        jstring upc,
        jstring name
        ){

    const char* upcChars = env->GetStringUTFChars(upc, nullptr);
    const char* nameChars = env->GetStringUTFChars(name, nullptr);

    std::string upcString(upcChars);
    std::string nameString(nameChars);

    inventory.addProduct(upcString, nameString);

    env->ReleaseStringUTFChars(upc, upcChars);
    env->ReleaseStringUTFChars(name, nameChars);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_upcscanner_NativeBridge_getSize(
        JNIEnv*,
        jobject
        ) {
    return inventory.getSize();
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_upcscanner_NativeBridge_getProductUPC(
        JNIEnv* env,
        jobject,
        jint index
        ) {
    Product product = inventory.getProduct(index);

    return env->NewStringUTF(product.upc.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_upcscanner_NativeBridge_getProductName(
        JNIEnv* env,
        jobject,
        jint index
        ) {
    Product product = inventory.getProduct(index);

    return env->NewStringUTF(product.name.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_upcscanner_NativeBridge_getProductQuantity(
        JNIEnv* env,
        jobject,
        jint index
        ){
    Product product = inventory.getProduct(index);

    return product.quantity;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_saveInventory(
        JNIEnv* env,
        jobject,
        jstring filename){

    const char* filenameChars =
            env->GetStringUTFChars(filename, nullptr);

    std::string filenameString(filenameChars);

    inventory.save(filenameString);

    env->ReleaseStringUTFChars(filename, filenameChars);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_loadInventory(
        JNIEnv* env,
        jobject,
        jstring filename) {
    const char* filenameChars =
            env->GetStringUTFChars(filename, nullptr);

    std::string filenameString(filenameChars);

    inventory.load(filenameString);

    env->ReleaseStringUTFChars(filename, filenameChars);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_changeQuantity(
        JNIEnv*,
        jobject,
        jint index,
        jint amount) {
    inventory.changeQuantity(index, amount);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_removeProduct(
        JNIEnv*,
        jobject,
        jint index) {
    inventory.removeProduct(index);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_editProductName(
        JNIEnv* env,
        jobject,
        jint index,
        jstring name) {

    const char* nameChars =
            env->GetStringUTFChars(name, nullptr);

    std::string nameString(nameChars);

    inventory.editProductName(index, nameString);

    env->ReleaseStringUTFChars(name, nameChars);
}

extern "C"

JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_editProductUPC(
        JNIEnv* env,
        jobject,
        jint index,
        jstring upc) {

    const char* upcChars =
            env->GetStringUTFChars(upc, nullptr);

    std::string upcString(upcChars);

    inventory.editProductUPC(index, upcString);

    env->ReleaseStringUTFChars(upc, upcChars);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_upcscanner_NativeBridge_getCustomName(
        JNIEnv* env,
        jobject,
        jstring upc) {

    const char* upcChars =
            env->GetStringUTFChars(upc, nullptr);

    std::string upcString(upcChars);

    std::string customName = inventory.getCustomName(upcString);

    env->ReleaseStringUTFChars(upc, upcChars);

    return env->NewStringUTF(customName.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_setCustomName(
        JNIEnv* env,
        jobject,
        jstring upc,
        jstring name) {

    const char* upcChars =
            env->GetStringUTFChars(upc, nullptr);

    const char* nameChars =
            env->GetStringUTFChars(name, nullptr);

    std::string upcString(upcChars);
    std::string nameString(nameChars);

    inventory.setCustomName(upcString, nameString);

    env->ReleaseStringUTFChars(upc, upcChars);
    env->ReleaseStringUTFChars(name, nameChars);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_saveCustomNames(
        JNIEnv* env,
        jobject,
        jstring filename) {

    const char* filenameChars =
            env->GetStringUTFChars(filename, nullptr);

    std::string filenameString(filenameChars);

    inventory.saveCustomNames(filenameString);

    env->ReleaseStringUTFChars(filename, filenameChars);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_loadCustomNames(
        JNIEnv* env,
        jobject,
        jstring filename) {

    const char* filenameChars =
            env->GetStringUTFChars(filename, nullptr);

    std::string filenameString(filenameChars);

    inventory.loadCustomNames(filenameChars);

    env->ReleaseStringUTFChars(filename, filenameChars);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_upcscanner_NativeBridge_removeCustomName(
        JNIEnv* env,
        jobject,
        jstring upc) {

    const char* upcChars =
            env->GetStringUTFChars(upc, nullptr);

    std::string upcString(upcChars);

    inventory.removeCustomName(upcString);

    env->ReleaseStringUTFChars(upc, upcChars);
}