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



@ServerEndpoint("/ws/tvrtka")
@ApplicationScoped
public class WebSocketTvrtka {

  static Queue<Session> queue = new ConcurrentLinkedQueue<>();

  @Inject
  private GlobalniPodaci globalniPodaci;

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
  
  public static void posaljiStatus(String status, GlobalniPodaci globalniPodaci) {
    String poruka = status + ";" +
        globalniPodaci.getBrojObracuna() + ";" +
        globalniPodaci.getInternaPoruka();
    send(poruka);
  }

  @OnOpen
  public void openConnection(Session session, EndpointConfig conf) {
    queue.add(session);
    System.out.println("Otvorena WebSocket veza.");
  }

  @OnClose
  public void closedConnection(Session session, CloseReason reason) {
    queue.remove(session);
    System.out.println("Zatvorena WebSocket veza.");
  }

  @OnMessage
  public void onMessage(Session session, String poruka) {
    System.out.println("Primljena poruka: " + poruka);

    String[] dijelovi = poruka.split(";", 2);
    String status = dijelovi[0];
    String internaPoruka = (dijelovi.length > 1) ? dijelovi[1] : "";

    if (!internaPoruka.isBlank()) {
      globalniPodaci.setInternaPoruka(internaPoruka);
    }

    String novaPoruka =
        status + ";" + globalniPodaci.getBrojObracuna() + ";" + globalniPodaci.getInternaPoruka();

    WebSocketTvrtka.send(novaPoruka);
  }

  @OnError
  public void error(Session session, Throwable t) {
    queue.remove(session);
    System.out.println("Pogreška u WebSocket vezi: " + t.getMessage());
  }
}
