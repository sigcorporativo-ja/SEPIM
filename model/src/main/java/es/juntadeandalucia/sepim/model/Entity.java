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

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Geometry;

@javax.persistence.Entity
@Table(schema = "public", name = "entities")
public class Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "entities_seq_gen")
	@SequenceGenerator(name = "entities_seq_gen", sequenceName = "public.entities_id_seq")
	protected Integer id;

	@Column(name = "name", nullable = false, length = 256)
	protected String name;

	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@JoinColumn(name = "entity_category_id", referencedColumnName = "id")
	@org.hibernate.annotations.ForeignKey(name = "fk_entities_entities_categories")
	protected EntityCategory category;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "entity", targetEntity = App.class, orphanRemoval = false, fetch=FetchType.LAZY)
	//@LazyCollection(LazyCollectionOption.FALSE)
	protected List<App> apps = new ArrayList<App>();

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "parent", targetEntity = Entity.class, orphanRemoval = false, fetch=FetchType.LAZY)
	//@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("name")
	protected List<Entity> childs = new ArrayList<Entity>();

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", updatable = true, insertable = true)
	@org.hibernate.annotations.ForeignKey(name = "entities_subentities_fk")
	protected Entity parent;

	@Column(name = "parent_id", updatable = false, insertable = false)
	protected Integer parentID;

	/**
	 * Geometria asociada
	 */
	@Column(name = "geom", nullable = false)
	@Type(type = "org.hibernate.spatial.GeometryType")
	protected Geometry geometry;

	/**
	 * Geometria como array de bytes
	 */
	@Formula("st_asewkb(geom)")
	protected byte[] geom;

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

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public byte[] getGeom() {
		return geom;
	}

	public void setGeom(byte[] geom) {
		this.geom = geom;
	}

	public List<App> getApps() {
		if (apps == null) {
			apps = new ArrayList<App>();
		}
		return this.apps;
	}

	public void addApp(App app) {
		if (app == null)
			throw new IllegalArgumentException("App is null");

		if (app.getEntity() != null) {
			app.getEntity().removeApp(app);
		}
		app.setEntity(this);
		this.apps.add(app);

	}

	public void removeApp(App app) {
		if (app == null)
			throw new IllegalArgumentException("App is null");
		this.apps.remove(app);
		app.setEntity(null);
	}

	public void removeAllApps() {
		for (App app : apps) {
			app.setEntity(null);
		}
		this.apps.clear();
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}

	public List<Entity> getChilds() {
		if (childs == null) {
			childs = new ArrayList<Entity>();
		}
		for (Entity entity : childs) {
			entity.setParentID(id);
		}
		return this.childs;
	}

	public void addChild(Entity child) {
		if (child == null)
			throw new IllegalArgumentException("Child is null");

		if (child.getParent() != null) {
			child.getParent().removeChild(child);
		}
		child.setParent(this);
		this.childs.add(child);

	}

	public void removeChild(Entity child) {
		if (child == null)
			throw new IllegalArgumentException("Entity is null");
		for (Entity entity : child.childs) {
			entity.removeAllChilds();
		}
		this.childs.remove(child);
		child.setParent(null);
	}

	public void removeAllChilds() {
		for (Entity child : childs) {
			child.setParent(null);
			child.removeAllChilds();
		}
		this.childs.clear();
	}

	public EntityCategory getCategory() {
		return category;
	}

	public void setCategory(EntityCategory category) {
		this.category = category;
	}

	public Integer getParentID() {
		return parentID;
	}

	public void setParentID(Integer parentID) {
		this.parentID = parentID;
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
		Entity other = (Entity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
