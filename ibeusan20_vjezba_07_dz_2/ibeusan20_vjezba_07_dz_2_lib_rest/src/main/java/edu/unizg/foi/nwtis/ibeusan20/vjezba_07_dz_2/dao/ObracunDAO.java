package edu.unizg.foi.nwtis.ibeusan20.vjezba_07_dz_2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import edu.unizg.foi.nwtis.podaci.Obracun;

public class ObracunDAO {
  private final Connection veza;

  public ObracunDAO(Connection veza) {
    this.veza = veza;
  }

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
        stmt.setTimestamp(6, new java.sql.Timestamp(o.vrijeme()));
        System.out.println("[DEBUG] INSERT: partner=" + o.partner() + ", id=" + o.id());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
    return true;
  }
}
