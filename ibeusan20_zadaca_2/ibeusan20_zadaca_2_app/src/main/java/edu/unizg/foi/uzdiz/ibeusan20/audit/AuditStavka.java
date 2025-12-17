package edu.unizg.foi.uzdiz.ibeusan20.audit;

import java.time.LocalDateTime;

/**
 * The Record AuditStavka.
 *
 * @param rbr the rbr
 * @param vrijeme the vrijeme
 * @param unos the unos
 * @param komandaNaziv the komanda naziv
 * @param status the status
 * @param trajanjeMs the trajanje ms
 * @param poruka the poruka
 */
public record AuditStavka(
    int rbr,
    LocalDateTime vrijeme,
    String unos,
    String komandaNaziv,
    String status,
    long trajanjeMs,
    String poruka
) {}