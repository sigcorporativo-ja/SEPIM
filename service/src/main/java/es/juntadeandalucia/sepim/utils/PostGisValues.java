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
package es.juntadeandalucia.sepim.utils;

import es.juntadeandalucia.sepim.common.PostGisVersion;

public class PostGisValues {

	protected final static String ASSTEWKB_FUNCTION_NAME = "st_asewkb"; //$NON-NLS-1$

	/** */
	protected final static String ASEWKB_FUNCTION_NAME = "asewkb"; //$NON-NLS-1$

	/** */
	protected final static String ASBINARY_FUNCTION_NAME = "asbinary"; //$NON-NLS-1$

	/** */
	protected final static String GEOMETRY_FROM_TEXT_FUNCTION_NAME = "geometryfromtext"; //$NON-NLS-1$

	/** */
	protected final static String ST_GEOMETRY_FROM_TEXT_FUNCTION_NAME = "st_geometryfromtext"; //$NON-NLS-1$

	/** */
	protected final static String EXTENT_FUNCTION_NAME = "extent"; //$NON-NLS-1$

	/** */
	protected final static String ST_EXTENT_FUNCTION_NAME = "st_extent"; //$NON-NLS-1$

	/** */
	protected final static String SRID_FUNCTION_NAME = "srid"; //$NON-NLS-1$

	/** */
	protected final static String ST_SRID_FUNCTION_NAME = "st_srid"; //$NON-NLS-1$

	/** */
	protected final static String DISTANCE_FUNCTION_NAME = "distance"; //$NON-NLS-1$

	protected final static String ST_DISTANCE_FUNCTION_NAME = "st_distance"; //$NON-NLS-1$

	/** */

	protected String postGisVersion = PostGisVersion.POSTGIS_1_1_OR_GREATER
			.name();
	protected String queryFunction = ASSTEWKB_FUNCTION_NAME;

	/** */
	protected String geometryFromTextFunction = ST_GEOMETRY_FROM_TEXT_FUNCTION_NAME;

	/** */
	protected String extentFunction = ST_EXTENT_FUNCTION_NAME;

	/** */
	protected String sridFunction = ST_SRID_FUNCTION_NAME;

	/** */
	protected String distanceFunction = ST_DISTANCE_FUNCTION_NAME;

	public String getPostGisVersion() {
		return postGisVersion;
	}

	public void setPostGisVersion(String postGisVersion) {
		this.postGisVersion = postGisVersion;
	}

	public String getQueryFunction() {
		return queryFunction;
	}

	public void setQueryFunction(String queryFunction) {
		this.queryFunction = queryFunction;
	}

	public String getGeometryFromTextFunction() {
		return geometryFromTextFunction;
	}

	public void setGeometryFromTextFunction(String geometryFromTextFunction) {
		this.geometryFromTextFunction = geometryFromTextFunction;
	}

	public String getSridFunction() {
		return sridFunction;
	}

	public void setSridFunction(String sridFunction) {
		this.sridFunction = sridFunction;
	}

	public String getExtentFunction() {
		return extentFunction;
	}

	public void setExtentFunction(String extentFunction) {
		this.extentFunction = extentFunction;
	}

	public String getDistanceFunction() {
		return distanceFunction;
	}

	public void setDistanceFunction(String distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

}
