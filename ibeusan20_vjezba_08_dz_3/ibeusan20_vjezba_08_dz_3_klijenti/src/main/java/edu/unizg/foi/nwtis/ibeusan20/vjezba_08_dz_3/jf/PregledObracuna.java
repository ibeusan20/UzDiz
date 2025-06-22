package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Obracuni;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Partneri;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.ObracuniFacade;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.PartneriFacade;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Klasa PregledObracuna  za istoimenu .xhtml stranicu.
 */
@Named("pregledObracuna")
@RequestScoped
public class PregledObracuna implements Serializable {
  
  /**  Constant serialVersionUID. */
  private static final long serialVersionUID = 64784345433L;

  /**  obracuni facade. */
  @Inject
  private ObracuniFacade obracuniFacade;

  /**  partneri facade. */
  @Inject
  private PartneriFacade partneriFacade;

  /**  id partnera. */
  private int idPartnera;
  
  /**  datum od. */
  private LocalDate datumOd;
  
  /**  datum do. */
  private LocalDate datumDo;

  /**  obracuni. */
  private List<Obracuni> obracuni;

  /**
   * dohvaća partnere.
   *
   * @return the partneri
   */
  public List<Partneri> getPartneri() {
    return partneriFacade.findAll();
  }

  /**
   * Pretrazivanje po datutmu.
   */
  public void pretrazi() {
    if (datumOd != null && datumDo != null && idPartnera > 0) {
      Timestamp od = Timestamp.valueOf(datumOd.atStartOfDay());
      Timestamp do_ = Timestamp.valueOf(datumDo.atTime(23, 59, 59));
      this.obracuni = obracuniFacade.dohvatiObracuneZaPartneraIRazdoblje(idPartnera, od, do_);
    }
  }

  /**
   * Dohvaća id partnera.
   *
   * @return the id partnera
   */
  public int getIdPartnera() {
    return idPartnera;
  }

  /**
   * Postavlja id partnera.
   *
   * @param idPartnera the new id partnera
   */
  public void setIdPartnera(int idPartnera) {
    this.idPartnera = idPartnera;
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
   * Dohvaća obracune.
   *
   * @return the obracuni
   */
  public List<Obracuni> getObracuni() {
    return obracuni;
  }

  /**
   * Postavlja obracune.
   *
   * @param obracuni the new obracuni
   */
  public void setObracuni(List<Obracuni> obracuni) {
    this.obracuni = obracuni;
  }
  
}
