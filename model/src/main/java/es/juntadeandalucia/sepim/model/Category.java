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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

/**
 * Representa una categoria. Si la categoria no tiene ningun padre asociado se
 * considera una categoria de primer nivel, la cual lleva asociada una app
 * 
 * @author Marco Antonio Fuentelsaz Perez
 * @since 1.0.0
 *
 */
@Entity
@Table(schema = "public", name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "categories_seq_gen")
	@SequenceGenerator(name = "categories_seq_gen", sequenceName = "public.categories_id_seq")
	protected Integer id;

	@Column(name = "name", nullable = false, length = 256)
	protected String name;

	@Column(name = "priority", nullable = false)
	protected Integer priority;

	@ManyToOne(optional = true)
	@JoinColumn(name = "app_id", referencedColumnName = "id")
	@org.hibernate.annotations.ForeignKey(name = "fk_categories_apps")
	protected App app;

	@OneToOne(mappedBy = "category", cascade = CascadeType.ALL, optional = true, orphanRemoval = true, targetEntity = DataSource.class, fetch=FetchType.LAZY)
	//@LazyCollection(LazyCollectionOption.FALSE)
	protected DataSource dataSource;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", updatable = true, insertable = true)
	@org.hibernate.annotations.ForeignKey(name = "categories_subcategories_fk")
	protected Category parent;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "parent", targetEntity = Category.class, orphanRemoval = false, fetch=FetchType.LAZY)
	//@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("priority")
	protected List<Category> subcategories = new ArrayList<Category>();

	@Column(name = "parent_id", updatable = false, insertable = false)
	protected Integer parentID;

	/**
	 * Nombre asociado al logo
	 */
	@Column(name = "logo_name", nullable = true, length = 1024)
	protected String logoName;

	/**
	 * Logo asociado a la categoria
	 */
	@Lob
	@Type(type = "org.hibernate.type.PrimitiveByteArrayBlobType")
	@Column(name = "logo", nullable = true)
	protected byte[] logo;

	/**
	 * Campo para almacenar el fichero KML, SHAPE, GML asociado
	 */
	@Lob
	@Type(type = "org.hibernate.type.PrimitiveByteArrayBlobType")
	@Column(name = "resource", nullable = true)
	protected byte[] resourceDS;

	/**
	 * Nombre asociado al recurso
	 */
	@Column(name = "resource_name", nullable = true, length = 1024)
	protected String resourceName;

	@Transient
	protected String dataSourceType;

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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public List<Category> getSubcategories() {
		if (subcategories == null) {
			subcategories = new ArrayList<Category>();
		}
		for (Category subcategory : subcategories) {
			subcategory.setParentID(id);
		}
		return this.subcategories;
	}

	public void addSubcategory(Category child) {
		if (child == null)
			throw new IllegalArgumentException("Child is null");

		if (child.getParent() != null) {
			child.getParent().removeSubcategory(child);
		}
		child.setParent(this);
		this.subcategories.add(child);

	}

	public void removeSubcategory(Category child) {
		if (child == null)
			throw new IllegalArgumentException("Category is null");
		for (Category category : child.subcategories) {
			category.removeAllSubcategories();
		}
		this.subcategories.remove(child);
		child.setParent(null);
	}

	public void removeAllSubcategories() {
		for (Category category : subcategories) {
			category.setParent(null);
			category.removeAllSubcategories();
		}
		this.subcategories.clear();
	}

	public Integer getParentID() {
		return parentID;
	}

	public void setParentID(Integer parentID) {
		this.parentID = parentID;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public void setSubcategories(List<Category> subcategories) {
		this.subcategories = subcategories;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getLogoName() {
		return logoName;
	}

	public void setLogoName(String logoName) {
		this.logoName = logoName;
	}

	public byte[] getResourceDS() {
		return resourceDS;
	}

	public void setResourceDS(byte[] resourceDS) {
		this.resourceDS = resourceDS;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
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
		Category other = (Category) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
