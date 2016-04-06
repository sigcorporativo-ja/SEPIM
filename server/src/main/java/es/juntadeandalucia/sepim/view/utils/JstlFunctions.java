package es.juntadeandalucia.sepim.view.utils;

import es.juntadeandalucia.sepim.model.Category;

public class JstlFunctions {
	 public static String renderTree(CategoryRenderer categoryRender, Category category){
	     return categoryRender.renderTree(category);
	 }
}
