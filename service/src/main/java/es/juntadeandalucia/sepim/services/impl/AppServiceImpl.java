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

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.juntadeandalucia.sepim.daos.AppDao;
import es.juntadeandalucia.sepim.daos.EntityDao;
import es.juntadeandalucia.sepim.daos.UserDao;
import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.App;
import es.juntadeandalucia.sepim.model.Entity;
import es.juntadeandalucia.sepim.model.User;
import es.juntadeandalucia.sepim.services.AppService;
import es.juntadeandalucia.sepim.web.AppWeb;

@Service
public class AppServiceImpl implements AppService {

	@Autowired
	private AppDao appDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private EntityDao entityDao;

	@Override
	@Transactional
	public App createApp(String userID, String name, String description,
			Integer idEntity, String wmcURL) throws AppMovCDAUException {
		App app = new App();
		app.setDescription(description);
		app.setName(name);
		User user = userDao.findByUserName(userID);
		user.addApp(app);
		if (idEntity != null) {
			Entity entity = entityDao.get(idEntity);
			if (entity == null) {
				throw new AppMovCDAUException("The entity whith id = "
						+ idEntity + " does not exist");
			}
			entity.addApp(app);
		}
		app.setWmcURL(wmcURL);
		appDao.saveOrUpdate(app);
		return app;
	}

	@Override
	@Transactional
	public void updateApp(Integer appID, String name, String description,
			String wmcURL) throws AppMovCDAUException {
		App app = appDao.get(appID);
		if (app == null) {
			throw new AppMovCDAUException("The app whith id = " + appID
					+ " does not exist");
		}
		app.setDescription(description);
		app.setName(name);
		app.setWmcURL(wmcURL);
		if (app.getEntity() != null) {
			app.getEntity().removeApp(app);
		}
		appDao.saveOrUpdate(app);
	}

	@Override
	@Transactional
	public void updateAppEntity(Integer appID, Integer idEntity)
			throws AppMovCDAUException {
		App app = appDao.get(appID);
		if (app == null) {
			throw new AppMovCDAUException("The app whith id = " + appID
					+ " does not exist");
		}
		if (idEntity != null) {
			if (app.getEntity() != null) {
				app.getEntity().removeApp(app);
			}
			Entity entity = entityDao.get(idEntity);
			if (entity == null) {
				throw new AppMovCDAUException("The entity whith id = "
						+ idEntity + " does not exist");
			}
			entity.addApp(app);
		} else {
			if (app.getEntity() != null) {
				app.getEntity().removeApp(app);
			}
		}
		appDao.saveOrUpdate(app);
	}

	@Override
	@Transactional
	public void deleteApp(Integer idApp) throws AppMovCDAUException {
		App app = appDao.get(idApp);
		if (app == null) {
			throw new AppMovCDAUException("The app whith id = " + idApp
					+ " does not exist");
		}
		app.getUser().removeApp(app);
		if (app.getEntity() != null) {
			app.getEntity().removeApp(app);
		}
		appDao.delete(app);
	}

	@Override
	@Transactional
	public App getApp(Integer idApp) throws AppMovCDAUException {
		App app = appDao.get(idApp);
		if (app == null) {
			throw new AppMovCDAUException("The app whith id = " + idApp
					+ " does not exist");
		}
		return app;
	}

	@Override
	@Transactional
	public List<App> getAll(Authentication authentication) {
		if (isAdministrator(authentication)) {
			return appDao.getAll(null);
		}
		return appDao.getAll(authentication.getName());
	}

	private boolean isAdministrator(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication
				.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional
	public AppWeb getAppForClient(Integer idApp) throws AppMovCDAUException {
		App app = appDao.get(idApp);
		if (app == null) {
			throw new AppMovCDAUException("The app whith id = " + idApp
					+ " does not exist");
		}
		AppWeb appWeb = new AppWeb();
		appWeb.setId(app.getId());
		appWeb.setName(app.getName());
		appWeb.setWmcURL(app.getWmcURL());
		if (app.getEntity() != null) {
			appWeb.setIdEntidad(app.getEntity().getId());
			if (app.getEntity().getParentID() != null) {
				appWeb.setIdEntidadPadre(app.getEntity().getParentID());
			}
		}
		return appWeb;
	}

	@Override
	@Transactional
	public Entity getEntity(Integer idEntity) {
		return entityDao.get(idEntity);
	}

}
