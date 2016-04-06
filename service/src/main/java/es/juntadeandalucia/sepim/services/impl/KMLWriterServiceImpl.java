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

import java.awt.Color;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.DefaultElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.DataSource;
import es.juntadeandalucia.sepim.model.LineSymbol;
import es.juntadeandalucia.sepim.model.PointSymbol;
import es.juntadeandalucia.sepim.model.PolygonSymbol;
import es.juntadeandalucia.sepim.model.Symbol;
import es.juntadeandalucia.sepim.services.KMLWriterService;
import es.juntadeandalucia.sepim.web.Item;
import es.juntadeandalucia.sepim.web.ItemFieldValue;
import es.juntadeandalucia.sepim.web.ItemWithGeometry;

/**
 * 
 * Implementación de la clase {@link KMLWriterService}
 * 
 * @author Marco Antonio Fuentelsaz Perez
 * @since 1.0.0
 */
@Service
public class KMLWriterServiceImpl implements KMLWriterService {

	/** log */
	private Logger logger = Logger.getLogger(KMLWriterServiceImpl.class);

	/** KML Namespace */
	public final static String KML_NAMESPACE = "http://earth.google.com/kml/2.2";

	/** The spec names another one for the future, change it when necessary */
	public final static String OGC_KML_NAMESPACE = "http://www.opengis.net/kml/2.2";

	/** Geometry factory */
	private GeometryFactory geomFac = new GeometryFactory();

	/** KML elements */
	private final static String DOCUMENT_ELEMENT_NAME = "Document";
	private final static String SCHEMA_ELEMENT_NAME = "Schema";
	private final static String PLACEMARK_ELEMENT_NAME = "Placemark";
	private final static String EXTENDED_DATA_ELEMENT_NAME = "ExtendedData";
	private final static String SCHEMA_DATA_ELEMENT_NAME = "SchemaData";
	private final static String SIMPLE_DATA_ELEMENT_NAME = "SimpleData";

	/** KML geometry elements */
	private final static String POINT_ELEMENT_NAME = "Point";
	private final static String LINESTRING_ELEMENT_NAME = "LineString";
	private final static String LINEARRING_ELEMENT_NAME = "LinearRing";
	private final static String POLYGON_ELEMENT_NAME = "Polygon";
	private final static String MULTIGEOMETRY_ELEMENT_NAME = "MultiGeometry";

	private final static String OUTER_BOUNDARY_ELEMENT_NAME = "outerBoundaryIs";
	private final static String INNER_BOUNDARY_ELEMENT_NAME = "innerBoundaryIs";

	private final static String COORDINATES_ELEMENT_NAME = "coordinates";

	private final static String STYLE_ELEMENT_NAME = "Style";
	private final static String LINE_STYLE_ELEMENT_NAME = "LineStyle";
	private final static String POLYGON_STYLE_ELEMENT_NAME = "PolyStyle";
	private final static String ICON_STYLE_ELEMENT_NAME = "IconStyle";

	private final static String ICON_ELEMENT_NAME = "Icon";
	private final static String HREF_ELEMENT_NAME = "href";

	private final static String COLOR_ELEMENT_NAME = "color";
	private final static String COLORMODE_ELEMENT_NAME = "colorMode";
	private final static String FILL_ELEMENT_NAME = "fill";
	private final static String OUTLINE_ELEMENT_NAME = "outline";
	private final static String WIDTH_ELEMENT_NAME = "width";

	@Override
	@Transactional
	public Document write(List<Item> items, List<Symbol> symbols, DataSource ds)
			throws AppMovCDAUException {
		// Create the document
		Document doc = DocumentHelper.createDocument();

		// Generate the root element
		Element root = writeRoot(doc);
		// Generate the Document element
		try {
			writeDocument(items, root, symbols, ds);
		} catch (Exception e) {
			logger.error("", e);
			throw new AppMovCDAUException(
					"Se ha producido un error generando el fichero KML. La causa es:"
							+ e.getMessage());
		}
		return doc;
	}

