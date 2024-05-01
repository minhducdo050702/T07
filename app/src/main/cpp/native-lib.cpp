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
using namespace std;
void handleErrors(void)
{
    unsigned long errCode;

    printf("An error occurred\n");
    errCode = ERR_get_error();
    while(errCode)
    {
        char *err = ERR_error_string(errCode, NULL);
        printf("%s\n", err);
        errCode = ERR_get_error();
    }
    abort();
}

int encrypt_CBC(unsigned char *key, unsigned char *iv, unsigned char *plain, int plain_length, unsigned char *cipher) {
    //key: 32byte 256bit
    //iv: 16byte 128bit
    int result = 0, encrypt = 1, outlen, len;
    EVP_CIPHER_CTX *ctx = NULL;
    //Init context
    ctx = EVP_CIPHER_CTX_new();
    if(!ctx) handleErrors();
    //Init encryption process
    if(!EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))handleErrors();
    //Provider plain msg
    if(!EVP_EncryptUpdate(ctx, cipher, &len, plain, plain_length)) handleErrors();

    outlen = len;
    //Final encryption
    if(!EVP_EncryptFinal_ex(ctx, cipher + len, &len)) handleErrors();

    outlen += len;

    //Clean up
    EVP_CIPHER_CTX_free(ctx);
    return outlen;
}

int decrypt_CBC(unsigned char *key, unsigned char *iv, unsigned char *cipher, int cipher_len, unsigned char *plain){
    //key: 32byte 256bit
    //iv: 16byte 128bit
    int len = 0, plain_len = 0, ret;

    EVP_CIPHER_CTX *ctx = NULL;

    ctx = EVP_CIPHER_CTX_new();

    if(!ctx) handleErrors();

    if(!EVP_DecryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv))handleErrors();

    if(cipher){
        if(!EVP_DecryptUpdate(ctx, plain, &len, cipher, cipher_len))
            handleErrors();
        plain_len = len;
    }

    if(!EVP_DecryptFinal(ctx, plain+len, &len)) handleErrors();
    plain_len +=len;

    EVP_CIPHER_CTX_free(ctx);

    return plain_len;
}
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
Java_com_example_openssldemo_Register_genKey(JNIEnv *env, jobject thiz) {
    // Call the genKey function to generate the key
    std::string key = genKey();

    // Return the key as a Java string
    return env->NewStringUTF(key.c_str());
}
