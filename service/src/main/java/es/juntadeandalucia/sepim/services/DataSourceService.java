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
package es.juntadeandalucia.sepim.services;

import java.sql.SQLException;
import java.util.Map;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.DataBaseDS;
import es.juntadeandalucia.sepim.model.DataSource;
import es.juntadeandalucia.sepim.model.Table;

public interface DataSourceService {

	DataSource getDataSourceByCategory(Integer idApp)
			throws AppMovCDAUException;

	void deleteDataSource(Integer idDataSource) throws AppMovCDAUException;

	DataSource getDataSource(Integer idDataSource) throws AppMovCDAUException;

	/**
	 * Obtiene las tablas geometricas asociadas a un datasource
	 * 
	 * @param ds
	 *            Datasource
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	Map<String, Table> getGeometryTables(DataBaseDS ds)
			throws ClassNotFoundException, SQLException;

	void updateDataBaseDS(Integer idDataSource, String urlDataBase,
			Integer port, String dataBase, String user, String password,
			String schema, String table, String geomField, String filter)
			throws AppMovCDAUException, ClassNotFoundException, SQLException;

	void updateWFSDS(Integer idDataSource, String url, String layer,
			String geomField, String filter) throws AppMovCDAUException,
			ClassNotFoundException, SQLException;

	/**
	 * Permite actualizar un campo asociado a una fuente de datos
	 * 
	 * @param idField
	 *            Identificador del campo
	 * @param publicName
	 *            Nombre público
	 * @param visible
	 *            Indica si es visible
	 * @throws AppMovCDAUException
	 */
	void updateField(Integer idField, String publicName, boolean visible,
			boolean primaryField) throws AppMovCDAUException;

	Integer addWFSDataSource(String url, String layerName, String geomField,
			String filter, Integer idCategory) throws SQLException,
			ClassNotFoundException, AppMovCDAUException;

	Integer addDataBaseDataSource(String url, int port, String user,
			String password, String dataBase, String schema, String table,
			String filter, Integer idCategory) throws SQLException,
			ClassNotFoundException, AppMovCDAUException;

	void setDefaultSymbol(Integer id) throws AppMovCDAUException;
}