	/**
	 * Escribe el root del documento KML
	 * 
	 * @param doc
	 *            Documento
	 */
	private Element writeRoot(Document doc) {
		Element kmlRoot = new DefaultElement("kml", new Namespace("",
				"http://www.opengis.net/kml/2.2"));
		doc.add(kmlRoot);
		return kmlRoot;
	}

	private void writeDocument(List<Item> items, Element root,
			List<Symbol> symbols, DataSource ds) {

		// Generate the Document element
		Element docElement = root.addElement(DOCUMENT_ELEMENT_NAME);
		docElement.addElement("name").addText("Elementos");
		docElement.addElement("open").addText("1");
		// Write the styles
		writeStyles(symbols, docElement);
		// Write the schema
		String schemaID = writeSchema(docElement, items.iterator().next());
		for (Item item : items) {
			writeGeometry(docElement, ((ItemWithGeometry) item).getGeometry(),
					schemaID,
					"STYLE_" + ((ItemWithGeometry) item).getIdSymbol(), item);
		}
	}

	/**
	 * Color and opacity (alpha) values are expressed in hexadecimal notation.
	 * The range of values for any one color is 0 to 255 (00 to ff). For alpha,
	 * 00 is fully transparent and ff is fully opaque. The order of expression
	 * is aabbggrr, where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00
	 * to ff); rr=red (00 to ff).
	 * 
	 * @param colorHexString
	 *            Color in hexadecimal notation
	 * @param opacity
	 *            Opacity value
	 * @return
	 */
	private String getColorFromExpression(String colorHexString,
			float opacityValue) {
		Color colorValue = Color.decode(colorHexString);
		// Build the KML color
		StringBuffer kmlColor = new StringBuffer();

		// Opacity value
		String opacityHex = Integer.toHexString((int) (opacityValue * 255.0));
		if (opacityHex.length() == 1) {
			opacityHex = "0" + opacityHex; //$NON-NLS-1$
		}
		kmlColor.append(opacityHex);

		// Blue value
		String blueHex = Integer.toHexString(colorValue.getBlue());
		if (blueHex.length() == 1) {
			blueHex = "0" + blueHex; //$NON-NLS-1$
		}
		kmlColor.append(blueHex);

		// Green value
		String greenHex = Integer.toHexString(colorValue.getGreen());
		if (greenHex.length() == 1) {
			greenHex = "0" + greenHex; //$NON-NLS-1$
		}
		kmlColor.append(greenHex);

		// Red value
		String redHex = Integer.toHexString(colorValue.getRed());
		if (redHex.length() == 1) {
			redHex = "0" + redHex; //$NON-NLS-1$
		}
		kmlColor.append(redHex);
		return kmlColor.toString();
	}

	private void writeStyles(List<Symbol> symbols, Element docElement) {
		writePointStyle(docElement.addElement(STYLE_ELEMENT_NAME));
		for (Symbol symbol : symbols) {
			Element styleElement = docElement.addElement(STYLE_ELEMENT_NAME);
			styleElement.addAttribute("id", "STYLE_" + symbol.getId());
			writeStyle(styleElement, symbol);
		}
	}

	/**
	 * Permite escribir un estilo
	 * 
	 * @param styleElement
	 *            Elemento
	 * @param servicioWFS
	 *            Servicio WFS asociado
	 */
	private void writeStyle(Element styleElement, Symbol symbol) {
		if (symbol instanceof PolygonSymbol) {
			writePolygonStyle(styleElement, (PolygonSymbol) symbol);
		} else if (symbol instanceof LineSymbol) {
			writeLineStyle(styleElement, (LineSymbol) symbol);
		} else if (symbol instanceof PointSymbol
				&& StringUtils.isNotEmpty(((PointSymbol) symbol)
						.getGraphicURL())) {
			writeIconStyle(styleElement, (PointSymbol) symbol);
		}
	}

