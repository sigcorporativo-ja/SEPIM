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

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.Category;
import es.juntadeandalucia.sepim.web.CategoryWeb;

public interface CategoryService {

	/**
	 * Permite anyadir una nueva categoria
	 * 
	 * @param name
	 *            Nombre asociado a la categoria
	 * @param idApp
	 *            Identificador de la app
	 * @param idParent
	 *            Identificador de la categoria padre
	 * @return Identificador de la nueva categoria
	 * @throws AppMovCDAUException
	 */
	Integer addCategory(String name, Integer idApp, Integer idParent,
			String logoName, byte[] logo, byte[] resource, String resourceName)
			throws AppMovCDAUException;

	/**
	 * Permite obtener una categoria por identificador
	 * 
	 * @param idCategory
	 *            Identificador de la categoria
	 * @return
	 * @throws AppMovCDAUException
	 */
	Category getCategory(Integer idCategory) throws AppMovCDAUException;

	/**
	 * Obtiene una lista de categorias asociadas a una app
	 * 
	 * @param idApp
	 *            Identificador de la app
	 * @return Lista de categorias
	 */
	List<Category> categoriesByApp(Integer idApp);

	/**
	 * Permite eliminar una categoria
	 * 
	 * @param idCategory
	 *            Identificador de la categoria
	 * @throws AppMovCDAUException
	 *             Lanzada si no se encuentra
	 */
	void deleteCategory(Integer idCategory) throws AppMovCDAUException;

	/**
	 * Permite eliminar las categorias huerfanas
	 */
	void deleteOrphans();

	/**
	 * Permite actualizar una categoria
	 * 
	 * @param idCategory
	 *            Identificador de la categoria
	 * @param idParent
	 *            Identificador de la categoria padre
	 * @param idApp
	 *            Identificador de la app
	 * @param name
	 *            Nombre asociado a la categoria
	 * @throws AppMovCDAUException
	 */
	void updateCategory(Integer idCategory, Integer idParent, Integer idApp,
			String name, String logoName, byte[] logo, byte[] resource,
			String resourceName) throws AppMovCDAUException;

	/**
	 * Permite subir la prioridad asociada a una categoria
	 * 
	 * @param idCategory
	 *            Identificador de la categoria
	 * @throws AppMovCDAUException
	 */
	void upCategory(Integer idCategory) throws AppMovCDAUException;

	/**
	 * Permite bajar la prioridad asociada a una categoria
	 * 
	 * @param idCategory
	 *            Identificador de la categoria
	 * @throws AppMovCDAUException
	 */
	void downCategory(Integer idCategory) throws AppMovCDAUException;

	List<CategoryWeb> getCategoriesForClient(Integer idApp, Integer idParent);

	/**
	 * Obtiene las subcategorias asociadas a una categoria padre
	 * 
	 * @param id
	 *            Identificador
	 * @return
	 */
	List<Category> getSubcategories(Integer id);

}
