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
import java.util.List;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.DataBaseDS;
import es.juntadeandalucia.sepim.web.Item;

public interface DataBaseAccessService {

	/**
	 * Obtiene los items asociados a una fuente de datos y que se encuentran
	 * cercanos a un determinado punto
	 * 
	 * @param idDataSource
	 *            Identificador de la fuente de datos
	 * @param limit
	 *            Cantidad de elementos a recuperar
	 * @param offset
	 *            Desplazamiento
	 * @param x
	 *            Coordenada x
	 * @param y
	 *            Coordenada y
	 * @return Lista de valores
	 * @throws AppMovCDAUException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	List<Item> getValues(Integer idDataSource, Integer limit, Integer offset,
			double x, double y, boolean includeGeometry, boolean includeFields)
			throws AppMovCDAUException, ClassNotFoundException, SQLException;

	/**
	 * Obtiene los items asociados a una fuente de datos y que intersectan con
	 * una determinada entidad
	 * 
	 * @param idDataSource
	 *            Identificador de la fuente de datos
	 * @param limit
	 *            Cantidad de elementos a recuperar
	 * @param offset
	 *            Desplazamiento
	 * @param entityId
	 *            Identificador asociado a la entidad
	 * @return Lista de valores
	 * @throws AppMovCDAUException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	List<Item> getValues(Integer dataSourceId, Integer limit, Integer offset,
			Integer entityId, boolean includeGeometry, boolean includeFields)
			throws AppMovCDAUException, ClassNotFoundException, SQLException;

	/**
	 * Permite comprobar la fuente de datos
	 * 
	 * @param dataBaseDS
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	void checkDataBaseAccess(DataBaseDS dataBaseDS) throws SQLException,
			ClassNotFoundException;

	/**
	 * Permite obtener los valores asociados a un item
	 * 
	 * @param dataSourceId
	 * @param itemId
	 * @param includeGeometry
	 * @return
	 * @throws AppMovCDAUException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	List<Item> getValuesByItem(Integer dataSourceId, String itemId,
			boolean includeGeometry) throws AppMovCDAUException,
			ClassNotFoundException, SQLException;

}
