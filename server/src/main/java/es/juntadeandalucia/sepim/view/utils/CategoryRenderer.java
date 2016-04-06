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
package es.juntadeandalucia.sepim.view.utils;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import es.juntadeandalucia.sepim.model.Category;
import es.juntadeandalucia.sepim.services.CategoryService;

public class CategoryRenderer {
	private static final String DELETE_ICON = "resources/img/icons/delete.png";
	private static final String ADD_ICON = "resources/img/icons/add.png";
	private static final String UP_ICON = "resources/img/icons/up.png";
	private static final String DOWN_ICON = "resources/img/icons/down.png";
	private String base;
	private Integer idApplication;
	private Integer idCategorySelected;
	private Locale locale;

	private CategoryService categoryService;

	public CategoryRenderer(String base, Integer idApplication, Locale locale,
			Integer idCategorySelected, CategoryService categoryService) {
		this.base = base;
		this.idApplication = idApplication;
		this.idCategorySelected = idCategorySelected;
		this.locale = locale;
		this.categoryService = categoryService;
	}

	public String renderTree(Category category) {
		Integer idc = category.getId();
		ResourceBundle bundle = ResourceBundle.getBundle("locales/messages",
				this.locale);

		String cssClass = (idc.equals(this.idCategorySelected)) ? "class='category_selected'"
				: "";
		String tree = "<span " + cssClass + ">";

		// up/down
		tree += "<span class='up_down_icon'>";
		tree += a("category/up/" + idApplication + "/" + idc, UP_ICON,
				bundle.getString("buttons.up"), "button_up_down", "");
		tree += a("category/down/" + idApplication + "/" + idc, DOWN_ICON,
				bundle.getString("buttons.down"), "button_up_down", "");
		tree += "</span>";

		tree += a("category/delete/" + idApplication + "/" + idc, DELETE_ICON,
				bundle.getString("buttons.delete"), "",
				"return confirmDelete()")
				+
				// a("category/edit/"+idApplication+"/"+idc,EDIT_ICON,bundle.getString("buttons.edit"),"","")+
				a("category/add/" + idApplication + "/" + idc, ADD_ICON,
						bundle.getString("buttons.add"), "", "")
				+ "<a href='"
				+ base
				+ "category/edit/"
				+ idApplication
				+ "/"
				+ idc
				+ "'>"
				+ category.getName() + "</a>" + "</span>";
		// List<Category> categories = category.getSubcategories();
		List<Category> categories = categoryService.getSubcategories(category
				.getId());
		if (categories.size() > 0) {
			tree += "<ul>";
			for (Category cat : categories) {
				tree += "<li>" + renderTree(cat) + "</li>";
			}
			tree += "</ul>";
		}
		return tree;
	}

	public String a(String href, String url, String titleAlt, String cssClass,
			String onClickFunction) {
		return "<a href='" + base + href + "' class='" + cssClass
				+ "'><img title='" + titleAlt + "' alt='" + titleAlt
				+ "' src='" + base + url + "' onclick='" + onClickFunction
				+ "'/></a>";
	}

	public String renderCombobox(Category category, Long idParentCategory,
			Long idCategory, int margin) {
		if (idCategory != null && idCategory.equals(category.getId())) {
			return "";
		} else {
			String optionSelected = (idParentCategory.equals(category.getId())) ? " selected "
					: " ";
			String combobox = "<option" + optionSelected
					+ "style='margin-left:" + margin + "px' value='"
					+ category.getId() + "' title='" + category.getName()
					+ "'>" + category.getName() + "</option>";
			List<Category> categories = category.getSubcategories();
			for (Category cat : categories) {
				combobox += renderCombobox(cat, idParentCategory, idCategory,
						margin * 2);
			}
			return combobox;
		}

	}

}