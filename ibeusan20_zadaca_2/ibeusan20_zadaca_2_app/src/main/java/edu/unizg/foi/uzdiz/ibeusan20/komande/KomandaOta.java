package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * OTA – otkaz turističkog aranžmana.
 */
public class KomandaOta implements Komanda {

  private final UpraviteljAranzmanima uprAranz;
  private final UpraviteljRezervacijama uprRez;
  private final String[] argumenti;

  public KomandaOta(UpraviteljAranzmanima uprAranz,
      UpraviteljRezervacijama uprRez, String... argumenti) {
    this.uprAranz = uprAranz;
    this.uprRez = uprRez;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      System.out.println("Sintaksa: OTA <oznaka>");
      return true;
    }

    String oznaka = argumenti[0].trim();
    Aranzman a = uprAranz.pronadiPoOznaci(oznaka);
    if (a == null) {
      System.out.println("Ne postoji turistički aranžman s oznakom: " + oznaka);
      return true;
    }

    // dohvat svih rezervacija tog aranžmana (sve vrste)
    List<Rezervacija> sveRez =
        uprRez.dohvatiZaAranzmanIVrste(oznaka, "PAČOD");

    if (sveRez.isEmpty()) {
      // i dalje moramo aranžman staviti na OTKAZAN
      a.postaviOtkazan();
      System.out.println(
          "Turistički aranžman " + oznaka + " je otkazan (nije bilo rezervacija).");
      return true;
    }

    boolean biloOtkaza = false;
    for (Rezervacija r : sveRez) {
      boolean uspjelo =
          uprRez.otkaziRezervaciju(r.getIme(), r.getPrezime(), oznaka);
      if (uspjelo) {
        biloOtkaza = true;
      }
    }

    // aranžman prelazi u stanje OTKAZAN – State na razini aranžmana
    a.postaviOtkazan();
    // rekalkulacija radi konzistencije
    uprRez.rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());

    if (biloOtkaza) {
      System.out.println(
          "Uspješno otkazan turistički aranžman " + oznaka
              + " i otkazane pripadajuće rezervacije.");
    } else {
      System.out.println(
          "Nije pronađena nijedna rezervacija za aranžman " + oznaka
              + ", ali je aranžman označen kao otkazan.");
    }

    return true;
  }
}
