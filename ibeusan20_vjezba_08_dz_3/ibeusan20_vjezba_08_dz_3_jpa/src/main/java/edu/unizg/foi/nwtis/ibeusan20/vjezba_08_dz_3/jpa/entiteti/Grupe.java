package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;

/**
 * Perzistencijska klasa za tablicu baze podataka GRUPE.
 * 
 */
@Entity
@NamedQuery(name="Grupe.findAll", query="SELECT g FROM Grupe g")
public class Grupe implements Serializable {
	
	/** konstanta serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** element grupa. */
	@Id
	private String grupa;

	/** element naziv. */
	private String naziv;

	/** The korisnicis. */
	//bi-directional many-to-many association to Korisnici
	@ManyToMany(mappedBy="grupes")
	private List<Korisnici> korisnicis;

	/**
	 * Instanciranje nove grupe.
	 */
	public Grupe() {
	}

	/**
	 * Dohvaćanje grupe.
	 *
	 * @return  grupa
	 */
	public String getGrupa() {
		return this.grupa;
	}

	/**
	 * Postavljanje grupe.
	 *
	 * @param grupa new grupa
	 */
	public void setGrupa(String grupa) {
		this.grupa = grupa;
	}

	/**
	 * Dohvaćanje naziva.
	 *
	 * @return vraća naziv
	 */
	public String getNaziv() {
		return this.naziv;
	}

	/**
	 * POstavljanje naziva.
	 *
	 * @param naziv new naziv
	 */
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	/**
	 * Dohvaćanje korisnika.
	 *
	 * @return vraća korisnicis
	 */
	public List<Korisnici> getKorisnicis() {
		return this.korisnicis;
	}

	/**
	 * Postavljanje korisnika.
	 *
	 * @param korisnicis new korisnicis
	 */
	public void setKorisnicis(List<Korisnici> korisnicis) {
		this.korisnicis = korisnicis;
	}

}