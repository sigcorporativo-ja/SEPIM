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
package es.juntadeandalucia.sepim.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.Category;
import es.juntadeandalucia.sepim.model.DataBaseDS;
import es.juntadeandalucia.sepim.model.Field;
import es.juntadeandalucia.sepim.model.LineSymbol;
import es.juntadeandalucia.sepim.model.PointSymbol;
import es.juntadeandalucia.sepim.model.PolygonSymbol;
import es.juntadeandalucia.sepim.model.Symbol;
import es.juntadeandalucia.sepim.services.AppService;
import es.juntadeandalucia.sepim.services.CategoryService;
import es.juntadeandalucia.sepim.services.DataSourceService;
import es.juntadeandalucia.sepim.services.SimbologyService;
import es.juntadeandalucia.sepim.validators.CategoryValidator;
import es.juntadeandalucia.sepim.view.utils.CategoryUtils;

@Controller
@RequestMapping("/category/")
@SessionAttributes("category")
public class CategoryController {

	private static final Logger logger = Logger
			.getLogger(CategoryController.class);

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private DataSourceService dataSourceService;

	@Autowired
	private SimbologyService symbologyService;

	@Autowired
	private AppService AppService;

	private void setCommonsAttributes(Model model, HttpServletRequest request,
			Locale locale, Integer idApplication, Integer idCategorySelected)
			throws AppMovCDAUException {
		CategoryUtils.addTree(idApplication, categoryService, request, locale,
				idCategorySelected, model);
		model.addAttribute("application", AppService.getApp(idApplication));
	}

	@RequestMapping("list/{id_application}")
	public String list(@PathVariable("id_application") Integer idApplication,
			Locale locale, Model model, Authentication authentication,
			HttpServletRequest request) throws AppMovCDAUException {
		setCommonsAttributes(model, request, locale, idApplication, null);

		return "category_list";
	}

	@RequestMapping("add/{id_application}")
	public String add1(Locale locale,
			@PathVariable("id_application") Integer idApplication,
			HttpServletRequest request, Model model) throws Exception,
			AppMovCDAUException {

		return add2(locale, idApplication, null, request, model);
	}

	@RequestMapping("add/{id_application}/{id_parent_category}")
	public String add2(Locale locale,
			@PathVariable("id_application") Integer idApplication,
			@PathVariable("id_parent_category") Integer idParentCategory,
			HttpServletRequest request, Model model) throws AppMovCDAUException {
		setCommonsAttributes(model, request, locale, idApplication,
				idParentCategory);

		Category category = new Category();
		category.setDataSource(new DataBaseDS());
		category.setParentID(idParentCategory);
		model.addAttribute("category", category);
		model.addAttribute("id_parent_category", idParentCategory);

		return "category_form";
	}

	@RequestMapping("edit/{id_application}/{id_category}")
	public String edit(@PathVariable("id_application") Integer idApplication,
			@PathVariable("id_category") Integer idCategory,
			HttpServletRequest request, Locale locale, Model model,
			Authentication authentication) throws AppMovCDAUException {
		setCommonsAttributes(model, request, locale, idApplication, idCategory);

		Category category = categoryService.getCategory(idCategory);
		if (category.getDataSource() == null) {
			category.setDataSource(new DataBaseDS());
		} else {
			category.setDataSourceType("dataBaseDS");
		}
		logger.info("==> " + category.getId() + ", padre: "
				+ category.getParentID());

		model.addAttribute("category", category);

		return "category_form";
	}

