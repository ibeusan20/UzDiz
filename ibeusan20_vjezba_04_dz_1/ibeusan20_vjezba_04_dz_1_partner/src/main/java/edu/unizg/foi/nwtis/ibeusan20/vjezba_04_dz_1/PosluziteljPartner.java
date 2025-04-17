package edu.unizg.foi.nwtis.ibeusan20.vjezba_04_dz_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Jelovnik;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.KartaPica;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Narudzba;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Obracun;

// TODO: Auto-generated Javadoc
/**
 * Klasa PosluziteljPartner.
 */
public class PosluziteljPartner {

  /** Konfiguracijski podaci. */
  private Konfiguracija konfig;

  /** Predložak za kraj. */
  private Pattern predlozakKraj = Pattern.compile("^KRAJ$");

  /** Predložak za partnera. */
  private Pattern predlozakPartner = Pattern.compile("^PARTNER$");

  // private AtomicBoolean kraj = new AtomicBoolean(false);

  /** Jelovnik kolekcija. */
  private Map<String, Jelovnik> jelovnik = new ConcurrentHashMap<>();

  /** Karta pića kolekcija. */
  private Map<String, KartaPica> kartaPica = new ConcurrentHashMap<>();

  /** Varijabla za zapis id-ja partnera. */
  private int idPartnera;

  /** Varijabla za zapis sigurnosnog koda partnera. */
  private String sigKodPartnera;

  /** Lista aktivnih dretvi. */
  private final List<Future<?>> aktivneDretve = new ArrayList<>();

  /** Mapa aktivnih dretvi i njihovih mrežnih utičnica. */
  private final Map<Future<?>, Socket> mapaDretviUticnica = new ConcurrentHashMap<>();

  /** Executor servis. */
  private ExecutorService executor;

  /** Lista otvorenih narudžbi. */
  private Map<String, List<Narudzba>> otvoreneNarudzbe = new ConcurrentHashMap<>();

  /** Lista plaćenih / zatvorneih narudžbi. */
  private final Map<String, List<Narudzba>> placeneNarudzbe = new ConcurrentHashMap<>();

  /** Brojač naplaćenih narudžbi. */
  private int brojNaplacenihNarudzbi = 0;

  /** Početna pauza dretve. */
  private int pauzaDretve = 1000;

  /** Zaključavanje. */
  private final Object lokotNarudzbe = new Object();

  /**
   * Glavna metoda.
   *
   * @param args su argumenti
   */
  public static void main(String[] args) {
    if (args.length < 1 || args.length > 2) {
      System.out.println("Neispravan broj argumenata. (1 <= broj argumenata <= 2)");
      return;
    }
    var program = new PosluziteljPartner();
    gracioznoZatvaranje(program);
    var nazivDatoteke = args[0];

    if (!program.ucitajKonfiguraciju(nazivDatoteke)) {
      return;
    }
    if (args.length == 1) {
      program.registrirajPartnera();
      return;
    }

    var drugiArg = args[1].trim();

    if (program.predlozakKraj.matcher(drugiArg).matches()) {
      program.posaljiKraj();
      return;
    } else if (program.predlozakPartner.matcher(drugiArg).matches()) {
      if (!program.poveziSeITraziJelovnikIKartu()) {
        System.out.println("Neuspješno učitavanje jelovnika ili karte pića. Kraj rada."); //OBAVEZNO
        return;
      }
      program.pokreniPosluziteljKupaca();
      return;
    }
    System.out.println("ERROR 40 - Format komande nije ispravan");
  }

  /**
   * Postavlja shutdown hook za graciozno zatvaranje programa.
   *
   * @param program instanca poslužitelja partnera
   */
  private static void gracioznoZatvaranje(PosluziteljPartner program) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      int brojZatvorenih = 0;
      int brojPrekinutihDretvi = 0;

