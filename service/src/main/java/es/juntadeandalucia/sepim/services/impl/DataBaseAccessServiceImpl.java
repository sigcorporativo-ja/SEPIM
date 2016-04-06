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
package es.juntadeandalucia.sepim.services.impl;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import es.juntadeandalucia.sepim.common.FieldType;
import es.juntadeandalucia.sepim.common.PostGisVersion;
import es.juntadeandalucia.sepim.daos.DataBaseDao;
import es.juntadeandalucia.sepim.daos.DataSourceDao;
import es.juntadeandalucia.sepim.daos.EntityDao;
import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.DataBaseDS;
import es.juntadeandalucia.sepim.model.DataSource;
import es.juntadeandalucia.sepim.model.Entity;
import es.juntadeandalucia.sepim.model.Field;
import es.juntadeandalucia.sepim.services.DataBaseAccessService;
import es.juntadeandalucia.sepim.utils.PostGisValues;
import es.juntadeandalucia.sepim.utils.SRStransforms;
import es.juntadeandalucia.sepim.utils.WKBParser;
import es.juntadeandalucia.sepim.web.Item;
import es.juntadeandalucia.sepim.web.ItemFieldValue;
import es.juntadeandalucia.sepim.web.ItemWithGeometry;

@Service
public class DataBaseAccessServiceImpl implements DataBaseAccessService {

	// PostGIS functions
	/** */
	protected final static String ASSTEWKB_FUNCTION_NAME = "st_asewkb"; //$NON-NLS-1$

	/** */
	protected final static String ASEWKB_FUNCTION_NAME = "asewkb"; //$NON-NLS-1$

	/** */
	protected final static String ASBINARY_FUNCTION_NAME = "asbinary"; //$NON-NLS-1$

	/** */
	protected final static String GEOMETRY_FROM_TEXT_FUNCTION_NAME = "geometryfromtext"; //$NON-NLS-1$

	/** */
	protected final static String ST_GEOMETRY_FROM_TEXT_FUNCTION_NAME = "st_geometryfromtext"; //$NON-NLS-1$

	/** */
	protected final static String EXTENT_FUNCTION_NAME = "extent"; //$NON-NLS-1$

	/** */
	protected final static String ST_EXTENT_FUNCTION_NAME = "st_extent"; //$NON-NLS-1$

	/** */
	protected final static String SRID_FUNCTION_NAME = "srid"; //$NON-NLS-1$

	/** */
	protected final static String ST_SRID_FUNCTION_NAME = "st_srid"; //$NON-NLS-1$

	/** */
	protected final static String DISTANCE_FUNCTION_NAME = "distance"; //$NON-NLS-1$

	/** */
	protected final static String ST_DISTANCE_FUNCTION_NAME = "st_distance"; //$NON-NLS-1$

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DataSourceDao dsDao;

	@Autowired
	private DataBaseDao dataBaseDao;

	@Autowired
	private EntityDao entityDao;

	@Autowired
	private SRStransforms SRStransforms;

	@Resource(name = "buffer-distance")
	private Double buffer;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");

	/** Geometry factory */
	public static GeometryFactory factory = new GeometryFactory(
			new PrecisionModel(), 4326);

	@Override
	@Transactional
	public List<Item> getValues(Integer dataSourceId, Integer limit,
			Integer offset, double x, double y, boolean includeGeometry,
			boolean includeFields) throws AppMovCDAUException,
			ClassNotFoundException, SQLException {
		DataSource ds = dsDao.get(dataSourceId);
		if (ds == null) {
			throw new AppMovCDAUException("DataSource " + dataSourceId
					+ "does not exist");
		}
		if (!(ds instanceof DataBaseDS)) {
			throw new AppMovCDAUException(
					"DataSource must be of type data base");
		}
		DataBaseDS dataBaseDS = (DataBaseDS) ds;
		PostGisValues value = setPostGISFunctions(dataBaseDS);
		Point geom = factory.createPoint(new Coordinate(x, y));
		String query = getQueryByDistance(geom, dataBaseDS, value);
		if (limit != null && offset != null) {
			query += " LIMIT " + limit + " OFFSET " + offset;
		}
		return executeQuery(query, dataBaseDS, includeGeometry, null, true,
				includeFields);
	}

