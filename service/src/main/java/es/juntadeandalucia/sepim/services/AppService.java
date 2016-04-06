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

import java.util.List;

import org.springframework.security.core.Authentication;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.App;
import es.juntadeandalucia.sepim.model.Entity;
import es.juntadeandalucia.sepim.web.AppWeb;

public interface AppService {

	/**
	 * Permite crear una nueva app
	 * 
	 * @param userID
	 *            Identificador del usuario que crea la aplicacion
	 * @param name
	 *            Nombre de la app
	 * @param description
	 *            Descripcion
	 * @return {@link App}
	 * @throws AppMovCDAUException
	 */
	App createApp(String userID, String name, String description,
			Integer idEntity, String wmcURL) throws AppMovCDAUException;

	/**
	 * Permite eliminar una app del sistema
	 * 
	 * @param idApp
	 *            Identificador de la app
	 * @throws AppMovCDAUException
	 *             Lanzada si no es encontrada
	 */
	void deleteApp(Integer idApp) throws AppMovCDAUException;

	/**
	 * Obtiene una app por identificador
	 * 
	 * @param idApp
	 *            Identificador de la app
	 * @return {@link App}
	 * @throws AppMovCDAUException
	 *             Lanzada si no es encontrada
	 */
	App getApp(Integer idApp) throws AppMovCDAUException;

	/**
	 * Obtiene el conjunto de apps existente en el sistema
	 * 
	 * @param userID
	 * 
	 * @return Conjunto de apps
	 */
	List<App> getAll(Authentication authentication);

	/**
	 * Permite actualizar una app
	 * 
	 * @param appID
	 *            Identificador de app
	 * @param name
	 *            Nombre
	 * @param description
	 *            Descripcion
	 * @throws AppMovCDAUException
	 */
	void updateApp(Integer appID, String name, String description, String wmcURL)
			throws AppMovCDAUException;

	void updateAppEntity(Integer appID, Integer idEntity)
			throws AppMovCDAUException;

	/**
	 * Obtiene una app por identificador para el cliente
	 * 
	 * @param idApp
	 *            Identificador de la app
	 * @return {@link App}
	 * @throws AppMovCDAUException
	 *             Lanzada si no es encontrada
	 */
	AppWeb getAppForClient(Integer idApp) throws AppMovCDAUException;

	/**
	 * Obtiene una entidad por identificador
	 * 
	 * @param idEntity
	 *            Identificador de la entidad
	 * @return
	 */
	Entity getEntity(Integer idEntity);

}
