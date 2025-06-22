package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the OBRACUNI database table.
 * 
 */
@Entity
@NamedQuery(name="Obracuni.findAll", query="SELECT o FROM Obracuni o")
public class Obracuni implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The rb. */
	@Id
	@SequenceGenerator(name="OBRACUNI_RB_GENERATOR", sequenceName="OBRACUNI_ID",initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OBRACUNI_RB_GENERATOR")
	private int rb;

	/** The cijena. */
	private double cijena;

	/** The id. */
	private String id;

	/** The jelo. */
	private boolean jelo;

	/** The kolicina. */
	private double kolicina;

	/** The vrijeme. */
	private Timestamp vrijeme;

	/** The partneri. */
	//bi-directional many-to-one association to Partneri
	@ManyToOne
	@JoinColumn(name="PARTNER")
	private Partneri partneri;

	/**
	 * Instantiates a new obracuni.
	 */
	public Obracuni() {
	}

	/**
	 * Gets the rb.
	 *
	 * @return the rb
	 */
	public int getRb() {
		return this.rb;
	}

	/**
	 * Sets the rb.
	 *
	 * @param rb the new rb
	 */
	public void setRb(int rb) {
		this.rb = rb;
	}

	/**
	 * Gets the cijena.
	 *
	 * @return the cijena
	 */
	public double getCijena() {
		return this.cijena;
	}

	/**
	 * Sets the cijena.
	 *
	 * @param cijena the new cijena
	 */
	public void setCijena(double cijena) {
		this.cijena = cijena;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the jelo.
	 *
	 * @return the jelo
	 */
	public boolean getJelo() {
		return this.jelo;
	}

	/**
	 * Sets the jelo.
	 *
	 * @param jelo the new jelo
	 */
	public void setJelo(boolean jelo) {
		this.jelo = jelo;
	}

	/**
	 * Gets the kolicina.
	 *
	 * @return the kolicina
	 */
	public double getKolicina() {
		return this.kolicina;
	}

	/**
	 * Sets the kolicina.
	 *
	 * @param kolicina the new kolicina
	 */
	public void setKolicina(double kolicina) {
		this.kolicina = kolicina;
	}

	/**
	 * Gets the vrijeme.
	 *
	 * @return the vrijeme
	 */
	public Timestamp getVrijeme() {
		return this.vrijeme;
	}

	/**
	 * Sets the vrijeme.
	 *
	 * @param vrijeme the new vrijeme
	 */
	public void setVrijeme(Timestamp vrijeme) {
		this.vrijeme = vrijeme;
	}

	/**
	 * Gets the partneri.
	 *
	 * @return the partneri
	 */
	public Partneri getPartneri() {
		return this.partneri;
	}

	/**
	 * Sets the partneri.
	 *
	 * @param partneri the new partneri
	 */
	public void setPartneri(Partneri partneri) {
		this.partneri = partneri;
	}

}