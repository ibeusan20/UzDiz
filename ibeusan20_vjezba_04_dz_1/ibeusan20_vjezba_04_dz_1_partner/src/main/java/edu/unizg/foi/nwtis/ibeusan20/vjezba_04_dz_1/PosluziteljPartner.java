package edu.unizg.foi.nwtis.ibeusan20.vjezba_04_dz_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

public class PosluziteljPartner {

  /** Konfiguracijski podaci */
  private Konfiguracija konfig;

  /** Predložak za kraj */
  private Pattern predlozakKraj = Pattern.compile("^KRAJ$");

  /** Predložak za partnera */
  private Pattern predlozakPartner = Pattern.compile("^PARTNER$");

  public static void main(String[] args) {
    if (args.length < 1 || args.length > 2) {
      System.out.println("Neispravan broj argumenata. (1 <= nArgs <= 2)");
      return;
    }

    var program = new PosluziteljPartner();
    var nazivDatoteke = args[0];

    if (!program.ucitajKonfiguraciju(nazivDatoteke)) {
      System.out.println("Neuspješno učitavanje konfiguracije.");
      return;
    }

    if (args.length == 1) {
      program.registrirajPartnera();
    }
    var drugiArg = args[1].trim();

    if (program.predlozakKraj.matcher(drugiArg).matches()) {
      program.posaljiKraj();
    } else if (program.predlozakPartner.matcher(drugiArg).matches()) {
      System.out.println("TOBE: " + drugiArg);
      // program.pokreniPosluziteljKupaca(); // TODO
    } else {
      System.out.println("Nepoznata opcija: " + drugiArg);
    }

  }

  /**
   * Ucitaj konfiguraciju.
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

  private void registrirajPartnera() {
    try {
      var adresa = konfig.dajPostavku("adresa");
      var vrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataRegistracija"));
      var id = Integer.parseInt(konfig.dajPostavku("id"));
      var naziv = konfig.dajPostavku("naziv");
      var vrsta = konfig.dajPostavku("kuhinja");
      var mreznaVrata = konfig.dajPostavku("mreznaVrata");
      var gpsSirina = konfig.dajPostavku("gpsSirina");
      var gpsDuzina = konfig.dajPostavku("gpsDuzina");

      String komanda = String.format("PARTNER %d \"%s\" %s %s %s %s %s", id, naziv, vrsta, adresa,
          mreznaVrata, gpsSirina, gpsDuzina);

      try (Socket socket = new Socket(adresa, vrata);
          PrintWriter out =
              new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"));
          BufferedReader in =
              new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"))) {

        out.write(komanda + "\n");
        out.flush();
        socket.shutdownOutput();

        String odgovor = in.readLine();
        socket.shutdownInput();

        if (odgovor != null && odgovor.startsWith("OK")) {
          String kod = odgovor.split("\\s+")[1];
          konfig.spremiPostavku("sigKod", kod);
          konfig.spremiKonfiguraciju();
          System.out.println("Partner registriran. Sigurnosni kod: " + kod);
        } else {
          System.out.println("Greška u registraciji: " + odgovor);
        }
      }
    } catch (Exception e) {
      System.err.println("Greška kod registracije partnera: " + e.getMessage());
    }
  }

  private void posaljiKraj() {
    var kodZaKraj = this.konfig.dajPostavku("kodZaKraj");
    var adresa = this.konfig.dajPostavku("adresa");
    var mreznaVrata = Integer.parseInt(this.konfig.dajPostavku("mreznaVrataKraj"));

    try {
      var mreznaUticnica = new Socket(adresa, mreznaVrata);
      BufferedReader in =
          new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
      PrintWriter out =
          new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
      out.write("KRAJ " + kodZaKraj + "\n");
      out.flush();
      mreznaUticnica.shutdownOutput();
      var linija = in.readLine();
      mreznaUticnica.shutdownInput();
      if (linija.equals("OK")) {
        System.out.println("Uspješan kraj poslužitelja.");
      }
      mreznaUticnica.close();
    } catch (IOException e) {
    }
  }
}
