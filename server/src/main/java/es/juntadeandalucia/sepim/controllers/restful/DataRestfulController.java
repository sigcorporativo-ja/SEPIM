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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.DataSource;
import es.juntadeandalucia.sepim.services.CategoryService;
import es.juntadeandalucia.sepim.services.DataBaseAccessService;
import es.juntadeandalucia.sepim.services.KMLWriterService;
import es.juntadeandalucia.sepim.web.Item;

@Controller
@RequestMapping("/api/datos")
public class DataRestfulController {

	private static final Logger logger = Logger
			.getLogger(DataRestfulController.class);

	@Autowired
	private DataBaseAccessService dataBaseAccessService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private KMLWriterService kmlWriterService;

	@Autowired
	private ServletContext context;

	@RequestMapping(value = "/{id_categoria}", method = { RequestMethod.GET }, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<Item> getData(
			@PathVariable("id_categoria") Integer idCategory,
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "offset", required = false) Integer offset,
			@RequestParam(value = "id_entidad", required = false) Integer idEntity,
			@RequestParam(value = "x", required = false) Double x,
			@RequestParam(value = "y", required = false) Double y,
			HttpServletResponse response) throws ClassNotFoundException,
			SQLException, AppMovCDAUException {
		logger.info("/api/datos/" + idCategory);

		DataSource dataSource = categoryService.getCategory(idCategory)
				.getDataSource();

		if (dataSource == null) {
			throw new AppMovCDAUException("Category without datasource");
		}

		List<Item> resultado = null;
		if (idEntity != null) {
			resultado = dataBaseAccessService.getValues(dataSource.getId(),
					limit, offset, idEntity, false, false);
		} else {
			resultado = dataBaseAccessService.getValues(dataSource.getId(),
					limit, offset, x, y, false,false);
		}

		return resultado;
	}

	@RequestMapping(value = "/kml/{id_categoria}", produces = "text/plain")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String getKML(
			@PathVariable("id_categoria") Integer idCategory,
			@RequestParam(value = "id_entidad", required = false) Integer idEntity,
			@RequestParam(value = "x", required = false) Double x,
			@RequestParam(value = "y", required = false) Double y,
			HttpServletResponse response) throws ClassNotFoundException,
			SQLException, AppMovCDAUException, IOException {
		logger.info("/api/datos/kml" + idCategory);
		DataSource dataSource = categoryService.getCategory(idCategory)
				.getDataSource();

		List<Item> resultado = null;
		if (idEntity != null) {
			resultado = dataBaseAccessService.getValues(dataSource.getId(),
					null, null, idEntity, true, true);
		} else {
			resultado = dataBaseAccessService.getValues(dataSource.getId(),
					null, null, x, y, true,true);
		}

		Document kml = kmlWriterService.write(resultado,
				dataSource.getSymbols(), dataSource);
		response.setContentType("application/vnd.google-earth.kml+xml");
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ "kml_" + idCategory + ".kml" + "\"");
		response.getOutputStream().write(kml.asXML().getBytes());
		return null;
	}

	@RequestMapping(value = "/kml/{id_categoria}/item/{id_item}", produces = "text/plain")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String getKMLForItem(
			@PathVariable("id_categoria") Integer idCategory,
			@PathVariable("id_item") String idItem, HttpServletResponse response)
			throws ClassNotFoundException, SQLException, AppMovCDAUException,
			IOException {
		logger.info("/api/datos/kml/" + idCategory + "/item/" + idItem);
		DataSource dataSource = categoryService.getCategory(idCategory)
				.getDataSource();

		List<Item> resultado = dataBaseAccessService.getValuesByItem(
				dataSource.getId(), idItem, true);
		Document kml = kmlWriterService.write(resultado,
				dataSource.getSymbols(), dataSource);
		response.setContentType("application/vnd.google-earth.kml+xml");
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ "kml_" + idCategory + ".kml" + "\"");
		response.getOutputStream().write(kml.asXML().getBytes());
		return null;
	}
}
