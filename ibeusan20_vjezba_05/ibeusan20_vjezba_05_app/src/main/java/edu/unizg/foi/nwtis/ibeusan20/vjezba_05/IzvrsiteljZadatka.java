package edu.unizg.foi.nwtis.ibeusan20.vjezba_05;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_05.dnevnik.ServisDnevnikRada;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_05.dnevnik.podaci.DnevnikRada;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_05.dnevnik.podaci.KorisnikInfo;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_05.dnevnik.podaci.SustavInfo;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

public class IzvrsiteljZadatka {
  private String klasaDnevnika;
  private String nazivDnevnika;
  private boolean obrisatiDnevnik;
  private KorisnikInfo korisnikInfo;
  private SustavInfo sustavInfo;
  private ServisDnevnikRada dnevnik;
  private String korisnickoImeBazaPodataka;
  private String lozinkaBazaPodataka;
  private String urlBazaPodataka;
  private String upravljacBazaPodataka;

  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Broj argumenata nije 3.");
      return;
    }

    var izvrsiteljZadatka = new IzvrsiteljZadatka();
    try {
      izvrsiteljZadatka.preuzmiPostavke(args);

      var korisnickoIme = args[1];
      var lozinka = args[2];
      izvrsiteljZadatka.korisnikInfo =
          new KorisnikInfo(korisnickoIme, lozinka, System.currentTimeMillis(), 0);

      izvrsiteljZadatka.sustavInfo = SustavInfo.preuzmiPodatke();

      izvrsiteljZadatka.kreirajServisDnevnikRada();
      izvrsiteljZadatka.obradiUnos();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

  private void kreirajServisDnevnikRada() throws Exception {
    var klasa = Class.forName(this.klasaDnevnika);
    var sucelja = klasa.getInterfaces();
    var brojSucelja =
        Arrays.stream(sucelja).filter(s -> s.getName().endsWith("ServisDnevnikRada")).count();
    if (brojSucelja == 0) {
      throw new Exception(
          "Klasa '" + this.klasaDnevnika + "' ne implementira sučelje 'ServisDnevnikRada'");
    }
    var pomocniObjekt = (ServisDnevnikRada) klasa.getDeclaredConstructor().newInstance();
    if (pomocniObjekt.koristiDatoteku()) {
      var konstruktor = klasa.getDeclaredConstructor(String.class, boolean.class);
      this.dnevnik =
          (ServisDnevnikRada) konstruktor.newInstance(this.nazivDnevnika, this.obrisatiDnevnik);
    } else if (pomocniObjekt.koristiBazuPodataka()) {
      var konstruktor =
          klasa.getDeclaredConstructor(String.class, String.class, String.class, String.class);
      this.dnevnik = (ServisDnevnikRada) konstruktor.newInstance(this.korisnickoImeBazaPodataka,
          this.lozinkaBazaPodataka, this.urlBazaPodataka, this.upravljacBazaPodataka);
    } else {
      throw new Exception("Klasa '" + this.klasaDnevnika + "' ne implementira traženo ponašanje");
    }
  }

  private void obradiUnos() throws Exception {
    Scanner in = new Scanner(System.in);
    boolean kraj = false;
    while (!kraj) {
      System.out.print("Upiši komandu: ");
      var linija = in.nextLine().trim();
      switch (linija) {
        case "q":
          kraj = true;
          break;
        default:
          var komanda = linija.split(" ");
          if (komanda.length != 3) {
            System.out.println("Nisu upisana 2 argumenta");
            continue;
          }
          switch (komanda[0]) {
            case "r":
              var brojIteracija = Integer.parseInt(komanda[1]);
              var pauzaIteracije = Integer.parseInt(komanda[2]);
              this.ponavljajZadatak(brojIteracija, pauzaIteracije);
              break;
            case "i":
              var vrijemeOd = Long.parseLong(komanda[1]);
              var vrijemeDo = Long.parseLong(komanda[2]);
              this.ispisiZapiseDnevnika(vrijemeOd, vrijemeDo);
              break;
            default:
              System.out.println("Nepoznata komanda");
              break;
          }
      }
    }
    in.close();
  }

  private void ponavljajZadatak(int brojIteracija, int pauzaIteracije) throws Exception {
    this.dnevnik.pripremiResurs();
    var pocetak = System.currentTimeMillis();
    for (int i = 0; i < brojIteracija; i++) {
      var dnevnikRada =
          new DnevnikRada(System.currentTimeMillis(), this.korisnikInfo.korisnickoIme(),
              this.sustavInfo.adresaRacunala(), this.sustavInfo.ipAdresaRacunala(),
              this.sustavInfo.nazivOS(), this.sustavInfo.verzijaVM(), "Poruka br: " + i);

      this.dnevnik.upisiDnevnik(dnevnikRada);

      Thread.sleep(pauzaIteracije);
    }
    var kraj = System.currentTimeMillis();
    System.out.println("Početak: %d Kraj: %d".formatted(pocetak, kraj));
    this.dnevnik.otpustiResurs();
  }

  private void ispisiZapiseDnevnika(long vrijemeOd, long vrijemeDo) throws Exception {
    this.dnevnik.pripremiResurs();
    var zapisiDnevnika =
        this.dnevnik.dohvatiDnevnik(vrijemeOd, vrijemeDo, this.korisnikInfo.korisnickoIme());
    this.dnevnik.otpustiResurs();

    zapisiDnevnika.stream()
        .forEach(dnevnikRada -> System.out.println(
            "%s %s %s %s %s %s %s".formatted(dnevnikRada.vrijeme(), dnevnikRada.korisnickoIme(),
                dnevnikRada.adresaRacunala(), dnevnikRada.ipAdresaRacunala(), dnevnikRada.nazivOS(),
                dnevnikRada.verzijaVM(), dnevnikRada.opisRada())));
  }

  private void preuzmiPostavke(String[] args)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

    this.klasaDnevnika = konfig.dajPostavku("klasaDnevnika");
    this.nazivDnevnika = konfig.dajPostavku("nazivDnevnika");
    this.obrisatiDnevnik = Boolean.valueOf(konfig.dajPostavku("obrisatiDnevnika"));
    this.korisnickoImeBazaPodataka = konfig.dajPostavku("korisnickoImeBazaPodataka");
    this.lozinkaBazaPodataka = konfig.dajPostavku("lozinkaBazaPodataka");
    this.urlBazaPodataka = konfig.dajPostavku("urlBazaPodataka");
    this.upravljacBazaPodataka = konfig.dajPostavku("upravljacBazaPodataka");
  }
}

