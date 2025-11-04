package ispisi;

/**
 * Sučelje Bridge uzorka koje definira apstrakciju za format ispisa.
 * <p>
 * Omogućuje različite implementacije ispisa (npr. tablični, tekstualni, JSON)
 * bez promjene adaptera koji pružaju podatke.
 * </p>
 */
public interface FormatIspisaBridge {
  /**
   * Ispisuje podatke korištenjem odgovarajućeg adaptera.
   *
   * @param adapter objekt adaptera koji sadrži podatke za ispis
   */
  void ispisi(Object adapter);
}
