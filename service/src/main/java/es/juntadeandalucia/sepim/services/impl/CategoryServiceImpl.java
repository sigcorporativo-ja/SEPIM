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
package es.juntadeandalucia.sepim.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.juntadeandalucia.sepim.daos.AppDao;
import es.juntadeandalucia.sepim.daos.CategoryDao;
import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.App;
import es.juntadeandalucia.sepim.model.Category;
import es.juntadeandalucia.sepim.services.CategoryService;
import es.juntadeandalucia.sepim.web.CategoryWeb;

@Service
public class CategoryServiceImpl implements CategoryService {

	private Logger logger = Logger.getLogger(CategoryServiceImpl.class);

	@Autowired
	private CategoryDao dao;

	@Autowired
	private AppDao appDao;

	@Override
	@Transactional
	public Integer addCategory(String name, Integer idApp, Integer idParent,
			String logoName, byte[] logo, byte[] resource, String resourceName)
			throws AppMovCDAUException {
		Category category = new Category();
		category.setName(name);
		Category parent = null;
		App app = null;
		if (idParent == null && idApp == null) {
			throw new AppMovCDAUException("Parent category and app are null");
		} else if (idParent != null && idApp == null) {
			throw new AppMovCDAUException(
					"Parent category and app are not null");
		} else {
			if (idParent != null) {
				parent = dao.getCategory(idParent);
				if (parent == null) {
					throw new AppMovCDAUException("Parent category " + idParent
							+ " does not exist");
				}

				Integer priority = dao.getNextPriority(app, parent);
				category.setPriority(priority);
				parent.addSubcategory(category);
			} else {
				app = appDao.get(idApp);
				if (app == null) {
					throw new AppMovCDAUException("App with id " + idApp
							+ " does not exist");
				}
				Integer priority = dao.getNextPriority(app, parent);
				category.setPriority(priority);
				app.addCategory(category);
			}
			if (logo != null && logo.length > 0) {
				category.setLogo(logo);
				category.setLogoName(logoName);
			}
			if (resource != null && resource.length > 0) {
				category.setResourceName(resourceName);
				category.setResourceDS(resource);
			}
			dao.saveOrUpdate(category);
		}

		return category.getId();
	}

	@Transactional
	@Override
	public Category getCategory(Integer idCategory) throws AppMovCDAUException {
		Category category = dao.getCategory(idCategory);
		if (category == null) {
			throw new AppMovCDAUException("Category with id " + idCategory
					+ " does not exist");
		}
		return category;
	}

	@Override
	@Transactional
	public List<Category> categoriesByApp(Integer idApp) {
		return dao.getCategoriesByApp(idApp);
	}

	@Override
	@Transactional
	public void deleteCategory(Integer idCategory) throws AppMovCDAUException {
		Category category = dao.getCategory(idCategory);
		if (category == null) {
			throw new AppMovCDAUException("Category with id " + idCategory
					+ " does not exist");
		}
		App app = category.getApp();
		if (app != null) {
			app.removeCategory(category);
		}
		Category parent = category.getParent();
		if (parent != null) {
			parent.removeSubcategory(category);
		}
		category.removeAllSubcategories();
		dao.delete(category);
		reorderCategoriesPriorities(app, parent, null);
	}

	@Override
	@Transactional
	public void deleteOrphans() {
		List<Category> orphansCategories = dao.getOrphansCategories();
		for (Category clientCategory : orphansCategories) {
			dao.delete(clientCategory);
		}
	}

	@Override
	@Transactional
	public void updateCategory(Integer idCategory, Integer idParent,
			Integer idApp, String name, String logoName, byte[] logo,
			byte[] resource, String resourceName) throws AppMovCDAUException {
		Category category = dao.getCategory(idCategory);
		if (category == null) {
			throw new AppMovCDAUException("Category with id " + idCategory
					+ " does not exist");
		}
		Category newParent = null;
		App app = null;
		Category oldParent = null;
		if (idParent != null) {
			if (category.getParentID() == null
					|| category.getParentID().intValue() != idParent.intValue()) {
				newParent = dao.getCategory(idParent);
				if (newParent == null) {
					throw new AppMovCDAUException("Parent category " + idParent
							+ " does not exist");
				}
				if (category.getParent() != null) {
					oldParent = category.getParent();
					oldParent.removeSubcategory(category);
				} else {
					app = category.getApp();
					app.removeCategory(category);
				}
				newParent.addSubcategory(category);
			}
		} else if (idApp != null) {
			app = appDao.get(idApp);
			if (app == null) {
				throw new AppMovCDAUException("App with id " + idApp
						+ " does not exist");
			}
			if (category.getParent() != null) {
				category.getParent().removeSubcategory(category);
			}
			app.addCategory(category);
		}
		if (logo != null && logo.length > 0) {
			category.setLogo(logo);
			category.setLogoName(logoName);
		}
		if (resource != null && resource.length > 0) {
			category.setResourceName(resourceName);
			category.setResourceDS(resource);
		}
		category.setName(name);
		dao.saveOrUpdate(category);
	}

