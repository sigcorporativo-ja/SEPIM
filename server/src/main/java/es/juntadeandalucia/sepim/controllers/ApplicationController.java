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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.App;
import es.juntadeandalucia.sepim.model.Entity;
import es.juntadeandalucia.sepim.services.AppService;
import es.juntadeandalucia.sepim.services.EntityCategoryService;
import es.juntadeandalucia.sepim.services.UserService;
import es.juntadeandalucia.sepim.validators.ApplicationValidator;

@Controller
@RequestMapping("/application/")
@SessionAttributes("application")
public class ApplicationController {

	@Autowired
	private AppService appService;

	@Autowired
	private UserService userService;

	@Autowired
	private EntityCategoryService entityCategoryService;

	private void setCommonsAttributes(Model model, Locale locale,
			Authentication authentication) {
		model.addAttribute("applications", appService.getAll(authentication));
		model.addAttribute("provincias", entityCategoryService.getEntities(1));
		model.addAttribute("users", userService.getAll());
	}

	@RequestMapping("list")
	public String list(Locale locale, Model model, Authentication authentication) {
		setCommonsAttributes(model, locale, authentication);

		return "app_list";
	}

	@RequestMapping("add")
	public String add(Locale locale, Model model, Authentication authentication) {
		setCommonsAttributes(model, locale, authentication);

		App application = new App();
		model.addAttribute("application", application);

		return "app_form";
	}

	@RequestMapping("edit/{id_application}")
	public String edit(@PathVariable("id_application") Integer idApplication,
			Locale locale, Model model, Authentication authentication)
			throws AppMovCDAUException {
		setCommonsAttributes(model, locale, authentication);

		App application = appService.getApp(idApplication);

		// gestion de la entidad
		if (application.getEntity() != null) {
			Entity appEntity = appService.getEntity(application.getEntityID());
			if (appEntity.getParentID() != null
					&& !appEntity.getParentID().equals(1)) {
				application.setIdProvincia(appEntity.getParentID());
				application.setIdMunicipio(appEntity.getId());
			} else {
				application.setIdProvincia(appEntity.getId());
			}
			application.setEnableGeographicFilter(true);
			model.addAttribute("municipios",
					entityCategoryService.getEntities(appEntity.getParentID()));
		}
		// gestion de la entidad

		model.addAttribute("application", application);

		return "app_form";
	}

	@RequestMapping("save")
	public String save(Locale locale,
			@Valid @ModelAttribute("application") App application,
			BindingResult result, HttpServletRequest request, Model model,
			SessionStatus status, Authentication authentication)
			throws AppMovCDAUException {
		if (result.hasErrors()) {
			setCommonsAttributes(model, locale, authentication);
			model.addAttribute("application", application);

			return "app_form";
		} else {
			Integer idEntity = getEntityFromApplication(application);
			if (application.getId() == null) {
				String userId = authentication.getName();
				//solo si tines ROLE_ADMIN puedes crear aplicaciones para otros usuarios
				if(request.isUserInRole("ROLE_ADMIN")){
					userId = application.getUser().getUsername();
				}
				appService.createApp(userId,
						application.getName(), application.getDescription(),
						idEntity, application.getWmcURL());
			} else {
				appService.updateApp(application.getId(),
						application.getName(), application.getDescription(),
						application.getWmcURL());
				appService.updateAppEntity(application.getId(), idEntity);

			}

			status.setComplete();

			return "redirect:/application/list";
		}
	}

	@RequestMapping("delete/{id_application}")
	public String delete(@PathVariable("id_application") Integer idApplication,
			Locale locale, Model model, Authentication authentication)
			throws AppMovCDAUException {
		setCommonsAttributes(model, locale, authentication);
		appService.deleteApp(idApplication);

		return "redirect:/application/list";
	}

	@RequestMapping("run/{id_application}")
	public String run(@PathVariable("id_application") Integer idApplication,
			Locale locale, Model model, Authentication authentication)
			throws AppMovCDAUException {
		App application = appService.getApp(idApplication);
		model.addAttribute("application", application);

		return "mobile/index";
	}

	@InitBinder("application")
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		String[] disallowedFields = { "id" };
		binder.setDisallowedFields(disallowedFields);
		binder.setValidator(new ApplicationValidator());
	}

	private Integer getEntityFromApplication(App application) {
		Integer idEntity = null;
		if (application.isEnableGeographicFilter()) {
			if (application.getIdProvincia() == null
					&& application.getIdMunicipio() == null) {
				// todas las provincias
				idEntity = 1;
			} else {
				if (application.getIdProvincia() != null) {
					idEntity = application.getIdProvincia();
				}
				if (application.getIdMunicipio() != null) {
					idEntity = application.getIdMunicipio();
				}
			}
		}
		return idEntity;
	}
}
