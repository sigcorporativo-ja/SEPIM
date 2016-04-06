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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKBConstants;

/**
 * Parse binary representation of geometries. Currently, only text rep (hexed) implementation is
 * tested. It should be easy to add char[] and CharSequence ByteGetter instances, although the
 * latter one is not compatible with older jdks. I did not implement real unsigned 32-bit integers
 * or emulate them with long, as both java Arrays and Strings currently can have only 2^31-1
 * elements (bytes), so we cannot even get or build Geometries with more than approx. 2^28
 * coordinates (8 bytes each).
 * 
 * @author markus.schaber@logi-track.com
 * @since 1.0
 */
public class WKBParser {

    /** Log */
    private static final Logger LOGGER = Logger.getLogger(WKBParser.class);

    /** M, Z y SRID */
    private boolean gHaveM, gHaveZ, gHaveS;

    /** Internal geometry factory */
    private static GeometryFactory geomFact = new GeometryFactory();

    /**
     * Parse a binary encoded geometry. Is synchronized to protect offset counter. (Unfortunately,
     * Java does not have neither call by reference nor multiple return values.)
     * 
     * @param value
     * @return Geometry
     */
    public synchronized Geometry parse( byte[] value ) {
        ByteBuffer buf = ByteBuffer.wrap(value);
        return parseGeometry(buf);
    }

    /**
     * @param data
     */
    protected void parseTypeAndSRID( ByteBuffer data ) {
        @SuppressWarnings("unused")
        byte endian = data.get(); // skip and test endian flag
        /*
         * if (endian != data.endian) { throw new IllegalArgumentException("Endian inconsistency!");
         * }
         */
        int typeword = data.getInt();

        @SuppressWarnings("unused")
        int realtype = typeword & 0x1FFFFFFF; // cut off high flag bits

        gHaveZ = (typeword & 0x80000000) != 0;
        gHaveM = (typeword & 0x40000000) != 0;
        gHaveS = (typeword & 0x20000000) != 0;

        @SuppressWarnings("unused")
        int srid = -1;

        if (gHaveS) {
            srid = data.getInt();
        }

    }

    /**
     * Parse a geometry starting at offset.
     * 
     * @param data
     * @return
     */
    protected Geometry parseGeometry( ByteBuffer data ) {
        byte endian = data.get(); // skip and test endian flag
        if (endian == 1) {
            data.order(ByteOrder.LITTLE_ENDIAN);
        }
        /*
         * if (endian != data.endian) { throw new IllegalArgumentException("Endian inconsistency!");
         * }
         */
        int typeword = data.getInt();

        int realtype = typeword & 0x1FFFFFFF; // cut off high flag bits

        boolean haveZ = (typeword & 0x80000000) != 0;
        boolean haveM = (typeword & 0x40000000) != 0;
        boolean haveS = (typeword & 0x20000000) != 0;

        int srid = -1;

        if (haveS) {
            srid = data.getInt();
        }
        Geometry result1 = null;
        switch( realtype ) {
        case WKBConstants.wkbPoint:
            result1 = parsePoint(data, haveZ, haveM);
            break;
        case WKBConstants.wkbLineString:
            result1 = parseLineString(data, haveZ, haveM);
            break;
        case WKBConstants.wkbPolygon:
            result1 = parsePolygon(data, haveZ, haveM);
            break;
        case WKBConstants.wkbMultiPoint:
            result1 = parseMultiPoint(data);
            break;
        case WKBConstants.wkbMultiLineString:
            result1 = parseMultiLineString(data);
            break;
        case WKBConstants.wkbMultiPolygon:
            result1 = parseMultiPolygon(data);
            break;
        // case WKBConstants.wkbGeometryCollection :
        // //result1 = parseCollection(data);
        // LOGGER.warn("GeometryCollection no soportada");
        // throw new IllegalArgumentException("Unknown Geometry Type!");
        // break;
        default:
            throw new IllegalArgumentException("Unknown Geometry Type!"); //$NON-NLS-1$
        }

        if (haveS) {
            result1.setSRID(srid);
        }
        return result1;
    }

    /**
     * Parse the input data and creates a {@link Point}
     * 
     * @param data
     * @param haveZ
     * @param haveM
     * @return
     */
    private Point parsePoint( ByteBuffer data, boolean haveZ, boolean haveM ) {
        double X = data.getDouble();
        double Y = data.getDouble();
        Point result;
        if (haveZ) {
            double Z = data.getDouble();
            result = geomFact.createPoint(new Coordinate(X, Y, Z));
        } else {
            result = geomFact.createPoint(new Coordinate(X, Y));
        }

        if (haveM) {
            LOGGER.warn("M no soportado. (WKBParser de Kosmo, dentro de parsePoint)"); //$NON-NLS-1$
            // double m = data.getDouble();
            // result.setM(m);
        }

        return result;
    }

