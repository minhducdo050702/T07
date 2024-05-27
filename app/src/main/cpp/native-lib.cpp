#include <jni.h>
#include <string>
#include <openssl/rand.h>
#include <openssl/evp.h>
#include <openssl/sha.h>
#include <iomanip>
#include <sstream>
#include <cstring>
#include <openssl/aes.h>
#include <openssl/err.h>
#include <string.h>
#include <iostream>
#include <vector>
#include "crypto.h"
#include <bitset>


#define LOG_TAG "CRYPTO"
//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
//#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


using namespace std;

const int masterKeyLength = 32; // Master key length in bytes (256 bits)
const int aesKeyLength = 32;    // AES key length in bytes (256 bits)
const int saltLength = 32;      // Salt length in bytes
const int n = 16384;            // CPU/memory cost parameter
const int r = 8;                // Block size parameter
const int p = 1;                // Parallelization parameter

std::string genKey(string masterkey_) {
    unsigned char masterKey[masterKeyLength];
    unsigned char aesKey[aesKeyLength];
    unsigned char aesSalt[saltLength];
    for (int i = 0; i < masterKeyLength; i++) {
        masterKey[i] = std::bitset<8>(masterkey_.substr(i * 8, 8)).to_ulong();
    }
    // Generate random salts for AES key
    if (RAND_bytes(aesSalt, saltLength) != 1) {
        std::cerr << "Error generating random bytes for AES salt." << std::endl;
        return "";
    }
    // Derive AES key using scrypt
    if (EVP_PBE_scrypt(reinterpret_cast<const char*>(masterKey), masterKeyLength,
                       aesSalt, saltLength, n, r, p, 0, aesKey, aesKeyLength) != 1) {
        std::cerr << "Error deriving AES key using scrypt." << std::endl;
        return "";
    }
    // Convert key to binary string
    std::string binary_key;
    for (size_t i = 0; i < aesKeyLength; i++) {
        binary_key += std::bitset<8>(aesKey[i]).to_string();
    }
    return binary_key;
}

std::string genMasterKey() {
    unsigned char masterKey[masterKeyLength];
    if (RAND_bytes(masterKey, masterKeyLength) != 1) {
        std::cerr << "Error generating random bytes for master key." << std::endl;
        return "";
    }
    std::string binary_master_key;
    for (size_t i = 0; i < masterKeyLength; i++) {
        binary_master_key += std::bitset<8>(masterKey[i]).to_string();
    }
    return binary_master_key;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_MainActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello from JNI!");
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_Register_genKey(JNIEnv *env, jobject thiz, jstring master_key_) {
    const char *master_key = env->GetStringUTFChars(master_key_, nullptr);
    //convert master_key to string
    string master_key_str(master_key);
    string key = genKey(master_key_str);
    env->ReleaseStringUTFChars(master_key_, master_key);
    return env->NewStringUTF(key.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_Register_genMasterKey(JNIEnv *env, jobject thiz) {
    std::string master_key = genMasterKey();
    return env->NewStringUTF(master_key.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_EncryptDecrypt_encryptGCM(JNIEnv *env, jobject thiz, jstring key,
                                                       jstring iv, jstring plain_data) {

    const char *key_c = env->GetStringUTFChars(key, nullptr);
    const char *iv_c = env->GetStringUTFChars(iv, nullptr);
    const char *plain_data_c = env->GetStringUTFChars(plain_data, nullptr);

    unsigned char ciphertext[1000]= {0};
    unsigned char tag[16] = {0};
    unsigned char aad_c[] = "";
    int plaintext_len = strlen(plain_data_c);
    int aad_len = strlen((char *)aad_c);
    int iv_len = strlen(iv_c);

    int ciphertext_len = encrypt_gcm((unsigned char*)plain_data_c, plaintext_len, aad_c, aad_len, (unsigned char*)key_c, (unsigned char*)iv_c, iv_len, ciphertext, tag);

    // Allocate buffer for ciphertext and tag
    unsigned char ciphertext_and_tag[1000 + 16]; // Adjust sizes accordingly
    memset(ciphertext_and_tag, 0, sizeof(ciphertext_and_tag)); // Ensure the buffer is zero-initialized
    memcpy(ciphertext_and_tag, ciphertext, ciphertext_len);
    memcpy(ciphertext_and_tag + ciphertext_len, tag, 16);

    int ciphertext_and_tag_len = strlen((char*)ciphertext_and_tag);

    std::string ciphertext_and_tag_str = toHexString(ciphertext_and_tag, ciphertext_and_tag_len);

    // Release memory
    env->ReleaseStringUTFChars(key, key_c);
    env->ReleaseStringUTFChars(iv, iv_c);
    env->ReleaseStringUTFChars(plain_data, plain_data_c);

    // Return the cipher_text as a Java string
    return env->NewStringUTF(ciphertext_and_tag_str.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_EncryptDecrypt_decryptGCM(JNIEnv *env, jobject thiz, jstring key,
                                                       jstring iv, jstring cipherText) {
    // TODO: implement decryptGCM()
    const char *key_c = env->GetStringUTFChars(key, nullptr);
    const char *iv_c = env->GetStringUTFChars(iv, nullptr);
    const char *ciphertext_and_tag = env->GetStringUTFChars(cipherText, nullptr);

    std::string cipher_text_and_tag_str(ciphertext_and_tag);
    env->ReleaseStringUTFChars(cipherText, ciphertext_and_tag);

    std::vector<unsigned char> cipher_text_and_tag_array(cipher_text_and_tag_str.length()/2);
    for (size_t i = 0; i < cipher_text_and_tag_str.length(); i += 2) {
        std::string byte = cipher_text_and_tag_str.substr(i, 2);
        cipher_text_and_tag_array[i/2] = static_cast<unsigned char>(std::stoul(byte, nullptr, 16));
    }

    std::vector<unsigned char> decryptedMsg(1000);
    std::vector<unsigned char> tag(cipher_text_and_tag_array.end()-16, cipher_text_and_tag_array.end());
    std::vector<unsigned char> ciphertext(cipher_text_and_tag_array.begin(),cipher_text_and_tag_array.end()-16);

    unsigned char aad_c[] = "";
    int ciphertext_and_tag_len = strlen(ciphertext_and_tag);
    int aad_len = strlen((char *)aad_c);
    int iv_len = strlen(iv_c);

    int decryptedtext_len = decrypt_gcm(ciphertext.data(), ciphertext.size() , aad_c, aad_len, tag.data(), (unsigned char*) key_c,(unsigned char*) iv_c, iv_len, decryptedMsg.data());

    if (decryptedtext_len < 0) {
        env->ReleaseStringUTFChars(key, key_c);
        env->ReleaseStringUTFChars(iv, iv_c);

        return env->NewStringUTF("Decryption failed\n");
    }else {
        env->ReleaseStringUTFChars(key, key_c);
        env->ReleaseStringUTFChars(iv, iv_c);

        decryptedMsg[decryptedtext_len] = '\0'; // Ensure null-termination
        return env->NewStringUTF(reinterpret_cast<const char *>(decryptedMsg.data()));
    }
}