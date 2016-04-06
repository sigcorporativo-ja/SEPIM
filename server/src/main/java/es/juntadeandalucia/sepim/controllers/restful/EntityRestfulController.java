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
package es.juntadeandalucia.sepim.controllers.restful;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.juntadeandalucia.sepim.services.EntityCategoryService;
import es.juntadeandalucia.sepim.web.EntityCategoryWeb;
import es.juntadeandalucia.sepim.web.EntityWeb;

@Controller
@RequestMapping("/api/entidades")
public class EntityRestfulController {

	private static final Logger logger = Logger
			.getLogger(EntityRestfulController.class);

	@Autowired
	private EntityCategoryService entityCategoryService;

	@RequestMapping(value = "/categorias", method = { RequestMethod.GET }, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<EntityCategoryWeb> getEntityCategories(
			HttpServletResponse response) {
		logger.info("/api/entidades/categorias");
		List<EntityCategoryWeb> resultado = entityCategoryService
				.getEntityCategories();
		return resultado;
	}

	@RequestMapping(value = "/{id_categoria}", method = { RequestMethod.GET }, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<EntityWeb> getEntities(
			@PathVariable("id_categoria") Integer idEntityCategory,
			HttpServletResponse response) {
		logger.info("/api/entidades/" + idEntityCategory);
		List<EntityWeb> resultado = entityCategoryService
				.getEntities(idEntityCategory);
		return resultado;
	}

	@RequestMapping(value = "/{id_entity}/hijos", method = { RequestMethod.GET }, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<EntityWeb> getChilds(
			@PathVariable("id_entity") Integer idEntity,
			HttpServletResponse response) {
		return entityCategoryService.getEntities(idEntity);
	}

}
