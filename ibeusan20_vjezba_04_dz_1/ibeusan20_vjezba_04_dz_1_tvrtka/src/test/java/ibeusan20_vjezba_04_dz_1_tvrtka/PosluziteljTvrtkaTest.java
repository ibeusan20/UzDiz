package ibeusan20_vjezba_04_dz_1_tvrtka;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.google.gson.Gson;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_04_dz_1.PosluziteljTvrtka;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Jelovnik;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Obracun;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Partner;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PosluziteljTvrtkaTest {
  private PosluziteljTvrtka posluziteljTvrtka;

  private static final String TEST_KONFIG = "test_tvrtka_konfig.txt";
  private static final String TEST_KARTA_PICA = "test_tvrtka_karta.json";
  private static final String TEST_PARTNERI = "test_tvrtka_partneri.json";
  private static final String TEST_JELOVNIK = "kuhinja_9.json";
  private static final String TEST_OBRACUN = "test_obracun.json";

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    String konfig = String.join("\n", "datotekaKartaPica=" + TEST_KARTA_PICA,
        "datotekaPartnera=" + TEST_PARTNERI, "kuhinja_9=IT;Talijanska", "kodZaKraj=testKod",
        "mreznaVrataKraj=9101", "mreznaVrataRegistracija=9102", "mreznaVrataRad=9103",
        "brojCekaca=10", "pauzaDretve=100", "datotekaObracuna=test_obracun.json");
    Files.writeString(Path.of(TEST_KONFIG), konfig);

    String kartaJson =
        "[{\"id\":\"p1\",\"jelo\":false,\"naziv\":\"Sok\",\"kolicina\":0.5,\"cijena\":2.5}]";
    Files.writeString(Path.of(TEST_KARTA_PICA), kartaJson);

    String partneriJson =
        "[{\"id\":1,\"naziv\":\"Rest1\",\"vrstaKuhinje\":\"IT\",\"adresa\":\"localhost\",\"mreznaVrata\":9111,\"gpsSirina\":45.0,\"gpsDuzina\":15.0,\"sigurnosniKod\":\"sig1\"}]";
    Files.writeString(Path.of(TEST_PARTNERI), partneriJson);

    String jelovnikJson = "[{\"id\":\"j1\",\"jelo\":false,\"naziv\":\"Pizza\",\"cijena\":8.0}]";
    Files.writeString(Path.of(TEST_JELOVNIK), jelovnikJson);

    String obracunJson = "[]";
    Files.writeString(Path.of(TEST_OBRACUN), obracunJson);
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {
    Files.deleteIfExists(Path.of(TEST_KONFIG));
    Files.deleteIfExists(Path.of(TEST_KARTA_PICA));
    Files.deleteIfExists(Path.of(TEST_PARTNERI));
    Files.deleteIfExists(Path.of(TEST_JELOVNIK));
    Files.deleteIfExists(Path.of(TEST_OBRACUN));
  }

  @BeforeEach
  void setUp() throws Exception {
    this.posluziteljTvrtka = new PosluziteljTvrtka();

    String partneriJson =
        "[{\"id\":1,\"naziv\":\"Rest1\",\"vrstaKuhinje\":\"IT\",\"adresa\":\"localhost\",\"mreznaVrata\":9111,\"gpsSirina\":45.0,\"gpsDuzina\":15.0,\"sigurnosniKod\":\"sig1\"}]";
    Files.writeString(Path.of(TEST_PARTNERI), partneriJson);

    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.ucitajPartnere();
  }

  @AfterEach
  void tearDown() throws Exception {}

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

      assertTrue(this.posluziteljTvrtka.ucitajKonfiguraciju(nazivDatoteke),
          "Problem kod učitavanja datoteke.");

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

    try (Socket socket = new Socket("localhost", 9101);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
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

    try (Socket socket = new Socket("localhost", 9101);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
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
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return true;
      }

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean isDone() {
        return false;
      }

      @Override
      public Object get() {
        return null;
      }

      @Override
      public Object get(long timeout, java.util.concurrent.TimeUnit unit) {
        return null;
      }
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
      @Override
      public InputStream getInputStream() {
        return ulaz;
      }

      @Override
      public OutputStream getOutputStream() {
        return izlaz;
      }

      @Override
      public InetAddress getInetAddress() {
        return InetAddress.getLoopbackAddress();
      }

      @Override
      public void shutdownInput() {}

      @Override
      public void shutdownOutput() {}

      @Override
      public void close() {}
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
      @Override
      public InputStream getInputStream() {
        return ulaz;
      }

      @Override
      public OutputStream getOutputStream() {
        return izlaz;
      }

      @Override
      public InetAddress getInetAddress() {
        return InetAddress.getLoopbackAddress();
      }

      @Override
      public void shutdownInput() {}

      @Override
      public void shutdownOutput() {}

      @Override
      public void close() {}
    };

    Boolean rezultat = posluziteljTvrtka.obradiKraj(socket);

    assertTrue(rezultat, "Metoda nije vratila TRUE čak i kod greške");
    String odgovor = izlaz.toString("utf8").trim();
    assertEquals("ERROR 10 - Format komande nije ispravan ili nije ispravan kod za kraj", odgovor,
        "Očekivan je ERROR 10");
    assertFalse(posluziteljTvrtka.kraj.get(),
        "Zastavica kraj ne smije biti postavljena na true kod pogrešnog koda");
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
      @Override
      public InputStream getInputStream() {
        return ulaz;
      }

      @Override
      public OutputStream getOutputStream() {
        return izlaz;
      }

      @Override
      public InetAddress getInetAddress() {
        try {
          return InetAddress.getByName("8.8.8.8");
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      public void shutdownInput() {}

      @Override
      public void shutdownOutput() {}

      @Override
      public void close() {}
    };

    Boolean rezultat = posluziteljTvrtka.obradiKraj(socket);

    assertTrue(rezultat, "Metoda nije vratila TRUE čak i kod greške");
    String odgovor = izlaz.toString("utf8").trim();
    assertEquals("ERROR 11 - Adresa računala s kojeg je poslan zahtjev nije lokalna adresa",
        odgovor, "Očekivan je ERROR 11 zbog udaljene IP adrese");
    assertFalse(posluziteljTvrtka.kraj.get(),
        "Zastavica kraj ne smije biti postavljena na true kad je IP nedozvoljen");
  }

  @Test
  @Order(9)
  @DisplayName("testProvjeriFormatKomande")
  void testProvjeriFormatKomande() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.kodZaKraj = posluziteljTvrtka.getKonfig().dajPostavku("kodZaKraj");

    assertTrue(posluziteljTvrtka.provjeriFormatKomande("KRAJ testKod"),
        "Ispravan format komande nije prepoznat");
    assertFalse(posluziteljTvrtka.provjeriFormatKomande("KRAJ pogresanKod"),
        "Neispravan kod je pogrešno prepoznat kao ispravan");
    assertFalse(posluziteljTvrtka.provjeriFormatKomande("KRJ testKod"),
        "Neispravna komanda je prepoznata kao ispravna");
    assertFalse(posluziteljTvrtka.provjeriFormatKomande("KRAJtestKod"),
        "Nedostaje razmak pa ne bi smjelo biti ispravno");
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
      @Override
      public OutputStream getOutputStream() {
        return izlaz;
      }

      @Override
      public void shutdownOutput() {}

      @Override
      public void close() {}
    };

    posluziteljTvrtka.posaljiPorukuGreske(socket);

    String rezultat = izlaz.toString("utf8").trim();
    assertEquals("ERROR 19 - Nešto drugo nije u redu.", rezultat, "Greška nije ispravno poslana");
  }

  @Test
  @Order(12)
  @DisplayName("testUcitajJelovnike")
  void testUcitajJelovnike() {
    assertTrue(posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG), "Konfiguracija nije učitana");

    boolean rezultat = posluziteljTvrtka.ucitajJelovnike();
    assertTrue(rezultat, "ucitajJelovnike() nije vratio true");

    assertTrue(posluziteljTvrtka.jelovnici.containsKey("IT"),
        "Jelovnik za kuhinju IT nije pronađen");

    var jelovnikMapa = posluziteljTvrtka.jelovnici.get("IT");
    assertNotNull(jelovnikMapa, "Mapa za jelovnik IT je null");

    assertTrue(jelovnikMapa.containsKey("j1"), "Jelovnik ne sadrži jelo j1 iz JSON datoteke");

    var jelo = jelovnikMapa.get("j1");
    assertEquals("Pizza", jelo.naziv(), "Naziv jela j1 nije ispravan");
    assertEquals(8.0f, jelo.cijena(), "Cijena jela j1 nije ispravna");
  }

  @Test
  @Order(13)
  @DisplayName("testUcitajJelovnikZaKuhinju")
  void testUcitajJelovnikZaKuhinju() {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    String nazivDatoteke = posluziteljTvrtka.getKonfig().dajPostavku("kuhinja_9") + ".json";
    Gson gson = new Gson();
    Path path = Path.of(TEST_JELOVNIK);
    String oznaka = "IT";

    posluziteljTvrtka.ucitajJelovnikZaKuhinju(gson, oznaka, nazivDatoteke, path);

    assertTrue(posluziteljTvrtka.jelovnici.containsKey(oznaka),
        "Nema unosa za oznaku kuhinje 'IT'");

    Map<String, Jelovnik> mapaJela = posluziteljTvrtka.jelovnici.get(oznaka);
    assertNotNull(mapaJela, "Mapa jela za kuhinju 'IT' je null");
    assertTrue(mapaJela.containsKey("j1"), "Nema jela s ID-jem 'j1'");

    Jelovnik jelo = mapaJela.get("j1");
    assertEquals("Pizza", jelo.naziv(), "Naziv jela nije ispravan");
    assertEquals(8.0f, jelo.cijena(), "Cijena jela nije ispravna");
  }

  @Test
  @Order(14)
  @DisplayName("testUcitajJelovnikZaKuhinju_NepostojecaDatoteka")
  void testUcitajJelovnikZaKuhinju_NepostojecaDatoteka() {
    Gson gson = new Gson();

    String oznaka = "XX";
    String nazivDatoteke = "kuhinja_nepostojeca.json";
    Path nepostojecaPutanja = Path.of(nazivDatoteke);

    assertDoesNotThrow(() -> posluziteljTvrtka.ucitajJelovnikZaKuhinju(gson, oznaka, nazivDatoteke,
        nepostojecaPutanja), "Metoda je bacila iznimku za nepostojeću datoteku");
    assertFalse(posluziteljTvrtka.jelovnici.containsKey(oznaka),
        "Jelovnik ne postoji jer ne postoji ni datoteka.");
  }

  @Test
  @Order(15)
  @DisplayName("testUcitajJelovnikZaKuhinju_NeispravanJSON")
  void testUcitajJelovnikZaKuhinju_NeispravanJSON() throws IOException {
    Gson gson = new Gson();
    String oznaka = "XX";
    String nazivDatoteke = "kuhinja_nepostojeca.json";
    Path nepostojecaPutanja = Path.of(nazivDatoteke);

    String neispravanJson =
        "[{\"id\":\"j1\",\"naziv\":\"Neispravno i nema sad zarez\" \"cijena\":10.0}]";
    Files.writeString(nepostojecaPutanja, neispravanJson);

    assertThrows(Exception.class, () -> posluziteljTvrtka.ucitajJelovnikZaKuhinju(gson, oznaka,
        nazivDatoteke, nepostojecaPutanja), "Neispravan JSON");
    assertFalse(posluziteljTvrtka.jelovnici.containsKey(oznaka),
        "Nema učitavanja jelovnika s neispravnim JSON-om");

    Files.deleteIfExists(nepostojecaPutanja);
  }

  @Test
  @Order(16)
  @DisplayName("testPokreniPosluziteljRegistracija")
  void testPokreniPosluziteljRegistracija() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    assertTrue(posluziteljTvrtka.ucitajPartnere(), "Partneri nisu učitani");

    posluziteljTvrtka.executor = Executors.newVirtualThreadPerTaskExecutor();

    Thread serverThread = new Thread(() -> posluziteljTvrtka.pokreniPosluziteljRegistracija());
    serverThread.start();

    Thread.sleep(500);

    try (Socket socket = new Socket("localhost", 9102);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      out.println("PARTNER 99 \"Rest1\" IT localhost 9111 45.0 15.0");
      String odgovor = in.readLine();
      assertNotNull(odgovor, "Nema odgovora od poslužitelja");
      assertTrue(odgovor.startsWith("OK"), "Očekivan odgovor koji počinje s OK");

      var partner = posluziteljTvrtka.partneri.get(99);
      assertNotNull(partner, "Partner nije registriran");
      assertEquals("Rest1", partner.naziv(), "Naziv partnera nije ispravan");
    }
    posluziteljTvrtka.kraj.set(true);
    serverThread.join(1000);
  }

  @Test
  @Order(17)
  @DisplayName("testSpremiPartnere")
  void testSpremiPartnere() throws Exception {
    assertTrue(posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG), "Konfiguracija nije učitana");

    var partner = new Partner(123, "TestRest", "IT", "Adresa123", 4567, 45.0f, 16.0f, "");
    posluziteljTvrtka.partneri.put(123, partner);
    posluziteljTvrtka.spremiPartnere();

    Path datoteka = Path.of(TEST_PARTNERI);
    assertTrue(Files.exists(datoteka), "Datoteka nije stvorena");

    String sadrzaj = Files.readString(datoteka);
    assertTrue(sadrzaj.contains("TestRest"), "JSON ne sadrži očekivani naziv partnera");

    Gson gson = new Gson();
    Partner[] partneriNiz = gson.fromJson(sadrzaj, Partner[].class);
    assertNotNull(partneriNiz, "Parsanje JSON-a nije uspjelo");
    boolean postoji =
        Arrays.stream(partneriNiz).anyMatch(p -> p.id() == 123 && p.naziv().equals("TestRest"));
    assertTrue(postoji, "Testni partner nije pronađen u spremljenom JSON-u");
  }

  @Test
  @Order(18)
  @DisplayName("testPokreniPosluziteljRad")
  void testPokreniPosluziteljRad() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.ucitajPartnere();
    posluziteljTvrtka.ucitajJelovnike();
    assertTrue(posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG), "Konfiguracija nije učitana");

    posluziteljTvrtka.executor = Executors.newVirtualThreadPerTaskExecutor();
    Thread serverThread = new Thread(() -> posluziteljTvrtka.pokreniPosluziteljRad());
    serverThread.start();
    Thread.sleep(500);

    var partner = posluziteljTvrtka.partneri.get(1);
    assertNotNull(partner, "Partner 1 nije tu. Dostupni: " + posluziteljTvrtka.partneri.keySet());

    try (Socket socket = new Socket("localhost", 9103);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      String komanda = "JELOVNIK " + partner.id() + " " + partner.sigurnosniKod();
      out.println(komanda);
      socket.shutdownOutput();
      String odgovor1 = in.readLine();
      String odgovor2 = in.readLine();

      assertEquals("OK", odgovor1, "Prvi redak odgovora trebao bi biti OK");
      assertTrue(odgovor2.contains("Pizza"), "Odgovor ne sadrži očekivani jelovnik");
    }
    posluziteljTvrtka.kraj.set(true);
    serverThread.join(1000);
  }

  @Test
  @Order(19)
  @DisplayName("testObradiRegistracijuPartnera")
  void testObradiRegistracijuPartnera() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.partneri.clear();
    String komanda = "PARTNER 200 \"NovaKuca\" IT localhost 12345 45.0 16.0\n";
    ByteArrayInputStream ulaz = new ByteArrayInputStream(komanda.getBytes("utf8"));
    ByteArrayOutputStream izlaz = new ByteArrayOutputStream();

    Socket socket = new Socket() {
      @Override
      public InputStream getInputStream() {
        return ulaz;
      }

      @Override
      public OutputStream getOutputStream() {
        return izlaz;
      }

      @Override
      public void shutdownOutput() {}

      @Override
      public void close() {}
    };

    posluziteljTvrtka.obradiRegistraciju(socket);
    String odgovor = izlaz.toString("utf8").trim();
    assertTrue(odgovor.startsWith("OK"), "Odgovor ne počinje s OK: " + odgovor);

    var partner = posluziteljTvrtka.partneri.get(200);
    assertNotNull(partner, "Partner nije spremljen");
    assertEquals("NovaKuca", partner.naziv(), "Naziv partnera nije ispravan");
    assertEquals("IT", partner.vrstaKuhinje(), "Kuhinja nije ispravna");
    assertEquals("localhost", partner.adresa(), "Adresa nije ispravna");
  }

  @Test
  @Order(20)
  @DisplayName("testObradiKomanduPopis")
  void testObradiKomanduPopis() {
    var partner =
        new Partner(303, "Neki tamo treći", "IT", "Adresa", 11111, 40.0f, 20.0f, "pop123");
    this.posluziteljTvrtka.partneri.put(303, partner);

    var izlaz = new StringWriter();
    var writer = new PrintWriter(izlaz);
    this.posluziteljTvrtka.obradiKomanduPopis(writer);
    writer.flush();
    var rezultat = izlaz.toString();
    String[] linije = rezultat.split("\n");

    assertEquals("OK", linije[0], "Prva linija mora biti OK");
    assertTrue(linije[1].contains("Neki tamo treći"), "JSON mora sadržavati podatke partnera");
    assertTrue(linije[1].contains("IT"), "JSON mora sadržavati kuhinju");
  }

  @Test
  @Order(21)
  @DisplayName("testObradiObrisiKomandu")
  void testObradiObrisiKomandu() throws IOException {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);

    var sigKod = Integer.toHexString(("TestniPartner" + "TestAdresa").hashCode());
    var partner = new Partner(77, "TestniPartner", "IT", "TestAdresa", 9999, 45.0f, 15.0f, sigKod);
    posluziteljTvrtka.partneri.put(77, partner);

    var out1 = new StringWriter();
    var writer1 = new PrintWriter(out1);
    posluziteljTvrtka.obradiObrisiKomandu(writer1, "OBRIŠI 77 " + sigKod);
    writer1.flush();
    assertEquals("OK", out1.toString().trim(), "Partner nije ispravno obrisan");

    var out2 = new StringWriter();
    var writer2 = new PrintWriter(out2);
    posluziteljTvrtka.partneri.put(77, partner);
    posluziteljTvrtka.obradiObrisiKomandu(writer2, "OBRIŠI 77 krivikod");
    writer2.flush();
    assertTrue(out2.toString().contains("ERROR 22"), "Očekivan ERROR 22 za krivi sigurnosni kod");

    var out3 = new StringWriter();
    var writer3 = new PrintWriter(out3);
    posluziteljTvrtka.obradiObrisiKomandu(writer3, "OBRIŠI 999 krivikod");
    writer3.flush();
    assertTrue(out3.toString().contains("ERROR 23"), "Očekivan ERROR 23 za nepostojećeg partnera");

    var out4 = new StringWriter();
    var writer4 = new PrintWriter(out4);
    posluziteljTvrtka.obradiObrisiKomandu(writer4, "OBRISI 77 " + sigKod);
    writer4.flush();
    assertTrue(out4.toString().contains("ERROR 20"), "Očekivan ERROR 20 za neispravan format");
  }

  @Test
  @Order(22)
  @DisplayName("testObradiPartnerKomandu")
  void testObradiPartnerKomandu() throws IOException {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);

    StringWriter out1 = new StringWriter();
    PrintWriter writer1 = new PrintWriter(out1);
    String komanda1 = "PARTNER 200 \"NovaKuca\" IT AdresaX 1234 45.0 15.0";
    posluziteljTvrtka.obradiPartnerKomandu(writer1, komanda1);
    writer1.flush();
    String odgovor1 = out1.toString().trim();
    assertTrue(odgovor1.startsWith("OK"), "Ispravan partner nije ispravno registriran");

    Partner p = posluziteljTvrtka.partneri.get(200);
    assertNotNull(p, "Partner nije spremljen");
    assertEquals("NovaKuca", p.naziv(), "Partner nije ispravno dodan.");

    StringWriter out2 = new StringWriter();
    PrintWriter writer2 = new PrintWriter(out2);
    String komanda2 = "PARTNER 200 \"Duplikat\" IT AdresaY 5678 44.0 16.0";
    posluziteljTvrtka.obradiPartnerKomandu(writer2, komanda2);
    writer2.flush();
    assertTrue(out2.toString().contains("ERROR 21"), "Partner s istim ID-em već postoji.");

    StringWriter out3 = new StringWriter();
    PrintWriter writer3 = new PrintWriter(out3);
    String komanda3 = "PARTNER 300 NovaKuca IT AdresaX 1234 45.0 15.0";
    posluziteljTvrtka.obradiPartnerKomandu(writer3, komanda3);
    writer3.flush();
    assertTrue(out3.toString().contains("ERROR 20"), "Neispravan format komande.");
  }

  @Test
  @Order(23)
  @DisplayName("testObradiRadPartnera")
  void testObradiRadPartnera() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.ucitajJelovnike();
    posluziteljTvrtka.ucitajPartnere();
    String komanda = "JELOVNIK 1 sig1\n";
    ByteArrayInputStream ulaz = new ByteArrayInputStream(komanda.getBytes("utf8"));
    ByteArrayOutputStream izlaz = new ByteArrayOutputStream();

    Socket socket = new Socket() {
      @Override
      public InputStream getInputStream() {
        return ulaz;
      }

      @Override
      public OutputStream getOutputStream() {
        return izlaz;
      }

      @Override
      public void shutdownOutput() {}

      @Override
      public void close() {}
    };
    posluziteljTvrtka.obradiRadPartnera(socket);
    String odgovor = izlaz.toString("utf8");
    assertTrue(odgovor.contains("OK"), "Odgovor ne sadrži OK");
    assertTrue(odgovor.contains("Pizza"), "Odgovor ne sadrži očekivano jelo iz jelovnika");
  }

  @Test
  @Order(24)
  @DisplayName("testObradiRadPartnera_NeispravnaKomanda")
  void testObradiRadPartnera_NeispravnaKomanda() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.ucitajPartnere();
    String komanda = "NEPOSTOJEĆEEE 1 sig1\n";
    ByteArrayInputStream ulaz = new ByteArrayInputStream(komanda.getBytes("utf8"));
    ByteArrayOutputStream izlaz = new ByteArrayOutputStream();
    Socket socket = new Socket() {
      @Override
      public InputStream getInputStream() {
        return ulaz;
      }

      @Override
      public OutputStream getOutputStream() {
        return izlaz;
      }

      @Override
      public void shutdownOutput() {}

      @Override
      public void close() {}
    };
    posluziteljTvrtka.obradiRadPartnera(socket);
    String odgovor = izlaz.toString("utf8").trim();
    assertEquals("ERROR 30 - Format komande nije ispravan", odgovor,
        "Neispravna komanda nije vraćena s ispravnom porukom");
  }

  @Test
  @Order(25)
  @DisplayName("testUcitajJsonObracune")
  void testUcitajJsonObracune() throws Exception {
    String json = """
        [
          {
            "partner": 1,
            "id": "j1",
            "jelo": false,
            "kolicina": 2.0,
            "cijena": 8.0,
            "vrijeme": 1680000000000
          },
          {
            "partner": 1,
            "id": "j2",
            "jelo": false,
            "kolicina": 2.0,
            "cijena": 8.0,
            "vrijeme": 1680000000876
          }
        ]
        """;
    BufferedReader ulaz = new BufferedReader(new StringReader(json));
    Obracun[] obracuni = posluziteljTvrtka.ucitajJsonObracune(ulaz);
    assertNotNull(obracuni, "Niz obračuna ne smije biti null");
    assertEquals(2, obracuni.length, "Niz bi trebao sadržavati 2 obračuna");
    assertEquals("j1", obracuni[0].id(), "ID prvog obračuna nije ispravan");
    assertEquals(false, obracuni[0].jelo(), "Vrsta stavke nije ispravna");
    assertEquals(2.0f, obracuni[0].kolicina(), "Količina nije ispravna");
    assertEquals(8.0f, obracuni[0].cijena(), "Cijena nije ispravna");
  }

  @Test
  @Order(26)
  @DisplayName("testProvjeriIspravnostObracuna")
  void testProvjeriIspravnostObracuna() throws Exception {
    posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG);
    posluziteljTvrtka.ucitajKartuPica();
    posluziteljTvrtka.ucitajJelovnike();
    posluziteljTvrtka.ucitajPartnere();

    var partner = posluziteljTvrtka.partneri.get(1);
    assertNotNull(partner, "Testni partner nije učitan");

    Obracun[] obracuni =
        new Obracun[] {new Obracun(1, "j1", true, 2.0f, 8.0f, new Date().getTime()),
            new Obracun(1, "p1", false, 1.0f, 2.5f, new Date().getTime())};
    StringWriter sw = new StringWriter();
    PrintWriter izlaz = new PrintWriter(sw, true);
    boolean rezultat = posluziteljTvrtka.provjeriIspravnostObracuna(partner, obracuni, izlaz);
    assertTrue(rezultat, "Unesen obračun nije ispravan.");
    assertEquals("", sw.toString().trim(), "Ne očekuje se greška u izlazu.");
  }

  @Test
  @Order(27)
  @DisplayName("testProvjeriIspravnostObracuna_NeispravanIdStavke")
  void testProvjeriIspravnostObracuna_NeispravanIdStavke() throws Exception {
    var partner = posluziteljTvrtka.partneri.get(1);
    assertNotNull(partner);
    Obracun[] obracuni =
        {new Obracun(1, "nePostoji", true, 1.0f, 1.0f, new Date().getTime())};
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    boolean rezultat = posluziteljTvrtka.provjeriIspravnostObracuna(partner, obracuni, pw);
    assertFalse(rezultat);
    assertTrue(sw.toString().contains("ERROR 35"), "Očekivan je ERROR 35 zbog nepostojećeg jela");
  }

  @Test
  @Order(28)
  @DisplayName("testOdradiLokotObracuna")
  void testOdradiLokotObracuna() throws Exception {
    String stvarniPut = posluziteljTvrtka.getKonfig().dajPostavku("datotekaObracuna");
    assertEquals(TEST_OBRACUN, stvarniPut, "Putanja do obračuna nije ispravna");

    Gson gson = new Gson();
    Obracun testObracun = new Obracun(1, "p1", false, 1.5f, 2.5f, new Date().getTime());
    Obracun[] niz = new Obracun[] {testObracun};

    posluziteljTvrtka.odradiLokotObracuna(gson, niz);

    Path datoteka = Path.of(TEST_OBRACUN);
    assertTrue(Files.exists(datoteka), "Datoteka s obračunima ne postoji!");

    String sadrzaj = Files.readString(datoteka);
    assertFalse(sadrzaj.isBlank(), "Datoteka je prazna!");

    Obracun[] procitani = gson.fromJson(sadrzaj, Obracun[].class);
    assertNotNull(procitani, "Niz obračuna ne smije biti null");
    assertTrue(procitani.length >= 1, "Očekuje se barem jedan obračun u datoteci");

    Obracun posljednji = procitani[procitani.length - 1];
    assertEquals("p1", posljednji.id(), "ID stavke nije ispravan");
    assertFalse(posljednji.jelo(), "Trebalo bi biti piće (false), ali nije");
    assertEquals(1.5f, posljednji.kolicina(), 0.001, "Količina nije ispravna");
    assertEquals(2.5f, posljednji.cijena(), 0.001, "Cijena nije ispravna");
  }

  @Test
  @Order(29)
  @DisplayName("testOdradiLokotObracuna_KonkurentniUpis")
  void testOdradiLokotObracuna_KonkurentniUpis() throws Exception {
    Path datoteka = Path.of(TEST_OBRACUN);
    Files.writeString(datoteka, "[]");

    int brojDretvi = 5;
    ExecutorService executor = Executors.newFixedThreadPool(brojDretvi);
    Gson gson = new Gson();

    CountDownLatch latch = new CountDownLatch(brojDretvi);

    for (int i = 0; i < brojDretvi; i++) {
      int indeks = i;
      executor.submit(() -> {
        try {
          Obracun obracun = new Obracun(1, "p" + indeks, false, 1.0f + indeks, 2.5f + indeks,
              new Date().getTime());
          Obracun[] niz = new Obracun[] {obracun};
          posluziteljTvrtka.odradiLokotObracuna(gson, niz);
        } catch (IOException e) {
          fail("Iznimka prilikom pisanja: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await();
    executor.shutdown();

    String sadrzaj = Files.readString(datoteka);
    Obracun[] svi = gson.fromJson(sadrzaj, Obracun[].class);
    assertNotNull(svi, "Niz obračuna ne smije biti null");
    assertEquals(brojDretvi, svi.length, "Broj zapisa u datoteci ne odgovara broju dretvi");
    List<String> idjevi = Arrays.stream(svi).map(Obracun::id).toList();
    for (int i = 0; i < brojDretvi; i++) {
      assertTrue(idjevi.contains("p" + i), "Nedostaje ID p" + i);
    }
  }

  @Test
  @Order(30)
  @DisplayName("testBezSinkronizacije_KonkurentniUpis")
  void testBezSinkronizacije_KonkurentniUpis() throws Exception {
    Gson gson = new Gson();
    Path datoteka = Path.of(TEST_OBRACUN);

    Runnable nesinkroniziraniUpis = () -> {
      try {
        Obracun o = new Obracun(1, "p1", false, 1.0f, 2.5f, new Date().getTime());
        Obracun[] niz = new Obracun[] {o};
        String json = gson.toJson(niz);
        Files.writeString(datoteka, json);
      } catch (IOException e) {
      }
    };

    ExecutorService executor = Executors.newFixedThreadPool(10);
    for (int i = 0; i < 50; i++) {
      executor.submit(nesinkroniziraniUpis);
    }
    executor.shutdown();
    boolean zavrseno = executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    assertTrue(zavrseno, "Sve dretve nisu završile na vrijeme");

    String sadrzaj = Files.readString(datoteka);
    try {
      Obracun[] niz = gson.fromJson(sadrzaj, Obracun[].class);
      assertNotNull(niz, "Parsanje nije uspjelo");
    } catch (Exception e) {
    }
  }

  @Test
  @Order(31)
  @DisplayName("testObradiKartaPicaKomandu")
  void testObradiKartaPicaKomandu() throws Exception {
    assertTrue(posluziteljTvrtka.ucitajPartnere(), "Partneri nisu učitani");
    assertTrue(posluziteljTvrtka.ucitajKartuPica(), "Karta pića nije učitana");

    String komanda = "KARTAPIĆA 1 sig1";
    StringWriter stringWriter = new StringWriter();
    PrintWriter izlaz = new PrintWriter(stringWriter, true);

    posluziteljTvrtka.obradiKartaPicaKomandu(izlaz, komanda);
    String rezultat = stringWriter.toString().trim();
    String[] linije = rezultat.split("\n");

    assertEquals("OK", linije[0], "Prva linija odgovora treba biti 'OK'");
    assertTrue(linije[1].contains("Sok"), "Karta pića treba sadržavati unos 'Sok'");
    assertTrue(linije[1].contains("p1"), "Karta pića treba sadržavati ID 'p1'");
  }

  @Test
  @Order(32)
  @DisplayName("testObradiKartaPicaKomandu_FormatKomandeNijeIspravan")
  void testObradiKartaPicaKomandu_FormatKomandeNijeIspravan() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    posluziteljTvrtka.obradiKartaPicaKomandu(pw, "KARTAPIĆA samoJedanParametar");
    assertEquals("ERROR 30 - Format komande nije ispravan", sw.toString().trim());
  }

  @Test
  @Order(33)
  @DisplayName("testObradiKartaPicaKomandu_NeispravanSigurnosniKod")
  void testObradiKartaPicaKomandu_NeispravanSigurnosniKod() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    posluziteljTvrtka.obradiKartaPicaKomandu(pw, "KARTAPIĆA 1 pogresanKod");
    assertEquals(
        "ERROR 31 - Ne postoji partner s id u kolekciji partnera i/ili neispravan sigurnosni kod partnera",
        sw.toString().trim());
  }

  @Test
  @Order(34)
  @DisplayName("testObradiJelovnikKomandu")
  void testObradiJelovnikKomandu() throws Exception {
    assertTrue(posluziteljTvrtka.ucitajKonfiguraciju(TEST_KONFIG), "Konfiguracija nije učitana");
    assertTrue(posluziteljTvrtka.ucitajPartnere(), "Partneri nisu učitani");
    assertTrue(posluziteljTvrtka.ucitajJelovnike(), "Jelovnici nisu učitani");

    String komanda = "JELOVNIK 1 sig1";
    StringWriter stringWriter = new StringWriter();
    PrintWriter izlaz = new PrintWriter(stringWriter, true);
    posluziteljTvrtka.obradiJelovnikKomandu(izlaz, komanda);
    String rezultat = stringWriter.toString().trim();
    String[] linije = rezultat.split("\n");

    assertEquals("OK", linije[0], "Prva linija odgovora treba biti 'OK'");
    assertTrue(linije[1].contains("Pizza"), "Jelovnik treba sadržavati jelo 'Pizza'");
    assertTrue(linije[1].contains("j1"), "Jelovnik treba sadržavati ID 'j1'");
  }

  @Test
  @Order(35)
  @DisplayName("testObradiJelovnikKomandu_FormatKomandeNijeIspravan")
  void testObradiJelovnikKomandu_FormatKomandeNijeIspravan() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter izlaz = new PrintWriter(stringWriter, true);
    posluziteljTvrtka.obradiJelovnikKomandu(izlaz, "JELOVNIK samoJedanParametar");
    String rezultat = stringWriter.toString().trim();
    assertEquals("ERROR 30 - Format komande nije ispravan", rezultat);
  }

  @Test
  @Order(36)
  @DisplayName("testObradiJelovnikKomandu_NeispravanSigurnosniKod")
  void testObradiJelovnikKomandu_NeispravanSigurnosniKod() throws Exception {
    posluziteljTvrtka.ucitajPartnere();
    posluziteljTvrtka.ucitajJelovnike();
    StringWriter stringWriter = new StringWriter();
    PrintWriter izlaz = new PrintWriter(stringWriter, true);
    posluziteljTvrtka.obradiJelovnikKomandu(izlaz, "JELOVNIK 1 pogresanKod");
    String rezultat = stringWriter.toString().trim();
    assertEquals(
        "ERROR 31 - Ne postoji partner s id u kolekciji partnera i/ili neispravan sigurnosni kod partnera",
        rezultat);
  }

  @Test
  @Order(37)
  @DisplayName("testObradiJelovnikKomandu_NemaJelovnikaZaVrstuKuhinje")
  void testObradiJelovnikKomandu_NemaJelovnikaZaVrstuKuhinje() throws Exception {
    Partner p = new Partner(99, "Fejkk", "XYZ", "lokacija", 9100, 0.0f, 0.0f, "testkod");
    posluziteljTvrtka.partneri.put(99, p);
    StringWriter stringWriter = new StringWriter();
    PrintWriter izlaz = new PrintWriter(stringWriter, true);
    posluziteljTvrtka.obradiJelovnikKomandu(izlaz, "JELOVNIK 99 testkod");
    String rezultat = stringWriter.toString().trim();
    assertEquals("ERROR 32 - Ne postoji jelovnik s vrstom kuhinje koju partner ima ugovorenu",
        rezultat);
  }

  @Test
  @DisplayName("testOdradiLokotObracuna_ParalelniUpis")
  void testOdradiLokotObracuna_ParalelniUpis() throws Exception {
    Gson gson = new Gson();
    Path datoteka = Path.of(TEST_OBRACUN);
    Files.writeString(datoteka, "[]");
    int brojUpisa = 50;
    ExecutorService executor = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(brojUpisa);

    for (int i = 0; i < brojUpisa; i++) {
      final int id = i;
      executor.submit(() -> {
        try {
          Obracun obracun =
              new Obracun(1, "p1", false, 1.0f + id, 2.0f + id, new Date().getTime());
          Obracun[] niz = new Obracun[] {obracun};
          posluziteljTvrtka.odradiLokotObracuna(gson, niz);
        } catch (IOException e) {
          fail("Upis nije uspio: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await();
    executor.shutdown();
    String sadrzaj = Files.readString(datoteka);
    Obracun[] svi = gson.fromJson(sadrzaj, Obracun[].class);
    assertNotNull(svi, "Parsanje JSON-a nije uspjelo");
    assertTrue(svi.length >= brojUpisa, "Nisu svi obračuni zapisani: očekivano barem " + brojUpisa);
  }
}
