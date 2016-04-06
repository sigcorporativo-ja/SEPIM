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
package es.juntadeandalucia.sepim.daos.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import es.juntadeandalucia.sepim.common.FieldType;
import es.juntadeandalucia.sepim.daos.DataBaseDao;
import es.juntadeandalucia.sepim.model.DataBaseDS;
import es.juntadeandalucia.sepim.model.Field;

@Repository
public class DataBaseDaoImpl implements DataBaseDao {

	/** Database type to Java class map */
	private static final Map<Integer, FieldType> TYPE_MAPPINGS = new HashMap<Integer, FieldType>();

	static {
		TYPE_MAPPINGS.put(Integer.valueOf(Types.VARCHAR), FieldType.STRING);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.CHAR), FieldType.STRING);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.LONGVARCHAR), FieldType.STRING);

		TYPE_MAPPINGS.put(Integer.valueOf(Types.BIT), FieldType.BOOLEAN);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.BOOLEAN), FieldType.BOOLEAN);

		TYPE_MAPPINGS.put(Integer.valueOf(Types.TINYINT), FieldType.INTEGER);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.SMALLINT), FieldType.INTEGER);

		TYPE_MAPPINGS.put(Integer.valueOf(Types.INTEGER), FieldType.INTEGER);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.BIGINT), FieldType.LONG);

		TYPE_MAPPINGS.put(Integer.valueOf(Types.REAL), FieldType.DOUBLE);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.FLOAT), FieldType.DOUBLE);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.DOUBLE), FieldType.DOUBLE);

		TYPE_MAPPINGS.put(Integer.valueOf(Types.DECIMAL), FieldType.LONG);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.NUMERIC), FieldType.LONG);

		TYPE_MAPPINGS.put(Integer.valueOf(Types.DATE), FieldType.DATE);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.TIME), FieldType.DATE);
		TYPE_MAPPINGS
				.put(Integer.valueOf(Types.TIMESTAMP), FieldType.TIMESTAMP);

		// Added BLOB, CLOB y BINARY types
		TYPE_MAPPINGS.put(Integer.valueOf(Types.BLOB), FieldType.OBJECT);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.CLOB), FieldType.OBJECT);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.BINARY), FieldType.OBJECT);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.VARBINARY), FieldType.OBJECT);
		TYPE_MAPPINGS.put(Integer.valueOf(Types.LONGVARBINARY),
				FieldType.OBJECT);
	}

	@Override
	public List<Field> getFields(DataBaseDS ds) throws ClassNotFoundException,
			SQLException {
		List<Field> columns = new ArrayList<Field>();
		Connection con = null;
		try {
			con = getJDBCConnection(ds);
			// Obtenemos la pK
			String candidatePKName = getPrimaryOrUniqueKey(ds.getSchema(),
					ds.getTable(), "p", con);
			if (candidatePKName == null) {
				candidatePKName = getPrimaryOrUniqueKey(ds.getSchema(),
						ds.getTable(), "u", con);
			}
			final int COLUMN_NAME = 4;
			DatabaseMetaData dmd = con.getMetaData();
			dmd.getDatabaseProductVersion();
			java.sql.ResultSet rs = dmd.getColumns(null, ds.getSchema(),
					ds.getTable(), "%");
			while (rs.next()) {
				String columnName = rs.getString(COLUMN_NAME);
				FieldType tipo = buildFieldType(rs);
				if (tipo == FieldType.GEOMETRY) {
					ds.setGeomField(columnName);
					continue;
				}

				Field column = new Field();
				column.setName(columnName);
				column.setPublicName(columnName);
				column.setType(FieldType.valueOf(tipo.value()));
				column.setPrimaryKey(columnName
						.equalsIgnoreCase(candidatePKName));
				column.setVisible(true);
				column.setPrimaryField(false);
				ds.addField(column);
				columns.add(column);
			}
			rs.close();
			if (ds.getFields().isEmpty()) {
				throw new SQLException("The table " + ds.getSchema() + "."
						+ ds.getTable() + " doesn't exist");
			} else {
				ds.getFields().get(0).setPrimaryField(true);
			}
			updateGeometryRelations(ds, con);
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return columns;
	}

	@Override
	public String getPrimaryOrUniqueKey(String dataBaseSchema,
			String tableName, String indexType, Connection con)
			throws SQLException {
		String sql = "SELECT a.attname as column_name, t.typname as data_type, " //$NON-NLS-1$
				+ "CASE " //$NON-NLS-1$
				+ "WHEN cc.contype='p' THEN 'PRI' " //$NON-NLS-1$
				+ "WHEN cc.contype='u' THEN 'UNI' " //$NON-NLS-1$
				+ "WHEN cc.contype='f' THEN 'FK' " //$NON-NLS-1$
				+ "ELSE '' END AS key, " //$NON-NLS-1$
				+ "CASE WHEN a.attnotnull=false THEN 'YES' ELSE 'NO' END AS is_nullable, " //$NON-NLS-1$
				+ "CASE WHEN a.attlen='-1' THEN (a.atttypmod - 4) ELSE a.attlen END as max_length, " //$NON-NLS-1$
				+ "d.adsrc as column_default " //$NON-NLS-1$
				+ "FROM pg_catalog.pg_attribute a " //$NON-NLS-1$
				+ "LEFT JOIN pg_catalog.pg_type t ON t.oid = a.atttypid " //$NON-NLS-1$
				+ "LEFT JOIN pg_catalog.pg_class c ON c.oid = a.attrelid " //$NON-NLS-1$
				+ "LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace " //$NON-NLS-1$
				+ "LEFT JOIN pg_catalog.pg_constraint cc ON cc.conrelid = c.oid AND cc.conkey[1] = a.attnum " //$NON-NLS-1$
				+ "LEFT JOIN pg_catalog.pg_attrdef d ON d.adrelid = c.oid AND a.attnum = d.adnum " //$NON-NLS-1$
				+ "WHERE n.nspname=? AND c.relname = ? AND a.attnum > 0 AND t.oid = a.atttypid AND cc.contype=?"; //$NON-NLS-1$
		String pkName = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, dataBaseSchema);
			ps.setString(2, tableName);
			ps.setString(3, indexType);
			res = ps.executeQuery();
			if (res.next()) {
				pkName = res.getString("column_name"); //$NON-NLS-1$
			} else {
				pkName = "";
			}
			res.close();
			ps.close();
		} finally {
			if (ps != null)
				ps.close();

			if (res != null)
				res.close();
		}
		return pkName;
	}

	private FieldType buildFieldType(ResultSet rs) throws SQLException {
		final int DATA_TYPE = 5;
		final int TYPE_NAME = 6;
		int dataType = rs.getInt(DATA_TYPE);
		String typeName = rs.getString(TYPE_NAME);
		if (typeName.equals("geometry")) { //$NON-NLS-1$
			return FieldType.GEOMETRY;
		} else {
			return TYPE_MAPPINGS.get(new Integer(dataType));
		}
	}

	@Override
	public String getPrimaryKey(String schema, String table, Connection con)
			throws SQLException {
		String pkName = getPrimaryOrUniqueKey(schema, table, "p", con);
		if (pkName == null) {
			pkName = getPrimaryOrUniqueKey(schema, table, "u", con);
		}
		return pkName;
	}

	@Override
	public Connection getJDBCConnection(DataBaseDS ds)
			throws ClassNotFoundException, SQLException {
		String url = "jdbc:postgresql://" + ds.getUrlDataBase() + ":"
				+ ds.getPort() + "/" + ds.getDataBase() + "?user="
				+ ds.getUser() + "&password=" + ds.getPassword();
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection(url);
	}

	private void updateGeometryRelations(DataBaseDS ds, Connection con)
			throws ClassNotFoundException, SQLException {
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			String sql = "SELECT  srid, \"type\" FROM geometry_columns WHERE f_table_schema=? AND f_table_name=? AND f_geometry_column=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, ds.getSchema());
			ps.setString(2, ds.getTable());
			ps.setString(3, ds.getGeomField());
			res = ps.executeQuery();
			while (res.next()) {
				ds.setSrid(res.getInt("srid"));
				ds.setGeometryType(res.getString("type"));

			}
			res.close();
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}

}