	/**
	 * Escribe un estilo de tipo puntual
	 * 
	 * @param styleElement
	 *            Elemento
	 */
	private void writePointStyle(Element styleElement) {
		// TODO: Nothing to write for now
	}

	/**
	 * Escribe un estilo de tipo poligonal asociado a un servicio WFS
	 * 
	 * @param styleElement
	 *            Elemento
	 * @param servicioWFS
	 *            Servicio WFS
	 */
	private void writePolygonStyle(Element styleElement, PolygonSymbol symbol) {
		Element lineElement = styleElement.addElement(LINE_STYLE_ELEMENT_NAME);

		lineElement.addElement(COLOR_ELEMENT_NAME).addText(
				getColorFromExpression(symbol.getStrokeColor(), 1f));
		lineElement.addElement(COLORMODE_ELEMENT_NAME).addText("normal");
		lineElement.addElement(WIDTH_ELEMENT_NAME).addText(
				Integer.toString(symbol.getStrokeWidth()));

		// Create the polygon style element
		Element polygonElement = styleElement
				.addElement(POLYGON_STYLE_ELEMENT_NAME);

		polygonElement.addElement(COLOR_ELEMENT_NAME).addText(
				getColorFromExpression(symbol.getFillColor(),
						symbol.getFillOpacity()));
		polygonElement.addElement(COLORMODE_ELEMENT_NAME).addText("normal");

		// Add the fill and outline elements
		polygonElement.addElement(FILL_ELEMENT_NAME).addText(
				StringUtils.isEmpty(symbol.getFillColor()) ? "0" : "1");
		polygonElement.addElement(OUTLINE_ELEMENT_NAME).addText(
				StringUtils.isEmpty(symbol.getStrokeColor()) ? "0" : "1");
	}

	/**
	 * Escribe un estilo de tipo lineal asociado a un servicio WFS
	 * 
	 * @param styleElement
	 *            Estilo
	 * @param servicioWFS
	 *            Servicio WFS
	 */
	private void writeLineStyle(Element styleElement, LineSymbol servicioWFS) {
		Element lineElement = styleElement.addElement(LINE_STYLE_ELEMENT_NAME);
		lineElement.addElement(COLOR_ELEMENT_NAME).addText(
				getColorFromExpression(servicioWFS.getStrokeColor(), 1f));
		lineElement.addElement(COLORMODE_ELEMENT_NAME).addText("normal");
		lineElement.addElement(WIDTH_ELEMENT_NAME).addText(
				Integer.toString(servicioWFS.getStrokeWidth()));
	}

	/**
	 * Permite construir un simbolo de tipo icono
	 * 
	 * @param styleElement
	 *            Elemento estilo
	 * @param servicioWFS
	 *            Servicio WFS
	 */
	private void writeIconStyle(Element styleElement, PointSymbol servicioWFS) {
		Element iconStyleElement = styleElement
				.addElement(ICON_STYLE_ELEMENT_NAME);
		Element iconElement = iconStyleElement.addElement(ICON_ELEMENT_NAME);
		iconElement.addElement(HREF_ELEMENT_NAME).addText(
				servicioWFS.getGraphicURL());
	}

	/**
	 * Escribe el esquema en el documento KML
	 * 
	 * @param docElement
	 *            Elemento
	 * @return String Identificador asociado al esquema
	 */
	private String writeSchema(Element docElement, Item item) {
		Element schema = docElement.addElement(SCHEMA_ELEMENT_NAME);
		String schemaName = "schemaName";
		String schemaID = "schemaID";
		schema.addAttribute("name", schemaName);
		schema.addAttribute("targetId", schemaID);
		Element simpleField = schema.addElement("SimpleField");
		for (ItemFieldValue field : item.getFields()) {
			simpleField.addAttribute("type", "string");
			simpleField.addAttribute("name", field.getField());
			Element displayName = simpleField.addElement("displayName");
			displayName.addText(field.getField());
		}
		return schemaID;
	}

