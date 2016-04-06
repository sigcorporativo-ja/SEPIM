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

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.juntadeandalucia.sepim.daos.UserDao;
import es.juntadeandalucia.sepim.model.User;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public User findByUserName(String username) {
		return (User) sessionFactory.getCurrentSession()
				.createQuery("from User where username=?")
				.setParameter(0, username).uniqueResult();

	}
	
	@Override
	public void saveOrUpdate(User user){
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(user);
	}
	
	@Override
	public void delete(User user) {
		sessionFactory.getCurrentSession().delete(user);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAll() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("FROM User ORDER BY username");
		return query.list();
	}

	

}