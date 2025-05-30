package edu.unizg.foi.nwtis.ibeusan20.vjezba_07_dz_2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.nwtis.podaci.Obracun;

/**
 * Klasa ObracunDAO.
 */
public class ObracunDAO {
  
  /** Veza na bazu podataka. */
  private final Connection veza;

  /**
   * Instancira novi obracun DAO.
   *
   * @param veza veza na bazu
   */
  public ObracunDAO(Connection veza) {
    this.veza = veza;
  }

  /**
   * Dodaj sve.
   *
   * @param obracuni obracuni za dodavanje
   * @return true, ako je uspješno
   * @throws SQLException moguća SQL iznimka
   */
  public boolean dodajSve(List<Obracun> obracuni) throws SQLException {
    String sql = """
        INSERT INTO OBRACUNI (PARTNER, ID, JELO, KOLICINA, CIJENA, VRIJEME)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (PreparedStatement stmt = veza.prepareStatement(sql)) {
      for (Obracun o : obracuni) {
        stmt.setInt(1, o.partner()+1);
        stmt.setString(2, o.id());
        stmt.setBoolean(3, o.jelo());
        stmt.setFloat(4, o.kolicina());
        stmt.setFloat(5, o.cijena());
        stmt.setTimestamp(6, new Timestamp(o.vrijeme()));
        System.out.println("[DEBUG] INSERT: partner=" + o.partner() + ", id=" + o.id());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
    return true;
  }
  
  /**
   * Dodaj obračun.
   *
   * @param o obračun
   * @return true, ako je uspješno
   */
  public boolean dodaj(Obracun o) {
    String upit = """
        INSERT INTO OBRACUNI (PARTNER, ID, JELO, KOLICINA, CIJENA, VRIJEME)
        VALUES (?, ?, ?, ?, ?, ?)
        """;
    try (var stmt = this.veza.prepareStatement(upit)) {
      stmt.setInt(1, o.partner());
      stmt.setString(2, o.id());
      stmt.setBoolean(3, o.jelo());
      stmt.setFloat(4, o.kolicina());
      stmt.setFloat(5, o.cijena());
      stmt.setTimestamp(6, new Timestamp(o.vrijeme()));
      return stmt.executeUpdate() == 1;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  
  /**
   * Dohvati sve obračune.
   *
   * @return lista obračuna
   */
  public List<Obracun> dohvatiSve() {
    List<Obracun> obracuni = new ArrayList<>();
    String sql = "SELECT partner, id, jelo, kolicina, cijena, vrijeme FROM OBRACUNI";

    try (PreparedStatement stmt = veza.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        int partner = rs.getInt("partner");
        String id = rs.getString("id");
        boolean jelo = rs.getBoolean("jelo");
        float kolicina = rs.getFloat("kolicina");
        float cijena = rs.getFloat("cijena");
        long vrijeme = rs.getTimestamp("vrijeme").getTime();

        obracuni.add(new Obracun(partner, id, jelo, kolicina, cijena, vrijeme));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return obracuni;
  }

}


