package edu.unizg.foi.uzdiz.ibeusan20.datoteke.api;

import java.time.LocalDateTime;

public interface RezervacijaPodaci {
  String getIme();
  String getPrezime();
  String getOznakaAranzmana();
  LocalDateTime getDatumVrijeme();
}
