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
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
import es.juntadeandalucia.sepim.model.User;
import es.juntadeandalucia.sepim.model.UserRole;
import es.juntadeandalucia.sepim.services.UserService;
import es.juntadeandalucia.sepim.validators.UserValidator;

@Controller
@RequestMapping("/user/")
@SessionAttributes("user")
public class UserController {

	@Autowired
	private UserService userService;

	private void setCommonsAttributes(Model model, Locale locale,
			Authentication authentication) {
		model.addAttribute("users", userService.getAll());
		model.addAttribute("currentUser", authentication.getName());

	}

	@RequestMapping("list")
	public String list(Locale locale, Model model, Authentication authentication) {
		setCommonsAttributes(model, locale, authentication);

		return "user_list";
	}

	@RequestMapping("add")
	public String add(Locale locale, Model model, Authentication authentication) {
		setCommonsAttributes(model, locale, authentication);

		User user = new User();
		user.setNewUser(true);
		model.addAttribute("user", user);

		return "user_form";
	}

	@RequestMapping("edit/{userName}")
	public String edit(@PathVariable("userName") String userName,
			Locale locale, Model model, Authentication authentication)
			throws AppMovCDAUException {
		setCommonsAttributes(model, locale, authentication);
		User user = userService.getUser(userName);
		user.setNewUser(false);
		for (UserRole role : user.getUserRole()) {
			user.setProfile(role.getRole());
		}
		model.addAttribute("user", user);

		return "user_form";
	}

	@RequestMapping("save")
	public String save(Locale locale, @Valid @ModelAttribute("user") User user,
			BindingResult result, HttpServletRequest request, Model model,
			SessionStatus status, Authentication authentication)
			throws AppMovCDAUException {
		if (result.hasErrors()) {
			setCommonsAttributes(model, locale, authentication);
			model.addAttribute("user", user);

			return "user_form";
		} else {
			String[] rol = { user.getProfile() };
			if (user.getNewUser() == true) {
				userService.createUser(user.getUsername(), user.getPassword(),
						user.getName(), rol);
			} else {
				userService.updateUser(user.getUsername(), user.getPassword(),
						user.getName());
				userService.updateUserRoles(user.getUsername(), rol);
			}
			status.setComplete();

			return "redirect:/user/list";
		}
	}

	@RequestMapping("delete/{userName}")
	public String delete(@PathVariable("userName") String userName,
			Locale locale, Model model, Authentication authentication)
			throws AppMovCDAUException {
		setCommonsAttributes(model, locale, authentication);
		userService.deleteUser(userName);

		return "redirect:/user/list";
	}

	@InitBinder("user")
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		String[] disallowedFields = { "id" };
		binder.setDisallowedFields(disallowedFields);
		binder.setValidator(new UserValidator(userService));
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
