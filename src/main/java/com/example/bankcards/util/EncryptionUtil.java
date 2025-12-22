package com.example.bankcards.util;

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionUtil {
  @Value("${encryption.secret-key}")
  private String secretKey;

  @Value("${encryption.salt}")
  private String salt;

  public String encrypt(String data) {
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
      SecretKey tmp = factory.generateSecret(spec);
      SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(new byte[16]));

      byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      throw new RuntimeException("Encryption failed", e);
    }
  }

  public String decrypt(String encryptedData) {
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
      SecretKey tmp = factory.generateSecret(spec);
      SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(new byte[16]));

      byte[] decoded = Base64.getDecoder().decode(encryptedData);
      byte[] decrypted = cipher.doFinal(decoded);
      return new String(decrypted, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException("Decryption failed", e);
    }
  }

  public String maskCardNumber(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 4) {
      return "**** **** **** ****";
    }
    String lastFour = cardNumber.substring(cardNumber.length() - 4);
    return "**** **** **** " + lastFour;
  }
}
