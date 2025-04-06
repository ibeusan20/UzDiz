package edu.unizg.foi.nwtis.ibeusan20.vjezba_04_dz_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Jelovnik;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.KartaPica;
import edu.unizg.foi.nwtis.vjezba_04_dz_1.podaci.Partner;

public class PosluziteljTvrtka {

	/** Konfiguracijski podaci */
	private Konfiguracija konfig;

	/** Pokretač dretvi */
	private ExecutorService executor = null;

	/** Pauza dretve. */
	private int pauzaDretve = 1000;

	/** Kod za kraj rada */
	private String kodZaKraj = "";

	/** Zastavica za kraj rada */
	private AtomicBoolean kraj = new AtomicBoolean(false);
	
	/** Thread-safe kolekcije */
    private Map<Integer, String> kuhinje = new ConcurrentHashMap<>();
    private Map<String, Map<String, Jelovnik>> jelovnici = new ConcurrentHashMap<>();
    private Map<String, KartaPica> kartaPica = new ConcurrentHashMap<>();
    private Map<Integer, Partner> partneri = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Broj argumenata nije 1.");
			return;
		}

		var program = new PosluziteljTvrtka();
		var nazivDatoteke = args[0];

		program.pripremiKreni(nazivDatoteke);
	}

	public void pripremiKreni(String nazivDatoteke) {
		if (!this.ucitajKonfiguraciju(nazivDatoteke)) {
			return;
		}
		this.kodZaKraj = this.konfig.dajPostavku("kodZaKraj");
		this.pauzaDretve = Integer.parseInt(this.konfig.dajPostavku("pauzaDretve"));

		this.ucitajJelovnike();
		this.ucitajKartuPica();
		this.ucitajPartnere();
		
		//privremeno
		System.out.println("Učitane kuhinje: " + kuhinje);
		System.out.println("Učitani jelovnici: " + jelovnici.keySet());
		for (var oznaka : jelovnici.keySet()) {
		    System.out.println("Jelovnik za " + oznaka + ": " + jelovnici.get(oznaka).keySet());
		}
		System.out.println("Učitana karta pića: " + kartaPica.keySet());
		for (var id : kartaPica.keySet()) {
		    var p = kartaPica.get(id);
		    System.out.println(id + ": " + p.naziv() + ", " + p.kolicina() + "L, " + p.cijena() + "€");
		}
		System.out.println("Učitani partneri: " + partneri.keySet());
		for (var id : partneri.keySet()) {
		    var p = partneri.get(id);
		    System.out.println(id + ": " + p.naziv() + ", " + p.vrstaKuhinje() + ", " + p.adresa());
		}
		//privremeno
		
		var builder = Thread.ofVirtual();
		var factory = builder.factory();
		this.executor = Executors.newThreadPerTaskExecutor(factory);

		var dretvaZaKraj = this.executor.submit(() -> this.pokreniPosluziteljKraj());

		while (!dretvaZaKraj.isDone()) {
			try {
				Thread.sleep(this.pauzaDretve);
			} catch (InterruptedException e) {
			}
		}
	}

	public void pokreniPosluziteljKraj() {
		var mreznaVrata = Integer.parseInt(this.konfig.dajPostavku("mreznaVrataKraj"));
		var brojCekaca = 0;
		try (ServerSocket ss = new ServerSocket(mreznaVrata, brojCekaca)) {
			while (!this.kraj.get()) {
				var mreznaUticnica = ss.accept();
				this.obradiKraj(mreznaUticnica);
			}
			ss.close();

		} catch (IOException e) {
		}
	}

	public Boolean obradiKraj(Socket mreznaUticnica) {
	    try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
	        PrintWriter out = new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
	        String linija = in.readLine();
	        mreznaUticnica.shutdownInput();

	        String regex = "^KRAJ\\s+" + Pattern.quote(this.kodZaKraj) + "$";
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(linija.trim());

	        if (matcher.matches()) {
	            out.write("OK\n");
	            this.kraj.set(true);
	        } else {
	            out.write("ERROR 10\n");
	        }

	        out.flush();
	        mreznaUticnica.shutdownOutput();
	        mreznaUticnica.close();
	    } catch (Exception e) {
	    }
	    return Boolean.TRUE;
	}

	/**
	 * Ucitaj konfiguraciju.
	 *
	 * @param nazivDatoteke naziv datoteke
	 * @return true, ako je uspješno učitavanje konfiguracije
	 */
	public boolean ucitajKonfiguraciju(String nazivDatoteke) {
		try {
			this.konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
			return true;
		} catch (NeispravnaKonfiguracija ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
	
	public void ucitajJelovnike() {
	    var gson = new com.google.gson.Gson();
	    for (Object kljucObj : this.konfig.dajSvePostavke().keySet()) {
	        String kljuc = (String) kljucObj;
	        if (kljuc.startsWith("kuhinja_")) {
	            var vrijednost = this.konfig.dajPostavku(kljuc);
	            var dijelovi = vrijednost.split(";");
	            if (dijelovi.length != 2) continue;

	            int broj = Integer.parseInt(kljuc.split("_")[1]);
	            String oznaka = dijelovi[0];
	            this.kuhinje.put(broj, oznaka);

	            var datoteka = kljuc + ".json";
	            try (var r = new java.io.FileReader(datoteka)) {
	                var lista = java.util.Arrays.asList(gson.fromJson(r, Jelovnik[].class));
	                var mapa = new java.util.concurrent.ConcurrentHashMap<String, Jelovnik>();
	                for (var j : lista) mapa.put(j.id(), j);
	                this.jelovnici.put(oznaka, mapa);
	            } catch (Exception e) {
	                System.err.println("Greška kod učitavanja: " + datoteka);
	            }
	        }
	    }
	}
	
	public void ucitajKartuPica() {
	    var gson = new com.google.gson.Gson();
	    var datoteka = this.konfig.dajPostavku("datotekaKartaPica");
	    try (var r = new java.io.FileReader(datoteka)) {
	        var lista = java.util.Arrays.asList(gson.fromJson(r, KartaPica[].class));
	        for (var pice : lista) {
	            this.kartaPica.put(pice.id(), pice);
	        }
	    } catch (Exception e) {
	        System.err.println("Greška kod učitavanja karte pića: " + e.getMessage());
	    }
	}
	
	public void ucitajPartnere() {
	    var gson = new com.google.gson.Gson();
	    var datoteka = this.konfig.dajPostavku("datotekaPartnera");
	    var f = new java.io.File(datoteka);
	    if (!f.exists()) {
	        System.out.println("Datoteka partnera ne postoji: " + datoteka);
	        return;
	    }
	    try (var r = new java.io.FileReader(f)) {
	        var lista = java.util.Arrays.asList(gson.fromJson(r, Partner[].class));
	        for (var p : lista) {
	            this.partneri.put(p.id(), p);
	        }
	    } catch (Exception e) {
	        System.err.println("Greška kod učitavanja partnera: " + e.getMessage());
	    }
	}



}