      for (var entry : program.mapaDretviUticnica.entrySet()) {
        Future<?> dretva = entry.getKey();
        Socket uticnica = entry.getValue();

        boolean prekinuta = dretva.cancel(true);
        if (prekinuta) brojPrekinutihDretvi++;

        try {
          if (uticnica != null && !uticnica.isClosed()) {
            uticnica.close();
            brojZatvorenih++;
            System.out.println("[INFO] Zatvorena mrežna utičnica za dretvu.");
          }
        } catch (IOException e) {
        }
        if (!dretva.isDone() && !dretva.isCancelled()) {
          prekinuta = dretva.cancel(true);
          if (prekinuta) {
            brojPrekinutihDretvi++;
          }
        }
      }
      System.out.println("[INFO] Ukupan broj zatvorenih veza: " + brojZatvorenih);
      System.out
          .println("[INFO] Ukupan broj prekinutih dretvi: " + brojPrekinutihDretvi);
    }));
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
      this.pauzaDretve = Integer.parseInt(konfig.dajPostavku("pauzaDretve"));
      return true;
    } catch (NeispravnaKonfiguracija ex) {
    }
    return false;
  }

  /**
   * Registriraj partnera.
   */
  private void registrirajPartnera() {
    try {
      String komanda = generirajKomanduPartnera();

      var adresa = konfig.dajPostavku("adresa");
      var vrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataRegistracija"));

      try (Socket mreznaUticnica = new Socket(adresa, vrata);
          PrintWriter pisac =
              new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
          BufferedReader citac =
              new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"))) {

        pisac.write(komanda + "\n");
        pisac.flush();
        mreznaUticnica.shutdownOutput();

        String odgovor = citac.readLine();
        mreznaUticnica.shutdownInput();

        if (odgovor != null && odgovor.startsWith("OK")) {
          String kod = odgovor.split("\\s+")[1];
          konfig.spremiPostavku("sigKod", kod);
          konfig.spremiKonfiguraciju();
        }
      }
    } catch (Exception e) {
    }
  }

  /**
   * Generiraj komandu partnera. Koristi se u registriranju partnera.
   *
   * @return vraća komandu PARTNER itd. za registriranje partnera
   */
  private String generirajKomanduPartnera() {
    var adresa = konfig.dajPostavku("adresa");
    var vrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataRegistracija"));
    var id = Integer.parseInt(konfig.dajPostavku("id"));
    var naziv = konfig.dajPostavku("naziv");
    var vrsta = konfig.dajPostavku("kuhinja");
    var mreznaVrata = konfig.dajPostavku("mreznaVrata");
    var gpsSirina = konfig.dajPostavku("gpsSirina");
    var gpsDuzina = konfig.dajPostavku("gpsDuzina");

    return String.format("PARTNER %d \"%s\" %s %s %s %s %s", id, naziv, vrsta, adresa, mreznaVrata,
        gpsSirina, gpsDuzina);
  }

  /**
   * Poveži se i traži jelovnik i kartu.
   *
   * @return ako je uspješno, vraća true
   */
  private boolean poveziSeITraziJelovnikIKartu() {
    try {
      this.idPartnera = Integer.parseInt(konfig.dajPostavku("id"));
      this.sigKodPartnera = konfig.dajPostavku("sigKod");
      if (this.sigKodPartnera == null || this.sigKodPartnera.isBlank()) {
        return false;
      }
      var adresa = konfig.dajPostavku("adresa");
      var vrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataRad"));

      if (!ucitajJelovnik(adresa, vrata))
        return false;
      if (!ucitajKartuPica(adresa, vrata))
        return false;
      
      return true;

    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Učitaj jelovnik.
   *
   * @param adresa je parametar kao mrežna adresa
   * @param vrata je parametar kao socket
   * @return vraća true u slučaju uspjeha
   */
  private boolean ucitajJelovnik(String adresa, int vrata) {
    String komanda = "JELOVNIK " + idPartnera + " " + sigKodPartnera;
    try (Socket socket = new Socket(adresa, vrata);
        PrintWriter pisac = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"));
        BufferedReader citac =
            new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"))) {

      pisac.write(komanda + "\n");
      pisac.flush();
      socket.shutdownOutput();

      if (!"OK".equals(citac.readLine()))
        return false;

      String json = citac.readLine();
      Jelovnik[] niz = new Gson().fromJson(json, Jelovnik[].class);
      for (Jelovnik j : niz)
        jelovnik.put(j.id(), j);
      return true;

    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Učitaj kartu pica.
   *
   * @param adresa je parametar kao mrežna adresa
   * @param vrata je parametar kao socket
   * @return vraća true u slučaju uspjeha
   */
  private boolean ucitajKartuPica(String adresa, int vrata) {
    String komanda = "KARTAPIĆA " + idPartnera + " " + sigKodPartnera;
    try (Socket mreznaUticnica = new Socket(adresa, vrata);
        PrintWriter pisac = new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
        BufferedReader citac =
            new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"))) {

      pisac.write(komanda + "\n");
      pisac.flush();
      mreznaUticnica.shutdownOutput();

      if (!"OK".equals(citac.readLine()))
        return false;

      String json = citac.readLine();
      KartaPica[] niz = new Gson().fromJson(json, KartaPica[].class);
      for (KartaPica p : niz)
        kartaPica.put(p.id(), p);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Pokreni posluzitelj kupaca.
   */
  private void pokreniPosluziteljKupaca() {
    int mreznaVrata = Integer.parseInt(konfig.dajPostavku("mreznaVrata"));
    int brojCekaca = Integer.parseInt(konfig.dajPostavku("brojCekaca"));
    this.pauzaDretve = Integer.parseInt(konfig.dajPostavku("pauzaDretve"));

    var builder = Thread.ofVirtual();
    this.executor = Executors.newThreadPerTaskExecutor(builder.factory());

    try (var serverSocket = new java.net.ServerSocket(mreznaVrata, brojCekaca)) {
      while (true) {
        var uticnica = serverSocket.accept();
        Future<?> dretva = executor.submit(() -> obradiZahtjevKupca(uticnica));
        aktivneDretve.add(dretva);
        mapaDretviUticnica.put(dretva, uticnica);

        aktivneDretve.removeIf(Future::isDone);
        Thread.sleep(pauzaDretve);
      }
    } catch (IOException | InterruptedException e) {
    }
  }

  /**
   * Obradi zahtjev kupca. Poziva funkcije ovisno o primljenom parametru: obradiJelovnikKupca.
   *
   * @param uticnica je parametar kao socket
   */
  private void obradiZahtjevKupca(Socket uticnica) {
    try (var ulaz = new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"));
        var izlaz = new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"))) {

      var linija = ulaz.readLine();
      
      if (linija == null || linija.isBlank()) {
        izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      } else if (linija.startsWith("JELOVNIK")) {
        obradiJelovnikKupca(ulaz, izlaz, linija);
      } else if (linija.startsWith("KARTAPIĆA")) {
        obradiKartaPicaKupca(ulaz, izlaz, linija);
      } else if (linija.startsWith("NARUDŽBA")) {
        obradiNarudzbuKupca(izlaz, linija);
      } else if (linija.startsWith("JELO")) {
        obradiJeloKupca(izlaz, linija);
      } else if (linija.startsWith("PIĆE")) {
        obradiPiceKupca(izlaz, linija);
      } else if (linija.startsWith("RAČUN")) {
        obradiRacunKupca(izlaz, linija);
      } else {
        izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      }
      izlaz.flush();
      uticnica.shutdownOutput();
      uticnica.close();
    } catch (IOException e) {
    }
  }

  /**
   * Obradi zahtjev kupca za jelovnikom.
   *
   * @param ulaz čitanje komande
   * @param izlaz izlaz
   * @param linija je primljena komanda
   */
  private void obradiJelovnikKupca(BufferedReader ulaz, PrintWriter izlaz, String linija) {
    var matcher = Pattern.compile("^JELOVNIK\\s+(\\w+)$").matcher(linija);
    if (!matcher.matches()) {
      izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      return;
    }
    String korisnik = matcher.group(1);
    if (this.jelovnik.isEmpty()) {
      izlaz.write("ERROR 46 - Neuspješno preuzimanje jelovnika\n");
    } else {
      izlaz.write("OK\n");
      Gson gson = new Gson();
      izlaz.write(gson.toJson(this.jelovnik.values()) + "\n");
    }
  }

  /**
   * Obradi zahtjev kupca za kartom pića.
   *
   * @param ulaz čitanje komande
   * @param izlaz izlaz
   * @param linija je primljena komanda
   */
  private void obradiKartaPicaKupca(BufferedReader ulaz, PrintWriter izlaz, String linija) {
    var matcher = Pattern.compile("^KARTAPIĆA\\s+(\\w+)$").matcher(linija);
    if (!matcher.matches()) {
      izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      return;
    }
    String korisnik = matcher.group(1);
    if (this.kartaPica.isEmpty()) {
      izlaz.write("ERROR 47 - Neuspješno preuzimanje karte pića\n");
    } else {
      izlaz.write("OK\n");
      Gson gson = new Gson();
      izlaz.write(gson.toJson(this.kartaPica.values()) + "\n");
    }
  }

  /**
   * Obradi zahtjev kupca za početak narudžbe.
   *
   * @param izlaz izlaz
   * @param linija je primljena komanda
   */
  private void obradiNarudzbuKupca(PrintWriter izlaz, String linija) {
    var matcher = Pattern.compile("^NARUDŽBA\\s+(\\w+)$").matcher(linija);
    if (!matcher.matches()) {
      izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      return;
    }
    String korisnik = matcher.group(1);
    synchronized (lokotNarudzbe) {
      if (otvoreneNarudzbe.containsKey(korisnik)) {
        izlaz.write("ERROR 44 - Već postoji otvorena narudžba za korisnika/kupca\n");
      } else {
        otvoreneNarudzbe.put(korisnik, new ArrayList<>());
        izlaz.write("OK\n");
      }
    }
  }

  /**
   * Obradi zahtjev kupca za dodavanje jela u narudžbu.
   *
   * @param izlaz izlaz
   * @param linija je primljena komanda
   */
  private void obradiJeloKupca(PrintWriter izlaz, String linija) {
    var matcher =
        Pattern.compile("^JELO\\s+(\\w+)\\s+(\\S+)\\s+([+-]?\\d*\\.?\\d+)$").matcher(linija);
    if (!matcher.matches()) {
      izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      return;
    }

    String korisnik = matcher.group(1);
    String idJela = matcher.group(2);
    float kolicina = Float.parseFloat(matcher.group(3));

    synchronized (lokotNarudzbe) {
      var narudzba = otvoreneNarudzbe.get(korisnik);
      if (narudzba == null) {
        izlaz.write("ERROR 43 - Ne postoji otvorena narudžba za korisnika/kupca\n");
        return;
      }
      var jelo = this.jelovnik.get(idJela);
      if (jelo == null) {
        izlaz.write("ERROR 41 - Ne postoji jelo s id u kolekciji jelovnika kod partnera\n");
        return;
      }
      float cijena = jelo.cijena() * kolicina;
      long vrijeme = new Date().getTime();
      narudzba.add(new Narudzba(korisnik, idJela, true, kolicina, cijena, vrijeme));
      izlaz.write("OK\n");
    }
  }

  /**
   * Obradi zahtjev kupca za dodavanje pića u narudžbu.
   *
   * @param izlaz je izlazni tok
   * @param linija je primjena komanda
   */
  private void obradiPiceKupca(PrintWriter izlaz, String linija) {
    var matcher =
        Pattern.compile("^PIĆE\\s+(\\w+)\\s+(\\S+)\\s+([+-]?\\d*\\.?\\d+)$").matcher(linija);
    if (!matcher.matches()) {
      izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      return;
    }

    String korisnik = matcher.group(1);
    String idPica = matcher.group(2);
    float kolicina = Float.parseFloat(matcher.group(3));

    synchronized (lokotNarudzbe) {
      var narudzba = otvoreneNarudzbe.get(korisnik);
      if (narudzba == null) {
        izlaz.write("ERROR 43 - Ne postoji otvorena narudžba za korisnika/kupca\n");
        return;
      }
      var pice = this.kartaPica.get(idPica);
      if (pice == null) {
        izlaz.write("ERROR 42 - Ne postoji piće s id u kolekciji karte pića kod partnera\n");
        return;
      }
      float cijena = pice.cijena() * kolicina;
      long vrijeme = System.currentTimeMillis();
      narudzba.add(new Narudzba(korisnik, idPica, false, kolicina, cijena, vrijeme));
      izlaz.write("OK\n");
    }
  }

  /**
   * Obradi zahtjev kupca za zatvaranje narudžbe i izdavanje računa.
   *
   * @param izlaz je izlazni tok
   * @param linija je primjena komanda
   */
  private void obradiRacunKupca(PrintWriter izlaz, String linija) {
    var matcher = Pattern.compile("^RAČUN\\s+(\\w+)$").matcher(linija);
    if (!matcher.matches()) {
      izlaz.write("ERROR 40 - Format komande nije ispravan\n");
      return;
    } 

    String korisnik = matcher.group(1);
    synchronized (lokotNarudzbe) {
      if (!provjeriOtvorenuNarudzbu(korisnik, izlaz))
        return;

      premjestiUNaplacene(korisnik);
      brojNaplacenihNarudzbi++;

      if (trebaSlatiObracun()) {
        var obracuni = generirajObracun();
        if (!posaljiObracun(obracuni)) {
          izlaz.write("ERROR 45 - Neuspješno slanje obračuna\n");
          return;
        }
        placeneNarudzbe.clear();
      }
      izlaz.write("OK\n");
    }
  }

  /**
   * Provjera otvorene narudžbe za obradiRacunKupca.
   *
   * @param korisnik je kupac koji naručuje
   * @param izlaz je izlazni tok
   * @return ako je uspješno, vraća true
   */
  private boolean provjeriOtvorenuNarudzbu(String korisnik, PrintWriter izlaz) {
    List<Narudzba> narudzbe = otvoreneNarudzbe.get(korisnik);
    if (narudzbe == null) {
      izlaz.write("ERROR 43 - Ne postoji otvorena narudžba za korisnika/kupca\n");
      return false;
    }
    return true;
  }

  /**
   * Premještanje narudžbe iz nenaplaćenih u naplaćene.
   *
   * @param korisnik je kupac
   */
  private void premjestiUNaplacene(String korisnik) {
    List<Narudzba> narudzbe = otvoreneNarudzbe.remove(korisnik);
    placeneNarudzbe.computeIfAbsent(korisnik, k -> new ArrayList<>()).addAll(narudzbe);
  }

  /**
   * Provjera treba li se slati obračun po kvoti učitanoj iz postavki.
   *
   * @return vraća true ako je istina
   */
  private boolean trebaSlatiObracun() {
    try {
      int kvota = Integer.parseInt(konfig.dajPostavku("kvotaNarudzbi"));
      return brojNaplacenihNarudzbi % kvota == 0;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Generiraj obračun.
   *
   * @return Vraća listu obračuna.
   */
  private List<Obracun> generirajObracun() {
    Map<String, Obracun> mapa = new HashMap<>();
    long sada = new Date().getTime();

    for (var lista : placeneNarudzbe.values()) {
      for (var n : lista) {
        mapa.compute(n.id(), (k, o) -> {
          float novaKolicina = (o != null ? o.kolicina() : 0) + n.kolicina();
          float novaCijena = n.cijena();
          return new Obracun(idPartnera, n.id(), n.jelo(), novaKolicina, novaCijena, sada);
        });
      }
    }
    return new ArrayList<>(mapa.values());
  }

  /**
   * Pošalji obračun.
   *
   * @param obracuni lista obračuna u kojoj su spremljeni obračuni
   * @return ako je uspješno vraća true
   */
  private boolean posaljiObracun(List<Obracun> obracuni) {
    try {
      var adresa = konfig.dajPostavku("adresa");
      var vrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataRad"));
      var uticnica = new Socket(adresa, vrata);

      PrintWriter out = new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"));
      BufferedReader in =
          new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"));

      out.write("OBRAČUN " + idPartnera + " " + sigKodPartnera + "\n");
      out.write(new Gson().toJson(obracuni) + "\n");
      out.flush();
      uticnica.shutdownOutput();
      boolean uspjeh = false;
      String odgovor;
      while ((odgovor = in.readLine()) != null) {
          if (odgovor.trim().startsWith("OK")) {
              uspjeh = true;
              break;
          }
      }
      uticnica.shutdownInput();
      uticnica.close();

      return uspjeh;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Slanje komande KRAJ.
   */
  private void posaljiKraj() {
    var kodZaKraj = konfig.dajPostavku("kodZaKraj");
    var sigKod = konfig.dajPostavku("sigKod");

    if (sigKod == null || sigKod.isBlank()) {
      return;
    }
    try (Socket uticnica = stvoriKrajSocket()) {
      posaljiKrajKomandu(uticnica, kodZaKraj);
      obradiOdgovorKraj(uticnica);
    } catch (IOException e) {
    }
  }

  /**
   * Otvara utučnicu za slanje komande KRAJ.
   *
   * @return the socket
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private Socket stvoriKrajSocket() throws IOException {
    var adresa = konfig.dajPostavku("adresa");
    var vrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataKraj"));
    return new Socket(adresa, vrata);
  }

  /**
   * Šalje komandu KRAJ kroz dani socket.
   *
   * @param uticnica the uticnica
   * @param kodZaKraj the kod za kraj
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void posaljiKrajKomandu(Socket uticnica, String kodZaKraj) throws IOException {
    var pisac = new PrintWriter(new OutputStreamWriter(uticnica.getOutputStream(), "utf8"));
    pisac.write("KRAJ " + kodZaKraj + "\n");
    pisac.flush();
    uticnica.shutdownOutput();
  }

  /**
   * Prima odgovor na komandu KRAJ i ispisuje rezultat.
   *
   * @param uticnica the uticnica
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void obradiOdgovorKraj(Socket uticnica) throws IOException {
    var citac = new BufferedReader(new InputStreamReader(uticnica.getInputStream(), "utf8"));
    String linija = citac.readLine();
    uticnica.shutdownInput();

    if ("OK".equals(linija)) {
      System.out.println("Uspješan kraj poslužitelja."); //obavezno
    }
  }
}
