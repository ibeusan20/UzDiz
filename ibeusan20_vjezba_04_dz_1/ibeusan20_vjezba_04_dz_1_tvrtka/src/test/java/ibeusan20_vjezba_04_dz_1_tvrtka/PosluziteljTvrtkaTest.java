package ibeusan20_vjezba_04_dz_1_tvrtka;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {
    this.posluziteljTvrtka = new PosluziteljTvrtka();
  }

  @AfterEach
  void tearDown() throws Exception {
    this.posluziteljTvrtka = null;
  }

  @Test
  @Order(6)
  @DisplayName("testMain")
  void testMain() {
  }

  @Test
  @Order(5)
  @DisplayName("testPripremiKreni")
  void testPripremiKreni() {
  }

  @Test
  @Order(4)
  @DisplayName("testPokreniPosluziteljKraj")
  void testPokreniPosluziteljKraj() {
  }

  @Test
  @Order(3)
  @DisplayName("testObradiKraj")
  void testObradiKraj() {
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
  void testUcitajKartuPica() {
  }

}