	/**
	 * Escribe la geolocalización en el documento KML
	 * 
	 * @param docElement
	 *            Elemento
	 * @param title
	 *            Título asociado al elemento
	 * @param geom
	 *            Geometria
	 * @param text
	 *            Texto asociado
	 * @param schemaID
	 *            Identificador del esquema
	 */
	private void writeGeometry(Element docElement, Geometry geom,
			String schemaID, String styleID, Item item) {
		// Create the placemark element
		Element placemark = docElement.addElement(PLACEMARK_ELEMENT_NAME);

		// Add the placemark attributes
		writePlacemarkName(placemark,
				StringEscapeUtils.escapeHtml(item.getName()));
		placemark.addElement("visibility").addText("1");
		placemark.addElement("styleUrl").addText("#" + styleID);
		String description = getTableItem(item);

		placemark.addElement("description").addText(description);

		// Add the extended data element
		Element extendedData = placemark.addElement(EXTENDED_DATA_ELEMENT_NAME);

		// Add the schema data element
		Element schemaData = extendedData.addElement(SCHEMA_DATA_ELEMENT_NAME);
		schemaData.addAttribute("schemaUrl", "#" + schemaID);
		for (ItemFieldValue itemFieldValue : item.getFields()) {
			String attrName = itemFieldValue.getField();
			String value = itemFieldValue.getValue();
			Element simpleData = schemaData
					.addElement(SIMPLE_DATA_ELEMENT_NAME);
			simpleData.addAttribute("name", attrName);
			if (value != null) {
				simpleData.addText(value);
			}
		}
		// Add the geometry
		writeGeometry(placemark, geom);

	}

	private String getTableItem(Item item) {
		String table = "<table>";
		for (ItemFieldValue itemFieldValue : item.getFields()) {

			/*table += "<tr> <td>"
					+ StringEscapeUtils.escapeHtml(itemFieldValue.getField())
					+ ":</td>" + "<td>"
					+ StringEscapeUtils.escapeHtml(itemFieldValue.getValue())
					+ "</td></tr>";*/
			table += "<tr> <td>"
					+ StringEscapeUtils.escapeHtml(itemFieldValue.getField())
					+ ":</td>" + "<td>"
					+ itemFieldValue.getValue()
					+ "</td></tr>";
		}
		table += "</table>";
		return table;
	}

	/**
	 * Escribe el placemark name
	 * 
	 * @param placemark
	 *            Elemento
	 */
	private void writePlacemarkName(Element placemark, String text) {
		placemark.addElement("name").addText(text);
	}

	private void writeGeometry(Element placemark, Geometry geometry) {
		// Check if the geometry is multiple
		if (geometry instanceof GeometryCollection) {
			writeMultiGeometry(placemark, geometry);
		} else {
			// Check the geometry type
			if (geometry instanceof Point) {
				Point point = (Point) geometry;
				writePoint(placemark, point);
			} else if (geometry instanceof LinearRing) {
				LinearRing linearRing = (LinearRing) geometry;
				writeLinearRing(placemark, linearRing);
			} else if (geometry instanceof LineString) {
				LineString lineString = (LineString) geometry;
				writeLineString(placemark, lineString);

			} else if (geometry instanceof Polygon) {
				Polygon polygon = (Polygon) geometry;
				writePolygon(placemark, polygon);
			}
		}
	}

	private void writeMultiGeometry(Element placemark, Geometry geometry) {

		// Create the multigeometry element
		Element multiGeomElement = placemark
				.addElement(MULTIGEOMETRY_ELEMENT_NAME);

		// Add the multigeometry attributes

		// Write each geometry
		for (int i = 0; i < geometry.getNumGeometries(); i++) {
			writeGeometry(multiGeomElement, geometry.getGeometryN(i));
		}
	}

