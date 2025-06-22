package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;

/**
 * Perzistencijska klasa za tablicu KORISNICI.
 * 
 */
@Entity
@NamedQuery(name="Korisnici.findAll", query="SELECT k FROM Korisnici k")
public class Korisnici implements Serializable {
	
	/** Konstanta serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** korisnik */
	@Id
	private String korisnik;

	/** email */
	private String email;

	/** ime */
	private String ime;

	/** lozinka */
	private String lozinka;

	/** prezime */
	private String prezime;

	/** The grupes. */
	//bi-directional many-to-many association to Grupe
	@ManyToMany
	@JoinTable(
		name="ULOGE"
		, joinColumns={
			@JoinColumn(name="KORISNIK")
			}
		, inverseJoinColumns={
			@JoinColumn(name="GRUPA")
			}
		)
	private List<Grupe> grupes;

	/**
	 * instanciranje.
	 */
	public Korisnici() {
	}

	/**
	 * Dohvaćanje koriusnika.
	 *
	 * @return  korisnik
	 */
	public String getKorisnik() {
		return this.korisnik;
	}

	/**
	 * Postavljanje korisnika.
	 *
	 * @param korisnik new korisnik
	 */
	public void setKorisnik(String korisnik) {
		this.korisnik = korisnik;
	}

	/**
	 * Dohvaćanje emaila.
	 *
	 * @return email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Postavljanje emaila.
	 *
	 * @param email new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Dohvaćanje imena.
	 *
	 * @return ime
	 */
	public String getIme() {
		return this.ime;
	}

	/**
	 * Postavljanje imena.
	 *
	 * @param ime new ime
	 */
	public void setIme(String ime) {
		this.ime = ime;
	}

	/**
	 * Dohvaćanje lozinke.
	 *
	 * @return lozinka
	 */
	public String getLozinka() {
		return this.lozinka;
	}

	/**
	 * Postavljanje lozinke.
	 *
	 * @param lozinka new lozinka
	 */
	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	/**
	 * Dohvaćanje prezimena.
	 *
	 * @return prezime
	 */
	public String getPrezime() {
		return this.prezime;
	}

	/**
	 * Postavljanje prezimena.
	 *
	 * @param prezime new prezime
	 */
	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	/**
	 * Dohvaćanje grupa.
	 *
	 * @return grupes
	 */
	public List<Grupe> getGrupes() {
		return this.grupes;
	}

	/**
	 * Postavljanej grupa.
	 *
	 * @param grupes new grupes
	 */
	public void setGrupes(List<Grupe> grupes) {
		this.grupes = grupes;
	}

}