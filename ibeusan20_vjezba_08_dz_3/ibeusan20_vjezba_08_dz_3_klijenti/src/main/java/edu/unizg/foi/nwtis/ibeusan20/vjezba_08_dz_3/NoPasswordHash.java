package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;


import java.util.Map;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

/**
 * Klasa NoPasswordHash.
 */
public class NoPasswordHash implements Pbkdf2PasswordHash {

  /**
   * Generiranje lozinke.
   *
   * @param password the password
   * @return the string
   */
  @Override
  public String generate(char[] password) {
    return password.toString();
  }

  /**
   * Verifikacija lozinke.
   *
   * @param password the password
   * @param hashedPassword the hashed password
   * @return true, if successful
   */
  @Override
  public boolean verify(char[] password, String hashedPassword) {
    var npassword = new String(password);
    if (npassword.trim().compareTo(hashedPassword.trim()) == 0) {
      return true;
    }
    return false;
  }

  /**
   * Inicijalizacija.
   *
   * @param parameters the parameters
   */
  @Override
  public void initialize(Map<String, String> parameters) {}

}
