package komande;

public class KomandaQ implements Komanda {

    @Override
    public boolean izvrsi() {
        System.out.println("Program završava. Doviđenja!");
        return false;
    }
}
