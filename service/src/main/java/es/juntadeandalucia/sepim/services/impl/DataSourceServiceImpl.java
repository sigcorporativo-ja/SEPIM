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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.juntadeandalucia.sepim.daos.CategoryDao;
import es.juntadeandalucia.sepim.daos.DataBaseDao;
import es.juntadeandalucia.sepim.daos.DataSourceDao;
import es.juntadeandalucia.sepim.daos.FieldDao;
import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.Category;
import es.juntadeandalucia.sepim.model.DataBaseDS;
import es.juntadeandalucia.sepim.model.DataSource;
import es.juntadeandalucia.sepim.model.Field;
import es.juntadeandalucia.sepim.model.LineSymbol;
import es.juntadeandalucia.sepim.model.PointSymbol;
import es.juntadeandalucia.sepim.model.PolygonSymbol;
import es.juntadeandalucia.sepim.model.Table;
import es.juntadeandalucia.sepim.model.WFSDS;
import es.juntadeandalucia.sepim.services.DataBaseAccessService;
import es.juntadeandalucia.sepim.services.DataSourceService;

@Service
public class DataSourceServiceImpl implements DataSourceService {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DataSourceDao dataSourceDao;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private DataBaseDao dataBaseDao;

	@Autowired
	private DataBaseAccessService dataBaseAccessService;

	@Autowired
	private FieldDao fieldDao;

	@Resource(name = "pointsymbol-default-url")
	private String defaultPointSymbol;

	@Override
	@Transactional
	public Integer addDataBaseDataSource(String url, int port, String user,
			String password, String dataBase, String schema, String table,
			String filter, Integer idCategory) throws SQLException,
			ClassNotFoundException, AppMovCDAUException {
		Category category = categoryDao.getCategory(idCategory);
		if (category == null) {
			throw new AppMovCDAUException("Category with id " + idCategory
					+ " does not exist");
		}
		DataBaseDS ds = new DataBaseDS();
		ds.setDataBase(dataBase);
		ds.setPassword(password);
		ds.setUser(user);
		ds.setUrlDataBase(url);
		ds.setFilter(filter);
		ds.setPort(port);
		ds.setTable(table);
		ds.setSchema(schema);
		dataBaseDao.getFields(ds);
		dataBaseAccessService.checkDataBaseAccess(ds);
		category.setDataSource(ds);
		ds.setCategory(category);
		dataSourceDao.saveOrUpdate(ds);
		return ds.getId();
	}

	@Override
	@Transactional
	public void setDefaultSymbol(Integer idDataSource)
			throws AppMovCDAUException {
		DataSource ds = dataSourceDao.get(idDataSource);
		if (ds == null) {
			throw new AppMovCDAUException("The datasource for category "
					+ idDataSource + " does not exist");
		}
		if (ds.getGeometryType().equalsIgnoreCase("POLYGON")
				|| ds.getGeometryType().equalsIgnoreCase("MULTIPOLYGON")) {
			createDefaultPolygonSymbol(ds);
		} else if (ds.getGeometryType().equalsIgnoreCase("LINESTRING")
				|| ds.getGeometryType().equalsIgnoreCase("MULTILINESTRING")) {
			createDefaultLineSymbol(ds);
		} else if (ds.getGeometryType().equalsIgnoreCase("POINT")
				|| ds.getGeometryType().equalsIgnoreCase("MULTIPOINT")) {
			createDefaultPointSymbol(ds);
		} else {
			logger.warn("Unspecific geometry type. Setting a polygon symbol");
			createDefaultPolygonSymbol(ds);
		}
		dataSourceDao.saveOrUpdate(ds);
	}

	private void createDefaultPointSymbol(DataSource ds) {
		PointSymbol pointSymbol = new PointSymbol();
		pointSymbol.setGraphicURL(defaultPointSymbol);
		ds.addSymbol(pointSymbol);
		pointSymbol.setDataSource(ds);
	}

	private void createDefaultPolygonSymbol(DataSource ds) {
		PolygonSymbol polygonSymbol = new PolygonSymbol();
		polygonSymbol.setFillColor("#2EFEF7");
		polygonSymbol.setFillOpacity(0.5f);
		polygonSymbol.setStrokeColor("#FFFF00");
		polygonSymbol.setStrokeWidth(3);
		polygonSymbol.setDataSource(ds);
		ds.addSymbol(polygonSymbol);
	}

	private void createDefaultLineSymbol(DataSource ds) {
		LineSymbol polygonSymbol = new LineSymbol();
		polygonSymbol.setStrokeColor("#FFFF00");
		polygonSymbol.setStrokeWidth(3);
		polygonSymbol.setDataSource(ds);
		ds.addSymbol(polygonSymbol);
	}

	@Override
	@Transactional
	public Integer addWFSDataSource(String url, String layerName,
			String geomField, String filter, Integer idCategory)
			throws SQLException, ClassNotFoundException, AppMovCDAUException {
		Category category = categoryDao.getCategory(idCategory);
		if (category == null) {
			throw new AppMovCDAUException("Category with id " + idCategory
					+ " does not exist");
		}
		WFSDS ds = new WFSDS();
		ds.setLayer(layerName);
		ds.setUrl(url);
		ds.setGeomField(geomField);
		category.setDataSource(ds);
		ds.setCategory(category);
		dataSourceDao.saveOrUpdate(ds);
		return ds.getId();
	}

