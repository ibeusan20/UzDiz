package datoteke;

import java.util.List;

public interface UcitavacPodataka<T> {

    List<T> ucitaj(String nazivDatoteke);
}