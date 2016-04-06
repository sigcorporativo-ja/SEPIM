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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.Category;
import es.juntadeandalucia.sepim.services.CategoryService;
import es.juntadeandalucia.sepim.web.CategoryWeb;

@Controller
@RequestMapping("/api/categorias")
public class CategoryRestfulController {

	private static final Logger logger = Logger
			.getLogger(CategoryRestfulController.class);

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ApplicationContext appContext;

	@RequestMapping(value = "", method = { RequestMethod.GET }, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<CategoryWeb> getCategories(
			@RequestParam(value = "id_aplicacion", required = false) Integer idApplication,
			@RequestParam(value = "id_categoria", required = false) Integer idCategory,
			HttpServletResponse response) {
		logger.info("/api/categorias" + ">> id_aplicacion: " + idApplication
				+ ", id_categoria: " + idCategory);
		List<CategoryWeb> resultado = categoryService.getCategoriesForClient(
				idApplication, idCategory);

		return resultado;
	}

	@RequestMapping(value = "/{id_categoria}/logo", method = RequestMethod.GET)
	public void getLogo(@PathVariable("id_categoria") Integer idCategoria,
			HttpServletResponse response) throws AppMovCDAUException,
			IOException {
		Category category = categoryService.getCategory(idCategoria);
		byte[] logoFile = null;
		if (category.getLogo() != null) {
			logoFile = category.getLogo();
		} else {
			Resource resource = appContext
					.getResource("classpath:images/none.png");
			logoFile = FileUtils.readFileToByteArray(resource.getFile());

		}

		enviarDocumento(logoFile,
				category.getLogoName() != null ? category.getLogoName()
						: "none.png", response);
	}

	@RequestMapping(value = "/{id_categoria}/resource", method = RequestMethod.GET)
	public void getResource(@PathVariable("id_categoria") Integer idCategoria,
			HttpServletResponse response) throws AppMovCDAUException,
			IOException {
		Category category = categoryService.getCategory(idCategoria);
		enviarDocumento(category.getResourceDS(),
				category.getResourceName() != null ? category.getResourceName()
						: "resource_" + System.currentTimeMillis(), response);
	}

	private void enviarDocumento(byte[] file, String fileName,
			HttpServletResponse response) throws IOException {
		InputStream inputStream = null;
		OutputStream outStream = null;
		try {
			if (file == null) {
				response.setStatus(HttpStatus.NO_CONTENT.value());
			} else {
				// get MIME type of the file
				String mimeType = "application/octet-stream";

				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength(file.length);

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format(
						"attachment; filename=\"%s\"", fileName);
				response.setHeader(headerKey, headerValue);

				inputStream = new ByteArrayInputStream(file);
				// get output stream of the response
				outStream = response.getOutputStream();

				byte[] buffer = new byte[1024];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outStream != null) {
				outStream.close();
			}

		}
	}

}
