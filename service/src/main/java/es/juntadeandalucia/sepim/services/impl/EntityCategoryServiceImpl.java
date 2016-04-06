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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.juntadeandalucia.sepim.daos.EntityCategoryDao;
import es.juntadeandalucia.sepim.daos.EntityDao;
import es.juntadeandalucia.sepim.model.Entity;
import es.juntadeandalucia.sepim.model.EntityCategory;
import es.juntadeandalucia.sepim.services.EntityCategoryService;
import es.juntadeandalucia.sepim.web.EntityCategoryWeb;
import es.juntadeandalucia.sepim.web.EntityWeb;

@Service
public class EntityCategoryServiceImpl implements EntityCategoryService {

	@Autowired
	private EntityCategoryDao dao;

	@Autowired
	private EntityDao entityDao;

	@Override
	@Transactional
	public List<EntityCategoryWeb> getEntityCategories() {
		List<EntityCategoryWeb> categories = new ArrayList<EntityCategoryWeb>();
		List<EntityCategory> all = dao.getAll();
		for (EntityCategory entityCategory : all) {
			EntityCategoryWeb entityCategoryWeb = new EntityCategoryWeb();
			entityCategoryWeb.setId(entityCategory.getId());
			entityCategoryWeb.setName(entityCategory.getName());
			categories.add(entityCategoryWeb);
		}
		return categories;
	}

	@Override
	@Transactional
	public List<EntityWeb> getEntities(Integer idEntity) {
		List<EntityWeb> entities = new ArrayList<EntityWeb>();
		if (idEntity != null) {
			Entity entity = entityDao.get(idEntity);
			for (Entity child : entity.getChilds()) {
				EntityWeb entityWeb = new EntityWeb();
				entityWeb.setId(child.getId());
				entityWeb.setName(child.getName());
				entityWeb.setIdParent(idEntity);
				entities.add(entityWeb);
			}
		}
		return entities;
	}

}
