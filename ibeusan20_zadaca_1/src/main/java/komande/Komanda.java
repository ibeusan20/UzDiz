package komande;

public interface Komanda {

  /**
   * Izvršava komandu.
   *
   * @return {@code true} ako se program nastavlja, {@code false} ako treba završiti rad
   */
  boolean izvrsi();
}
