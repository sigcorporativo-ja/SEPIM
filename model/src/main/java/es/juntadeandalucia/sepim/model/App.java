/**
 * Empresa desarrolladora: SAIG S.L.
 * 
 * Autor: Junta de Andalucía
 * 
 * Derechos de explotación propiedad de la Junta de Andalucía.
 * 
 * Este programa es software libre: usted tiene derecho a redistribuirlo y/o modificarlo bajo los términos de la
 * 
 * Licencia EUPL European Public License publicada por el organismo IDABC de la Comisión Europea, en su versión 1.0.
 * o posteriores.
 * 
 * Este programa se distribuye de buena fe, pero SIN NINGUNA GARANTÍA, incluso sin las presuntas garantías implícitas
 * de USABILIDAD o ADECUACIÓN A PROPÓSITO CONCRETO. Para mas información consulte la Licencia EUPL European Public
 * License.
 * 
 * Usted recibe una copia de la Licencia EUPL European Public License junto con este programa, si por algún motivo no
 * le es posible visualizarla, puede consultarla en la siguiente URL: http://ec.europa.eu/idabc/servlets/Doc?id=31099
 * 
 * You should have received a copy of the EUPL European Public License along with this program. If not, see
 * http://ec.europa.eu/idabc/servlets/Doc?id=31096
 * 
 * Vous devez avoir reçu une copie de la EUPL European Public License avec ce programme. Si non, voir
 * http://ec.europa.eu/idabc/servlets/Doc?id=30194
 * 
 * Sie sollten eine Kopie der EUPL European Public License zusammen mit diesem Programm. Wenn nicht, finden Sie da
 * http://ec.europa.eu/idabc/servlets/Doc?id=29919
 */
package es.juntadeandalucia.sepim.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * Representa una configuracion de la aplicacion movil
 * 
 * @author Marco Antonio Fuentelsaz Perez
 * @since 1.0.0
 *
 */
@Entity
@Table(schema = "public", name = "apps")
public class App {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "apps_seq_gen")
	@SequenceGenerator(name = "apps_seq_gen", sequenceName = "public.apps_id_seq")
	protected Integer id;

	@Column(name = "name", nullable = false, length = 256)
	protected String name;

	@Column(name = "mwc_url", nullable = true, length = 2048)
	protected String wmcURL;

	@Column(name = "description", nullable = true)
	@Type(type = "text")
	protected String description;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "app", targetEntity = Category.class, orphanRemoval = true, fetch = FetchType.LAZY)
	// @LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("priority")
	protected List<Category> categories = new ArrayList<Category>();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", updatable = true, insertable = true)
	@org.hibernate.annotations.ForeignKey(name = "users_apps_fk")
	protected User user;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_id", updatable = true, insertable = true)
	@org.hibernate.annotations.ForeignKey(name = "apps_entities_fk")
	protected es.juntadeandalucia.sepim.model.Entity entity;

	@Column(name = "entity_id", updatable = false, insertable = false)
	protected Integer entityID;

	@Transient
	protected Integer idProvincia;

	@Transient
	protected Integer idMunicipio;

	@Transient
	protected boolean enableGeographicFilter;

	public es.juntadeandalucia.sepim.model.Entity getEntity() {
		return entity;
	}

	public void setEntity(es.juntadeandalucia.sepim.model.Entity entity) {
		this.entity = entity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Category> getCategories() {
		if (categories == null) {
			categories = new ArrayList<Category>();
		}
		return this.categories;
	}

	public void addCategory(Category category) {
		if (category == null)
			throw new IllegalArgumentException("Category is null");

		if (category.getApp() != null) {
			category.getApp().removeCategory(category);
		}
		category.setApp(this);
		this.categories.add(category);

	}

	public void removeCategory(Category category) {
		if (category == null)
			throw new IllegalArgumentException("Category is null");
		for (Category subcategory : category.subcategories) {
			subcategory.removeAllSubcategories();
		}
		this.categories.remove(category);
		category.setApp(null);
	}

	public void removeAllCategories() {
		for (Category category : categories) {
			category.removeAllSubcategories();
		}
		this.categories.clear();
	}

	public Integer getIdProvincia() {
		return idProvincia;
	}

	public void setIdProvincia(Integer idProvincia) {
		this.idProvincia = idProvincia;
	}

	public Integer getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public boolean isEnableGeographicFilter() {
		return enableGeographicFilter;
	}

	public void setEnableGeographicFilter(boolean enableGeographicFilter) {
		this.enableGeographicFilter = enableGeographicFilter;
	}

	public String getWmcURL() {
		return wmcURL;
	}

	public void setWmcURL(String wmcURL) {
		this.wmcURL = wmcURL;
	}

	public Integer getEntityID() {
		return entityID;
	}

	public void setEntityID(Integer entityID) {
		this.entityID = entityID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		App other = (App) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
