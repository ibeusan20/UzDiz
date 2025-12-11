package edu.unizg.foi.uzdiz.ibeusan20.datoteke.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AranzmanCsv {
    public String oznaka;
    public String naziv;
    public String program;
    public LocalDate pocetniDatum;
    public LocalDate zavrsniDatum;
    public LocalTime vrijemeKretanja;
    public LocalTime vrijemePovratka;
    public float cijena;
    public int minPutnika;
    public int maxPutnika;
    public int brojNocenja;
    public float doplataJednokrevetna;
    public List<String> prijevoz;
    public int brojDorucaka;
    public int brojRuckova;
    public int brojVecera;
}
