package edu.unizg.foi.nwtis.ibeusan20.vjezba_07_dz_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
import java.util.regex.Pattern;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.podaci.Jelovnik;
import edu.unizg.foi.nwtis.podaci.KartaPica;
import edu.unizg.foi.nwtis.podaci.Obracun;
import edu.unizg.foi.nwtis.podaci.Partner;
import edu.unizg.foi.nwtis.podaci.PartnerPopis;


// TODO: Auto-generated Javadoc
/**
 * The Class PosluziteljTvrtka.
 */
public class PosluziteljTvrtka {

  /** Konfiguracijski podaci. */
  private Konfiguracija konfig;

  /** Pokretač dretvi. */
  public ExecutorService executor = null;

  /** Lista aktivnih dretvi. */
  public final List<Future<?>> aktivneDretve = new ArrayList<>();

  /** Mapa dretvi i utičnica. */
  private final Map<Future<?>, Socket> mapaDretviUticnica = new ConcurrentHashMap<>();

  /** Broj zatvorenih veza. */
  private int brojZatvorenihVeza = 0;

  /** Future objekti za dretve. */
  public Future<?> dretvaZaKraj;

  /** Dretva za registraciju. */
  public Future<?> dretvaRegistracija;

  /** Dretva za rad partnera. */
  public Future<?> dretvaRadPartnera;

  /** Pauza dretve. */
  private int pauzaDretve = 1000;

  /** Varijabla za kod za kraj rada. */
  public String kodZaKraj = "";

  /** Zastavica za kraj rada. */
  public AtomicBoolean kraj = new AtomicBoolean(false);

  /** Thread-safe kolekcija kuhinja. */
  private Map<Integer, String> kuhinje = new ConcurrentHashMap<>();

  /** Thread-safe kolekcija jelovnika. */
  public Map<String, Map<String, Jelovnik>> jelovnici = new ConcurrentHashMap<>();

  /** Thread-safe kolekcija karta pića. */
  public Map<String, KartaPica> kartaPica = new ConcurrentHashMap<>();

  /** Thread-safe kolekcija partnera. */
  public Map<Integer, Partner> partneri = new ConcurrentHashMap<>();

