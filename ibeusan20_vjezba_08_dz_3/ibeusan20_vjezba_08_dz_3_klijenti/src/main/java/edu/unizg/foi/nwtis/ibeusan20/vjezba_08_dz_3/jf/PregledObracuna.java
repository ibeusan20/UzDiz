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

@Named("pregledObracuna")
@RequestScoped
public class PregledObracuna implements Serializable {
  private static final long serialVersionUID = 64784345433L;

  @Inject
  private ObracuniFacade obracuniFacade;

  @Inject
  private PartneriFacade partneriFacade;

  private int idPartnera;
  private LocalDate datumOd;
  private LocalDate datumDo;

  private List<Obracuni> obracuni;

  public List<Partneri> getPartneri() {
    return partneriFacade.findAll();
  }

  public void pretrazi() {
    if (datumOd != null && datumDo != null && idPartnera > 0) {
      Timestamp od = Timestamp.valueOf(datumOd.atStartOfDay());
      Timestamp do_ = Timestamp.valueOf(datumDo.atTime(23, 59, 59));
      this.obracuni = obracuniFacade.dohvatiObracuneZaPartneraIRazdoblje(idPartnera, od, do_);
    }
  }

  public int getIdPartnera() {
    return idPartnera;
  }

  public void setIdPartnera(int idPartnera) {
    this.idPartnera = idPartnera;
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

  public List<Obracuni> getObracuni() {
    return obracuni;
  }

  public void setObracuni(List<Obracuni> obracuni) {
    this.obracuni = obracuni;
  }
  
}
