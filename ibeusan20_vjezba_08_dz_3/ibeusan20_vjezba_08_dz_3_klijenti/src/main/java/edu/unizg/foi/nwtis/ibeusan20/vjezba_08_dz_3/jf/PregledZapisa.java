package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Korisnici;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Zapisi;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.KorisniciFacade;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.ZapisiFacade;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Klasa PregledZapisa za istoimenu .xhtml stranicu.
 */
@Named("pregledZapisa")
@RequestScoped
public class PregledZapisa implements Serializable {
  
  /**  Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**  zapisi facade. */
  @Inject
  private ZapisiFacade zapisiFacade;

  /**  korisnici facade. */
  @Inject
  private KorisniciFacade korisniciFacade;

  /**  korisnicko ime. */
  private String korisnickoIme;
  
  /**  datum od. */
  private LocalDate datumOd;
  
  /**  datum do. */
  private LocalDate datumDo;

  /**  zapisi. */
  private List<Zapisi> zapisi;

  /**
   * Pretrazivanje zapisa po korisnickom imenu.
   */
  public void pretrazi() {
    if (korisnickoIme != null && !korisnickoIme.isBlank() && datumOd != null && datumDo != null) {
      Timestamp od = Timestamp.valueOf(datumOd.atStartOfDay());
      Timestamp do_ = Timestamp.valueOf(datumDo.atTime(23, 59, 59));
      this.zapisi = zapisiFacade.dohvatiZapiseZaKorisnikaIRazdoblje(korisnickoIme, od, do_);
    }
  }

  /**
   * Dohvaća sve korisnike.
   *
   * @return the svi korisnici
   */
  public List<Korisnici> getSviKorisnici() {
    return korisniciFacade.findAll();
  }

  /**
   * Dohvaća korisnicko ime.
   *
   * @return the korisnicko ime
   */
  public String getKorisnickoIme() {
    return korisnickoIme;
  }

  /**
   * Postavlja korisnicko ime.
   *
   * @param korisnickoIme the new korisnicko ime
   */
  public void setKorisnickoIme(String korisnickoIme) {
    this.korisnickoIme = korisnickoIme;
  }

  /**
   * Dohvaća datum od.
   *
   * @return the datum od
   */
  public LocalDate getDatumOd() {
    return datumOd;
  }

  /**
   * Postavlja datum od.
   *
   * @param datumOd the new datum od
   */
  public void setDatumOd(LocalDate datumOd) {
    this.datumOd = datumOd;
  }

  /**
   * Dohvaća datum do.
   *
   * @return the datum do
   */
  public LocalDate getDatumDo() {
    return datumDo;
  }

  /**
   * Postavlja datum do.
   *
   * @param datumDo the new datum do
   */
  public void setDatumDo(LocalDate datumDo) {
    this.datumDo = datumDo;
  }

  /**
   * Dohvaća zapise.
   *
   * @return the zapisi
   */
  public List<Zapisi> getZapisi() {
    return zapisi;
  }

  /**
   * Postavlja zapise.
   *
   * @param zapisi the new zapisi
   */
  public void setZapisi(List<Zapisi> zapisi) {
    this.zapisi = zapisi;
  }
}
