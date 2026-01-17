package edu.unizg.foi.uzdiz.ibeusan20.spremiste;

import java.time.LocalDateTime;
import java.util.List;

public record AranzmanMemento(
    String oznakaAranzmana,
    String stanjeAranzmana,
    LocalDateTime vrijemeSpremanja,
    List<RezervacijaSnapshot> rezervacije
) {}
