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
    if (PKCS5_PBKDF2_HMAC(NULL, -1, aesSalt, saltLength, iterations, EVP_sha256(),
                          aesKeyLength, aesKey) != 1) {
        std::cerr << "Error deriving AES key." << std::endl;
        return "";
    }

    // Derive MAC key
    if (PKCS5_PBKDF2_HMAC(NULL, -1, macSalt, saltLength, iterations, EVP_sha256(),
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
void handleErrors(void)
{
    ERR_print_errors_fp(stderr);
    abort();
}


int hmac_it(const unsigned char *msg, size_t mlen, unsigned char **val, size_t *vlen, EVP_PKEY *pkey)
{
    /* Returned to caller */
    int result = 0;
    EVP_MD_CTX* ctx = NULL;
    size_t req = 0;
    int rc;

    if(!msg || !mlen || !val || !pkey)
        return 0;

    *val = NULL;
    *vlen = 0;

    ctx = EVP_MD_CTX_new();
    if (ctx == NULL) {
        printf("EVP_MD_CTX_create failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    rc = EVP_DigestSignInit(ctx, NULL, EVP_sha256(), NULL, pkey);
    if (rc != 1) {
        printf("EVP_DigestSignInit failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    rc = EVP_DigestSignUpdate(ctx, msg, mlen);
    if (rc != 1) {
        printf("EVP_DigestSignUpdate failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    rc = EVP_DigestSignFinal(ctx, NULL, &req);
    if (rc != 1) {
        printf("EVP_DigestSignFinal failed (1), error 0x%lx\n", ERR_get_error());
        goto err;
    }

    *val = (unsigned char*)OPENSSL_malloc(req);
    if (*val == NULL) {
        printf("OPENSSL_malloc failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    *vlen = req;
    rc = EVP_DigestSignFinal(ctx, *val, vlen);
    if (rc != 1) {
        printf("EVP_DigestSignFinal failed (3), return code %d, error 0x%lx\n", rc, ERR_get_error());
        goto err;
    }

    result = 1;


    err:
    EVP_MD_CTX_free(ctx);
    if (!result) {
        OPENSSL_free(*val);
        *val = NULL;
    }
    return result;
}

int verify_it(const unsigned char *msg, size_t mlen, const unsigned char *val, size_t vlen, EVP_PKEY *pkey)
{
    /* Returned to caller */
    int result = 0;
    EVP_MD_CTX* ctx = NULL;
    unsigned char buff[EVP_MAX_MD_SIZE];
    size_t size;
    int rc;

    if(!msg || !mlen || !val || !vlen || !pkey)
        return 0;

    ctx = EVP_MD_CTX_new();
    if (ctx == NULL) {
        printf("EVP_MD_CTX_create failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    rc = EVP_DigestSignInit(ctx, NULL, EVP_sha256(), NULL, pkey);
    if (rc != 1) {
        printf("EVP_DigestSignInit failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    rc = EVP_DigestSignUpdate(ctx, msg, mlen);
    if (rc != 1) {
        printf("EVP_DigestSignUpdate failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    size = sizeof(buff);
    rc = EVP_DigestSignFinal(ctx, buff, &size);
    if (rc != 1) {
        printf("EVP_DigestSignFinal failed, error 0x%lx\n", ERR_get_error());
        goto err;
    }

    result = (vlen == size) && (CRYPTO_memcmp(val, buff, size) == 0);
    err:
    EVP_MD_CTX_free(ctx);
    return result;
}

string genHMAC(string data, string key) {
    const unsigned char *msg = (const unsigned char *)data.c_str();
    const unsigned char *key_ = (const unsigned char *)key.c_str();
    unsigned char *val = NULL;
    size_t vlen = 0;
    EVP_PKEY *pkey = EVP_PKEY_new_mac_key(EVP_PKEY_HMAC, NULL, key_, key.length());
    if (!pkey) {
        printf("EVP_PKEY_new_mac_key failed, error 0x%lx\n", ERR_get_error());
        return "";
    }
    if (!hmac_it(msg, data.length(), &val, &vlen, pkey)) {
        printf("hmac_it failed\n");
        return "";
    }
    EVP_PKEY_free(pkey);
    string result = toHexString(val, vlen);
    OPENSSL_free(val);
    return result;
}


int checkHMAC(string data, string key, string hmac) {
    const unsigned char *msg = (const unsigned char *)data.c_str();
    const unsigned char *key_ = (const unsigned char *)key.c_str();
    //convert hmac back to unsigned char*
    unsigned char *hmac_ = (unsigned char *)malloc(hmac.length() / 2);
    for (int i = 0; i < hmac.length(); i += 2) {
        string byte = hmac.substr(i, 2);
        hmac_[i / 2] = (unsigned char) (int)strtol(byte.c_str(), NULL, 16);
    }
    EVP_PKEY *pkey = EVP_PKEY_new_mac_key(EVP_PKEY_HMAC, NULL, key_, key.length());
    if (!pkey) {
        printf("EVP_PKEY_new_mac_key failed, error 0x%lx\n", ERR_get_error());
        return 0;
    }
    int result = verify_it(msg, data.length(), hmac_, hmac.length() / 2, pkey);
    EVP_PKEY_free(pkey);
    return result;

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
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_openssldemo_HMac_genHmac(JNIEnv *env, jobject thiz, jstring data, jstring key) {
    const char *data_ = env->GetStringUTFChars(data, nullptr);
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    string hmac = genHMAC(data_, key_);
    env->ReleaseStringUTFChars(data, data_);
    env->ReleaseStringUTFChars(key, key_);
    return env->NewStringUTF(hmac.c_str());
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_openssldemo_HMac_verifyHmac(JNIEnv *env, jobject thiz, jstring data, jstring key,
                                             jstring hmac) {
    // TODO: implement verifyHmac()
    const char *data_ = env->GetStringUTFChars(data, nullptr);
    const char *key_ = env->GetStringUTFChars(key, nullptr);
    const char *hmac_ = env->GetStringUTFChars(hmac, nullptr);
    int result = checkHMAC(data_, key_, hmac_);
    env->ReleaseStringUTFChars(data, data_);
    env->ReleaseStringUTFChars(key, key_);
    env->ReleaseStringUTFChars(hmac, hmac_);
    return result;

}