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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;
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
@Table(schema = "public", name = "datasources")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 64)
@DiscriminatorValue(value = "DS_BASE")
public class DataSource {

	@Id
	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "category"))
	@GeneratedValue(generator = "generator")
	@Column(name = "id", unique = true, nullable = false)
	protected Integer id;

	@Column(name = "geom_field", nullable = false, length = 128)
	protected String geomField;

	@Column(name = "geometry_type", nullable = false, length = 25)
	protected String geometryType;

	@Column(name = "srid", nullable = false)
	protected Integer srid;

	@Column(name = "filter", nullable = true)
	@Type(type = "text")
	protected String filter;

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	protected Category category;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "dataSource", targetEntity = Symbol.class, orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	protected List<Symbol> symbols = new ArrayList<Symbol>();

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "dataSource", targetEntity = Field.class, orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy("publicName")
	protected List<Field> fields = new ArrayList<Field>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGeomField() {
		return geomField;
	}

	public void setGeomField(String geomField) {
		this.geomField = geomField;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public void setGeometryType(String geometryType) {
		this.geometryType = geometryType;
	}

	public List<Symbol> getSymbols() {
		if (symbols == null) {
			symbols = new ArrayList<Symbol>();
		}
		return this.symbols;
	}

	public void addSymbol(Symbol symbol) {
		if (symbol == null)
			throw new IllegalArgumentException("Symbol is null");

		if (symbol.getDataSource() != null) {
			symbol.getDataSource().removeSymbol(symbol);
		}
		symbol.setDataSource(this);
		this.symbols.add(symbol);

	}

	public void removeSymbol(Symbol symbol) {
		if (symbol == null)
			throw new IllegalArgumentException("Symbol is null");
		this.symbols.remove(symbol);
		symbol.setDataSource(null);
	}

	public Integer getSrid() {
		return srid;
	}

	public void setSrid(Integer srid) {
		this.srid = srid;
	}

	public List<Field> getFields() {
		if (fields == null) {
			fields = new ArrayList<Field>();
		}
		return this.fields;
	}

	public void addField(Field field) {
		if (field == null)
			throw new IllegalArgumentException("Field is null");

		if (field.getDataSource() != null) {
			field.getDataSource().removeField(field);
		}
		field.setDataSource(this);
		this.fields.add(field);

	}

	public void removeField(Field field) {
		if (field == null)
			throw new IllegalArgumentException("Field is null");
		this.fields.remove(field);
		field.setDataSource(null);
	}

	public void removeAllFields() {
		for (Field field : fields) {
			field.setDataSource(null);
		}
		this.fields.clear();
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
		DataSource other = (DataSource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
