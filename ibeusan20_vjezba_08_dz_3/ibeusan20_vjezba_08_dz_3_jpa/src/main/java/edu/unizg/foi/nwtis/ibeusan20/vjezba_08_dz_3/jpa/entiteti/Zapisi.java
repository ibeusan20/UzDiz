package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the ZAPISI database table.
 * 
 */
@Entity
@NamedQuery(name="Zapisi.findAll", query="SELECT z FROM Zapisi z")
public class Zapisi implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	@SequenceGenerator(name="ZAPISI_ID_GENERATOR", sequenceName="ZAPISI_ID",initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ZAPISI_ID_GENERATOR")
	private int id;

	/** The adresaracunala. */
	private String adresaracunala;

	/** The ipadresaracunala. */
	private String ipadresaracunala;

	/** The korisnickoime. */
	private String korisnickoime;

	/** The opisrada. */
	private String opisrada;

	/** The vrijeme. */
	private Timestamp vrijeme;

	/**
	 * Instantiates a new zapisi.
	 */
	public Zapisi() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the adresaracunala.
	 *
	 * @return the adresaracunala
	 */
	public String getAdresaracunala() {
		return this.adresaracunala;
	}

	/**
	 * Sets the adresaracunala.
	 *
	 * @param adresaracunala the new adresaracunala
	 */
	public void setAdresaracunala(String adresaracunala) {
		this.adresaracunala = adresaracunala;
	}

	/**
	 * Gets the ipadresaracunala.
	 *
	 * @return the ipadresaracunala
	 */
	public String getIpadresaracunala() {
		return this.ipadresaracunala;
	}

	/**
	 * Sets the ipadresaracunala.
	 *
	 * @param ipadresaracunala the new ipadresaracunala
	 */
	public void setIpadresaracunala(String ipadresaracunala) {
		this.ipadresaracunala = ipadresaracunala;
	}

	/**
	 * Gets the korisnickoime.
	 *
	 * @return the korisnickoime
	 */
	public String getKorisnickoime() {
		return this.korisnickoime;
	}

	/**
	 * Sets the korisnickoime.
	 *
	 * @param korisnickoime the new korisnickoime
	 */
	public void setKorisnickoime(String korisnickoime) {
		this.korisnickoime = korisnickoime;
	}

	/**
	 * Gets the opisrada.
	 *
	 * @return the opisrada
	 */
	public String getOpisrada() {
		return this.opisrada;
	}

	/**
	 * Sets the opisrada.
	 *
	 * @param opisrada the new opisrada
	 */
	public void setOpisrada(String opisrada) {
		this.opisrada = opisrada;
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

}