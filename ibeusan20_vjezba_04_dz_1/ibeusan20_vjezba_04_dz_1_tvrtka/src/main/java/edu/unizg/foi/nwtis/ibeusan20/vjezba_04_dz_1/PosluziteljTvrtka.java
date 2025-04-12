package edu.unizg.foi.nwtis.ibeusan20.vjezba_04_dz_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Jelovnik;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.KartaPica;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Obracun;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Partner;

public class PosluziteljTvrtka {

  /** Konfiguracijski podaci */
  private Konfiguracija konfig;

  /** Pokretač dretvi */
  private ExecutorService executor = null;

  /** Lista aktivnih dretvi. */
  private final List<Future<?>> aktivneDretve = new ArrayList<>();

  /** Future objekti za dretve */
  private Future<?> dretvaZaKraj;
  private Future<?> dretvaRegistracija;
  private Future<?> dretvaRadPartnera;

  /** Pauza dretve. */
  private int pauzaDretve = 1000;

  /** Kod za kraj rada */
  private String kodZaKraj = "";

  /** Zastavica za kraj rada */
  private AtomicBoolean kraj = new AtomicBoolean(false);

  /** Thread-safe kolekcije */
  private Map<Integer, String> kuhinje = new ConcurrentHashMap<>();
  private Map<String, Map<String, Jelovnik>> jelovnici = new ConcurrentHashMap<>();
  private Map<String, KartaPica> kartaPica = new ConcurrentHashMap<>();
  private Map<Integer, Partner> partneri = new ConcurrentHashMap<>();

  /**
   * Dohvaća konfiguraciju.
   *
   * @return Konf
   */
  public Konfiguracija getKonfig() {
    return konfig;
  }

  /**
   * Glavna metoda.
   *
   * @param args su argumenti koje prima glavna metoda
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Broj argumenata nije 1.");
      return;
    }
    var program = new PosluziteljTvrtka();
    var nazivDatoteke = args[0];

    program.pripremiKreni(nazivDatoteke);
  }

  /**
   * Priprema pokretanje svega u klasi.
   *
   * @param nazivDatoteke je parametar
   */
  public void pripremiKreni(String nazivDatoteke) {
    if (!this.ucitajKonfiguraciju(nazivDatoteke) || !this.ucitajKartuPica()
        || !this.ucitajJelovnike() || !this.ucitajPartnere()) {
      return;
    }
    this.kodZaKraj = this.konfig.dajPostavku("kodZaKraj");
    this.pauzaDretve = Integer.parseInt(this.konfig.dajPostavku("pauzaDretve"));

    var builder = Thread.ofVirtual();
    var factory = builder.factory();
    this.executor = Executors.newThreadPerTaskExecutor(factory);

    this.dretvaZaKraj = this.executor.submit(() -> this.pokreniPosluziteljKraj());
    this.dretvaRegistracija = this.executor.submit(() -> this.pokreniPosluziteljRegistracija());
    this.dretvaRadPartnera = this.executor.submit(() -> this.pokreniPosluziteljRad());

    while (!dretvaZaKraj.isDone()) {
      try {
        Thread.sleep(this.pauzaDretve);
      } catch (InterruptedException e) {
      }
    }
    if (this.kraj.get()) {
      for (Future<?> dretva : this.aktivneDretve) {
        if (!dretva.isDone()) {
          dretva.cancel(true);
        }
      }
      if (!dretvaRegistracija.isDone())
        dretvaRegistracija.cancel(true);
      if (!dretvaRadPartnera.isDone())
        dretvaRadPartnera.cancel(true);
    }
  }

  /**
   * Pokreni posluzitelj kraj.
   */
  public void pokreniPosluziteljKraj() {
    var mreznaVrata = Integer.parseInt(this.konfig.dajPostavku("mreznaVrataKraj"));
    var brojCekaca = 0;
    try (ServerSocket ss = new ServerSocket(mreznaVrata, brojCekaca)) {
      while (!this.kraj.get()) {
        var mreznaUticnica = ss.accept();
        this.obradiKraj(mreznaUticnica);
      }
      ss.close();
    } catch (IOException e) {
    }
  }