  private final AtomicBoolean pauzaRegistracija = new AtomicBoolean(false);
  private final AtomicBoolean pauzaPartneri = new AtomicBoolean(false);
  private String kodZaAdminTvrtke = "";


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
    // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    // int br = program.kraj.get() ? 3 : 0;
    // int zatvorene = program.zatvoriDretveIMrezneVeze(br);
    // System.out.println("[INFO] Ukupan broj prekinutih dretvi: " + zatvorene);
    // }));
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
    this.kodZaAdminTvrtke = this.konfig.dajPostavku("kodZaAdminTvrtke");
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
      int zatvorene = 1;
      System.out.println("[INFO] Gašenje poslužitelja za kraj, zatvorena 1 veza.");
      zatvoriDretveIMrezneVeze(zatvorene);
    }
  }

  /**
   * Pokreni poslužitelja za kraj rada.
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
   * Zatvori dretve i mrežne veze.
   *
   * @param zatvorene prima broj zatvorenih dretvi ako ih ima
   * @return vraća broj zatvorenih dretvi
   */
  public int zatvoriDretveIMrezneVeze(int zatvorene) {

    for (Future<?> dretva : aktivneDretve) {
      if (!dretva.isDone()) {
        dretva.cancel(true);
        zatvorene++;
      }
    }
    if (dretvaRegistracija != null && !dretvaRegistracija.isDone()) {
      dretvaRegistracija.cancel(true);
      zatvorene++;
    }
    if (dretvaRadPartnera != null && !dretvaRadPartnera.isDone()) {
      dretvaRadPartnera.cancel(true);
      zatvorene++;
    }
    if (dretvaZaKraj != null && !dretvaZaKraj.isDone()) {
      dretvaZaKraj.cancel(true);
      zatvorene++;
      System.out.println("[INFO] Gašenje poslužitelja za kraj, zatvorena 1 veza.");
    }
    return zatvorene;
  }

  /**
   * Čita dolazne poruke. Provjerava komandu KRAJ i gleda IP adresu.
   * 
   * Koristi metode provjeriFormatKomande, provjeriLokalnuAdresu i posaljiPorukuGreske.
   *
   * @param mreznaUticnica je parametar
   * @return vraća bool
   */
  public Boolean obradiKraj(Socket mreznaUticnica) {
    try (
        var ulaz =
            new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
        var izlaz =
            new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"))) {
      String linija = ulaz.readLine();
      mreznaUticnica.shutdownInput();
      
      String[] dijelovi = linija.trim().split("\\s+");
      if (dijelovi.length == 0) {
        izlaz.write("ERROR 10 - Format komande nije ispravan\n");
      } else {
        String komanda = dijelovi[0];
        switch (komanda) {
          case "STATUS":
            obradiStatusKomandu(izlaz, dijelovi);
            break;
          case "PAUZA":
            obradiPauzaKomandu(izlaz, dijelovi);
            break;
          case "START":
            obradiStartKomandu(izlaz, dijelovi);
            break;
          case "KRAJ":
            obradiKrajKomandu(izlaz, dijelovi);
            break;
          case "SPAVA":
            obradiSpavaKomandu(izlaz, dijelovi);
            break;
          case "OSVJEŽI":
            obradiOsvjeziKomandu(izlaz, dijelovi);
            break;
          case "KRAJWS":
            obradiKrajWSKomandu(izlaz, dijelovi);
            break;
          default:
            izlaz.write("ERROR 10 - Nepoznata komanda\n");
            break;
        }
      }
      izlaz.flush();
      mreznaUticnica.shutdownOutput();
      mreznaUticnica.close();
      synchronized (this) {
        brojZatvorenihVeza++;
      }
    } catch (Exception e) {
      posaljiPorukuGreske(mreznaUticnica);
    }
    return Boolean.TRUE;
  }
  
  /**
   * Obradi kraj komandu.
   *
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiKrajKomandu(PrintWriter izlaz, String[] dijelovi) {
    if (dijelovi.length != 2 || !dijelovi[1].equals(kodZaKraj)) {
      izlaz.write("ERROR 10 - Format komande nije ispravan ili nije ispravan kod za kraj\n");
      return;
    }
    boolean sviPartneri = posaljiKrajPartnerima(kodZaKraj);
    if (!sviPartneri) {
      izlaz.write("ERROR 14 - Barem jedan partner nije završio rad\n");
      return;
    }
    boolean rest = posaljiRestZahtjevKraj();
    if (!rest) {
      izlaz.write("ERROR 17 - RESTful zahtjev nije uspješan\n");
      return;
    }
    kraj.set(true);
    izlaz.write("OK\n");
  }

  /**
   * Obradi status komandu.
   *
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiStatusKomandu(PrintWriter izlaz, String[] dijelovi) {
    if (dijelovi.length != 3 || (!dijelovi[2].equals("1") && !dijelovi[2].equals("2"))) {
      izlaz.write("ERROR 10 - Format komande nije ispravan\n");
      return;
    }
    if (!dijelovi[1].equals(this.kodZaAdminTvrtke)) {
      izlaz.write("ERROR 12 - Pogrešan kodZaAdminTvrtke\n");
      return;
    }
    int dio = Integer.parseInt(dijelovi[2]);
    boolean stanje = (dio == 1) ? !pauzaRegistracija.get() : !pauzaPartneri.get();
    izlaz.write("OK " + (stanje ? "1" : "0") + "\n");
  }

  /**
   * Obradi pauza komandu.
   *
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiPauzaKomandu(PrintWriter izlaz, String[] dijelovi) {
    if (dijelovi.length != 3 || (!dijelovi[2].equals("1") && !dijelovi[2].equals("2"))) {
      izlaz.write("ERROR 10 - Format komande nije ispravan\n");
      return;
    }
    if (!dijelovi[1].equals(kodZaAdminTvrtke)) {
      izlaz.write("ERROR 12 - Pogrešan kodZaAdminTvrtke\n");
      return;
    }
    AtomicBoolean pauza = dijelovi[2].equals("1") ? pauzaRegistracija : pauzaPartneri;
    if (pauza.get()) {
      izlaz.write("ERROR 13 - Pogrešna promjena pauze ili starta\n");
    } else {
      pauza.set(true);
      izlaz.write("OK\n");
    }
  }

  /**
   * Obradi start komandu.
   *
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiStartKomandu(PrintWriter izlaz, String[] dijelovi) {
    if (dijelovi.length != 3 || (!dijelovi[2].equals("1") && !dijelovi[2].equals("2"))) {
      izlaz.write("ERROR 10 - Format komande nije ispravan\n");
      return;
    }
    if (!dijelovi[1].equals(kodZaAdminTvrtke)) {
      izlaz.write("ERROR 12 - Pogrešan kodZaAdminTvrtke\n");
      return;
    }

    AtomicBoolean pauza = dijelovi[2].equals("1") ? pauzaRegistracija : pauzaPartneri;
    if (!pauza.get()) {
      izlaz.write("ERROR 13 - Pogrešna promjena pauze ili starta\n");
    } else {
      pauza.set(false);
      izlaz.write("OK\n");
    }
  }

  /**
   * Obradi spava komandu.
   *
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiSpavaKomandu(PrintWriter izlaz, String[] dijelovi) {
    if (dijelovi.length != 3) {
      izlaz.write("ERROR 10 - Format komande nije ispravan\n");
      return;
    }
    if (!dijelovi[1].equals(kodZaAdminTvrtke)) {
      izlaz.write("ERROR 12 - Pogrešan kodZaAdminTvrtke\n");
      return;
    }
    try {
      int ms = Integer.parseInt(dijelovi[2]);
      Thread.sleep(ms);
      izlaz.write("OK\n");
    } catch (InterruptedException e) {
      izlaz.write("ERROR 16 - Prekid spavanja dretve\n");
    } catch (NumberFormatException e) {
      izlaz.write("ERROR 10 - Format komande nije ispravan\n");
    }
  }

  /**
   * Obradi osvjezi komandu.
   *
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiOsvjeziKomandu(PrintWriter izlaz, String[] dijelovi) {
    if (dijelovi.length != 2) {
      izlaz.write("ERROR 10 - Format komande nije ispravan\n");
      return;
    }
    if (!dijelovi[1].equals(kodZaAdminTvrtke)) {
      izlaz.write("ERROR 12 - Pogrešan kodZaAdminTvrtke\n");
      return;
    }
    if (pauzaPartneri.get()) {
      izlaz.write("ERROR 15 - Poslužitelj za partnere u pauzi\n");
      return;
    }
    boolean uspjeh1 = ucitajKartuPica();
    boolean uspjeh2 = ucitajJelovnike();
    if (uspjeh1 && uspjeh2) {
      izlaz.write("OK\n");
    } else {
      izlaz.write("ERROR 17 - Greška kod učitavanja podataka\n");
    }
  }

  /**
   * Obradi kraj WS komandu.
   *
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiKrajWSKomandu(PrintWriter izlaz, String[] dijelovi) {
    if (dijelovi.length != 2 || !dijelovi[1].equals(kodZaKraj)) {
      izlaz.write("ERROR 10 - Format komande nije ispravan ili nije ispravan kod za kraj\n");
      return;
    }
    boolean sviOK = posaljiKrajPartnerima(kodZaKraj);
    if (!sviOK) {
      izlaz.write("ERROR 14 - Barem jedan partner nije završio rad\n");
      return;
    }
    this.kraj.set(true);
    izlaz.write("OK\n");
  }

  /**
   * Posalji kraj partnerima.
   *
   * @param kod kod
   * @return true, sko je udpješno
   */
  private boolean posaljiKrajPartnerima(String kod) {
    for (Partner p : this.partneri.values()) {
      try (Socket socket = new Socket(p.adresa(), p.mreznaVrataKraj())) {
        var izlaz = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"));
        var ulaz = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));

        izlaz.write("KRAJ " + kod + "\n");
        izlaz.flush();
        socket.shutdownOutput();
        String odgovor = ulaz.readLine();
        if (!"OK".equals(odgovor))
          return false;
        System.out.println("[DEBUG] Partneri OK: " + p);
        socket.shutdownInput();
      } catch (Exception e) {
        return false;
      }
    }
    return true;
  }

  /**
   * Posalji rest zahtjev kraj.
   *
   * @return true, ako je upješno
   */
  private boolean posaljiRestZahtjevKraj() {
    try {
      HttpClient client = HttpClient.newHttpClient();
      String url = this.konfig.dajPostavku("restAdresa") + "/kraj/info";
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
          .method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
      System.out.println("[DEBUG] URL za REST: " + url);
      System.out.println("[DEBUG] REST URL: " + url);
      HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
      System.out.println("[DEBUG] REST status: " + response.statusCode());
      System.out.println("[DEBUG] REST response headers: " + response.headers());

      return response.statusCode() == 200;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Provjerava ispravnost formata komande KRAJ.
   *
   * @param linija ulazna komanda
   * @return true ako je komanda ispravna i sadrži točan kod za kraj, inače false
   */
  public boolean provjeriFormatKomande(String linija) {
    String izraz = "^KRAJ\\s+" + Pattern.quote(this.kodZaKraj) + "$";
    return Pattern.compile(izraz).matcher(linija.trim()).matches();
  }

  /**
   * Provjerava je li mrežna adresa lokalna.
   *
   * @param mreznaUticnica mrežna utičnica s koje je došla komanda
   * @return true ako adresa pripada lokalnoj mreži, inače false
   */
  public boolean provjeriLokalnuAdresu(Socket mreznaUticnica) {
    InetAddress adresa = mreznaUticnica.getInetAddress();
    return adresa.isLoopbackAddress() || adresa.isAnyLocalAddress() || adresa.isSiteLocalAddress();
  }

  /**
   * Šalje generičku poruku greške klijentu u slučaju iznimke.
   *
   * @param mreznaUticnica mrežna utičnica prema klijentu
   */
  public void posaljiPorukuGreske(Socket mreznaUticnica) {
    try {
      var izlaz = new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
      izlaz.write("ERROR 19 - Nešto drugo nije u redu.\n");
      izlaz.flush();
      mreznaUticnica.shutdownOutput();
      mreznaUticnica.close();
      synchronized (this) {
        brojZatvorenihVeza++;
      }
    } catch (IOException ignored) {
    }
  }

  /**
   * Učitava konfiguraciju iz zadane datoteke.
   *
   * @param nazivDatoteke putanja do konfiguracijske datoteke
   * @return true ako je konfiguracija uspješno učitana, inače false
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
   * Učitava kartu pića iz JSON datoteke definirane u konfiguraciji.
   *
   * @return true ako je učitavanje bilo uspješno, inače false
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
    return true;
  }

  /**
   * Učitava sve jelovnike iz datoteka na temelju konfiguracije.
   *
   * @return true ako su svi jelovnici uspješno učitani, inače false
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

      ucitajJelovnikZaKuhinju(gson, oznaka, nazivDatoteke, datoteka);
    }
    return true;
  }

  /**
   * Učitava jelovnik za određenu vrstu kuhinje iz JSON datoteke i sprema ga u memoriju.
   *
   * @param gson instanca Gson za parsiranje JSON-a
   * @param oznaka oznaka kuhinje (npr. "MK", "IT")
   * @param nazivDatoteke naziv datoteke iz koje se učitava jelovnik
   * @param datoteka put do datoteke koja sadrži JSON zapis jelovnika
   */
  public void ucitajJelovnikZaKuhinju(Gson gson, String oznaka, String nazivDatoteke,
      Path datoteka) {
    try (var citac = Files.newBufferedReader(datoteka)) {
      Jelovnik[] niz = gson.fromJson(citac, Jelovnik[].class);
      Map<String, Jelovnik> mapa = new ConcurrentHashMap<>();
      for (var j : niz) {
        mapa.put(j.id(), j);
      }
      this.jelovnici.put(oznaka, mapa);
    } catch (IOException e) {
    }
  }

  /**
   * Učitava popis partnera iz konfiguracije.
   *
   * @return true ako je učitavanje partnera bilo uspješno, inače false
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
      try (var citac = Files.newBufferedReader(datoteka)) {
        Partner[] niz = gson.fromJson(citac, Partner[].class);
        if (niz != null) {
          for (Partner p : niz) {
            this.partneri.put(p.id(), p);
          }
        }
      }
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Pokreće poslužitelj za registraciju partnera. Na svaku mrežnu vezu stvara novu dretvu za obradu
   * zahtjeva.
   */
  public void pokreniPosluziteljRegistracija() {
    var mreznaVrata = Integer.parseInt(this.konfig.dajPostavku("mreznaVrataRegistracija"));
    var brojCekaca = Integer.parseInt(this.konfig.dajPostavku("brojCekaca"));

    try (ServerSocket ss = new ServerSocket(mreznaVrata, brojCekaca)) {
      while (!this.kraj.get()) {
        var uticnica = ss.accept();
        var dretva = this.executor.submit(() -> obradiRegistraciju(uticnica));
        this.aktivneDretve.add(dretva);
        this.mapaDretviUticnica.put(dretva, uticnica);
      }
    } catch (IOException e) {
      System.err.println("[INFO] Gašenje poslužitelja za registraciju, zatvorena 1 veza.");
    }
  }

  /**
   * Sprema partnere u datoteku partnera.
   */
  public synchronized void spremiPartnere() {
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
        this.mapaDretviUticnica.put(dretva, uticnica);
      }
    } catch (IOException e) {
      System.err.println("[INFO] Gašenje poslužitelja za rad s partnerima, zatvorena 1 veza.");
    }
  }

  /**
   * Obradi zahtjev za registraciju partnera.
   * 
   * Metoda prihvaća dolaznu mrežnu utičnicu i obrađuje komande: - PARTNER ... → registracija novog
   * partnera - OBRIŠI ... → brisanje postojećeg partnera - POPIS → dohvaćanje popisa svih partnera
   * 
   * Sve specifične komande obrađuju se svojim metodama: - {@code obradiPartnerKomandu} -
   * {@code obradiObrisiKomandu} - {@code obradiKomanduPopis}
   *
   * @param uticnica mrežna utičnica s koje je primljen zahtjev za registraciju
   */
  public void obradiRegistraciju(Socket uticnica) {
    try (var ulaz = new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"));
        var izlaz = new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"))) {

      var linija = ulaz.readLine();
      if (pauzaRegistracija.get()) {
        izlaz.write("ERROR 24 - Poslužitelj za registraciju partnera u pauzi\n");
        izlaz.flush();
        return;
      }

      var komanda = linija.trim();
      var dijelovi = komanda.split("\\s+", 2);
      if (dijelovi.length < 1) {
        izlaz.write("ERROR 20 - Format komande nije ispravan\n");
        izlaz.flush();
        return;
      }

      if (komanda.startsWith("PARTNER")) {
        obradiPartnerKomandu(izlaz, komanda);
      } else if (komanda.startsWith("OBRIŠI")) {
        obradiObrisiKomandu(izlaz, komanda);
      } else if (komanda != null && komanda.trim().equalsIgnoreCase("POPIS")) {
        obradiKomanduPopis(izlaz);
      } else {
        izlaz.write("ERROR 20 - Format komande nije ispravan\n");
      }

      izlaz.flush();
      uticnica.shutdownOutput();
      uticnica.close();

      synchronized (this) {
        brojZatvorenihVeza++;
      }
    } catch (IOException | NumberFormatException e) {
    }
  }


  /**
   * Obradi komandu POPIS koja dohvaća listu svih registriranih partnera.
   *
   * @param izlaz ispisni tok prema klijentu
   */
  public void obradiKomanduPopis(PrintWriter izlaz) {
    izlaz.write("OK\n");
    Gson gson = new Gson();
    var popis = this.partneri.values().stream().map(p -> new PartnerPopis(p.id(), p.naziv(),
        p.vrstaKuhinje(), p.adresa(), p.mreznaVrata(), p.gpsSirina(), p.gpsDuzina())).toList();
    izlaz.write(gson.toJson(popis) + "\n");
  }

  /**
   * Obradi komandu za brisanje partnera iz sustava.
   *
   * Metoda provjerava ispravnost komande "OBRIŠI <id> <sigurnosniKod>", validira postoji li partner
   * s danim ID-jem i je li sigurnosni kod točan. Ako su uvjeti ispunjeni, partner se uklanja iz
   * kolekcije i ažurira se datoteka partnera.
   *
   * @param izlaz ispis odgovora klijentu
   * @param komanda puna komanda za brisanje partnera
   */
  public void obradiObrisiKomandu(PrintWriter izlaz, String komanda) {
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
   * Obradi komandu za registraciju novog partnera.
   *
   * Metoda provjerava format komande "PARTNER", parsira podatke o partneru (ID, naziv, vrsta
   * kuhinje, adresa, mrežna vrata, GPS koordinate) te provjerava postoji li već partner s istim
   * ID-jem. Ako ne postoji, partner se dodaje u kolekciju i sprema u datoteku.
   *
   * @param izlaz ispis prema partneru
   * @param komanda puna komanda za registraciju partnera
   */
  public void obradiPartnerKomandu(PrintWriter izlaz, String komanda) {
    String regex =
        "^PARTNER\\s([0-9]+)\\s\"([^\"]+)\"\\s([A-Za-z]+)\\s(\\S+)\\s([0-9]+)\\s([0-9]*\\.[0-9]+)\\s([0-9]*\\.[0-9]+)\\s([0-9]+)\\s([A-Za-z0-9]+)$";
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
      int vrataKraj = Integer.parseInt(matcher.group(8));
      String adminKod = matcher.group(9);


      if (this.partneri.containsKey(id)) {
        izlaz.write("ERROR 21 - Već postoji partner s id u kolekciji partnera\n");
      } else {
        String kod = Integer.toHexString((naziv + adresa).hashCode());
        String kodAdmin = adminKod;
        // int vrataKraj = Integer.parseInt(this.konfig.dajPostavku("mreznaVrataKraj"));
        Partner novi =
            new Partner(id, naziv, vrsta, adresa, vrata, vrataKraj, sirina, duzina, kod, kodAdmin);
        this.partneri.put(id, novi);
        spremiPartnere();
        izlaz.write("OK " + kod + "\n");
      }
    }
  }

  /**
   * Objekt za zaključavanje pristupa datoteci obračuna. Koristi se u metodi
   * {@code odradiLokotObracuna} za sinkronizaciju.
   */
  private final Object lokotObracuna = new Object();

  /**
   * Obrada zahtjeva partnera tijekom rada poslužitelja.
   *
   * Prima mrežnu utičnicu, čita prvu liniju zahtjeva te na temelju komande prosljeđuje obradu
   * jednoj od metoda:
   * <ul>
   * <li>{@code obradiJelovnikKomandu}</li>
   * <li>{@code obradiKartaPicaKomandu}</li>
   * <li>{@code obradiObracunKomandu}</li>
   * </ul>
   * U slučaju neispravne komande, vraća odgovarajuću grešku partneru.
   *
   * @param uticnica mrežna utičnica preko koje partner šalje zahtjev
   */
  public void obradiRadPartnera(Socket uticnica) {
    try (var ulaz = new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"));
        var izlaz = new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"))) {

      var linija = ulaz.readLine();
      var komanda = linija.trim();

      if (komanda.startsWith("JELOVNIK")) {
        obradiJelovnikKomandu(izlaz, komanda);
      } else if (komanda.startsWith("KARTAPIĆA")) {
        obradiKartaPicaKomandu(izlaz, komanda);
      } else if (komanda.startsWith("OBRAČUNWS")) {
        obradiObracunKomandu(ulaz, izlaz, komanda);
      } else if (komanda.startsWith("OBRAČUN")) {
        obradiObracunRestKomandu(ulaz, izlaz, komanda);
      } else {
        izlaz.write("ERROR 30 - Format komande nije ispravan\n");
      }
      izlaz.flush();
      uticnica.shutdownOutput();
      uticnica.close();
      synchronized (this) {
        brojZatvorenihVeza++;
      }
    } catch (IOException e) {
    }
  }

  /**
   * Provjera za obradiRadPartnera. Poziva provjeriIspravnostObracuna i ucitajJsonObracune
   * 
   * Obrada komande OBRAČUN koju partner šalje za dostavu obračuna narudžbi.
   *
   * @param ulaz ulazni tok s podacima koji sadrži JSON zapis obračuna
   * @param izlaz izlazni tok za slanje odgovora partneru
   * @param komanda komanda koja se obrađuje
   */
  public void obradiObracunKomandu(BufferedReader ulaz, PrintWriter izlaz, String komanda) {
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
      var novi = ucitajJsonObracune(ulaz);
      if (!provjeriIspravnostObracuna(partner, novi, izlaz))
        return;

      Gson gson = new Gson();
      odradiLokotObracuna(gson, novi);
      izlaz.write("OK\n");
    } catch (JsonSyntaxException e) {
      izlaz.write("ERROR 35 - Neispravan obračun\n");
    } catch (Exception e) {
      izlaz.write("ERROR 39 - Nešto drugo nije u redu\n");
    }
  }
  
  /**
   * Obradi obracun rest komandu.
   *
   * @param ulaz za čitanjez
   * @param izlaz za pisanje
   * @param dijelovi dijelovi komande
   */
  private void obradiObracunRestKomandu(BufferedReader ulaz, PrintWriter izlaz, String komanda) {
    var matcher = Pattern.compile("^OBRAČUN\\s+(\\d+)\\s+(\\S+)$").matcher(komanda);
    if (!matcher.matches()) {
        izlaz.write("ERROR 30 - Format komande nije ispravan\n");
        return;
    }

    int id = Integer.parseInt(matcher.group(1));
    String kod = matcher.group(2);
    var partner = this.partneri.get(id);

    if (partner == null || !partner.sigurnosniKod().equals(kod)) {
        izlaz.write("ERROR 31 - Ne postoji partner s id u kolekciji partnera i/ili neispravan sigurnosni kod partnera\n");
        return;
    }
    if (pauzaPartneri.get()) {
        izlaz.write("ERROR 36 - Poslužitelj za partnere u pauzi\n");
        return;
    }
    try {
        var novi = ucitajJsonObracune(ulaz);
        Gson gson = new Gson();
        odradiLokotObracuna(gson, novi);

        boolean ok = posaljiRestObracune(novi);
        if (!ok) {
            izlaz.write("ERROR 37 - RESTful zahtjev nije uspješan\n");
            return;
        }
        izlaz.write("OK\n");
    } catch (JsonSyntaxException e) {
        izlaz.write("ERROR 35 - Neispravan obračun\n");
    } catch (Exception e) {
        izlaz.write("ERROR 39 - Nešto drugo nije u redu\n");
    }
}
  
  /**
   * Posalji rest obracune.
   *
   * @param obracuni koji se šalju
   * @return true, vraća true ako je uspješno
   */
  private boolean posaljiRestObracune(Obracun[] obracuni) {
    try {
        String url = this.konfig.dajPostavku("restAdresa") + "/obracun";
        HttpClient client = HttpClient.newHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(obracuni);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("[DEBUG] REST POST /obracun status: " + response.statusCode());

        return response.statusCode() == 201;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

  /**
   * Učitava JSON zapis obračuna iz ulaznog toka i pretvara ga u niz objekata
   * 
   * Metoda čita ulazni tok sve dok ne pročita cijeli JSON.
   *
   * @param ulaz ulazni tok podataka koji sadrži JSON zapis
   * @return niz objekata tipa {@code Obracun} učitanih iz JSON zapisa
   * @throws IOException ako dođe do pogreške prilikom čitanja ulaznog toka
   */
  public Obracun[] ucitajJsonObracune(BufferedReader ulaz) throws IOException {
    StringBuilder json = new StringBuilder();
    String linijaJson;
    while ((linijaJson = ulaz.readLine()) != null) {
      json.append(linijaJson).append("\n");
      if (linijaJson.trim().endsWith("]"))
        break;
    }
    Gson gson = new Gson();
    return gson.fromJson(json.toString(), Obracun[].class);
  }

  /**
   * Provjeri ispravnost obracuna. Poziva se u obradiObracunKomandu.
   *
   * @param partner partner za kojeg je obračun primljen
   * @param novi niz novih zapisa obračuna za provjeru
   * @param izlaz tok za ispis rezultata provjere
   * @return true ako su svi zapisi u obračunu ispravni
   */
  public boolean provjeriIspravnostObracuna(Partner partner, Obracun[] novi, PrintWriter izlaz) {
    var jelovnik = this.jelovnici.get(partner.vrstaKuhinje());
    for (var o : novi) {
      if (o.jelo()) {
        if (jelovnik == null || !jelovnik.containsKey(o.id())) {
          izlaz.write("ERROR 35 - Neispravan obračun\n");
          return false;
        }
      } else {
        if (!this.kartaPica.containsKey(o.id())) {
          izlaz.write("ERROR 35 - Neispravan obračun\n");
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Sinkronizirano zapisuje obračune u datoteku koristeći hehanizam međusobnog isključivanja kako
   * bi se spriječila istovremena izmjena podataka iz više dretvi.
   * 
   * Metoda za zapis u obračun za obraduObračunKomandu
   *
   * @param gson objekt za serijalizaciju u JSON
   * @param novi niz novih obračuna koji se dodaju
   * @throws IOException ako dođe do greške pri čitanju ili pisanju datoteke
   */
  public void odradiLokotObracuna(Gson gson, Obracun[] novi) throws IOException {
    synchronized (lokotObracuna) {
      String nazivDatoteke = this.konfig.dajPostavku("datotekaObracuna");
      Path datoteka = Path.of(nazivDatoteke);

      Obracun[] postojeci = new Obracun[0];
      if (Files.exists(datoteka) && Files.size(datoteka) > 0) {
        try (var citac = Files.newBufferedReader(datoteka)) {
          postojeci = gson.fromJson(citac, Obracun[].class);
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
   * Obradi komandu KARTAPIĆA i pošalji partneru njegovu kartu pića. Provodi validaciju partnera i
   * njegovog sigurnosnog koda.
   *
   * @param izlaz izlazni tok za slanje odgovora partneru
   * @param komanda ulazna komanda u obliku "KARTAPIĆA <id> <sigurnosniKod>"
   */
  public void obradiKartaPicaKomandu(PrintWriter izlaz, String komanda) {
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
   * Obradi komandu JELOVNIK i pošalji partneru njegov jelovnik. Provodi validaciju partnera,
   * sigurnosnog koda i postoji li jelovnik za traženu vrstu kuhinje.
   *
   * @param izlaz izlazni tok za slanje odgovora partneru
   * @param komanda ulazna komanda u obliku "JELOVNIK <id> <sigurnosniKod>"
   */
  public void obradiJelovnikKomandu(PrintWriter izlaz, String komanda) {
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
