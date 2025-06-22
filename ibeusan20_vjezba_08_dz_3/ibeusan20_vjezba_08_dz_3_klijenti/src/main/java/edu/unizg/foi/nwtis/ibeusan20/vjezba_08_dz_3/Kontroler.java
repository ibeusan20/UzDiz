/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ws.WebSocketTvrtka;
import edu.unizg.foi.nwtis.podaci.Obracun;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.GenericType;

/**
 * Klasa Kontroler. Služi kao kontroler za .jsp stranice.
 *
 * @author Ivan Beusan
 */
@Controller
@Path("tvrtka")
@RequestScoped
public class Kontroler {

  /**  globalni podaci. */
  @Inject
  private GlobalniPodaci globalniPodaci;

  /**  model. */
  @Inject
  private Models model;

  /**  binding result. */
  @Inject
  private BindingResult bindingResult;

  /**  servis tvrtka. */
  @Inject
  @RestClient
  ServisTvrtkaKlijent servisTvrtka;

  /**
   * Pocetak putanja.
   */
  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  /**
   * Kraj putanja.
   *
   * @return the string
   */
  @GET
  @Path("kraj")
  @View("status.jsp")
  public String kraj() {
    try {
      var status = this.servisTvrtka.headPosluziteljKraj().getStatus();
      this.model.put("statusOperacije", status);
      if (status== 200) {
        posaljiStatusPoruku("NE RADI");
      } else {
        posaljiStatusPoruku("RADI");
      }
      dohvatiStatuse();
      
      return "redirect:/tvrtka/admin/panel";
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/tvrtka/admin/panel";
  }

  /**
   * Status putanja.
   */
  @GET
  @Path("status")
  @View("status.jsp")
  public void status() {
    dohvatiStatuse();
  }

  /**
   * Start id putanja.
   *
   * @param id the id
   * @return the string
   */
  @GET
  @Path("start/{id}")
  @View("status.jsp")
  public String startId(@PathParam("id") int id) {
    var status = this.servisTvrtka.headPosluziteljStart(id).getStatus();
    this.model.put("status", status);
    if (status == 200) {
      posaljiStatusPoruku("RADI");
    } else {
      posaljiStatusPoruku("NE RADI");
    }
    this.model.put("samoOperacija", true);
    return "redirect:/tvrtka/admin/panel";
  }

  /**
   * Pauzat id putanja.
   *
   * @param id the id
   * @return the string
   */
  @GET
  @Path("pauza/{id}")
  @View("status.jsp")
  public String pauzatId(@PathParam("id") int id) {
    var status = this.servisTvrtka.headPosluziteljPauza(id).getStatus();
    this.model.put("status", status);
    if (status == 200) {
      posaljiStatusPoruku("NE RADI");
    }
    this.model.put("samoOperacija", true);
    return "redirect:/tvrtka/admin/panel";
  }

  /**
   * Partneri putanja.
   */
  @GET
  @Path("partner")
  @View("partneri.jsp")
  public void partneri() {
    var odgovor = this.servisTvrtka.getPartneri();
    var status = odgovor.getStatus();
    if (status == 200) {
      var partneri = odgovor.readEntity(new GenericType<List<Partner>>() {});
      this.model.put("status", status);
      this.model.put("partneri", partneri);
    }
  }

  /**
   * Partner putanja.
   *
   * @param id the id
   */
  @GET
  @Path("partner/{id}")
  @View("partner.jsp")
  public void partner(@PathParam("id") int id) {
    var odgovor = this.servisTvrtka.getPartner(id);
    var status = odgovor.getStatus();
    if (status == 200) {
      var partner = odgovor.readEntity(Partner.class);
      this.model.put("status", status);
      this.model.put("partner", partner);
    } else {
      this.model.put("status", status);
    }
  }


  /**
   * Nadzorna konzola tvrtka putanja.
   */
  @GET
  @Path("admin/nadzornaKonzolaTvrtka")
  @View("nadzornaKonzolaTvrtka.jsp")
  public void nadzornaKonzolaTvrtka() {}

  /**
   * Dohvati statuse funkcija koja dohvaća statuse.
   */
  private void dohvatiStatuse() {
    this.model.put("samoOperacija", false);
    var statusT = this.servisTvrtka.headPosluzitelj().getStatus();
    this.model.put("statusT", statusT);
    var statusT1 = this.servisTvrtka.headPosluziteljStatus(1).getStatus();
    this.model.put("statusT1", statusT1);
    var statusT2 = this.servisTvrtka.headPosluziteljStatus(2).getStatus();
    this.model.put("statusT2", statusT2);
  }

  /**
   * Obracun putanja.
   *
   * @param od the od
   * @param ddo the ddo
   * @param tip the tip
   */
  @GET
  @Path("privatno/obracun")
  @View("obracun.jsp")
  public void obracun(@jakarta.ws.rs.QueryParam("od") String od,
      @jakarta.ws.rs.QueryParam("do") String ddo, @jakarta.ws.rs.QueryParam("tip") String tip) {
    List<edu.unizg.foi.nwtis.podaci.Obracun> lista = new ArrayList<>();

    if (od != null && ddo != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
      long epochOd = LocalDate.parse(od, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant()
          .toEpochMilli();

      long epochDo = LocalDate.parse(ddo, formatter).atStartOfDay(ZoneId.systemDefault())
          .toInstant().toEpochMilli();

      switch (tip) {
        case "jelo":
          lista =
              servisTvrtka.dohvatiObracuneJelo(String.valueOf(epochOd), String.valueOf(epochDo));
          break;
        case "pice":
          lista =
              servisTvrtka.dohvatiObracunePice(String.valueOf(epochOd), String.valueOf(epochDo));
          break;
        default:
          lista = servisTvrtka.dohvatiObracune(String.valueOf(epochOd), String.valueOf(epochDo));
          break;
      }
    }
    model.put("obracuni", lista);
  }

  /**
   * Obracun partner putanja.
   *
   * @param id the id
   * @param od the od
   * @param ddo the ddo
   */
  @GET
  @Path("privatno/obracunPartner")
  @View("obracunPartner.jsp")
  public void obracunPartner(@jakarta.ws.rs.QueryParam("id") int id,
      @jakarta.ws.rs.QueryParam("od") String od, @jakarta.ws.rs.QueryParam("do") String ddo) {
    List<Obracun> lista = new ArrayList<>();
    try {
      if (od != null && ddo != null && id > 0) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        long epochOd = LocalDateTime.parse(od, formatter).atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli();
        long epochDo = LocalDateTime.parse(ddo, formatter).atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli();

        var response = servisTvrtka.dohvatiObracunePartner(id, epochOd, epochDo);
        if (response.getStatus() == 200) {
          lista = response.readEntity(new GenericType<List<Obracun>>() {});
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    model.put("obracuni", lista);
  }

  /**
   * Prikazi admin panel putanja.
   */
  @GET
  @Path("admin/panel")
  @View("adminPanel.jsp")
  public void prikaziAdminPanel() {
    dohvatiStatuse();
  }

  /**
   * Prikazi formu partnera putanja.
   */
  @GET
  @Path("admin/dodajPartnera")
  @View("dodajPartnera.jsp")
  public void prikaziFormuPartnera() {
    // samo prikaz forme
  }

  /**
   * Dodaj partnera putanja.
   *
   * @param id the id
   * @param naziv the naziv
   * @param vrstaKuhinje the vrsta kuhinje
   * @param adresa the adresa
   * @param mreznaVrata the mrezna vrata
   * @param mreznaVrataKraj the mrezna vrata kraj
   * @param gpsSirina the gps sirina
   * @param gpsDuzina the gps duzina
   * @param sigurnosniKod the sigurnosni kod
   * @param adminKod the admin kod
   */
  @POST
  @Path("admin/dodajPartnera")
  @View("dodajPartnera.jsp")
  public void dodajPartnera(@FormParam("id") int id, @FormParam("naziv") String naziv,
      @FormParam("vrstakuhinje") String vrstaKuhinje, @FormParam("adresa") String adresa,
      @FormParam("mreznaVrata") int mreznaVrata, @FormParam("mreznaVrataKraj") int mreznaVrataKraj,
      @FormParam("gpssirina") float gpsSirina, @FormParam("gpsduzina") float gpsDuzina,
      @FormParam("sigurnosnikod") String sigurnosniKod, @FormParam("adminkod") String adminKod) {
    var partner = new Partner(id, naziv, vrstaKuhinje, adresa, mreznaVrata, mreznaVrataKraj,
        gpsSirina, gpsDuzina, sigurnosniKod, adminKod);
    var odgovor = servisTvrtka.dodajPartnera(partner);
    model.put("statusDodavanja", odgovor.getStatus());
  }

  /**
   * Aktiviraj spavanje putanja.
   *
   * @param vrijeme the vrijeme
   */
  @POST
  @Path("admin/aktivirajSpavanje")
  @View("adminPanel.jsp")
  public void aktivirajSpavanje(@FormParam("vrijeme") int vrijeme) {
    var odgovor = servisTvrtka.aktivirajSpavanje(vrijeme);
    model.put("statusSpavanje", odgovor.getStatus());
  }

  /**
   * Posalji status poruku putanja.
   *
   * @param status the status
   */
  private void posaljiStatusPoruku(String status) {
    String poruka =
        status + ";" + globalniPodaci.getBrojObracuna() + ";" + globalniPodaci.getInternaPoruka();
    WebSocketTvrtka.send(poruka);
  }

}