  /**
   * Obradi kraj Čita dolazne poruke. Koristi regex za provjeru forme komande KRAJ i gleda IP
   * adresu. Zatvara izlaz, u slučaju bilo kakve greške, pošalje grešku. U krajnjem slučaju
   * ignorira.
   *
   * @param mreznaUticnica the mrezna uticnica
   * @return the boolean
   */
  public Boolean obradiKraj(Socket mreznaUticnica) {
    try {
      BufferedReader ulaz =
          new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
      PrintWriter izlaz =
          new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
      String linija = ulaz.readLine();
      mreznaUticnica.shutdownInput();

      String izraz = "^KRAJ\\s+" + Pattern.quote(this.kodZaKraj) + "$";
      Pattern uzorak = Pattern.compile(izraz);
      Matcher podudaranje = uzorak.matcher(linija.trim());

      if (!podudaranje.matches()) {
        izlaz.write("ERROR 10\n");
      } else {
        InetAddress adresa = mreznaUticnica.getInetAddress();
        if (!adresa.isLoopbackAddress() && !adresa.isAnyLocalAddress()
            && !adresa.isSiteLocalAddress()) {
          izlaz.write("ERROR 11\n");
        } else {
          this.kraj.set(true);
          izlaz.write("OK\n");
        }
      }
      izlaz.flush();
      mreznaUticnica.shutdownOutput();
      mreznaUticnica.close();
    } catch (Exception e) {
      try {
        PrintWriter izlaz =
            new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
        izlaz.write("ERROR 19\n");
        izlaz.flush();
        mreznaUticnica.shutdownOutput();
        mreznaUticnica.close();
      } catch (IOException ex) {
      }
    }
    return Boolean.TRUE;
  }