	@RequestMapping("save/{id_application}")
	public String save(Locale locale,
			@PathVariable("id_application") Integer idApplication,
			@Valid @ModelAttribute("category") Category category,
			BindingResult result, HttpServletRequest request, Model model,
			SessionStatus status, Authentication authentication,
			@RequestParam MultipartFile logo,
			@RequestParam MultipartFile resourceDS) throws AppMovCDAUException,
			ClassNotFoundException {

		if (result.hasErrors()) {
			setCommonsAttributes(model, request, locale, idApplication, null);
			model.addAttribute("category", category);

			return "category_form";
		} else {
			if (category.getId() == null) {
				logger.info("Guardando categoria con padre = "
						+ category.getParentID());
				Integer id = null;
				try {
					id = categoryService.addCategory(
							category.getName(),
							idApplication,
							category.getParentID(),
							logo != null ? FilenameUtils.getBaseName(logo
									.getOriginalFilename())
									+ "."
									+ FilenameUtils.getExtension(logo
											.getOriginalFilename()) : null,
							category.getLogo(),
							category.getResourceDS(),
							resourceDS != null ? FilenameUtils
									.getBaseName(resourceDS
											.getOriginalFilename())
									+ "."
									+ FilenameUtils.getExtension(resourceDS
											.getOriginalFilename()) : null);
					DataBaseDS dataBaseDS = (DataBaseDS) category
							.getDataSource();
					if (dataBaseDS.getUrlDataBase() != null) {
						Integer idDataBaseDataSource = dataSourceService
								.addDataBaseDataSource(
										dataBaseDS.getUrlDataBase(),
										dataBaseDS.getPort(),
										dataBaseDS.getUser(),
										dataBaseDS.getPassword(),
										dataBaseDS.getDataBase(),
										dataBaseDS.getSchema(),
										dataBaseDS.getTable(),
										dataBaseDS.getFilter(), id);
						dataSourceService
								.setDefaultSymbol(idDataBaseDataSource);
					}
				} catch (Exception e) {
					logger.error("", e);
					if (id != null) {
						categoryService.deleteCategory(id);
					}
					result.addError(new ObjectError(
							"datasource_connection",
							"No se ha podido realizar la conexión con la Fuente de datos, verifique que la información de conexión es correcta. El error producido es: "
									+ e.getMessage()));
					logger.error("", e);
					setCommonsAttributes(model, request, locale, idApplication,
							null);
					model.addAttribute("category", category);

					return "category_form";
				}
			} else {
				logger.info("Editando categoria " + category.getId()
						+ " con padre = " + category.getParentID());

				// añadimos datasource
				if (category.getDataSource().getId() == null
						&& category.getDataSourceType().equals("dataBaseDS")) {
					logger.info("Añadiendo dataSource con id="
							+ category.getDataSource().getId()
							+ " de la categoria " + category.getId());
					DataBaseDS dataBaseDS = (DataBaseDS) category
							.getDataSource();
					try {
						Integer idDataBaseDataSource = dataSourceService
								.addDataBaseDataSource(
										dataBaseDS.getUrlDataBase(),
										dataBaseDS.getPort(),
										dataBaseDS.getUser(),
										dataBaseDS.getPassword(),
										dataBaseDS.getDataBase(),
										dataBaseDS.getSchema(),
										dataBaseDS.getTable(),
										dataBaseDS.getFilter(),
										category.getId());
						dataSourceService
								.setDefaultSymbol(idDataBaseDataSource);

					} catch (SQLException e) {
						// si hay un error guardando la bbdd se borra la
						// categoría creda
						// categoryService.deleteCategory(id);
						result.addError(new ObjectError(
								"datasource_connection",
								"No se ha podido realizar la conexión con la Fuente de datos, verifique que la información de conexión es correcta"));
						logger.error("", e);
						setCommonsAttributes(model, request, locale,
								idApplication, null);
						model.addAttribute("category", category);

						return "category_form";
					}
				}
				// borramos el datasource
				if (category.getDataSource().getId() != null
						&& !category.getDataSourceType().equals("dataBaseDS")) {
					logger.info("Borrando dataSource con id="
							+ category.getDataSource().getId()
							+ " de la categoria " + category.getId());
					dataSourceService.deleteDataSource(category.getDataSource()
							.getId());
				}
				// modificamos campos del datasource y simbología
				if (category.getDataSource().getId() != null
						&& category.getDataSourceType().equals("dataBaseDS")) {
					logger.info("Actualizando campos y la simbologia del dataSource con id="
							+ category.getDataSource().getId()
							+ " de la categoria " + category.getId());
					if (category.getDataSource().getFields().size() > 0) {
						for (Field field : category.getDataSource().getFields()) {
							dataSourceService.updateField(field.getId(),
									field.getPublicName(), field.getVisible(),
									field.getPrimaryField());
						}
					}

					Symbol symbology = category.getDataSource().getSymbols()
							.get(0);
					if (symbology instanceof PointSymbol) {
						PointSymbol pointSymbol = (PointSymbol) symbology;
						symbologyService.updateGraphicPointSymbol(
								pointSymbol.getId(),
								pointSymbol.getGraphicURL());
					} else if (symbology instanceof LineSymbol) {
						LineSymbol lineSymbol = (LineSymbol) symbology;
						symbologyService.updateLineSymbol(lineSymbol.getId(),
								lineSymbol.getStrokeColor(),
								lineSymbol.getStrokeWidth());
					} else {
						PolygonSymbol polygonSymbol = (PolygonSymbol) symbology;
						symbologyService.updatePolygonSymbol(
								polygonSymbol.getId(),
								polygonSymbol.getStrokeColor(),
								polygonSymbol.getStrokeWidth(),
								polygonSymbol.getFillColor(),
								polygonSymbol.getFillOpacity());
					}
				}
				categoryService.updateCategory(
						category.getId(),
						category.getParentID(),
						idApplication,
						category.getName(),
						logo != null ? FilenameUtils.getBaseName(logo
								.getOriginalFilename())
								+ "."
								+ FilenameUtils.getExtension(logo
										.getOriginalFilename()) : null,
						category.getLogo(),
						category.getResourceDS(),
						resourceDS != null ? FilenameUtils
								.getBaseName(resourceDS.getOriginalFilename())
								+ "."
								+ FilenameUtils.getExtension(resourceDS
										.getOriginalFilename()) : null);
			}
			status.setComplete();

			return "redirect:/category/list/" + idApplication;
		}
	}