	/**
	 * Permite escribir una geometría de tipo puntual
	 * 
	 * @param placemark
	 *            Elemento
	 * @param point
	 *            Punto
	 */
	private void writePoint(Element placemark, Point point) {

		// Create the linearRing element
		Element pointElement = placemark.addElement(POINT_ELEMENT_NAME);

		// Add the line attributes
		pointElement.addElement("extrude").addText("0");
		pointElement.addElement("altitudeMode").addText("clampToGround");

		// Write the coordinates transformed
		writeCoordinates(pointElement, point.getCoordinateSequence());
	}

	/**
	 * Permite escribir una geometría de tipo linearRing
	 * 
	 * @param placemark
	 *            Elemento
	 * @param linearRing
	 *            Linea
	 */
	private void writeLinearRing(Element placemark, LinearRing linearRing) {

		// Create the linearRing element
		Element linearRingElement = placemark
				.addElement(LINEARRING_ELEMENT_NAME);

		// Add the line attributes
		linearRingElement.addElement("extrude").addText("0");
		linearRingElement.addElement("tessellate").addText("0");
		linearRingElement.addElement("altitudeMode").addText("clampToGround");

		// Write the coordinates transformed
		writeCoordinates(linearRingElement, linearRing.getCoordinateSequence());
	}

	/**
	 * Permite escribir una geometría de tipo lineString
	 * 
	 * @param placemark
	 *            Elemento
	 * @param lineString
	 *            Linea
	 */
	private void writeLineString(Element placemark, LineString lineString) {
		// Create the linestring element
		Element lineElement = placemark.addElement(LINESTRING_ELEMENT_NAME);

		// Add the line attributes
		lineElement.addElement("extrude").addText("0");
		lineElement.addElement("tessellate").addText("0");
		lineElement.addElement("altitudeMode").addText("clampToGround");

		// Write the coordinates transformed
		writeCoordinates(lineElement, lineString.getCoordinateSequence());
	}

	/**
	 * Permite escribir una geometría de tipo polygon
	 * 
	 * @param placemark
	 *            Elemento
	 * @param polygon
	 *            Polígono
	 */
	private void writePolygon(Element placemark, Polygon polygon) {

		// Create the polygon element
		Element polygonElement = placemark.addElement(POLYGON_ELEMENT_NAME);

		// Add the polygon attributes
		polygonElement.addElement("extrude").addText("0");
		polygonElement.addElement("tessellate").addText("0");
		polygonElement.addElement("altitudeMode").addText("clampToGround");

		// Write the outer boundary
		LineString extRing = polygon.getExteriorRing();
		Element outerBoundaryElement = polygonElement
				.addElement(OUTER_BOUNDARY_ELEMENT_NAME);
		writeLinearRing(outerBoundaryElement,
				geomFac.createLinearRing(extRing.getCoordinates()));

		// write the inner boundary
		if (polygon.getNumInteriorRing() > 0) {
			Element innerBoundaryElement = polygonElement
					.addElement(INNER_BOUNDARY_ELEMENT_NAME);
			for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
				LineString innerRing = polygon.getInteriorRingN(i);
				writeLinearRing(innerBoundaryElement,
						geomFac.createLinearRing(innerRing.getCoordinates()));
			}
		}
	}

	/**
	 * Permite escribir un conjuto de coordenadas
	 * 
	 * @param geomElement
	 *            Elemento
	 * @param coordinateSequence
	 *            Secuencia de coordenadas
	 */
	private void writeCoordinates(Element geomElement,
			CoordinateSequence sequence) {

		Element coordinatesElement = geomElement
				.addElement(COORDINATES_ELEMENT_NAME);

		// Add one line for each coordinate
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < sequence.size(); i++) {
			if (i != 0) {
				buffer.append("\n");
			}

			Coordinate coord = sequence.getCoordinate(i);
			buffer.append(coord.x);
			buffer.append(",");
			buffer.append(coord.y);

			if (!Double.isNaN(coord.z)) {
				buffer.append(",");
				buffer.append(coord.z);
			}
		}
		coordinatesElement.addText(buffer.toString());
	}
}
