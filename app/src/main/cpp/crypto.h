//
// Created by dangd on 4/27/2024.
//

#ifndef MY_APPLICATION2_CRYPTO_H
#define MY_APPLICATION2_CRYPTO_H

#include <openssl/evp.h>
#include <openssl/aes.h>
#include <openssl/err.h>
#include <string.h>

std::string toHexString(const unsigned char* data, int len) {
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (int i = 0; i < len; ++i) {
        ss << std::setw(2) << static_cast<unsigned>(data[i]);
    }
    return ss.str();
}

void handleErrors(void)
{
//    unsigned long errCode;
//
//    printf("An error occurred\n");
//    while(errCode == ERR_get_error())
//    {
//        char *err = ERR_error_string(errCode, NULL);
//        printf("%s\n", err);
//    }
//    abort();
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

#endif //MY_APPLICATION2_CRYPTO_H
