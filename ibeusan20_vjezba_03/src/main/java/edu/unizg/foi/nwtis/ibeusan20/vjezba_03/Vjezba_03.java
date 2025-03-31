package edu.unizg.foi.nwtis.ibeusan20.vjezba_03;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Klasa Vjezba_03.
 */
public class Vjezba_03 {
  private Konfiguracija konfig;
  private int maksDubina = 0;
  private int pauzaDretve = 0;
  private int maksDretvi = 0;
  private AtomicInteger brojSlobodnihDretvi = new AtomicInteger(0);
  private AtomicInteger ukupanBrojDretvi = new AtomicInteger(0);
  private ExecutorService izvrsitelj;
  private Queue<PodaciOdgodenaPutanja> odgodenePutanje = new ConcurrentLinkedQueue<>();
  private Queue<Future<SkupPodatakaPretrazivanja>> rezultatiDretvi = new ConcurrentLinkedQueue<>();
  private String prelozakNazivaDatoteke = "";
  private String trazenaRijec = "";

  /**
   * Glavna metoda.
   *
   * @param args 4 argumenta
   */
  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Broj argumenata nije 4.");
      return;
    }

    var program = new Vjezba_03();
    program.pokreniPostupak(args);
  }

  /**
   * Pokreni postupak.
   *
   * @param args mora bit argument
   */
  private void pokreniPostupak(String[] args) {
    if (!this.ucitajKonfigiraciju(args[0])) {
      return;
    }
    this.maksDubina = Integer.parseInt(this.konfig.dajPostavku("maksDubina"));
    this.pauzaDretve = Integer.parseInt(this.konfig.dajPostavku("pauzaDretve"));
    this.maksDretvi = Integer.parseInt(this.konfig.dajPostavku("maksDretvi"));

    this.brojSlobodnihDretvi.set(this.maksDretvi);

    this.prelozakNazivaDatoteke = args[2];
    this.trazenaRijec = args[3];

    Thread.Builder graditelj = Thread.ofVirtual();
    ThreadFactory tvornicaDretvi = graditelj.factory();
    this.izvrsitelj = Executors.newThreadPerTaskExecutor(tvornicaDretvi);

    var inicijalnaPutanja = Path.of(args[1]).toAbsolutePath();

    this.rezultatiDretvi
        .add(this.izvrsitelj.submit(() -> this.pretraziPutanju(inicijalnaPutanja, 0)));

    try {
      Thread.sleep(this.pauzaDretve);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (var p : this.odgodenePutanje) {
      while (this.brojSlobodnihDretvi.get() == 0) {
        try {
          Thread.sleep(this.pauzaDretve);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      this.rezultatiDretvi.add(
          this.izvrsitelj.submit(() -> this.pretraziPutanju(p.nazivPutanje(), p.vazecaDubina())));
    }

    var rezultatiRada = new ArrayList<PodaciPretrazivanja>();

    for (var f : this.rezultatiDretvi) {
      if (f.isDone()) {
        rezultatiRada.addAll(f.resultNow().getPodaci());
      } else {
        while (!f.isDone()) {
          try {
            Thread.sleep(this.pauzaDretve);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }

    for (var p : rezultatiRada) {
      System.out
          .println(p.nazivPutanje() + " => " + p.nazivDatoteke() + " : " + p.brojPronadenih());
    }

  }

  /**
   * Pretrazi putanju.
   *
   * @param putanjeDirektorija putanje direktorija
   * @param dubinaDirektorija dubina direktorija
   * @return skup podataka pretrazivanja
   */
  public SkupPodatakaPretrazivanja pretraziPutanju(Path putanjeDirektorija, int dubinaDirektorija) {
    this.brojSlobodnihDretvi.decrementAndGet();
    this.ukupanBrojDretvi.incrementAndGet();
    var podaci = new SkupPodatakaPretrazivanja();

    System.out.println(putanjeDirektorija + " " + dubinaDirektorija);

    try (var tokPutanja = Files.list(putanjeDirektorija)) {
      tokPutanja.forEach((element) -> {
        if (Files.isDirectory(element)) {
          if (dubinaDirektorija < this.maksDubina) {
            if (this.brojSlobodnihDretvi.get() > 0) {
              this.rezultatiDretvi.add(this.izvrsitelj
                  .submit(() -> this.pretraziPutanju(element, dubinaDirektorija + 1)));
            } else {
              var novaOdgoda = new PodaciOdgodenaPutanja(element, dubinaDirektorija + 1);
              this.odgodenePutanje.add(novaOdgoda);
            }
          }
        } else if (Files.isReadable(element)) {
          pretraziDatoteku(putanjeDirektorija, element, podaci);
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }

    this.brojSlobodnihDretvi.incrementAndGet();
    return podaci;
  }

  /**
   * Pretrazi datoteku.
   *
   * @param putanjeDirektorija putanje direktorija
   * @param element element
   * @param podaci podaci
   */
  private void pretraziDatoteku(Path putanjeDirektorija, Path element,
      SkupPodatakaPretrazivanja podaci) {

    System.out.println("Datoteka: " + element);
    int brojPronalazenja = 0;

    try (var citacDatoteke = Files.newBufferedReader(element)) {
      while (true) {
        var linija = citacDatoteke.readLine();
        if (linija == null) {
          break;
        }
        var indeks = linija.indexOf(this.trazenaRijec);
        if (indeks == -1) {
          continue;
        }
        brojPronalazenja++;
        // TODO Riješiti za ostali dio linije
      }
      if (brojPronalazenja > 0) {
        var noviPodatak = new PodaciPretrazivanja(putanjeDirektorija.toString(),
            element.getFileName().toString(), brojPronalazenja);
        podaci.dodajPodatak(noviPodatak);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Ucitaj konfigiraciju.
   *
   * @param nazivDatoteke naziv datotekejava -jar target/{LDAP_korisnik}_vjezba_03-1.0.0.jar
   *        NWTiS_03.txt . "*.java" class
   * @return true, ako je uspješno
   */
  private boolean ucitajKonfigiraciju(String nazivDatoteke) {
    try {
      this.konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
      return true;
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }
}
