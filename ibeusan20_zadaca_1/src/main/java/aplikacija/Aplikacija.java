package aplikacija;

import datoteke.*;
import model.*;
import logika.*;
import java.util.List;

public class Aplikacija {

  public static void main(String[] args) {
      Argumenti argumenti = new Argumenti(args);

      UcitavacPodataka<Aranzman> ucitacAranzmana =
              UcitavacFactory.createAranzmanReader();
      UcitavacPodataka<Rezervacija> ucitacRezervacija =
              UcitavacFactory.createRezervacijaReader();

      List<Aranzman> aranzmani =
              ucitacAranzmana.ucitaj(argumenti.dohvatiPutanjuAranzmana());
      List<Rezervacija> rezervacije =
              ucitacRezervacija.ucitaj(argumenti.dohvatiPutanjuRezervacija());

      UpraviteljAranzmanima uprAranz = new UpraviteljAranzmanima(aranzmani);
      UpraviteljRezervacijama uprRez = new UpraviteljRezervacijama(rezervacije);

      for (Aranzman a : aranzmani) {
        uprRez.rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
    }
      
      System.out.println("Učitano aranžmana: " + uprAranz.brojAranzmana());
      System.out.println("Učitano rezervacija: " + uprRez.brojRezervacija());
      
      Komande komande = new Komande(uprAranz, uprRez);
      komande.pokreni();
  }
}
