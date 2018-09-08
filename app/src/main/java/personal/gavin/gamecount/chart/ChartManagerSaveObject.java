/**
 * 
 */
package personal.gavin.gamecount.chart;

import java.io.Serializable;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/**
 * @author Gavin
 * @version  2013-4-27 下午9:38:32
 */
public class ChartManagerSaveObject implements Serializable {
	private static final long serialVersionUID = -2268260136546831312L;
	public XYMultipleSeriesDataset mDataset;
	public XYMultipleSeriesRenderer mRenderer;
	public int mDefaultColor;
	public int SPACE_MAGIN;
};
