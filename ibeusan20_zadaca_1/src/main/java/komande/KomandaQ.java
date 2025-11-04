package komande;

/**
 * Komanda Q - završetak rada programa.
 */
public class KomandaQ implements Komanda {

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    System.out.println("Program završava. Doviđenja!");
    return false;
  }
}
