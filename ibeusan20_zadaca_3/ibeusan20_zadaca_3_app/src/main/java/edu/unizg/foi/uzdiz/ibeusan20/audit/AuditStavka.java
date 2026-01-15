package edu.unizg.foi.uzdiz.ibeusan20.audit;

import java.time.LocalDateTime;

public record AuditStavka(int rbr, LocalDateTime vrijeme, String unos, String komandaNaziv,
    String status, long trajanjeMs, String poruka) {
}