	@Override
	@Transactional
	public void checkDataBaseAccess(DataBaseDS dataBaseDS) throws SQLException,
			ClassNotFoundException {
		PostGisValues value = setPostGISFunctions(dataBaseDS);
		String sql = getSQLOnlyGeometryForQuery(null, dataBaseDS, value,
				factory.createPoint(new Coordinate(0, 0)));
		sql += " LIMIT 1";
		executeQuery(sql, dataBaseDS, false, null, true, true);
	}

	@Override
	@Transactional
	public List<Item> getValues(Integer dataSourceId, Integer limit,
			Integer offset, Integer entityId, boolean includeGeometry,
			boolean includeFields) throws AppMovCDAUException,
			ClassNotFoundException, SQLException {
		DataSource ds = dsDao.get(dataSourceId);
		if (ds == null) {
			throw new AppMovCDAUException("DataSource " + dataSourceId
					+ "does not exist");
		}
		if (!(ds instanceof DataBaseDS)) {
			throw new AppMovCDAUException(
					"DataSource must be of type data base");
		}
		Entity entity = entityDao.get(entityId);
		if (entity == null) {
			throw new AppMovCDAUException("Entity " + entityId
					+ "does not exist");
		}
		DataBaseDS dataBaseDS = (DataBaseDS) ds;
		PostGisValues value = setPostGISFunctions(dataBaseDS);
		Geometry geom = entity.getGeometry();
		String query = getSQLOnlyGeometryForQuery(geom, dataBaseDS, value,
				geom.getCentroid());
		query += " ORDER BY distance ASC";
		if (limit != null && offset != null) {
			query += " LIMIT " + limit + " OFFSET " + offset;
		}
		return executeQuery(query, dataBaseDS, includeGeometry, geom, true,
				includeFields);
	}

	@Override
	@Transactional
	public List<Item> getValuesByItem(Integer dataSourceId, String itemId,
			boolean includeGeometry) throws AppMovCDAUException,
			ClassNotFoundException, SQLException {
		DataSource ds = dsDao.get(dataSourceId);
		if (ds == null) {
			throw new AppMovCDAUException("DataSource " + dataSourceId
					+ "does not exist");
		}
		if (!(ds instanceof DataBaseDS)) {
			throw new AppMovCDAUException(
					"DataSource must be of type data base");
		}
		DataBaseDS dataBaseDS = (DataBaseDS) ds;
		PostGisValues value = setPostGISFunctions(dataBaseDS);
		String query = getSQLByPKQuery(dataBaseDS, value, itemId);
		return executeQuery(query, dataBaseDS, includeGeometry, null, false,
				true);
	}

