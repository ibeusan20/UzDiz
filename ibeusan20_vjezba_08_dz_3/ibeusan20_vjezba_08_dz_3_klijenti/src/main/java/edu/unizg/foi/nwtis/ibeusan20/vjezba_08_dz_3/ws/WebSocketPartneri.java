package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ws;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.GlobalniPodaci;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Klasa WebSocketPartneri za poruke partnera.
 */
@ServerEndpoint("/ws/partneri")
@ApplicationScoped
public class WebSocketPartneri {

  /** The queue. */
  static Queue<Session> queue = new ConcurrentLinkedQueue<>();

  /** The globalni podaci. */
  @Inject
  private GlobalniPodaci globalniPodaci;

  /**
   * Metoda koja se poziva kad se promijeni stanje za partnera.
   * Slanje poruke: "ID_PARTNERA;brojOtvorenihNarudzbi;brojRacuna"
   *
   * @param idPartnera the id partnera
   * @param globalniPodaci the globalni podaci
   */
  public static void posaljiPoruku(int idPartnera, GlobalniPodaci globalniPodaci) {
    try {
      int otvorene = globalniPodaci.getBrojOtvorenihNarudzbi().getOrDefault(idPartnera, 0);
      int racuni = globalniPodaci.getBrojRacuna().getOrDefault(idPartnera, 0);
      boolean pauza = globalniPodaci.jeUPauzi(idPartnera);
      String status = pauza ? "NE RADI" : "RADI";
      String poruka = status + ";" + otvorene + ";" + racuni;

      for (Session session : queue) {
        if (session.isOpen()) {
          System.out.println("Šaljem partner poruku: " + poruka);
          session.getBasicRemote().sendText(poruka);
        }
      }
    } catch (IOException ex) {
      System.out.println("Greška pri slanju WS partner poruke: " + ex.getMessage());
    }
  }

  /**
   * Open connection.
   *
   * @param session the session
   * @param conf the conf
   */
  @OnOpen
  public void openConnection(Session session, EndpointConfig conf) {
    queue.add(session);
    int idPartnera = 1;
    posaljiPoruku(idPartnera, globalniPodaci);
    System.out.println("Otvorena WebSocket veza /ws/partneri.");
  }

  /**
   * Closed connection.
   *
   * @param session the session
   * @param reason the reason
   */
  @OnClose
  public void closedConnection(Session session, CloseReason reason) {
    queue.remove(session);
    System.out.println("Zatvorena WebSocket veza /ws/partneri.");
  }

  /**
   * On message.
   *
   * @param session the session
   * @param poruka the poruka
   */
  @OnMessage
  public void onMessage(Session session, String poruka) {
    System.out.println("Primljena poruka od klijenta (nije obavezna): " + poruka);
  }
  

  /**
   * Error.
   *
   * @param session the session
   * @param t the t
   */
  @OnError
  public void error(Session session, Throwable t) {
    queue.remove(session);
    System.out.println("Greška u WebSocket vezi /ws/partneri: " + t.getMessage());
  }
}