	@Override
	@Transactional
	public void upCategory(Integer idCategory) throws AppMovCDAUException {
		Category category = dao.getCategory(idCategory);
		if (category == null) {
			throw new AppMovCDAUException("Category " + idCategory
					+ " does not exist");
		}
		if (category.getPriority() == 1) {
			return;
		}

		Category nextCategory = getCategoryByPriority(category.getParent(),
				category.getApp(), category.getPriority() - 1);
		if (nextCategory != null) {
			nextCategory.setPriority(category.getPriority());
			category.setPriority(category.getPriority() - 1);
			dao.saveOrUpdate(category);
			dao.saveOrUpdate(nextCategory);
		}
	}

	@Override
	@Transactional
	public void downCategory(Integer idCategory) throws AppMovCDAUException {
		Category clientCategory = dao.getCategory(idCategory);
		if (clientCategory == null) {
			throw new AppMovCDAUException("Category " + idCategory
					+ " does not exist");
		}
		Category previusCategory = getCategoryByPriority(
				clientCategory.getParent(), clientCategory.getApp(),
				clientCategory.getPriority() + 1);
		if (previusCategory != null) {
			previusCategory.setPriority(clientCategory.getPriority());
			clientCategory.setPriority(clientCategory.getPriority() + 1);
			dao.saveOrUpdate(clientCategory);
			dao.saveOrUpdate(previusCategory);
		}
	}

	private void reorderCategoriesPriorities(App app, Category oldParent,
			Category newParent) {
		if (app != null) {
			reorderCategories(dao.getCategoriesByApp(app.getId()));
		}
		if (oldParent != null) {
			reorderCategories(oldParent.getSubcategories());
		}
		if (newParent != null) {
			reorderCategories(newParent.getSubcategories());
		}

	}

	private void reorderCategories(List<Category> categories) {
		int priority = 1;
		for (Category category : categories) {
			category.setPriority(priority++);
			dao.saveOrUpdate(category);
		}
	}

	private Category getCategoryByPriority(Category parent, App app,
			int priority) {
		Category result = null;
		if (parent != null) {
			result = dao.getCategoryByParentAndPriority(parent, priority);
		} else if (app != null) {
			result = dao.getCategoryByEntityAndPriority(app, priority);
		}
		return result;
	}

	@Override
	@Transactional
	public List<CategoryWeb> getCategoriesForClient(Integer idApp,
			Integer idParent) {
		List<CategoryWeb> categories = new ArrayList<CategoryWeb>();
		if (idApp != null) {
			List<Category> categoriesByApp = dao.getCategoriesByApp(idApp);
			for (Category category : categoriesByApp) {
				categories.add(toWeb(category));
			}
		} else if (idParent != null) {
			Category categoryParent = dao.getCategory(idParent);
			if (categoryParent != null) {
				for (Category category : categoryParent.getSubcategories()) {
					categories.add(toWeb(category));
				}
			}
		}
		return categories;
	}

	private CategoryWeb toWeb(Category category) {
		CategoryWeb categoryWeb = new CategoryWeb();
		categoryWeb.setId(category.getId());
		categoryWeb.setLast(category.getSubcategories().isEmpty());
		categoryWeb.setName(category.getName());
		categoryWeb.setParentID(category.getParentID());
		categoryWeb.setPriority(category.getPriority());
		return categoryWeb;
	}

	@Override
	@Transactional
	public List<Category> getSubcategories(Integer idCategory) {
		Category category = dao.getCategory(idCategory);
		List<Category> categories = new ArrayList<Category>();
		if (category == null) {
			logger.error("Category " + idCategory + " does not exist");
		} else {
			for (Category child : category.getSubcategories()) {
				child.setParentID(idCategory);
				categories.add(child);
			}
		}
		return categories;
	}
}
