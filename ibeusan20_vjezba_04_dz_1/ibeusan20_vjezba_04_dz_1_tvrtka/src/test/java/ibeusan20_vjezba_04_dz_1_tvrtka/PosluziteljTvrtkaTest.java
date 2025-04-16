package ibeusan20_vjezba_04_dz_1_tvrtka;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_04_dz_1.PosluziteljTvrtka;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PosluziteljTvrtkaTest {
  private PosluziteljTvrtka posluziteljTvrtka;
  
  private static final String TEST_KONFIG = "test-tvrtka-konfig.txt";
  private static final String TEST_KARTA_PICA = "test-tvrtka-karta.json";
  private static final String TEST_PARTNERI = "test-tvrtka-partneri.json";
  private static final String TEST_JELOVNIK = "kuhinja_9.json";

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    String konfig = String.join("\n",
      "datotekaKartaPica=" + TEST_KARTA_PICA,
      "datotekaPartnera=" + TEST_PARTNERI,
      "kuhinja_9=IT;Talijanska",
      "kodZaKraj=testKod",
      "mreznaVrataKraj=9101",
      "mreznaVrataRegistracija=9102",
      "mreznaVrataRad=9103",
      "brojCekaca=1",
      "pauzaDretve=500"
    );
    Files.writeString(Path.of(TEST_KONFIG), konfig);

    String kartaJson = "[{\"id\":\"p1\",\"naziv\":\"Sok\",\"kolicina\":0.5,\"cijena\":2.5}]";
    Files.writeString(Path.of(TEST_KARTA_PICA), kartaJson);

    String partneriJson = "[{\"id\":1,\"naziv\":\"Rest1\",\"vrstaKuhinje\":\"IT\",\"adresa\":\"Adresa1\",\"mreznaVrata\":1234,\"gpsSirina\":45.0,\"gpsDuzina\":15.0,\"sigurnosniKod\":\"sig1\"}]";
    Files.writeString(Path.of(TEST_PARTNERI), partneriJson);

    String jelovnikJson = "[{\"id\":\"j1\",\"naziv\":\"Pizza\",\"cijena\":8.0}]";
    Files.writeString(Path.of(TEST_JELOVNIK), jelovnikJson);
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    Files.deleteIfExists(Path.of(TEST_KONFIG));
    Files.deleteIfExists(Path.of(TEST_KARTA_PICA));
    Files.deleteIfExists(Path.of(TEST_PARTNERI));
    Files.deleteIfExists(Path.of(TEST_JELOVNIK));
  }

  @BeforeEach
  void setUp() throws Exception {
    this.posluziteljTvrtka = new PosluziteljTvrtka();
  }

  @AfterEach
  void tearDown() throws Exception {
    this.posluziteljTvrtka = null;
  }

  @Test
  @Order(1)
  @DisplayName("testUcitajKonfiguraciju")
  void testUcitajKonfiguraciju() {
    try {
      String nazivDatoteke = this.getClass().getName() + ".txt";
      Konfiguracija konfig = KonfiguracijaApstraktna.kreirajKonfiguraciju(nazivDatoteke);

      konfig.spremiPostavku(this.getClass().getName(), "1");
      konfig.spremiPostavku("2", this.getClass().getName());
      konfig.spremiPostavku("3", "4");
      konfig.spremiPostavku(this.getClass().getName(), this.getClass().getName());

      konfig.spremiKonfiguraciju();
      
//      assertFalse(this.posluziteljTvrtka.ucitajKonfiguraciju(nazivDatoteke + ".pero"), "Ne propoznaje neispravni naziv datoteke. ");
      assertTrue(this.posluziteljTvrtka.ucitajKonfiguraciju(nazivDatoteke), "Problem kod učitavanja datoteke.");

      var props1 = konfig.dajSvePostavke();
      var props2 = this.posluziteljTvrtka.getKonfig().dajSvePostavke();

      var kljucevi1 = props1.keySet().stream().sorted().toArray();
      var kljucevi2 = props2.keySet().stream().sorted().toArray();
      
      assertArrayEquals(kljucevi1, kljucevi2, "Ključevi nisu isti");

      var vrijednosti1 = props1.values().stream().sorted().toArray();
      var vrijednosti2 = props2.values().stream().sorted().toArray();

      assertArrayEquals(vrijednosti1, vrijednosti2, "Vrijednosti nisu iste");      
      
      for (var p : props1.keySet()) {
        if (!props2.get((String) p).equals(props1.get(p))) {
          fail("Nema sve postavke.");
        }
      }
    
      Files.delete(Path.of(nazivDatoteke));
    } catch (NeispravnaKonfiguracija | IOException e) {
      fail(e.getMessage());
    }
  }

  @Test
  @Order(2)
  @DisplayName("testUcitajKartuPica")
  void testUcitajKartaPica() {
    this.posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    boolean rezultat = this.posluziteljTvrtka.ucitajKartuPica();
    //ISPIS
//    if (rezultat) {
//      System.out.println("[INFO] Karta pića uspješno učitana.");
//    } else {
//      System.out.println("[ERROR] Karta pića nije učitana.");
//    }
    assertTrue(rezultat, "Karta pića nije uspješno učitana.");
  }
  
  @Test
  @Order(3)
  @DisplayName("testPripremiKreni")
  void testPripremiKreni() throws Exception {
    Thread testThread = new Thread(() -> {
      posluziteljTvrtka.pripremiKreni(TEST_KONFIG);
    });
    testThread.start();

    Thread.sleep(1000);

    try (
      Socket socket = new Socket("localhost", 9101);
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      out.println("KRAJ testKod");
      String response = in.readLine();
      assertNotNull(response, "Odgovor ne smije biti null");
      assertEquals("OK", response, "Očekivan odgovor na KRAJ komandu");
    }

    testThread.join(2000);

    assertNotNull(this.posluziteljTvrtka.getKonfig(), "Konfiguracija nije učitana");
    assertTrue(this.posluziteljTvrtka.kartaPica.containsKey("p1"), "Piće nije učitano");
    assertTrue(this.posluziteljTvrtka.jelovnici.containsKey("IT"), "Jelovnik nije učitan");
    assertTrue(this.posluziteljTvrtka.partneri.containsKey(1), "Partner nije učitan");
  }
  
  @Test
  @Order(4)
  @DisplayName("testPokreniPosluziteljKraj")
  void testPokreniPosluziteljKraj() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.kodZaKraj = posluziteljTvrtka.getKonfig().dajPostavku("kodZaKraj");

    Thread dretva = new Thread(() -> {
      posluziteljTvrtka.pokreniPosluziteljKraj();
    });
    dretva.start();

    Thread.sleep(500);

    try (
      Socket socket = new Socket("localhost", 9101);
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      out.println("KRAJ testKod");
      String odgovor = in.readLine();
      assertEquals("OK", odgovor, "Očekivani odgovor na KRAJ komandu je OK");
    }

    dretva.join(1000);

    assertTrue(posluziteljTvrtka.kraj.get(), "Zastavica 'kraj' nije postavljena na true");
  }
  
  @Test
  @Order(5)
  @DisplayName("testZatvoriDretveIMrezneVeze")
  void testZatvoriDretveIMrezneVeze() {
    Future<?> future1 = new Future<>() {
      @Override public boolean cancel(boolean mayInterruptIfRunning) { return true; }
      @Override public boolean isCancelled() { return false; }
      @Override public boolean isDone() { return false; }
      @Override public Object get() { return null; }
      @Override public Object get(long timeout, java.util.concurrent.TimeUnit unit) { return null; }
    };

    posluziteljTvrtka.aktivneDretve.add(future1);
    posluziteljTvrtka.dretvaRegistracija = future1;
    posluziteljTvrtka.dretvaRadPartnera = future1;
    posluziteljTvrtka.dretvaZaKraj = future1;

    int broj = posluziteljTvrtka.zatvoriDretveIMrezneVeze();

    assertEquals(4, broj, "Broj zatvorenih dretvi nije točan");
  }
  
  @Test
  @Order(6)
  @DisplayName("testObradiKraj")
  void testObradiKraj() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.kodZaKraj = posluziteljTvrtka.getKonfig().dajPostavku("kodZaKraj");

    String ulaznaKomanda = "KRAJ testKod\n";
    ByteArrayInputStream ulaz = new ByteArrayInputStream(ulaznaKomanda.getBytes("utf8"));
    ByteArrayOutputStream izlaz = new ByteArrayOutputStream();

    Socket socket = new Socket() {
      @Override public InputStream getInputStream() { return ulaz; }
      @Override public OutputStream getOutputStream() { return izlaz; }
      @Override public InetAddress getInetAddress() { return InetAddress.getLoopbackAddress(); }
      @Override public void shutdownInput() {}
      @Override public void shutdownOutput() {}
      @Override public void close() {}
    };

    Boolean rezultat = posluziteljTvrtka.obradiKraj(socket);

    assertTrue(rezultat, "Metoda nije vratila TRUE");
    String odgovor = izlaz.toString("utf8").trim();
    assertEquals("OK", odgovor, "Nije vraćen OK odgovor");
    assertTrue(posluziteljTvrtka.kraj.get(), "Zastavica kraj nije postavljena na true");
  }
  
  @Test
  @Order(7)
  @DisplayName("testObradiKraj_NeispravanKod")
  void testObradiKraj_NeispravanKod() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.kodZaKraj = posluziteljTvrtka.getKonfig().dajPostavku("kodZaKraj");

    String ulaznaKomanda = "KRAJ kriviKod\n";
    ByteArrayInputStream ulaz = new ByteArrayInputStream(ulaznaKomanda.getBytes("utf8"));
    ByteArrayOutputStream izlaz = new ByteArrayOutputStream();

    Socket socket = new Socket() {
      @Override public InputStream getInputStream() { return ulaz; }
      @Override public OutputStream getOutputStream() { return izlaz; }
      @Override public InetAddress getInetAddress() { return InetAddress.getLoopbackAddress(); }
      @Override public void shutdownInput() {}
      @Override public void shutdownOutput() {}
      @Override public void close() {}
    };

    Boolean rezultat = posluziteljTvrtka.obradiKraj(socket);

    assertTrue(rezultat, "Metoda nije vratila TRUE čak i kod greške");
    String odgovor = izlaz.toString("utf8").trim();
    assertEquals("ERROR 10 - Format komande nije ispravan ili nije ispravan kod za kraj", odgovor, "Očekivan je ERROR 10");
    assertFalse(posluziteljTvrtka.kraj.get(), "Zastavica kraj ne smije biti postavljena na true kod pogrešnog koda");
  }

  @Test
  @Order(8)
  @DisplayName("testObradiKraj_NeuspioZbogNeispravneAdrese")
  void testObradiKraj_NeuspioZbogNeispravneAdrese() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.kodZaKraj = posluziteljTvrtka.getKonfig().dajPostavku("kodZaKraj");

    String ulaznaKomanda = "KRAJ " + posluziteljTvrtka.kodZaKraj + "\n";
    ByteArrayInputStream ulaz = new ByteArrayInputStream(ulaznaKomanda.getBytes("utf8"));
    ByteArrayOutputStream izlaz = new ByteArrayOutputStream();

    Socket socket = new Socket() {
      @Override public InputStream getInputStream() { return ulaz; }
      @Override public OutputStream getOutputStream() { return izlaz; }

      @Override public InetAddress getInetAddress() {
        try {
          return InetAddress.getByName("8.8.8.8");
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override public void shutdownInput() {}
      @Override public void shutdownOutput() {}
      @Override public void close() {}
    };

    Boolean rezultat = posluziteljTvrtka.obradiKraj(socket);

    assertTrue(rezultat, "Metoda nije vratila TRUE čak i kod greške");
    String odgovor = izlaz.toString("utf8").trim();
    assertEquals("ERROR 11 - Adresa računala s kojeg je poslan zahtjev nije lokalna adresa", odgovor,
        "Očekivan je ERROR 11 zbog udaljene IP adrese");
    assertFalse(posluziteljTvrtka.kraj.get(), "Zastavica kraj ne smije biti postavljena na true kad je IP nedozvoljen");
  }
  
  @Test
  @Order(9)
  @DisplayName("testProvjeriFormatKomande")
  void testProvjeriFormatKomande() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.kodZaKraj = posluziteljTvrtka.getKonfig().dajPostavku("kodZaKraj");

    assertTrue(posluziteljTvrtka.provjeriFormatKomande("KRAJ testKod"), "Ispravan format komande nije prepoznat");
    assertFalse(posluziteljTvrtka.provjeriFormatKomande("KRAJ pogresanKod"), "Neispravan kod je pogrešno prepoznat kao ispravan");
    assertFalse(posluziteljTvrtka.provjeriFormatKomande("KRJ testKod"), "Neispravna komanda je prepoznata kao ispravna");
    assertFalse(posluziteljTvrtka.provjeriFormatKomande("KRAJtestKod"), "Nedostaje razmak pa ne bi smjelo biti ispravno");
  }
  
  @Test
  @Order(10)
  @DisplayName("testProvjeriLokalnuAdresu")
  void testProvjeriLokalnuAdresu() throws Exception {
    Socket socket = new Socket() {
      @Override
      public InetAddress getInetAddress() {
        return InetAddress.getLoopbackAddress();
      }
    };
    
    boolean rezultat = posluziteljTvrtka.provjeriLokalnuAdresu(socket);
    assertTrue(rezultat, "Loopback adresa nije prepoznata kao lokalna");
  }
  
  @Test
  @Order(11)
  @DisplayName("testPosaljiPorukuGreske")
  void testPosaljiPorukuGreske() throws Exception {
    ByteArrayOutputStream izlaz = new ByteArrayOutputStream();

    Socket socket = new Socket() {
      @Override public OutputStream getOutputStream() { return izlaz; }
      @Override public void shutdownOutput() {}
      @Override public void close() {}
    };

    posluziteljTvrtka.posaljiPorukuGreske(socket);

    String rezultat = izlaz.toString("utf8").trim();
    assertEquals("ERROR 19 - Nešto drugo nije u redu.", rezultat, "Greška nije ispravno poslana");
  }
  
  @Test
  @Order(12)
  @DisplayName("testUcitajJelovnike")
  void testUcitajJelovnike() {
    assertTrue(posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG),
        "Konfiguracija nije učitana");

    boolean rezultat = posluziteljTvrtka.ucitajJelovnike();
    assertTrue(rezultat, "ucitajJelovnike() nije vratio true");

    assertTrue(posluziteljTvrtka.jelovnici.containsKey("IT"),
        "Jelovnik za kuhinju IT nije pronađen");

    var jelovnikMapa = posluziteljTvrtka.jelovnici.get("IT");
    assertNotNull(jelovnikMapa, "Mapa za jelovnik IT je null");

    assertTrue(jelovnikMapa.containsKey("j1"),
        "Jelovnik ne sadrži jelo j1 iz JSON datoteke");

    var jelo = jelovnikMapa.get("j1");
    assertEquals("Pizza", jelo.naziv(),
        "Naziv jela j1 nije ispravan");
    assertEquals(8.0f, jelo.cijena(), "Cijena jela j1 nije ispravna");
  }


}
