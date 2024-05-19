#include "./crypto.h"
#include <iostream>

unsigned char key[] = "01234567890123456789012345678901";

/* A 128 bit IV */
unsigned char iv[] = "0123456789012345";

/* Message to be encrypted */
unsigned char plaintext[] = "Hello World!!!!";

/* AAD */
unsigned char aad[] = "";

void testCBC();
void testGCM();

int main(){

    testGCM();
    return 0;
}

void testGCM(){
    std::cout << "------------------------GCM MODE------------------------------\n";

    unsigned char ciphertext[1000];
    unsigned char decryptedtext[1000];
    unsigned char tag[16];

    int plaintext_len = strlen((char *)plaintext);
    int aad_len = strlen((char *)aad);
    int iv_len = strlen((char *)iv);

    // Encrypt the plaintext
    int ciphertext_len = encrypt_gcm(plaintext, plaintext_len, aad, aad_len, key, iv, iv_len, ciphertext, tag);

//    printf("%d", ciphertext[ciphertext_len]);

    // Print the results

    // Allocate buffer for ciphertext and tag
    unsigned char ciphertext_and_tag[1000 + 16]; // Adjust sizes accordingly
    memset(ciphertext_and_tag, 0, sizeof(ciphertext_and_tag)); // Ensure the buffer is zero-initialized
    memcpy(ciphertext_and_tag, ciphertext, ciphertext_len);
    memcpy(ciphertext_and_tag + ciphertext_len, tag, 16);

    printf("CIPHERTEXT : ");
    for (int i = 0; i < ciphertext_len; i++) {
        printf("%02x", ciphertext[i]);
    }
    printf("\n");

    printf("TAG: ");
    for (int i = 0; i < 16; i++) {
        printf("%02x", tag[i]);
    }
    printf("\n");

    printf("CIPHER + TAG: ");
    for (int i = 0; i < ciphertext_len + 16; i++) {
        printf("%02x", ciphertext_and_tag[i]);
    }
    printf("\n");

    printf("----------------UNATTACKED MSG----------------\n");
    unsigned char tag_decrypt[16];
    unsigned char ciphertext_decrypt[1000];
    int msg_len = strlen((char*)ciphertext_and_tag);
    int cipher_len = msg_len - 16;
    memcpy(ciphertext_decrypt, ciphertext_and_tag, cipher_len);
    memcpy(tag_decrypt, ciphertext_and_tag + cipher_len, 16);

    int decryptedtext_len = decrypt_gcm(ciphertext_decrypt, cipher_len, aad, aad_len, tag_decrypt, key, iv, iv_len, decryptedtext);

    if (decryptedtext_len < 0) {
        printf("Decryption failed\n");
    } else {
        decryptedtext[decryptedtext_len] = '\0';
        printf("Decrypted text is:\n");
        printf("%s\n", decryptedtext);
    }
    printf("----------------ATTACKED MSG----------------\n");
    ciphertext_and_tag[5]++;
    memcpy(ciphertext_decrypt, ciphertext_and_tag, cipher_len);
    memcpy(tag_decrypt, ciphertext_and_tag + cipher_len, 16);

    decryptedtext_len = decrypt_gcm(ciphertext_decrypt, cipher_len, aad, aad_len, tag_decrypt, key, iv, iv_len, decryptedtext);

    if (decryptedtext_len < 0) {
        printf("Decryption failed\n");
    } else {
        decryptedtext[decryptedtext_len] = '\0';
        printf("Decrypted text is:\n");
        printf("%s\n", decryptedtext);
    }
    return;
}


void testCBC(){
    unsigned char ciphertext[1000];
    unsigned char decryptedText[1000];
    int plain_len = strlen(reinterpret_cast<const char *const>(plaintext));
    int decryptedText_len = 0, ciphertext_len = 0;

    ciphertext_len = encrypt_CBC(key, iv, plaintext,plain_len, ciphertext);
    std::cout << "CIPHER_TEXT: " << ciphertext << "\n";


    decryptedText_len = decrypt_CBC(key, iv, ciphertext, ciphertext_len, decryptedText);

    decryptedText[decryptedText_len] = '\0';
    std::cout << "DECRYPTED_TEXT: " << decryptedText << "\n";

}
