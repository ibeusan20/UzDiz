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
 * Klasa WebSocketTvrtka za poruke tvrtke.
 */
@ServerEndpoint("/ws/tvrtka")
@ApplicationScoped
public class WebSocketTvrtka {

  /** The queue. */
  static Queue<Session> queue = new ConcurrentLinkedQueue<>();

  /** The globalni podaci. */
  @Inject
  private GlobalniPodaci globalniPodaci;

  /**
   * Send.
   *
   * @param poruka the poruka
   */
  public static void send(String poruka) {
    try {
      for (Session session : queue) {
        if (session.isOpen()) {
          System.out.println("Šaljem poruku: " + poruka);
          session.getBasicRemote().sendText(poruka);
        }
      }
    } catch (IOException ex) {
      System.out.println("Greška pri slanju poruke: " + ex.getMessage());
    }
  }
  
  /**
   * Posalji status.
   *
   * @param status the status
   * @param globalniPodaci the globalni podaci
   */
  public static void posaljiStatus(String status, GlobalniPodaci globalniPodaci) {
    String poruka = status + ";" +
        globalniPodaci.getBrojObracuna() + ";" +
        globalniPodaci.getInternaPoruka();
    send(poruka);
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
    System.out.println("Otvorena WebSocket veza.");
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
    System.out.println("Zatvorena WebSocket veza.");
  }
  
  /**
   * On message.
   *
   * @param session the session
   * @param porukica the porukica
   */
  @OnMessage
  public void onMessage(Session session, String porukica) {
    System.out.println("Primljena poruka: " + porukica);

    String[] dijelovi = porukica.split(";", 3);
    String tip = dijelovi.length > 0 ? dijelovi[0].trim() : "";
    String broj = dijelovi.length > 1 ? dijelovi[1].trim() : "";
    String interna = dijelovi.length > 2 ? dijelovi[2].trim() : "";

    if ("INTERNA".equalsIgnoreCase(tip) && !interna.trim().isEmpty()) {
      globalniPodaci.setInternaPoruka(interna);
      System.out.println("Interna poruka postavljena: " + interna);
      System.out.println("tip: [" + tip + "]");
      System.out.println("broj: [" + broj + "]");
      System.out.println("interna: [" + interna + "]");
      System.out.println("interna.isBlank(): " + interna.isBlank());
    }

    String novaPoruka =
        tip + ";" + globalniPodaci.getBrojObracuna() + ";" + globalniPodaci.getInternaPoruka();
    send(novaPoruka);
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
    System.out.println("Pogreška u WebSocket vezi: " + t.getMessage());
  }
}
