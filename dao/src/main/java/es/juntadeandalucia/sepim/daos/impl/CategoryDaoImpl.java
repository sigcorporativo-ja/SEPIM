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
package es.juntadeandalucia.sepim.daos.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.juntadeandalucia.sepim.daos.CategoryDao;
import es.juntadeandalucia.sepim.model.App;
import es.juntadeandalucia.sepim.model.Category;

@Repository
public class CategoryDaoImpl implements CategoryDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void saveOrUpdate(Category category) {
		Session session = sessionFactory.getCurrentSession();
		if (category.getId() != null) { // it is an update
			session.merge(category);
		} else { // you are saving a new one
			session.saveOrUpdate(category);
		}
		session.flush();
	}

	@Override
	public void saveOrUpdate(Collection<Category> categories) {
		for (Iterator<Category> iterator = categories.iterator(); iterator
				.hasNext();) {
			Category category = iterator.next();
			saveOrUpdate(category);
		}
	}

	@Override
	public void delete(Category category) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.delete(category);
		currentSession.flush();

	}

	@Override
	public void delete(Integer idCategory) {
		sessionFactory.getCurrentSession().delete(getCategory(idCategory));
	}

	@Override
	public Category getCategory(Integer idCategory) {
		Session session = sessionFactory.getCurrentSession();
		return (Category) session.get(Category.class, idCategory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> getCategoriesByApp(Integer idApp) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("from Category WHERE app.id=? AND parent is NULL ORDER BY priority");
		query.setInteger(0, idApp);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> getCategories() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from Category");
		return query.list();
	}

	@Override
	public Integer getNextPriority(App app, Category parent) {
		Session session = sessionFactory.getCurrentSession();
		Query query = null;
		if (parent != null) {
			String hql = "SELECT count(*) FROM Category WHERE parent=?";
			query = session.createQuery(hql);
			query.setEntity(0, parent);
		} else {
			String hql = "SELECT count(*) FROM Category WHERE app=?";
			query = session.createQuery(hql);
			query.setEntity(0, app);
		}
		Number result = (Number) query.uniqueResult();
		if (result == null || result.intValue() == 0) {
			return 1;
		} else {
			return result.intValue() + 1;
		}
	}

	@Override
	public Category getCategoryByParentAndPriority(Category parent, int priority) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Category WHERE parent = :parent AND priority = :priority";
		Query query = session.createQuery(hql);
		query.setEntity("parent", parent);
		query.setInteger("priority", priority);
		List<?> results = query.list();
		Category result = null;
		if (!results.isEmpty()) {
			result = (Category) results.iterator().next();
		}
		return result;
	}

	@Override
	public Category getCategoryByEntityAndPriority(App app, int priority) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Category WHERE app = :app AND priority = :priority";
		Query query = session.createQuery(hql);
		query.setEntity("app", app);
		query.setInteger("priority", priority);
		List<?> results = query.list();
		Category result = null;
		if (!results.isEmpty()) {
			result = (Category) results.iterator().next();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> getOrphansCategories() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Category WHERE parent IS NULL AND app IS NULL";
		Query query = session.createQuery(hql);
		return query.list();
	}
}