    /**
     * Parse the input data and creates a {@link Coordinate}
     * 
     * @param data
     * @param haveZ
     * @param haveM
     * @return
     */
    private Coordinate parseCoordinate( ByteBuffer data, boolean haveZ, boolean haveM ) {
        double X = data.getDouble();
        double Y = data.getDouble();
        Coordinate result;
        if (haveZ) {
            double Z = data.getDouble();
            result = new Coordinate(X, Y, Z);
        } else {
            result = new Coordinate(X, Y);
        }

        if (haveM) {
            LOGGER.warn("M no soportado. (WKBParser de Kosmo, dentro de parsePoint)"); //$NON-NLS-1$
            // double m = data.getDouble();
            // result.setM(m);
        }

        return result;
    }

    /**
     * Parse an Array of "slim" Points (without endianness and type, part of LinearRing and
     * Linestring, but not MultiPoint!
     * 
     * @param haveZ
     * @param haveM
     */
    private Coordinate[] parsePointArray( ByteBuffer data, boolean haveZ, boolean haveM ) {
        int count = data.getInt();
        Coordinate[] result = new Coordinate[count];
        for( int i = 0; i < count; i++ ) {
            result[i] = parseCoordinate(data, haveZ, haveM);
        }
        return result;
    }

    /**
     * Parse the input data and creates a {@link MultiPoint}
     * 
     * @param data
     * @return
     */
    private MultiPoint parseMultiPoint( ByteBuffer data ) {
        Coordinate[] points = new Coordinate[data.getInt()];
        for( int i = 0; i < points.length; i++ ) {
            parseTypeAndSRID(data);
            points[i] = parseCoordinate(data, gHaveZ, gHaveM);
        }
        return geomFact.createMultiPoint(points);
    }

    /**
     * Parse the input data and creates a {@link LineString}
     * 
     * @param data
     * @param haveZ
     * @param haveM
     * @return
     */
    private LineString parseLineString( ByteBuffer data, boolean haveZ, boolean haveM ) {
        Coordinate[] points = parsePointArray(data, haveZ, haveM);
        return geomFact.createLineString(points);
    }

    /**
     * Parse the input data and creates a {@link Linear}
     * 
     * @param data
     * @param haveZ
     * @param haveM
     * @return
     */
    private LinearRing parseLinearRing( ByteBuffer data, boolean haveZ, boolean haveM ) {
        Coordinate[] points = parsePointArray(data, haveZ, haveM);
        return geomFact.createLinearRing(points);
    }

    /**
     * Parse the input data and creates a {@link Polygon}
     * 
     * @param data
     * @param haveZ
     * @param haveM
     * @return
     */
    private Polygon parsePolygon( ByteBuffer data, boolean haveZ, boolean haveM ) {
        int count = data.getInt();
        LinearRing extLinearRing = null;
        LinearRing[] rings = null;
        if (count > 1) {
            rings = new LinearRing[count - 1];
        }
        for( int i = 0; i < count; i++ ) {
            if (i == 0) {
                extLinearRing = parseLinearRing(data, haveZ, haveM);
            } else {
                rings[i - 1] = parseLinearRing(data, haveZ, haveM);
            }

        }
        return geomFact.createPolygon(extLinearRing, rings);
    }

    /**
     * Parse the input data and creates a {@link MultiLineString}
     * 
     * @param data
     * @return
     */
    private MultiLineString parseMultiLineString( ByteBuffer data ) {
        int count = data.getInt();
        LineString[] lines = new LineString[count];
        for( int i = 0; i < count; i++ ) {
            parseTypeAndSRID(data);
            lines[i] = parseLineString(data, gHaveZ, gHaveM);
        }
        return geomFact.createMultiLineString(lines);
    }

    /**
     * Parse the input data and creates a {@link MultiPolygon}
     * 
     * @param data
     * @return
     */
    private MultiPolygon parseMultiPolygon( ByteBuffer data ) {
        int count = data.getInt();
        Polygon[] polys = new Polygon[count];
        for( int i = 0; i < count; i++ ) {
            parseTypeAndSRID(data);
            polys[i] = parsePolygon(data, gHaveZ, gHaveM);
        }
        return geomFact.createMultiPolygon(polys);
    }

}