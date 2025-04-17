package edu.unizg.foi.nwtis.ibeusan20.vjezba_04_dz_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

// TODO: Auto-generated Javadoc
/**
 * Klasa KorisnikKupac. Šalje poslužiteljuPartneru komande iz CSV datoteke 
 * 
 */
public class KorisnikKupac {

  /** Definiranje varijable konfiguracije. */
  private Konfiguracija konfig;
  
  /** Predložak za prepoznavanje komande "KRAJ". */
  private Pattern predlozakKraj = Pattern.compile("^KRAJ$");

  /** Aktivne mrežne utičnice. */
  private final Map<String, Socket> aktivneUticnice = new ConcurrentHashMap<>();

  /**
   * Glavna metoda klase kojom se program pokreće i gasi.
   *
   * @param args argumenti main metode
   */
  public static void main(String[] args) {
    if (args.length > 2) {
      System.out.println("Broj argumenata veći od 2.");
      return;
    }
    var program = new KorisnikKupac();
    var nazivKonfiguracije = args[0];

    if (!program.ucitajKonfiguraciju(nazivKonfiguracije))
      return;
    if (args.length == 1)
      return;

    String naredba = args[1];
    if (program.predlozakKraj.matcher(naredba).matches()) {
      program.posaljiKraj();
    } else {
      program.obradiCSV(naredba);
    }
    program.zatvoriSveVeze();
  }

  /**
   * Učitavanje konfiguracije iz zadane datoteke.
   *
   * @param nazivDatoteke naziv datoteke konfiguracije
   * @return vraća true, ako je uspješno učitavanje konfiguracije
   */
  private boolean ucitajKonfiguraciju(String nazivDatoteke) {
    try {
      this.konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
      return true;
    } catch (NeispravnaKonfiguracija ex) {
    }
    return false;
  }

  /**
   * Pošalji kraj.
   */
  private void posaljiKraj() {
    try {
      var kodZaKraj = konfig.dajPostavku("kodZaKraj");
      var adresa = konfig.dajPostavku("adresa");
      var mreznaVrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataKraj"));

      try (Socket uticnica = new Socket(adresa, mreznaVrata);
          PrintWriter pisac =
              new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"), true);
          BufferedReader citac =
              new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"))) {

         pisac.println("KRAJ " + kodZaKraj);
         String odgovor = citac.readLine();
         if ("OK".equals(odgovor)) {
         System.out.println("[INFO] Uspješno poslan KRAJ.");
         } else {
         System.out.println("[GREŠKA] Odgovor: " + odgovor);
         } 
      }
    } catch (IOException e) {
    }
  }

  /**
   * Obrada CSV datoteke s popisom komandi.
   *
   * @param nazivCSV putanja do CSV datoteke
   */
  private void obradiCSV(String nazivCSV) {
    Path putanja = Path.of(nazivCSV);
    if (!Files.exists(putanja)) {
      System.err.println("[GREŠKA] CSV datoteka ne postoji: " + nazivCSV);
      return;
    }

    try (BufferedReader citac = Files.newBufferedReader(putanja)) {
      String redak;
      while ((redak = citac.readLine()) != null) {
        String[] polja = redak.split(";", 5);
        if (polja.length < 5) {
          System.out.println("[UPOZORENJE] Preskočen redak: " + redak);
          continue;
        }

        String korisnik = polja[0];
        String adresa = polja[1];
        int port = Integer.parseInt(polja[2]);
        int spavanje = Integer.parseInt(polja[3]);
        String komanda = polja[4];

        try {
          Thread.sleep(spavanje);
        } catch (InterruptedException ignored) {
        }
        posaljiKomandu(korisnik, adresa, port, komanda);
      }
    } catch (IOException e) {
    }
  }

  /**
   * Slanje komande na danu adresu .
   *
   * @param korisnik ime korisnika koji šalje komandu
   * @param adresa adresa poslužitelja na koji se šalje komanda
   * @param port mrežna vrata na koja se šalje komanda
   * @param komanda komanda koja se šalje
   */
  private void posaljiKomandu(String korisnik, String adresa, int port, String komanda) {
    if (!provjeriImeKorisnikaUKomandi(korisnik, komanda)) {
      //System.err.println("[SIGURNOST] Korisnik '" + korisnik + "' pokušao slati komandu u tuđe ime: " + komanda);
      return;
    }
    try (Socket mreznaUticnica = new Socket(adresa, port);
         PrintWriter pisac = new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"), true)) {

      pisac.println(komanda);
      mreznaUticnica.shutdownOutput();

      mreznaUticnica.shutdownInput();
    } catch (IOException e) {
    }
  }
  
  /**
   * Provjeri ime korisnika U komandi.
   *
   * @param korisnikCSV CSV datoteka konfiguracije za korisnika
   * @param komanda komanda koja se šalje
   * @return true, ako je upsješno
   */
  private boolean provjeriImeKorisnikaUKomandi(String korisnikCSV, String komanda) {
    String[] komande = { "JELOVNIK", "JELO", "PIĆE", "RAČUN", "NARUDŽBA", "KARTAPIĆA"};

    for (String naredba : komande) {
      if (komanda.startsWith(naredba + " ")) {
        String[] dijelovi = komanda.split("\\s+");
        if (dijelovi.length >= 2) {
          String korisnikKomande = dijelovi[1];
          return korisnikCSV.equalsIgnoreCase(korisnikKomande);
        }
      }
    }
    return true;
  }

  /**
   * Zatvaranje svih veza.
   */
  private void zatvoriSveVeze() {
    for (Map.Entry<String, Socket> entry : aktivneUticnice.entrySet()) {
      try {
        entry.getValue().close();
      } catch (IOException e) {
      }
    }
    aktivneUticnice.clear();
  }
}
