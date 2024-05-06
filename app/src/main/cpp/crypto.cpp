#include "./crypto.h"
#include <iostream>
int main(){
    /* A 256 bit key */
    unsigned char key[] = "01234567890123456789012345678901";

    /* A 128 bit IV */
    unsigned char iv[] = "0123456789012345";

    /* Message to be encrypted */
    unsigned char plaintext[] = "Hello World!!!!";

    unsigned char ciphertext[1000];
    unsigned char decryptedText[1000];
    int plain_len = strlen(reinterpret_cast<const char *const>(plaintext));
    int decryptedText_len = 0, ciphertext_len = 0;

    ciphertext_len = encrypt_CBC(key, iv, plaintext,plain_len, ciphertext);
    std::cout << "CIPHER_TEXT: " << ciphertext << "\n";


    decryptedText_len = decrypt_CBC(key, iv, ciphertext, ciphertext_len, decryptedText);

    decryptedText[decryptedText_len] = '\0';
    std::cout << "DECRYPTED_TEXT: " << decryptedText << "\n";

    return 0;
}