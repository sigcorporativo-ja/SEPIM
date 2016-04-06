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
package es.juntadeandalucia.sepim.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.juntadeandalucia.sepim.model.Category;
import es.juntadeandalucia.sepim.model.DataBaseDS;
import es.juntadeandalucia.sepim.model.DataSource;
import es.juntadeandalucia.sepim.model.LineSymbol;
import es.juntadeandalucia.sepim.model.PointSymbol;
import es.juntadeandalucia.sepim.model.PolygonSymbol;
import es.juntadeandalucia.sepim.model.Symbol;

public class CategoryValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return Category.class.equals(clazz);
	}

	public void validate(Object obj, Errors errors) {
		Category category = (Category) obj;

		// control de categoría
		if (category.getName() == null) {
			errors.rejectValue("name", "errors.required");
		}

		// control de datasource
		if (category.getDataSourceType().equals("dataBaseDS")) {
			validateDataSource(category.getDataSource(), errors);
		}

	}

	private void validateDataSource(DataSource dataSource, Errors errors) {
		DataBaseDS dataBaseDS = (DataBaseDS) dataSource;

		if (dataBaseDS.getUrlDataBase() == null) {
			errors.rejectValue("dataSource.urlDataBase", "errors.required");
		}
		if (dataBaseDS.getPort() == null) {
			errors.rejectValue("dataSource.port", "errors.required");
		}
		if (dataBaseDS.getDataBase() == null) {
			errors.rejectValue("dataSource.dataBase", "errors.required");
		}
		if (dataBaseDS.getSchema() == null) {
			errors.rejectValue("dataSource.schema", "errors.required");
		}
		if (dataBaseDS.getTable() == null) {
			errors.rejectValue("dataSource.table", "errors.required");
		}
		if (dataBaseDS.getUser() == null) {
			errors.rejectValue("dataSource.user", "errors.required");
		}
		if (dataBaseDS.getPassword() == null) {
			errors.rejectValue("dataSource.password", "errors.required");
		}

		if (dataBaseDS.getId() != null && !dataBaseDS.getSymbols().isEmpty()) {
			validateSymbology(dataBaseDS.getSymbols().get(0), errors);
		}

	}

	private void validateSymbology(Symbol symbology, Errors errors) {
		// punto
		if (symbology instanceof PointSymbol) {
			PointSymbol pointSymbol = (PointSymbol) symbology;
			// if(pointSymbol.getStrokeWidth() == null){
			// errors.rejectValue("dataSource.symbols[0].strokeWidth",
			// "errors.required");
			// }
			// if(pointSymbol.getStrokeColor() == null){
			// errors.rejectValue("dataSource.symbols[0].strokeColor",
			// "errors.required");
			// }
			if (pointSymbol.getGraphicURL() == null) {
				errors.rejectValue("dataSource.symbols[0].graphicURL",
						"errors.required");
			}
			// linea
		} else if (symbology instanceof LineSymbol) {
			LineSymbol lineSymbol = (LineSymbol) symbology;
			if (lineSymbol.getStrokeWidth() == null) {
				errors.rejectValue("dataSource.symbols[0].strokeWidth",
						"errors.required");
			}
			if (lineSymbol.getStrokeColor() == null) {
				errors.rejectValue("dataSource.symbols[0].strokeColor",
						"errors.required");
			}
			// poligono
		} else {
			PolygonSymbol polygonSymbol = (PolygonSymbol) symbology;
			if (polygonSymbol.getStrokeWidth() == null) {
				errors.rejectValue("dataSource.symbols[0].strokeWidth",
						"errors.required");
			}
			if (polygonSymbol.getStrokeColor() == null) {
				errors.rejectValue("dataSource.symbols[0].strokeColor",
						"errors.required");
			}
			if (polygonSymbol.getFillColor() == null) {
				errors.rejectValue("dataSource.symbols[0].fillColor",
						"errors.required");
			}
			if (polygonSymbol.getFillOpacity() == null) {
				errors.rejectValue("dataSource.symbols[0].fillOpacity",
						"errors.required");
			}
		}

	}
}