	@Override
	@Transactional
	public DataSource getDataSourceByCategory(Integer idCategory)
			throws AppMovCDAUException {
		DataSource dataSource = dataSourceDao.getByCategory(idCategory);
		if (dataSource == null) {
			throw new AppMovCDAUException("The datasource for category "
					+ idCategory + " does not exist");
		}
		return dataSource;
	}

	@Override
	@Transactional
	public void deleteDataSource(Integer idDataSource)
			throws AppMovCDAUException {
		DataSource ds = dataSourceDao.get(idDataSource);
		if (ds == null) {
			throw new AppMovCDAUException("The dataSource " + idDataSource
					+ "does not exist");
		}
		ds.getCategory().setDataSource(null);
		dataSourceDao.delete(ds);
	}

	@Override
	@Transactional
	public DataSource getDataSource(Integer idDataSource)
			throws AppMovCDAUException {
		DataSource ds = dataSourceDao.get(idDataSource);
		if (ds == null) {
			throw new AppMovCDAUException("DataSourceService " + idDataSource
					+ "does not exist");
		}
		return ds;
	}

	@Override
	@Transactional
	public void updateField(Integer idField, String publicName,
			boolean visible, boolean primaryField) throws AppMovCDAUException {
		Field field = fieldDao.get(idField);
		if (field == null) {
			throw new AppMovCDAUException("Field " + idField + "does not exist");
		}
		field.setPublicName(publicName);
		field.setVisible(visible);
		field.setPrimaryField(primaryField);
		fieldDao.saveOrUpdate(field);
	}

	@Override
	@Transactional
	public Map<String, Table> getGeometryTables(DataBaseDS ds)
			throws ClassNotFoundException, SQLException {
		Map<String, Table> dsTables = new HashMap<String, Table>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			con = dataBaseDao.getJDBCConnection(ds);
			String sql = "SELECT f_table_schema, f_table_name, f_geometry_column,  srid, \"type\" FROM geometry_columns";
			ps = con.prepareStatement(sql);
			res = ps.executeQuery();

			while (res.next()) {
				Table modelTable = new Table();
				modelTable.setSchema(res.getString("f_table_schema"));
				modelTable.setTable(res.getString("f_table_name"));
				modelTable
						.setGeometryColumn(res.getString("f_geometry_column"));
				modelTable.setSrid(res.getInt("srid"));
				modelTable.setGeometryType(res.getString("type"));
				dsTables.put(
						modelTable.getSchema() + "_" + modelTable.getTable(),
						modelTable);
			}
			res.close();
			ps.close();
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return dsTables;
	}


	@Override
	@Transactional
	public void updateDataBaseDS(Integer idDataSource, String urlDataBase,
			Integer port, String dataBase, String user, String password,
			String schema, String table, String geomField, String filter)
			throws AppMovCDAUException, ClassNotFoundException, SQLException {
		DataSource ds = dataSourceDao.get(idDataSource);
		if (ds == null) {
			throw new AppMovCDAUException("DataSource " + idDataSource
					+ "does not exist");
		}
		if (!(ds instanceof DataBaseDS)) {
			throw new AppMovCDAUException(
					"DataSource must be of type data base");
		}
		DataBaseDS dataBaseDS = (DataBaseDS) ds;
		if (StringUtils.isNotEmpty(urlDataBase)) {
			dataBaseDS.setDataBase(dataBase);
			dataBaseDS.setPassword(password);
			dataBaseDS.setPort(port);
			dataBaseDS.setUrlDataBase(urlDataBase);
			dataBaseDS.setUser(user);
			dataBaseDS.setFilter(filter);
			dataBaseDS.setTable(table);
			dataBaseDS.setSchema(schema);
			ds.setGeomField(geomField);
			dataBaseDS.removeAllFields();
			dataBaseDao.getFields(dataBaseDS);
			dataBaseAccessService.checkDataBaseAccess(dataBaseDS);
			dataSourceDao.saveOrUpdate(ds);
		}

	}

	@Override
	@Transactional
	public void updateWFSDS(Integer idDataSource, String url, String layer,
			String geomField, String filter) throws AppMovCDAUException,
			ClassNotFoundException, SQLException {
		DataSource ds = dataSourceDao.get(idDataSource);
		if (ds == null) {
			throw new AppMovCDAUException("DataSource " + idDataSource
					+ "does not exist");
		}
		if (!(ds instanceof WFSDS)) {
			throw new AppMovCDAUException("DataSource must be of type WFS");
		}
		WFSDS dataBaseDS = (WFSDS) ds;
		if (StringUtils.isNotEmpty(url)) {
			dataBaseDS.setUrl(url);
			dataBaseDS.setFilter(filter);
			dataBaseDS.setLayer(layer);
			ds.setGeomField(geomField);
		}
		dataSourceDao.saveOrUpdate(ds);
	}

}
