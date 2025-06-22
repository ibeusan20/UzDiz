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

@Named("pregledZapisa")
@RequestScoped
public class PregledZapisa implements Serializable {
  private static final long serialVersionUID = 1L;

  @Inject
  private ZapisiFacade zapisiFacade;

  @Inject
  private KorisniciFacade korisniciFacade;

  private String korisnickoIme;
  private LocalDate datumOd;
  private LocalDate datumDo;

  private List<Zapisi> zapisi;

  public void pretrazi() {
    if (korisnickoIme != null && !korisnickoIme.isBlank() && datumOd != null && datumDo != null) {
      Timestamp od = Timestamp.valueOf(datumOd.atStartOfDay());
      Timestamp do_ = Timestamp.valueOf(datumDo.atTime(23, 59, 59));
      this.zapisi = zapisiFacade.dohvatiZapiseZaKorisnikaIRazdoblje(korisnickoIme, od, do_);
    }
  }

  public List<Korisnici> getSviKorisnici() {
    return korisniciFacade.findAll();
  }

  public String getKorisnickoIme() {
    return korisnickoIme;
  }

  public void setKorisnickoIme(String korisnickoIme) {
    this.korisnickoIme = korisnickoIme;
  }

  public LocalDate getDatumOd() {
    return datumOd;
  }

  public void setDatumOd(LocalDate datumOd) {
    this.datumOd = datumOd;
  }

  public LocalDate getDatumDo() {
    return datumDo;
  }

  public void setDatumDo(LocalDate datumDo) {
    this.datumDo = datumDo;
  }

  public List<Zapisi> getZapisi() {
    return zapisi;
  }

  public void setZapisi(List<Zapisi> zapisi) {
    this.zapisi = zapisi;
  }
}
