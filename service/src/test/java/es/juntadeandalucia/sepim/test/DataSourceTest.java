package es.juntadeandalucia.sepim.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.services.CategoryService;
import es.juntadeandalucia.sepim.services.DataBaseAccessService;
import es.juntadeandalucia.sepim.services.EntityCategoryService;
import es.juntadeandalucia.sepim.web.CategoryWeb;
import es.juntadeandalucia.sepim.web.EntityCategoryWeb;
import es.juntadeandalucia.sepim.web.EntityWeb;
import es.juntadeandalucia.sepim.web.Item;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ApplicationContext.xml")
public class DataSourceTest {

	@Autowired
	private DataBaseAccessService dataBaseAccessService;

	@Autowired
	private EntityCategoryService entityCategoryService;

	@Autowired
	private CategoryService categoryService;

	@Test
	public void checkDataSourceTest() {
		try {
			// -6.538510217632938 : -4.6510296615108455, 36.8421237442017 :
			// 38.19696107217902
			List<Item> values = dataBaseAccessService.getValues(18, 10, 10,
					-6.538510217632938, 36.8421237442017, false, true);
			Assert.assertNotNull(values);
		} catch (AppMovCDAUException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void checkDataSourceTest2() {
		try {
			List<Item> values = dataBaseAccessService.getValues(18, 10, 10, 4,
					false, true);
			Assert.assertNotNull(values);
		} catch (AppMovCDAUException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getEntityCategoriesTest() {
		try {
			List<EntityCategoryWeb> entityCategories = entityCategoryService
					.getEntityCategories();
			Assert.assertNotNull(entityCategories);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getEntitiesCategoriesTest() {
		try {
			List<EntityWeb> entityCategories = entityCategoryService
					.getEntities(1);
			Assert.assertNotNull(entityCategories);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getCategoriesByApp() {
		try {
			List<CategoryWeb> categoriesByApp = categoryService
					.getCategoriesForClient(2, null);
			Assert.assertNotNull(categoriesByApp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getCategoriesByCategory() {
		try {
			List<CategoryWeb> categoriesByApp = categoryService
					.getCategoriesForClient(null, 14);
			Assert.assertNotNull(categoriesByApp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getKML() {
		try {
			List<CategoryWeb> categoriesByApp = categoryService
					.getCategoriesForClient(null, 14);
			Assert.assertNotNull(categoriesByApp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
