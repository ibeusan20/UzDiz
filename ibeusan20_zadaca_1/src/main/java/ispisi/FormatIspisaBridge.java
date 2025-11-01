package ispisi;

/**
 * Bridge sučelje za format ispisa.
 * Omogućuje ispis bilo kojeg tipa adaptera.
 */
public interface FormatIspisaBridge {
    void ispisi(Object adapter);
}