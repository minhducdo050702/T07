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
#include <openssl/evp.h>
#include <openssl/aes.h>
#include <openssl/err.h>
#include <string.h>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <vector>

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

    if(!EVP_DecryptFinal_ex(ctx, plain+len, &len)) return -1;
    plain_len += len;

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

    // Convert key to binary string
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


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_EncryptDecrypt_encrypt(JNIEnv *env, jobject thiz, jstring key,
                                                    jstring iv, jstring plain_data) {

    // Convert the key, IV, and plain_data to C++ unsigned char arrays
    const char *key_c = env->GetStringUTFChars(key, nullptr);
    const char *iv_c = env->GetStringUTFChars(iv, nullptr);
    const char *plain_data_c = env->GetStringUTFChars(plain_data, nullptr);
    unsigned char ciphertext[1000]= {0};
    int cipher_len= encrypt_CBC((unsigned char *) key_c, (unsigned char *) iv_c,
                                (unsigned char *) plain_data_c, strlen(plain_data_c), ciphertext);


 std::string cipher_text = toHexString(ciphertext, cipher_len);

    // Release memory
    env->ReleaseStringUTFChars(key, key_c);
    env->ReleaseStringUTFChars(iv, iv_c);
    env->ReleaseStringUTFChars(plain_data, plain_data_c);

    // Return the cipher_text as a Java string
    return env->NewStringUTF(cipher_text.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_EncryptDecrypt_decrypt(JNIEnv *env, jobject thiz, jstring key,
                                                    jstring iv, jstring cipher_text) {

    const char *key_c = env->GetStringUTFChars(key, nullptr);
    const char *iv_c = env->GetStringUTFChars(iv, nullptr);
    const char *cipher_text_c = env->GetStringUTFChars(cipher_text, nullptr);
    std::string cipher_text_str(cipher_text_c);
    env->ReleaseStringUTFChars(cipher_text, cipher_text_c);

    std::vector<unsigned char> cipher_text_array(cipher_text_str.length()/2);
    for (size_t i = 0; i < cipher_text_str.length(); i += 2) {
        std::string byte = cipher_text_str.substr(i, 2);
        cipher_text_array[i/2] = static_cast<unsigned char>(std::stoul(byte, nullptr, 16));
    }

    std::vector<unsigned char> decrypted(1000);
    int decrypted_len = decrypt_CBC((unsigned char*)key_c, (unsigned char*)iv_c, cipher_text_array.data(),
                                    cipher_text_array.size(), decrypted.data());

    env->ReleaseStringUTFChars(key, key_c);
    env->ReleaseStringUTFChars(iv, iv_c);



    decrypted[decrypted_len] = '\0'; // Ensure null-termination

    return env->NewStringUTF(reinterpret_cast<char*>(decrypted.data()));
}