package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;

/**
 * The persistent class for the PARTNERI database table.
 * 
 */
@Entity
@NamedQuery(name="Partneri.findAll", query="SELECT p FROM Partneri p")
public class Partneri implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	private int id;

	/** The adminkod. */
	private String adminkod;

	/** The adresa. */
	private String adresa;

	/** The gpsduzina. */
	private double gpsduzina;

	/** The gpssirina. */
	private double gpssirina;

	/** The mreznavrata. */
	private int mreznavrata;

	/** The mreznavratakraj. */
	private int mreznavratakraj;

	/** The naziv. */
	private String naziv;

	/** The sigurnosnikod. */
	private String sigurnosnikod;

	/** The vrstakuhinje. */
	private String vrstakuhinje;

	/** The obracunis. */
	//bi-directional many-to-one association to Obracuni
	@OneToMany(mappedBy="partneri")
	private List<Obracuni> obracunis;

	/**
	 * Instantiates a new partneri.
	 */
	public Partneri() {
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
	 * Gets the adminkod.
	 *
	 * @return the adminkod
	 */
	public String getAdminkod() {
		return this.adminkod;
	}

	/**
	 * Sets the adminkod.
	 *
	 * @param adminkod the new adminkod
	 */
	public void setAdminkod(String adminkod) {
		this.adminkod = adminkod;
	}

	/**
	 * Gets the adresa.
	 *
	 * @return the adresa
	 */
	public String getAdresa() {
		return this.adresa;
	}

	/**
	 * Sets the adresa.
	 *
	 * @param adresa the new adresa
	 */
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}

	/**
	 * Gets the gpsduzina.
	 *
	 * @return the gpsduzina
	 */
	public double getGpsduzina() {
		return this.gpsduzina;
	}

	/**
	 * Sets the gpsduzina.
	 *
	 * @param gpsduzina the new gpsduzina
	 */
	public void setGpsduzina(double gpsduzina) {
		this.gpsduzina = gpsduzina;
	}

	/**
	 * Gets the gpssirina.
	 *
	 * @return the gpssirina
	 */
	public double getGpssirina() {
		return this.gpssirina;
	}

	/**
	 * Sets the gpssirina.
	 *
	 * @param gpssirina the new gpssirina
	 */
	public void setGpssirina(double gpssirina) {
		this.gpssirina = gpssirina;
	}

	/**
	 * Gets the mreznavrata.
	 *
	 * @return the mreznavrata
	 */
	public int getMreznavrata() {
		return this.mreznavrata;
	}

	/**
	 * Sets the mreznavrata.
	 *
	 * @param mreznavrata the new mreznavrata
	 */
	public void setMreznavrata(int mreznavrata) {
		this.mreznavrata = mreznavrata;
	}

	/**
	 * Gets the mreznavratakraj.
	 *
	 * @return the mreznavratakraj
	 */
	public int getMreznavratakraj() {
		return this.mreznavratakraj;
	}

	/**
	 * Sets the mreznavratakraj.
	 *
	 * @param mreznavratakraj the new mreznavratakraj
	 */
	public void setMreznavratakraj(int mreznavratakraj) {
		this.mreznavratakraj = mreznavratakraj;
	}

	/**
	 * Gets the naziv.
	 *
	 * @return the naziv
	 */
	public String getNaziv() {
		return this.naziv;
	}

	/**
	 * Sets the naziv.
	 *
	 * @param naziv the new naziv
	 */
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	/**
	 * Gets the sigurnosnikod.
	 *
	 * @return the sigurnosnikod
	 */
	public String getSigurnosnikod() {
		return this.sigurnosnikod;
	}

	/**
	 * Sets the sigurnosnikod.
	 *
	 * @param sigurnosnikod the new sigurnosnikod
	 */
	public void setSigurnosnikod(String sigurnosnikod) {
		this.sigurnosnikod = sigurnosnikod;
	}

	/**
	 * Gets the vrstakuhinje.
	 *
	 * @return the vrstakuhinje
	 */
	public String getVrstakuhinje() {
		return this.vrstakuhinje;
	}

	/**
	 * Sets the vrstakuhinje.
	 *
	 * @param vrstakuhinje the new vrstakuhinje
	 */
	public void setVrstakuhinje(String vrstakuhinje) {
		this.vrstakuhinje = vrstakuhinje;
	}

	/**
	 * Gets the obracunis.
	 *
	 * @return the obracunis
	 */
	public List<Obracuni> getObracunis() {
		return this.obracunis;
	}

	/**
	 * Sets the obracunis.
	 *
	 * @param obracunis the new obracunis
	 */
	public void setObracunis(List<Obracuni> obracunis) {
		this.obracunis = obracunis;
	}

	/**
	 * Adds the obracuni.
	 *
	 * @param obracuni the obracuni
	 * @return the obracuni
	 */
	public Obracuni addObracuni(Obracuni obracuni) {
		getObracunis().add(obracuni);
		obracuni.setPartneri(this);

		return obracuni;
	}

	/**
	 * Removes the obracuni.
	 *
	 * @param obracuni the obracuni
	 * @return the obracuni
	 */
	public Obracuni removeObracuni(Obracuni obracuni) {
		getObracunis().remove(obracuni);
		obracuni.setPartneri(null);

		return obracuni;
	}

}