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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.juntadeandalucia.sepim.daos.UserDao;
import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.User;
import es.juntadeandalucia.sepim.model.UserRole;
import es.juntadeandalucia.sepim.services.UserService;

@SuppressWarnings("deprecation")
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public User createUser(String userName, String password, String name,
			String[] roles) throws AppMovCDAUException {
		User user = userDao.findByUserName(userName);
		if (user != null) {
			throw new AppMovCDAUException("The user " + userName
					+ " already exists in the system");
		}
		user = new User();
		user.setEnabled(true);
		user.setUsername(userName);
		user.setPassword(passwordEncoder.encodePassword(password, null));
		for (int i = 0; i < roles.length; i++) {
			UserRole userRole = new UserRole(user, roles[i]);
			user.getUserRole().add(userRole);
		}
		user.setName(name);
		userDao.saveOrUpdate(user);
		return user;
	}

	@Override
	@Transactional
	public User updateUser(String userName, String password, String name)
			throws AppMovCDAUException {
		User user = userDao.findByUserName(userName);
		if (user == null) {
			throw new AppMovCDAUException("The user " + userName
					+ " doesn't exists in the system");
		}
		user.setEnabled(true);
		if (password != null) {
			user.setPassword(passwordEncoder.encodePassword(password, null));
		}
		for (UserRole userRole : user.getUserRole()) {
			userRole.setUser(null);
		}
		user.getUserRole().clear();
		user.setName(name);
		userDao.saveOrUpdate(user);
		return user;
	}

	@Override
	@Transactional
	public User updateUserRoles(String userName, String[] roles)
			throws AppMovCDAUException {
		User user = userDao.findByUserName(userName);
		if (user == null) {
			throw new AppMovCDAUException("The user " + userName
					+ " doesn't exists in the system");
		}
		for (int i = 0; i < roles.length; i++) {
			UserRole userRole = new UserRole(user, roles[i]);
			user.getUserRole().add(userRole);
		}
		userDao.saveOrUpdate(user);
		return user;
	}

	@Override
	@Transactional
	public void deleteUser(String userName) throws AppMovCDAUException {
		User user = userDao.findByUserName(userName);
		if (user == null) {
			throw new AppMovCDAUException("The user " + userName
					+ " doesn't exists in the system");
		}
		user.removeAllApps();
		userDao.delete(user);
	}

	@Override
	@Transactional
	public User getUser(String userName) throws AppMovCDAUException {
		User user = userDao.findByUserName(userName);
		if (user == null) {
			throw new AppMovCDAUException("The user " + userName
					+ " doesn't exists in the system");
		}
		return user;
	}

	@Override
	@Transactional
	public List<User> getAll() {
		return userDao.getAll();
	}

	@Override
	@Transactional
	public boolean existsUser(String userName) {
		boolean result = false;
		User user = userDao.findByUserName(userName);
		if (user != null) {
			result = true;
		}
		return result;
	}

}