	@SuppressWarnings("unchecked")
	private List<Item> executeQuery(String sql, DataBaseDS ds,
			boolean includeGeometry, Geometry geom, boolean hasDistance,
			boolean includeFields) throws ClassNotFoundException, SQLException {
		List<Item> items = new ArrayList<Item>();
		Connection con = null;
		PreparedStatement statement = null;
		try {
			con = dataBaseDao.getJDBCConnection(ds);
			statement = con.prepareStatement(sql);
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				Geometry readGeometry = readGeometry(res, ds);
				if (geom != null) {
					if (!readGeometry.intersects(geom)) {
						continue;
					}
				}
				Item item = null;
				if (includeGeometry) {
					item = new ItemWithGeometry();
					((ItemWithGeometry) item).setGeometry(readGeometry);
					((ItemWithGeometry) item).setIdSymbol(ds.getSymbols()
							.iterator().next().getId());
				} else {
					item = new Item();
				}
				if (hasDistance) {
					item.setDistance(res.getDouble("distance"));
				}
				String pkValue = ds.getPKField() != null ? getValue(res, ds
						.getPKField().getName()) : null;
				item.setPkValue(pkValue);

				Object[] readFieldValues = readFieldValues(res, ds);
				item.setName((String) readFieldValues[0]);
				if (includeFields) {
					item.getFields()
							.addAll((Collection<? extends ItemFieldValue>) readFieldValues[1]);
				}
				Envelope envelope = readGeometry.getEnvelopeInternal();
				if (readGeometry instanceof Point
						|| readGeometry instanceof MultiPoint) {
					envelope.expandBy(0.0005);
				}
				item.setMaxX(envelope.getMaxX());
				item.setMaxY(envelope.getMaxY());
				item.setMinX(envelope.getMinX());
				item.setMinY(envelope.getMinY());
				items.add(item);
			}
			res.close();
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (con != null) {
				con.close();
			}
		}
		return items;
	}

	private Object[] readFieldValues(ResultSet resultSet, DataBaseDS dataBaseDS)
			throws SQLException {
		List<ItemFieldValue> values = new ArrayList<ItemFieldValue>();
		String value = null;
		for (Field field : dataBaseDS.getFields()) {
			if (!field.getVisible()) {
				continue;
			}

			String fieldValue = getValue(resultSet, field.getName());
			if (field.getPrimaryField() != null && field.getPrimaryField()) {
				value = fieldValue;
			}
			ItemFieldValue itemFieldValue = new ItemFieldValue();
			itemFieldValue.setField(field.getPublicName());
			itemFieldValue.setValue(fieldValue);
			values.add(itemFieldValue);
		}
		return new Object[] { value, values };
	}

	/**
	 * Treatment for data recovered from the database in binary format
	 * 
	 * @param rs
	 *            Database result cursor
	 * @param fieldName
	 *            Field name
	 * @return Object - Value
	 * @throws SQLException
	 */
	protected String getValue(ResultSet rs, String fieldName)
			throws SQLException {
		byte[] data = null;
		String result = null;
		Object val = null;
		int fieldId = rs.findColumn(fieldName);
		try {
			byte[] byteBuf = rs.getBytes(fieldId);
			if (byteBuf != null) {
				@SuppressWarnings("unused")
				ByteBuffer buf = ByteBuffer.wrap(byteBuf);
				ResultSetMetaData metaData = rs.getMetaData();
				int columnType = metaData.getColumnType(fieldId);
				switch (columnType) {
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
				case Types.CHAR:
					result = rs.getString(fieldId);
					break;
				case Types.FLOAT:
				case Types.REAL:
					val = rs.getFloat(fieldId);
					if (val != null) {
						result = Float.toString((Float) val);
					}
					break;
				case Types.DOUBLE:
					val = rs.getDouble(fieldId);
					if (val != null) {
						result = Double.toString((Double) val);
					}
					break;
				case Types.INTEGER:
					val = rs.getInt(fieldId);
					if (val != null) {
						result = Integer.toString((Integer) val);
					}
					break;
				case Types.BIGINT:
					val = rs.getLong(fieldId);
					if (val != null) {
						result = Long.toString((Long) val);
					}
					break;
				case Types.BIT:
					val = rs.getObject(fieldId);
					if (val != null) {
						result = val.toString();
					}
					break;
				case Types.BOOLEAN:
					val = rs.getBoolean(fieldId);
					if (val != null) {
						result = Boolean.toString((Boolean) val);
					}
					break;
				case Types.SMALLINT:
				case Types.TINYINT:
					val = rs.getShort(fieldId);
					if (val != null) {
						result = Short.toString((Short) val);
					}
					break;
				case Types.DATE:
					val = rs.getDate(fieldId);
					if (val != null) {
						result = dateFormat.format((Date) val);
					}
					break;
				case Types.TIMESTAMP:
					val = rs.getTimestamp(fieldId);
					if (val != null) {
						result = dateFormat.format((Timestamp) val);
					}
					break;
				case Types.NUMERIC:
					val = rs.getBigDecimal(fieldId);
					if (val != null) {
						result = val.toString();
					}
					break;
				default:
					logger.warn("Unknown data type -> " + columnType); //$NON-NLS-1$
					val = data;
					break;
				}
			}
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	private Geometry readGeometry(ResultSet resultset, DataBaseDS ds) {
		WKBParser parser = new WKBParser();
		Geometry databaseObject = null;
		try {
			byte[] data = resultset.getBytes(ds.getGeomField());
			if (data == null) {
				databaseObject = factory.createGeometryCollection(null);
			} else {
				databaseObject = parser.parse(data);
			}
		} catch (Exception e) {
			logger.error("", e); //$NON-NLS-1$
		}
		return databaseObject;
	}

	private String getQueryByDistance(Point geom, DataBaseDS dataBaseDS,
			PostGisValues value) {
		String sql = getSQLOnlyGeometryForQuery(geom, dataBaseDS, value, geom);

		sql += " AND " //$NON-NLS-1$
				+ value.getDistanceFunction()
				+ "(" //$NON-NLS-1$
				+ value.getGeometryFromTextFunction()
				+ "('POINT(" + geom.getX() + " " + geom.getY() + ")', 4326), " + getGeometryFieldIn4326(dataBaseDS) + ") <= " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ buffer;
		sql += " ORDER BY distance ASC";
		return sql;
	}

	private String escapeAttributeName(String attrName) {
		return "\"" + attrName + "\""; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getFullTableName(DataBaseDS dataBaseDS) {
		if (StringUtils.isEmpty(dataBaseDS.getSchema())) {
			return "\"" + dataBaseDS.getTable() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return "\"" + dataBaseDS.getSchema() + "\".\"" + dataBaseDS.getTable() + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private String getGeometryQueryString(DataBaseDS dataBaseDS,
			String queryFunction) {
		String result = queryFunction + "("
				+ getGeometryFieldIn4326(dataBaseDS) + ",'XDR')";
		return result + " AS " + escapeAttributeName(dataBaseDS.getGeomField());
	}

	private String getGeometryQueryDistanceString(DataBaseDS dataBaseDS,
			Geometry geom, PostGisValues value) {
		String result = "round(CAST(ST_Distance_Sphere(ST_Centroid("
				+ getGeometryFieldIn4326(dataBaseDS) + "),ST_Centroid("
				+ value.getGeometryFromTextFunction() + "('" + geom.toText()
				+ "', 4326))) As numeric),2) ";
		// String result = value.getDistanceFunction() + "("
		// + getGeometryFieldIn4326(dataBaseDS) + ","
		// + value.getGeometryFromTextFunction() + "('" + geom.toText()
		// + "', 4326)" + ")";
		return result + " AS distance"; //$NON-NLS-1$
	}

	private String getGeometryFieldIn4326(DataBaseDS dataBaseDS) {
		String sqlTransform = SRStransforms.getSQLTransform("EPSG:"
				+ dataBaseDS.getSrid());
		return StringUtils
				.replaceOnce(
						sqlTransform,
						"?", getFullTableName(dataBaseDS) + "." + escapeAttributeName(dataBaseDS.getGeomField()) //$NON-NLS-1$ //$NON-NLS-2$
								+ "");
	}

	/**
	 * Sets an appropiate query function based on the postgresql/postgis
	 * version. If the st_asewkb function exists, it will use it instead of the
	 * asBinary function to allow the use of 3D geometries. This should be true
	 * for PostGIS 1.0 or upper. If PostGIS version is 0.9X or lower, use
	 * directly the asBinary. TODO: This can be improved by checking directly if
	 * the function exists. But this would need to have some rights over the
	 * database to check system tables
	 */
	protected PostGisValues setPostGISFunctions(DataBaseDS dataBaseDS) {
		PostGisValues value = new PostGisValues();
		// Check the PostGIS version. If it's 1.X the function should exist
		String postgisVersionQuery = "SELECT postgis_lib_version()"; //$NON-NLS-1$
		Connection connection = null;
		Statement statement = null;
		try {
			connection = dataBaseDao.getJDBCConnection(dataBaseDS);
			statement = connection.createStatement();
			ResultSet res = statement.executeQuery(postgisVersionQuery);

			if (res.next()) {
				String version = res.getString(1);

				// Check the PostGIS version
				// If the PostGIS version is 0.X, use the asbinary and non st_*
				// functions. If it's 1.0.X, use asewkb and non st_* functions.
				// Otherwise, st_asewkb
				// and st_* functions
				if (StringUtils.isNotEmpty(version)) {

					if (version.charAt(0) == '0') {
						value.setPostGisVersion(PostGisVersion.POSTGIS_0_X
								.name());
						value.setQueryFunction(ASBINARY_FUNCTION_NAME);
						value.setGeometryFromTextFunction(GEOMETRY_FROM_TEXT_FUNCTION_NAME);
						value.setSridFunction(SRID_FUNCTION_NAME);
						value.setExtentFunction(EXTENT_FUNCTION_NAME);
						value.setDistanceFunction(DISTANCE_FUNCTION_NAME);
					} else if (version.startsWith("1.0")) { //$NON-NLS-1$
						value.setPostGisVersion(PostGisVersion.POSTGIS_1_0_X
								.name());
						value.setQueryFunction(ASEWKB_FUNCTION_NAME);
						value.setGeometryFromTextFunction(GEOMETRY_FROM_TEXT_FUNCTION_NAME);
						value.setSridFunction(SRID_FUNCTION_NAME);
						value.setExtentFunction(EXTENT_FUNCTION_NAME);
						value.setDistanceFunction(DISTANCE_FUNCTION_NAME);
					}
				}
			}
			res.close();
			statement.close();

		} catch (Exception ex) {
			// Ignore the error, just use default
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("", e);
				}
			}
		} // finally
		return value;
	}

	private String getSQLOnlyGeometryForQuery(Geometry geom,
			DataBaseDS dataBaseDS, PostGisValues value, Point loc) {
		String sqlQuery = "SELECT "
				+ getGeometryQueryString(dataBaseDS, value.getQueryFunction())

				+ ",";
		if (loc != null) {
			sqlQuery += getGeometryQueryDistanceString(dataBaseDS, loc, value)
					+ ",";
		}
		if (dataBaseDS.getPKField() != null) {
			sqlQuery += getFullTableName(dataBaseDS) + "."
					+ escapeAttributeName(dataBaseDS.getPKField().getName())
					+ ",";
		}
		if (!dataBaseDS.getFields().isEmpty()) {
			for (Iterator<Field> iter = dataBaseDS.getFields().iterator(); iter
					.hasNext();) {
				Field field = iter.next();
				if (!field.getVisible())
					continue;
				String campo = field.getName();

				sqlQuery += getFullTableName(dataBaseDS)
						+ "." + escapeAttributeName(campo) + ","; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1) + " FROM "
				+ getFullTableName(dataBaseDS);
		Envelope rectangle = null;
		if (geom != null) {
			rectangle = geom.getEnvelopeInternal();
			if (geom instanceof Point) {
				rectangle.expandBy(buffer);
			}
		}

		if (rectangle != null || dataBaseDS.getFilter() != null) {

			sqlQuery += " WHERE "; //$NON-NLS-1$
			if (rectangle != null) {
				double xmin = rectangle.getMinX();
				double xmax = rectangle.getMaxX();
				double ymin = rectangle.getMinY();
				double ymax = rectangle.getMaxY();

				sqlQuery += getGeometryFieldIn4326(dataBaseDS) + " && " //$NON-NLS-1$ //$NON-NLS-2$
						+ value.getGeometryFromTextFunction() + "('POLYGON((" + //$NON-NLS-1$
						xmin + " " + ymin + ", " + //$NON-NLS-1$ //$NON-NLS-2$
						xmax + " " + ymin + ", " + //$NON-NLS-1$ //$NON-NLS-2$
						xmax + " " + ymax + ", " + //$NON-NLS-1$ //$NON-NLS-2$
						xmin + " " + ymax + ", " + //$NON-NLS-1$ //$NON-NLS-2$
						xmin + " " + ymin + "))',4326) AND "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			if (StringUtils.isNotEmpty(dataBaseDS.getFilter())) {
				sqlQuery += "(" + dataBaseDS.getFilter() + ") AND "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 4);

		}

		return sqlQuery;
	}

	private String getSQLByPKQuery(DataBaseDS dataBaseDS, PostGisValues value,
			String itemID) {
		String sqlQuery = "SELECT "
				+ getGeometryQueryString(dataBaseDS, value.getQueryFunction())

				+ ",";

		if (dataBaseDS.getPKField() != null) {
			sqlQuery += getFullTableName(dataBaseDS) + "."
					+ escapeAttributeName(dataBaseDS.getPKField().getName())
					+ ",";
		}
		if (!dataBaseDS.getFields().isEmpty()) {
			for (Iterator<Field> iter = dataBaseDS.getFields().iterator(); iter
					.hasNext();) {
				Field field = iter.next();
				if (!field.getVisible())
					continue;
				String campo = field.getName();

				sqlQuery += getFullTableName(dataBaseDS)
						+ "." + escapeAttributeName(campo) + ","; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 1) + " FROM "
				+ getFullTableName(dataBaseDS);

		FieldType type = dataBaseDS.getPKField().getType();

		sqlQuery += " WHERE " + getFullTableName(dataBaseDS) + "."
				+ escapeAttributeName(dataBaseDS.getPKField().getName()) + "="
				+ (type == FieldType.STRING ? "'" + itemID + "'" : itemID)
				+ " AND";

		if (StringUtils.isNotEmpty(dataBaseDS.getFilter())) {
			sqlQuery += "(" + dataBaseDS.getFilter() + ") AND "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 4);

		return sqlQuery;
	}

}