	@RequestMapping("delete/{id_application}/{id_category}")
	public String delete(@PathVariable("id_application") Integer idApplication,
			@PathVariable("id_category") Integer idCategory, Locale locale,
			Model model, Authentication authentication)
			throws AppMovCDAUException {
		categoryService.deleteCategory(idCategory);

		return "redirect:/category/list/" + idApplication;
	}

	@RequestMapping("up/{id_application}/{id_category}")
	public String upCategory(Locale locale,
			@PathVariable("id_application") Integer idApplication,
			@PathVariable("id_category") Integer idCategory,
			HttpServletRequest request, Model model, SessionStatus status)
			throws AppMovCDAUException {
		categoryService.upCategory(idCategory);
		String referer = request.getHeader("Referer");

		// devuelve la misma pagina que llamo al controlador
		return "redirect:" + referer;
	}

	@RequestMapping("down/{id_application}/{id_category}")
	public String downCategory(Locale locale,
			@PathVariable("id_application") Integer idApplication,
			@PathVariable("id_category") Integer idCategory,
			HttpServletRequest request, Model model, SessionStatus status)
			throws AppMovCDAUException {
		categoryService.downCategory(idCategory);
		String referer = request.getHeader("Referer");

		// devuelve la misma pagina que llamo al controlador
		return "redirect:" + referer;
	}

	@RequestMapping("download_logo/{id_category}")
	public void downloadLogo(Locale locale,
			@PathVariable("id_category") Integer idCategory, Model model,
			HttpServletResponse response) throws AppMovCDAUException,
			IOException {
		Category category = categoryService.getCategory(idCategory);
		enviarDocumento(category.getLogo(), category.getLogoName(), response);
	}

	@RequestMapping("download_resource/{id_category}")
	public void downloadResource(Locale locale,
			@PathVariable("id_category") Integer idCategory, Model model,
			HttpServletResponse response) throws AppMovCDAUException,
			IOException {
		Category category = categoryService.getCategory(idCategory);
		enviarDocumento(category.getResourceDS(), category.getResourceName(),
				response);
	}

	@InitBinder("category")
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		String[] disallowedFields = { "id", "parentID" };
		binder.setDisallowedFields(disallowedFields);
		binder.setValidator(new CategoryValidator());
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
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
