package datoteke;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Jednostavan CSV parser koji podržava:
 * - UTF-8
 * - odvajanje polja zarezom ,
 * - polja unutar "..." u kojima mogu biti zarezi i novi redovi
 * - prazna polja (,,)
 * - retke koji počinju s # i prazne retke – preskače ih se
 *
 * Koristi se tako da se prva linija pročita izvana,
 * a onda se pozove procitajZapis(...) koji po potrebi
 * čita i dodatne linije dok se ne zatvore navodnici.
 */
public final class CsvParser {

    private CsvParser() {
    }

    /**
     * Otvara datoteku u UTF-8 i vraća BufferedReader.
     */
    public static BufferedReader otvoriUtf8(String putanja) throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(putanja),
                        StandardCharsets.UTF_8));
    }

    /**
     * Prima prvi redak jednog zapisa i reader.
     * Ako redak ima nedozatvoren broj navodnika,
     * čita dodatne retke i spaja ih dok se navodnici ne zatvore.
     *
     * Na kraju vraća listu polja.
     */
    public static List<String> procitajZapis(String prviRedak,
                                             BufferedReader br)
            throws IOException {
        if (prviRedak == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(prviRedak);
        int brojNavodnika = brojNavodnika(sb);

        // ako je broj navodnika neparan -> polje se nastavlja u sljedećem retku
        while (brojNavodnika % 2 != 0) {
            String nastavak = br.readLine();
            if (nastavak == null) {
                break;
            }
            sb.append("\n").append(nastavak);
            brojNavodnika = brojNavodnika(sb);
        }

        return razdvojiJedanZapis(sb.toString());
    }

    /**
     * Razdvaja jedan CSV zapis u polja.
     * Podržava "escaped" navodnike unutar navodnika ("").
     */
    public static List<String> razdvojiJedanZapis(String zapis) {
        List<String> polja = new ArrayList<>();
        StringBuilder polje = new StringBuilder();
        boolean uNavodnicima = false;

        for (int i = 0; i < zapis.length(); i++) {
            char c = zapis.charAt(i);

            if (c == '"') {
                // ako smo u navodnicima i slijedi još jedan navodnik -> to je znak "
                if (uNavodnicima
                        && i + 1 < zapis.length()
                        && zapis.charAt(i + 1) == '"') {
                    polje.append('"');
                    i++; // preskoči drugi navodnik
                } else {
                    uNavodnicima = !uNavodnicima;
                }
            } else if (c == ',' && !uNavodnicima) {
                // kraj polja
                polja.add(ocisti(polje.toString()));
                polje.setLength(0);
            } else {
                polje.append(c);
            }
        }

        // zadnje polje
        polja.add(ocisti(polje.toString()));

        return polja;
    }

    private static int brojNavodnika(CharSequence tekst) {
        int br = 0;
        for (int i = 0; i < tekst.length(); i++) {
            if (tekst.charAt(i) == '"') {
                br++;
            }
        }
        return br;
    }

    /**
     * skida vanjske razmake i eventualne vanjske navodnike
     */
    private static String ocisti(String polje) {
        if (polje == null) {
            return "";
        }
        String r = polje.trim();
        if (r.startsWith("\"") && r.endsWith("\"") && r.length() >= 2) {
            r = r.substring(1, r.length() - 1);
        }
        return r;
    }
}
