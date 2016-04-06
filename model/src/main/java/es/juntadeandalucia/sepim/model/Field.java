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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import es.juntadeandalucia.sepim.common.FieldType;

/**
 * Campos a tener en cuenta para ser mostrados al cliente
 * 
 * @author Marco Antonio Fuentelsaz Perez
 * @since 1.0.0
 *
 */
@Entity
@Table(schema = "public", name = "fields")
public class Field {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fields_seq_gen")
	@SequenceGenerator(name = "fields_seq_gen", sequenceName = "public.fields_id_seq")
	protected Integer id;

	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	@org.hibernate.annotations.ForeignKey(name = "fk_fields_datasources")
	protected DataSource dataSource;

	@Column(name = "field_name", nullable = false, length = 256)
	protected String name;

	@Column(name = "public_name", nullable = false, length = 256)
	protected String publicName;
	
	@Column(name="primary_key", nullable=true)
	protected Boolean primaryKey;
	
	@Column(name="visible", nullable=true)
	protected Boolean visible;
	
	@Column(name="primary_field", nullable=true)
	protected Boolean primaryField;
	

	@Column(name = "type", nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	protected FieldType type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublicName() {
		return publicName;
	}

	public void setPublicName(String publicName) {
		this.publicName = publicName;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public Boolean getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getPrimaryField() {
		return primaryField;
	}

	public void setPrimaryField(Boolean primaryField) {
		this.primaryField = primaryField;
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
		Field other = (Field) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