  /**
   * Ucitaj konfiguraciju.
   *
   * @param nazivDatoteke naziv datoteke
   * @return true, ako je uspješno učitavanje konfiguracije
   */
  public boolean ucitajKonfiguraciju(String nazivDatoteke) {
    try {
      this.konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
      return true;
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  /**
   * Ucitaj kartu pica.
   *
   * @return true, ako je uspješno učitano
   */
  public boolean ucitajKartuPica() {
    var nazivDatoteke = this.konfig.dajPostavku("datotekaKartaPica");
    var datoteka = Path.of(nazivDatoteke);
    if (!Files.exists(datoteka) || !Files.isRegularFile(datoteka) || !Files.isReadable(datoteka)) {
      return false;
    }
    try (var br = Files.newBufferedReader(datoteka)) {
      Gson gson = new Gson();
      var kartaPicaNiz = gson.fromJson(br, KartaPica[].class);
      var kartaPicaTok = Stream.of(kartaPicaNiz);
      kartaPicaTok.forEach(kp -> this.kartaPica.put(kp.id(), kp));
    } catch (IOException ex) {
      return false;
    }
    // PRIVREMENI ISPIS
    System.out.println("Učitana pića:");
    for (var p : this.kartaPica.values()) {
      System.out
          .println(" " + p.id() + " " + p.naziv() + " " + p.kolicina() + " " + p.cijena() + " ");
    }
    return true;
  }

  /**
   * Ucitaj jelovnike.
   *
   * @return true, ako je uspješno učitano
   */
  public boolean ucitajJelovnike() {
    Gson gson = new Gson();
    Properties svePostavke = this.konfig.dajSvePostavke();

    for (String kljuc : svePostavke.stringPropertyNames()) {
      if (!kljuc.startsWith("kuhinja_"))
        continue;

      String vrijednost = this.konfig.dajPostavku(kljuc);
      String[] dijelovi = vrijednost.split(";");
      if (dijelovi.length != 2)
        continue;

      int broj = Integer.parseInt(kljuc.split("_")[1]);
      String oznaka = dijelovi[0];
      this.kuhinje.put(broj, oznaka);

      String nazivDatoteke = kljuc + ".json";
      Path datoteka = Path.of(nazivDatoteke);
      if (!Files.exists(datoteka))
        continue;

      try (var reader = Files.newBufferedReader(datoteka)) {
        Jelovnik[] niz = gson.fromJson(reader, Jelovnik[].class);
        Map<String, Jelovnik> mapa = new ConcurrentHashMap<>();
        for (var j : niz) {
          mapa.put(j.id(), j);
        }
        this.jelovnici.put(oznaka, mapa);
      } catch (IOException e) {
        System.err.println("Greška kod učitavanja jelovnika: " + nazivDatoteke);
      }
    }
    // PRIVREMENI ISPIS
    System.out.println("Učitani jelovnici:");
    for (var ulaz : this.jelovnici.entrySet()) {
      String oznakaKuhinje = ulaz.getKey();
      Map<String, Jelovnik> jela = ulaz.getValue();
      System.out.println("Kuhinja: " + oznakaKuhinje);
      for (var j : jela.values()) {
        System.out.println(" " + j.id() + " " + j.naziv() + " " + j.cijena() + " ");
      }
    }
    return true;
  }

  /**
   * Ucitaj partnere.
   *
   * @return true, ako je uspješno učitano
   */
  public boolean ucitajPartnere() {
    Gson gson = new Gson();
    String nazivDatoteke = this.konfig.dajPostavku("datotekaPartnera");
    Path datoteka = Path.of(nazivDatoteke);

    try {
      if (!Files.exists(datoteka)) {
        Files.createFile(datoteka);
        return true;
      }
      try (var reader = Files.newBufferedReader(datoteka)) {
        Partner[] niz = gson.fromJson(reader, Partner[].class);
        if (niz != null) {
          for (Partner p : niz) {
            this.partneri.put(p.id(), p);
          }
        }
      }
    } catch (IOException e) {
      System.err.println("Greška kod učitavanja partnera: " + e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Pokreni posluzitelja za registraciju.
   */
  public void pokreniPosluziteljRegistracija() {
    var mreznaVrata = Integer.parseInt(this.konfig.dajPostavku("mreznaVrataRegistracija"));
    var brojCekaca = Integer.parseInt(this.konfig.dajPostavku("brojCekaca"));

    try (ServerSocket ss = new ServerSocket(mreznaVrata, brojCekaca)) {
      while (!this.kraj.get()) {
        var uticnica = ss.accept();
        var dretva = this.executor.submit(() -> obradiRegistraciju(uticnica));
        this.aktivneDretve.add(dretva);
      }
    } catch (IOException e) {
      System.err.println("Greška u poslužitelju za registraciju: " + e.getMessage());
    }
  }

  /**
   * Spremi partnere.
   */
  private synchronized void spremiPartnere() {
    String nazivDatoteke = this.konfig.dajPostavku("datotekaPartnera");
    Path datoteka = Path.of(nazivDatoteke);
    try {
      if (!Files.exists(datoteka)) {
        Files.createFile(datoteka);
      }
      try (var pisac = Files.newBufferedWriter(datoteka)) {
        Gson gson = new Gson();
        var niz = this.partneri.values().toArray();
        gson.toJson(niz, pisac);
      }
    } catch (IOException e) {
      System.err.println("Greška pri spremanju partnera: " + e.getMessage());
    }
  }

  /**
   * Pokreni posluzitelja za rad s partnerima.
   */
  public void pokreniPosluziteljRad() {
    var mreznaVrata = Integer.parseInt(this.konfig.dajPostavku("mreznaVrataRad"));
    var brojCekaca = Integer.parseInt(this.konfig.dajPostavku("brojCekaca"));

    try (ServerSocket ss = new ServerSocket(mreznaVrata, brojCekaca)) {
      while (!this.kraj.get()) {
        var uticnica = ss.accept();
        var dretva = this.executor.submit(() -> this.obradiRadPartnera(uticnica));
        this.aktivneDretve.add(dretva);
      }
    } catch (IOException e) {
      System.err.println("Greška u Poslužitelju za rad s partnerima: " + e.getMessage());
    }
  }


  /**
   * Obradi registraciju. Obarada komandi je u zasebnim metodama obradiKomanduPopis,
   * obradiObrisiKomandu, i obradiPartnerKomandu.
   *
   * @param uticnica je parametar
   */
  public void obradiRegistraciju(Socket uticnica) {
    try (var ulaz = new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"));
        var izlaz = new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"))) {
      var linija = ulaz.readLine();
      var komanda = linija.trim();
      var dijelovi = komanda.split("\\s+", 2);
      // PRIVREMENI ISPIS
      System.out.println("primljena linija: " + linija);

      if (dijelovi.length < 1) {
        izlaz.write("ERROR 20 - Format komande nije ispravan\n");
        izlaz.flush();
        return;
      }
      if (komanda.startsWith("PARTNER")) {
        obradiPartnerKomandu(izlaz, komanda);
      } else if (komanda.startsWith("OBRIŠI")) {
        obradiObrisiKomandu(izlaz, komanda);
      } else if (komanda.equals("POPIS")) {
        obradiKomanduPopis(izlaz);
      } else {
        izlaz.write("ERROR 20 - Format komande nije ispravan\n");
      }
      izlaz.flush();
      uticnica.shutdownOutput();
      uticnica.close();

    } catch (IOException | NumberFormatException e) {
      System.err.println("Greška u obradi registracije: " + e.getMessage());
    }
  }

  /**
   * Obradi komandu popis. Provjera za obradiRegistraciju.
   *
   * @param izlaz je ispis
   */
  private void obradiKomanduPopis(PrintWriter izlaz) {
    izlaz.write("OK\n");
    Gson gson = new Gson();
    var popis =
        this.partneri.values().stream()
            .map(p -> new edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.PartnerPopis(p.id(), p.naziv(),
                p.vrstaKuhinje(), p.adresa(), p.mreznaVrata(), p.gpsSirina(), p.gpsDuzina()))
            .toList();
    izlaz.write(gson.toJson(popis) + "\n");
  }

  /**
   * Obradi obrisi komandu. Provjera za obradiRegistraciju.
   *
   * @param izlaz je ispis
   * @param komanda je parametar
   */
  private void obradiObrisiKomandu(PrintWriter izlaz, String komanda) {
    var matcher = Pattern.compile("OBRIŠI\\s+(\\d+)\\s+(\\S+)").matcher(komanda);
    if (!matcher.matches()) {
      izlaz.write("ERROR 20 - Format komande nije ispravan\n");
    } else {
      int id = Integer.parseInt(matcher.group(1));
      String kod = matcher.group(2);

      var p = this.partneri.get(id);
      if (p == null) {
        izlaz.write(
            "ERROR 23 - Ne postoji partner s id u kolekciji partnera i/ili neispravan sigurnosni kod partnera\n");
      } else if (!p.sigurnosniKod().equals(kod)) {
        izlaz.write("ERROR 22 - Neispravan sigurnosni kod partnera\n");
      } else {
        this.partneri.remove(id);
        spremiPartnere();
        izlaz.write("OK\n");
      }
    }
  }

  /**
   * Obradi partner komandu. Provjera za obradiRegistraciju.
   *
   * @param izlaz je ispis
   * @param komanda je parametar
   */
  private void obradiPartnerKomandu(PrintWriter izlaz, String komanda) {
    String regex =
        "^PARTNER\\s+(\\d+)\\s+\"(.+?)\"\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+([+-]?\\d*\\.?\\d+)\\s+([+-]?\\d*\\.?\\d+)$";
    var matcher = Pattern.compile(regex).matcher(komanda);
    if (!matcher.matches()) {
      izlaz.write("ERROR 20 - Format komande nije ispravan\n");
      return;
    } else {
      int id = Integer.parseInt(matcher.group(1));
      String naziv = matcher.group(2);
      String vrsta = matcher.group(3);
      String adresa = matcher.group(4);
      int vrata = Integer.parseInt(matcher.group(5));
      float sirina = Float.parseFloat(matcher.group(6));
      float duzina = Float.parseFloat(matcher.group(7));

      if (this.partneri.containsKey(id)) {
        izlaz.write("ERROR 21 - Već postoji partner s id u kolekciji partnera\n");
      } else {
        String kod = Integer.toHexString((naziv + adresa).hashCode());
        Partner novi = new Partner(id, naziv, vrsta, adresa, vrata, sirina, duzina, kod);
        this.partneri.put(id, novi);
        spremiPartnere();
        izlaz.write("OK " + kod + "\n");
      }
    }
  }

  /** Objekt obracuna. */
  private final Object lokotObracuna = new Object();

  /**
   * Obrada rada partnera. Provjere komandi partnera su u obradiObracunKomandu,
   * obradiKartaPicaKomandu i u obradiJelovnikKomandu.
   *
   * @param uticnica je parametar
   */
  public void obradiRadPartnera(Socket uticnica) {
    try (var ulaz = new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"));
        var izlaz = new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"))) {

      var linija = ulaz.readLine();
      var komanda = linija.trim();
      System.out.println("Primljena komanda: " + komanda);

      if (komanda.startsWith("JELOVNIK")) {
        obradiJelovnikKomandu(izlaz, komanda);
      } else if (komanda.startsWith("KARTAPIĆA")) {
        obradiKartaPicaKomandu(izlaz, komanda);
      } else if (komanda.startsWith("OBRAČUN")) {
        obradiObracunKomandu(ulaz, izlaz, komanda);
      } else {
        izlaz.write("ERROR 30 - Format komande nije ispravan\n");
      }
      izlaz.flush();
      uticnica.shutdownOutput();
      uticnica.close();
    } catch (IOException e) {
      System.err.println("Greška u obradi partnera: " + e.getMessage());
    }
  }

  /**
   * Obradi obracun komandu. Provjera za obradiRadPartnera.
   *
   * @param ulaz je parametar
   * @param izlaz je parametar
   * @param komanda je parametar
   */
  private void obradiObracunKomandu(BufferedReader ulaz, PrintWriter izlaz, String komanda) {
    var matcher = Pattern.compile("^OBRAČUN\\s+(\\d+)\\s+(\\S+)$").matcher(komanda);
    if (!matcher.matches()) {
      izlaz.write("ERROR 30 - Format komande nije ispravan\n");
      return;
    }
    int id = Integer.parseInt(matcher.group(1));
    String kod = matcher.group(2);

    var partner = this.partneri.get(id);
    if (partner == null || !partner.sigurnosniKod().equals(kod)) {
      izlaz.write(
          "ERROR 31 - Ne postoji partner s id u kolekciji partnera i/ili neispravan sigurnosni kod partnera\n");
      return;
    }

    try {
      StringBuilder json = new StringBuilder();
      String linijaJson;
      while ((linijaJson = ulaz.readLine()) != null) {
        json.append(linijaJson).append("\n");
        if (linijaJson.trim().endsWith("]"))
          break;
      }
      Gson gson = new Gson();
      Obracun[] novi = gson.fromJson(json.toString(), Obracun[].class);

      var jelovnik = this.jelovnici.get(partner.vrstaKuhinje());
      for (var o : novi) {
        if (o.jelo()) {
          if (jelovnik == null || !jelovnik.containsKey(o.id())) {
            izlaz.write("ERROR 35 - Neispravan obračun\n");
            return;
          }
        } else {
          if (!this.kartaPica.containsKey(o.id())) {
            izlaz.write("ERROR 35 - Neispravan obračun\n");
            return;
          }
        }
      }
      odradiLokotObracuna(gson, novi);
      izlaz.write("OK\n");
    } catch (JsonSyntaxException e) {
      izlaz.write("ERROR 35 - Neispravan obračun\n");
    } catch (Exception e) {
      izlaz.write("ERROR 39 - Nešto drugo nije u redu\n");
    }
  }

  /**
   * Odradi lokot obracuna. Metoda za zapis u obačun za obraduObračunKomandu
   *
   * @param gson je parametar
   * @param novi je parametar
   * @throws IOException izbacuje grešku
   */
  private void odradiLokotObracuna(Gson gson, Obracun[] novi) throws IOException {
    synchronized (lokotObracuna) {
      String nazivDatoteke = this.konfig.dajPostavku("datotekaObracuna");
      Path datoteka = Path.of(nazivDatoteke);

      Obracun[] postojeci = new Obracun[0];
      if (Files.exists(datoteka) && Files.size(datoteka) > 0) {
        try (var reader = Files.newBufferedReader(datoteka)) {
          postojeci = gson.fromJson(reader, Obracun[].class);
        }
      }

      List<Obracun> svi = new ArrayList<>();
      if (postojeci != null)
        svi.addAll(List.of(postojeci));
      svi.addAll(List.of(novi));

      try (var writer = Files.newBufferedWriter(datoteka)) {
        gson.toJson(svi, writer);
      }
    }
  }

  /**
   * Obradi karta pica komandu. Provjera za obradiRadPartnera.
   *
   * @param izlaz je parametar
   * @param komanda je parametar
   */
  private void obradiKartaPicaKomandu(PrintWriter izlaz, String komanda) {
    var matcher = Pattern.compile("^KARTAPIĆA\\s+(\\d+)\\s+(\\S+)$").matcher(komanda);
    if (!matcher.matches()) {
      izlaz.write("ERROR 30 - Format komande nije ispravan\n");
      return;
    } else {
      int id = Integer.parseInt(matcher.group(1));
      String kod = matcher.group(2);

      var partner = this.partneri.get(id);
      if (partner == null || !partner.sigurnosniKod().equals(kod)) {
        izlaz.write(
            "ERROR 31 - Ne postoji partner s id u kolekciji partnera i/ili neispravan sigurnosni kod partnera\n");
        return;
      } else {
        try {
          izlaz.write("OK\n");
          Gson gson = new Gson();
          izlaz.write(gson.toJson(this.kartaPica.values()) + "\n");
        } catch (Exception e) {
          izlaz.write("ERROR 34 - Neispravna karta pića\n");
        }
      }
    }
  }

  /**
   * Obradi jelovnik komandu. Provjera za obradiRadPartnera.
   *
   * @param izlaz je parametar
   * @param komanda je parametar
   */
  private void obradiJelovnikKomandu(PrintWriter izlaz, String komanda) {
    var matcher = Pattern.compile("^JELOVNIK\\s+(\\d+)\\s+(\\S+)$").matcher(komanda);
    if (!matcher.matches()) {
      izlaz.write("ERROR 30 - Format komande nije ispravan\n");
      return;
    } else {
      int id = Integer.parseInt(matcher.group(1));
      String kod = matcher.group(2);

      var partner = this.partneri.get(id);
      if (partner == null || !partner.sigurnosniKod().equals(kod)) {
        izlaz.write(
            "ERROR 31 - Ne postoji partner s id u kolekciji partnera i/ili neispravan sigurnosni kod partnera\n");
        return;
      } else {
        var jelovnik = this.jelovnici.get(partner.vrstaKuhinje());
        if (jelovnik == null) {
          izlaz.write(
              "ERROR 32 - Ne postoji jelovnik s vrstom kuhinje koju partner ima ugovorenu\n");
          return;
        } else {
          try {
            izlaz.write("OK\n");
            Gson gson = new Gson();
            izlaz.write(gson.toJson(jelovnik.values()) + "\n");
          } catch (Exception e) {
            izlaz.write("ERROR 33 - Neispravan jelovnik\n");
          }
        }
      }
    }
  }
}
