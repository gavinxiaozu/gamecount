/**
 * 
 */
package personal.gavin.gamecount.chart;

import java.io.Serializable;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import personal.gavin.gamecount.R;
import personal.gavin.gamecount.widget.NameAndNumber;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;

/**
 * @author Gavin
 * @version  2013-4-24 下午4:35:22
 */
public class ChartManager {
	private ChartManagerSaveObject mSaveObject = new ChartManagerSaveObject();
	
	public void setSaveObject(ChartManagerSaveObject saveObject) {
		mDataset = saveObject.mDataset;
		mRenderer = saveObject.mRenderer;
		mDefaultColor = saveObject.mDefaultColor;
		SPACE_MAGIN = saveObject.SPACE_MAGIN;
	}
	
	public Serializable getSaveObject() {
		mSaveObject.mDataset = mDataset;
		mSaveObject.mRenderer = mRenderer;
		mSaveObject.mDefaultColor = mDefaultColor;
		mSaveObject.SPACE_MAGIN = SPACE_MAGIN;
		return mSaveObject;
	}
	
	private GraphicalView mChartView;
	
	/** The main dataset that includes all the series that go into a chart. */
	private XYMultipleSeriesDataset mDataset;
	/** The main renderer that includes all the renderers customizing a chart. */
	private XYMultipleSeriesRenderer mRenderer;
	
	private int mDefaultColor;
	private final PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE, PointStyle.POINT };
	private int SPACE_MAGIN;
	
	public ChartManager(Context context) {
		mDefaultColor = context.getResources().getColor(R.color.defalut_user_color);
		SPACE_MAGIN = context.getResources().getInteger(R.integer.chart_margin_space_times);
	}
	
	public GraphicalView inflence(Context context) {
		mChartView = ChartFactory.getLineChartView(context, mDataset, mRenderer);
		return mChartView;
	}
	
	public void repaint(){
		getChartView().repaint();
	}
	
	public Intent newActivityIntent(Context context, String title) {
		return ChartFactory.getLineChartIntent(context, mDataset, mRenderer,title);
	}
	
	public GraphicalView getChartView() {
		if (mChartView == null)
			throw new IllegalStateException("Chart View must inflence first.");
		return mChartView;
	}
	
	public void setSeries(ChartSerie[] datas){
		mDataset = new XYMultipleSeriesDataset();
		mRenderer = new XYMultipleSeriesRenderer();
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_NORMAL;
		int maxRound = 0;
		for (int i = 0; i < datas.length; i++) {
			//Set Series
			XYSeries series = new XYSeries(datas[i].name, 0);
			double[] xV = getRoundsNumber(datas[i].data.length);
			double[] yV = datas[i].data;
			int seriesLength = xV.length;
			if (seriesLength > maxRound) maxRound = seriesLength;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
				
				//Find Min and Max
				if(yV[k] < min) min = yV[k];
				if(yV[k] > max) max = yV[k];
			}
			mDataset.addSeries(series);
						  
			//Set Series Renderer
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(datas[i].color);
			r.setPointStyle(styles[i%styles.length]);
			r.setFillPoints(true);
			mRenderer.addSeriesRenderer(r);
	    }
		
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setPointSize(5f);
		mRenderer.setApplyBackgroundColor(true);
	    mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setMargins(new int[] { 20, 30, 15, 20 });
		
		mRenderer.setXLabels(12);
		mRenderer.setYLabels(10);
		mRenderer.setShowGrid(true);
		mRenderer.setXLabelsAlign(Align.RIGHT);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setZoomButtonsVisible(true);
		
		double xMin = 0;
		double xMax = maxRound + 1;
		double yMin = min / (SPACE_MAGIN-1) * SPACE_MAGIN - max / (SPACE_MAGIN-1);
		double yMax = max / (SPACE_MAGIN-1) * SPACE_MAGIN - min / (SPACE_MAGIN-1);
		mRenderer.setPanLimits(new double[] { xMin, xMax, yMin, yMax });
		mRenderer.setZoomLimits(new double[] { xMin, xMax, yMin, yMax });
		
		mRenderer.setXAxisMin(xMin);
		mRenderer.setXAxisMax(xMax);
		mRenderer.setYAxisMin(yMin);
		mRenderer.setYAxisMax(yMax);
	}
	
	public XYMultipleSeriesRenderer getRenderer() {
		return mRenderer;
	}
	
	public void setChartSettings(String title, String xTitle, String yTitle, int axesColor, int labelsColor) {
		mRenderer.setChartTitle(title);
		mRenderer.setXTitle(xTitle);
		mRenderer.setYTitle(yTitle);
		mRenderer.setAxesColor(axesColor);
		mRenderer.setLabelsColor(labelsColor);
	}
	
	private double[] getRoundsNumber(int round) {
		double[] result = new double[round];
		for (int i = 0; i < round; i++){
			result[i] = i + 1;
		}
		return result;
	}
}
