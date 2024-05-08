#include <jni.h>
#include <string>
#include <iostream>
#include <openssl/rand.h>
#include <openssl/evp.h>
#include <openssl/sha.h>
#include <iomanip>
#include <sstream>
#include <cstring>
#include <openssl/aes.h>
#include <openssl/err.h>
#include <string.h>
#include "crypto.h"

using namespace std;

std::string genKey() {
    const int masterKeyLength = 32; // Master key length in bytes (256 bits)
    const int aesKeyLength = 32;    // AES key length in bytes (256 bits)
    const int macKeyLength = 32;    // MAC key length in bytes (256 bits)
    const int saltLength = 32;      // Salt length in bytes
    const int iterations = 1000;    // Iteration count

    unsigned char masterKey[masterKeyLength];
    unsigned char aesKey[aesKeyLength];
    unsigned char macKey[macKeyLength];
    unsigned char aesSalt[saltLength]; // SHA-256 salt length
    unsigned char macSalt[saltLength]; // SHA-256 salt length

    // Generate random master key
    if (RAND_bytes(masterKey, masterKeyLength) != 1) {
        std::cerr << "Error generating random bytes for master key." << std::endl;
        return "";
    }

    // Generate random salts for AES key and MAC key
    if (RAND_bytes(aesSalt, saltLength) != 1) {
        std::cerr << "Error generating random bytes for AES salt." << std::endl;
        return "";
    }
    if (RAND_bytes(macSalt, saltLength) != 1) {
        std::cerr << "Error generating random bytes for MAC salt." << std::endl;
        return "";
    }

    // Derive AES key
    if (PKCS5_PBKDF2_HMAC("password", 8, aesSalt, saltLength, iterations, EVP_sha256(),
                          aesKeyLength, aesKey) != 1) {
        std::cerr << "Error deriving AES key." << std::endl;
        return "";
    }

    // Derive MAC key
    if (PKCS5_PBKDF2_HMAC("password", 8, macSalt, saltLength, iterations, EVP_sha256(),
                          macKeyLength, macKey) != 1) {
        std::cerr << "Error deriving MAC key." << std::endl;
        return "";
    }

    // Concatenate master key, AES key, and MAC key
    std::string key(reinterpret_cast<char*>(masterKey), masterKeyLength);
    key.append(reinterpret_cast<char*>(aesKey), aesKeyLength);
    key.append(reinterpret_cast<char*>(macKey), macKeyLength);

    //convert key to binary string
    std::string binary_key;
    for (int i = 0; i < key.size(); i++) {
        binary_key += std::bitset<8>(key[i]).to_string();
    }
    return binary_key;

}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello from JNI!");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_Register_genKey(JNIEnv *env, jobject) {
    // Call the genKey function to generate the key
    std::string key = genKey();

    // Return the key as a Java string
    return env->NewStringUTF(key.c_str());
}

extern "C" JNIEXPORT jstring JNICALL Java_com_example_openssldemo_EncryptDecrypt_encrypt(JNIEnv *env,  jobject,
                                                                                         jstring jKey,
                                                                                         jstring jIv,
                                                                                         jstring jPlainText){
    unsigned char cipherText[1000] = "";

    const char *key = env->GetStringUTFChars(jKey, nullptr);
    const char *iv = env->GetStringUTFChars(jIv, nullptr);
    const char *plainText = env->GetStringUTFChars(jPlainText, nullptr);

    int plainLen = strlen(plainText);
    int cipherLen = encrypt_CBC((unsigned char *) key, (unsigned char *) iv,
                                (unsigned char *) plainText, plainLen, cipherText);

    std::string cipherTextHex = toHexString(cipherText, cipherLen);
    return env->NewStringUTF(cipherTextHex.c_str());
}

extern "C" JNIEXPORT jstring JNICALL Java_com_example_openssldemo_EncryptDecrypt_decrypt(JNIEnv *env,  jobject,
                                                                                         jstring jKey,
                                                                                         jstring jIv,
                                                                                         jstring jCipherText) {
    unsigned char decryptedMsg[1000] = "";
    const char *key = env->GetStringUTFChars(jKey, nullptr);
    const char *iv = env->GetStringUTFChars(jIv, nullptr);
    const char *cipherText = env->GetStringUTFChars(jCipherText, nullptr);



    int cipherLen = strlen(cipherText);
    int decryptedLen = decrypt_CBC((unsigned char *) key, (unsigned char *) iv,
                                   (unsigned char *) cipherText, cipherLen, decryptedMsg);

    if (decryptedLen < 0) {
        //LOGD("MSG is changed");

    }
    std::string decipherTextHex = toHexString(decryptedMsg, decryptedLen);
    return env->NewStringUTF(decipherTextHex.c_str());
}