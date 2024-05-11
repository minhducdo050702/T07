//
// Created by dangd on 4/27/2024.
//

#ifndef MY_APPLICATION2_CRYPTO_H
#define MY_APPLICATION2_CRYPTO_H

#include <openssl/evp.h>
#include <openssl/aes.h>
#include <openssl/err.h>
#include <string.h>
#include <iostream>
#include <iomanip>
#include <sstream>
using namespace std;
std::string toHexString(const unsigned char* data, int len) {
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (int i = 0; i < len; ++i) {
        ss << std::setw(2) << static_cast<unsigned>(data[i]);
    }
    return ss.str();
}

int encrypt_CBC(unsigned char *key, unsigned char *iv, unsigned char *plain, int plain_length, unsigned char *cipher) {
    int result = 0, len, outlen;
    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    if(!ctx) return -1;

    if(!EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv)) return -1;

    if(!EVP_EncryptUpdate(ctx, cipher, &len, plain, plain_length)) return -1;

    outlen = len;

    if(!EVP_EncryptFinal_ex(ctx, cipher + len, &len)) return -1;

    outlen += len;

    EVP_CIPHER_CTX_free(ctx);
    return outlen;
}

int decrypt_CBC(unsigned char *key, unsigned char *iv, unsigned char *cipher, int cipher_len, unsigned char *plain){
    int len = 0, plain_len = 0;

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    if(!ctx) return -1;

    if(!EVP_DecryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv)) return -1;

    if(cipher){
        if(!EVP_DecryptUpdate(ctx, plain, &len, cipher, cipher_len)) return -1;
        plain_len = len;
    }

    if(!EVP_DecryptFinal(ctx, plain+len, &len)) return -1;
    plain_len +=len;

    EVP_CIPHER_CTX_free(ctx);

    return plain_len;
}
string convert_hex_to_alphabet_string(string hex_string) {
    string result = "";
    for (int i = 0; i < hex_string.length(); i += 2) {
        string byte = hex_string.substr(i, 2);
        char chr = (char) (int)strtol(byte.c_str(), NULL, 16);
        result.push_back(chr);
    }
    return result;
}

#endif //MY_APPLICATION2_CRYPTO_H
