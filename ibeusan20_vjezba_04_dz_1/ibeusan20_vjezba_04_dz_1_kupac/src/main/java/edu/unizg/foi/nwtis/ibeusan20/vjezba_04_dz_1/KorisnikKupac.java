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

public class KorisnikKupac {

  private Konfiguracija konfig;
  private Pattern predlozakKraj = Pattern.compile("^KRAJ$");

  private final Map<String, Socket> aktivneUticnice = new ConcurrentHashMap<>();

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
   * Učitaj konfiguraciju.
   *
   * @param nazivDatoteke naziv datoteke
   * @return true, ako je uspješno učitavanje konfiguracije
   */
  private boolean ucitajKonfiguraciju(String nazivDatoteke) {
    try {
      this.konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
      return true;
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  private void posaljiKraj() {
    try {
      var kodZaKraj = konfig.dajPostavku("kodZaKraj");
      var adresa = konfig.dajPostavku("adresa");
      var mreznaVrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataKraj"));

      try (Socket uticnica = new Socket(adresa, mreznaVrata);
          PrintWriter out =
              new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"), true);
          BufferedReader in =
              new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"))) {

        //out.println("KRAJ " + kodZaKraj);
        String odgovor = in.readLine();
//        if ("OK".equals(odgovor)) {
//          System.out.println("[INFO] Uspješno poslan KRAJ.");
//        } else {
//          System.out.println("[GREŠKA] Odgovor: " + odgovor);
//        }
      }
    } catch (IOException e) {
      //System.err.println("[GREŠKA] Slanje KRAJ nije uspjelo: " + e.getMessage());
    }
  }

  private void obradiCSV(String nazivCSV) {
    Path putanja = Path.of(nazivCSV);
    if (!Files.exists(putanja)) {
      System.err.println("[GREŠKA] CSV datoteka ne postoji: " + nazivCSV);
      return;
    }

    try (BufferedReader reader = Files.newBufferedReader(putanja)) {
      String redak;
      while ((redak = reader.readLine()) != null) {
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
      System.err.println("[GREŠKA] Čitanje CSV datoteke nije uspjelo: " + e.getMessage());
    }
  }

  private void posaljiKomandu(String korisnik, String adresa, int port, String komanda) {
    try (
        Socket socket = new Socket(adresa, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"), true)
    ) {
        out.println(komanda);
        socket.shutdownOutput();

        String odgovor;
//        while ((odgovor = in.readLine()) != null) {
//            System.out.println("[" + korisnik + "] " + odgovor);
//        }

        socket.shutdownInput();
    } catch (IOException e) {
        System.err.println("[GREŠKA] Slanje komande '" + komanda + "' nije uspjelo: " + e.getMessage());
    }
}


  private void zatvoriSveVeze() {
    for (Map.Entry<String, Socket> entry : aktivneUticnice.entrySet()) {
      try {
        entry.getValue().close();
        //System.out.println("[INFO] Zatvorena veza za: " + entry.getKey());
      } catch (IOException e) {
        //System.err.println("[GREŠKA] Ne mogu zatvoriti vezu: " + entry.getKey());
      }
    }
    aktivneUticnice.clear();
  }
}


