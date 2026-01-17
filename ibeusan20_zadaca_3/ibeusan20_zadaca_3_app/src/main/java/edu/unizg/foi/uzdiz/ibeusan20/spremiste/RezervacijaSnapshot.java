package edu.unizg.foi.uzdiz.ibeusan20.spremiste;

import java.time.LocalDateTime;

public record RezervacijaSnapshot(
    String ime,
    String prezime,
    String oznakaAranzmana,
    LocalDateTime datumVrijeme,
    String stanjeNaziv,
    LocalDateTime datumVrijemeOtkaza
) {}
