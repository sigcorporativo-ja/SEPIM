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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.juntadeandalucia.sepim.daos.DataSourceDao;
import es.juntadeandalucia.sepim.daos.SimbologyDao;
import es.juntadeandalucia.sepim.exceptions.AppMovCDAUException;
import es.juntadeandalucia.sepim.model.DataSource;
import es.juntadeandalucia.sepim.model.LineSymbol;
import es.juntadeandalucia.sepim.model.PointSymbol;
import es.juntadeandalucia.sepim.model.PolygonSymbol;
import es.juntadeandalucia.sepim.model.Symbol;
import es.juntadeandalucia.sepim.services.SimbologyService;

@Service
public class SimbologyServiceImpl implements SimbologyService {

	@Autowired
	private SimbologyDao dao;

	@Autowired
	private DataSourceDao dataSourceDao;

	@Override
	@Transactional
	public PointSymbol createGraphicPointSymbol(Integer dataSourceID,
			String graphicURL) throws AppMovCDAUException {
		DataSource dataSource = dataSourceDao.get(dataSourceID);
		if (dataSource == null) {
			throw new AppMovCDAUException("The dataSource " + dataSourceID
					+ "does not exist");
		} else if (dataSource.getSymbols() != null
				&& !dataSource.getSymbols().isEmpty()) {
			throw new AppMovCDAUException("The dataSource " + dataSourceID
					+ "has already got a symbol");
		}
		PointSymbol pointSymbol = new PointSymbol();
		pointSymbol.setGraphicURL(graphicURL);
		dataSource.addSymbol(pointSymbol);
		pointSymbol.setDataSource(dataSource);
		dao.saveOrUpdate(pointSymbol);
		return pointSymbol;
	}

	@Override
	@Transactional
	public PointSymbol updateGraphicPointSymbol(Integer symbolID,
			String graphicURL) throws AppMovCDAUException {
		Symbol symbol = dao.get(symbolID);
		if (symbol == null) {
			throw new AppMovCDAUException("The symbol " + symbolID
					+ "does not exist");
		} else if (!(symbol instanceof PointSymbol)) {
			throw new AppMovCDAUException("The symbol " + symbolID
					+ "is not a point symbol");
		}
		PointSymbol pointSymbol = (PointSymbol) symbol;
		pointSymbol.setGraphicURL(graphicURL);
		dao.saveOrUpdate(pointSymbol);
		return pointSymbol;
	}

	@Override
	@Transactional
	public LineSymbol updateLineSymbol(Integer symbolID, String strokeColor,
			Integer strokeWidth) throws AppMovCDAUException {
		Symbol symbol = dao.get(symbolID);
		if (symbol == null) {
			throw new AppMovCDAUException("The symbol " + symbolID
					+ "does not exist");
		} else if (!(symbol instanceof LineSymbol)) {
			throw new AppMovCDAUException("The symbol " + symbolID
					+ "is not a line symbol");
		}
		LineSymbol lineSymbol = (LineSymbol) symbol;
		lineSymbol.setStrokeColor(strokeColor);
		lineSymbol.setStrokeWidth(strokeWidth);
		dao.saveOrUpdate(lineSymbol);
		return lineSymbol;
	}

	@Override
	@Transactional
	public LineSymbol createLineSymbol(Integer dataSourceID,
			String strokeColor, Integer strokeWidth) throws AppMovCDAUException {
		DataSource dataSource = dataSourceDao.get(dataSourceID);
		if (dataSource == null) {
			throw new AppMovCDAUException("The dataSource " + dataSourceID
					+ "does not exist");
		} else if (dataSource.getSymbols() != null
				&& !dataSource.getSymbols().isEmpty()) {
			throw new AppMovCDAUException("The dataSource " + dataSourceID
					+ "has already got a symbol");
		}
		LineSymbol lineSymbol = new LineSymbol();
		lineSymbol.setStrokeColor(strokeColor);
		lineSymbol.setStrokeWidth(strokeWidth);
		dataSource.addSymbol(lineSymbol);
		lineSymbol.setDataSource(dataSource);
		dao.saveOrUpdate(lineSymbol);
		return lineSymbol;
	}

	@Override
	@Transactional
	public PolygonSymbol createPolygonSymbol(Integer dataSourceID,
			String strokeColor, Integer strokeWidth, String fillColor,
			Float fillOpacity) throws AppMovCDAUException {
		DataSource dataSource = dataSourceDao.get(dataSourceID);
		if (dataSource == null) {
			throw new AppMovCDAUException("The dataSource " + dataSourceID
					+ "does not exist");
		} else if (dataSource.getSymbols() != null
				&& !dataSource.getSymbols().isEmpty()) {
			throw new AppMovCDAUException("The dataSource " + dataSourceID
					+ "has already got a symbol");
		}
		PolygonSymbol polygonSymbol = new PolygonSymbol();
		polygonSymbol.setStrokeColor(strokeColor);
		polygonSymbol.setStrokeWidth(strokeWidth);
		polygonSymbol.setFillColor(fillColor);
		polygonSymbol.setFillOpacity(fillOpacity);
		dataSource.addSymbol(polygonSymbol);
		polygonSymbol.setDataSource(dataSource);
		dao.saveOrUpdate(polygonSymbol);
		return polygonSymbol;
	}

	@Override
	@Transactional
	public PolygonSymbol updatePolygonSymbol(Integer symbolID,
			String strokeColor, Integer strokeWidth, String fillColor,
			Float fillOpacity) throws AppMovCDAUException {
		Symbol symbol = dao.get(symbolID);
		if (symbol == null) {
			throw new AppMovCDAUException("The symbol " + symbolID
					+ "does not exist");
		} else if (!(symbol instanceof PolygonSymbol)) {
			throw new AppMovCDAUException("The symbol " + symbolID
					+ "is not a polygon symbol");
		}
		PolygonSymbol polygonSymbol = (PolygonSymbol) symbol;
		polygonSymbol.setStrokeColor(strokeColor);
		polygonSymbol.setStrokeWidth(strokeWidth);
		polygonSymbol.setFillColor(fillColor);
		polygonSymbol.setFillOpacity(fillOpacity);
		dao.saveOrUpdate(polygonSymbol);
		return polygonSymbol;
	}

}
