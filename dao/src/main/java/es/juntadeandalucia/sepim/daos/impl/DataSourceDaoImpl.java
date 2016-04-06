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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.juntadeandalucia.sepim.daos.DataSourceDao;
import es.juntadeandalucia.sepim.model.DataSource;

@Repository
public class DataSourceDaoImpl implements DataSourceDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void saveOrUpdate(DataSource dataSource) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(dataSource);
	}

	@Override
	public void delete(DataSource dataSource) {
		sessionFactory.getCurrentSession().delete(dataSource);
	}

	@Override
	public DataSource get(Integer id) {
		DataSource app = null;
		if (id != null) {
			Session session = sessionFactory.getCurrentSession();
			app = (DataSource) session.get(DataSource.class, id);
		}
		return app;
	}

	@Override
	public DataSource getByCategory(Integer idCategory) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("FROM DataSource WHERE category.id = :idCategory");
		query.setInteger("idCategory", idCategory);
		DataSource result = (DataSource) query.uniqueResult();
		return result;
	}

}
