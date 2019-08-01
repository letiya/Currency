package com.letiyaha.android.currency.charts;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belle Lee on 7/31/2019.
 */

public class DrawLineChart {

    private LineChart mLineChart;
    private XAxis xAxis;
    private YAxis leftYAxis;
    private YAxis rightYAxis;
    private Legend legend;

    public DrawLineChart(List<String> xAxisValues, LineChart lineChart, List<Float> dataValueList, String chartName) {
        mLineChart = lineChart;

        initLineChart();
        showLineChart(xAxisValues, dataValueList, chartName, Color.BLUE);
    }

    private void initLineChart() {
        // Background gridline
        mLineChart.setDrawGridBackground(false);
        // Chart border
        mLineChart.setDrawBorders(true);

        mLineChart.getDescription().setEnabled(false);

        // Set up X, Y axis.
        xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f); // Set X axis starts from 0
        xAxis.setGranularity(1f);

        leftYAxis = mLineChart.getAxisLeft();
        leftYAxis.setAxisMinimum(0f); // Set left Y axis starts from 0

        rightYAxis = mLineChart.getAxisRight();
        rightYAxis.setAxisMinimum(0f); // Set right Y axis starts from 0
        rightYAxis.setEnabled(false);

        // Set up legend
        legend = mLineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        // legend position
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);

        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);

        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);

        lineDataSet.setDrawValues(false);

        if (mode == null) {
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }

    private void showLineChart(final List<String> xAxisValues, List<Float> dataValueList, String name, int color) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < dataValueList.size(); i++) {
            entries.add(new Entry(i, dataValueList.get(i)));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisValues.get((int) value % xAxisValues.size());
            }
        });

        LineData linedata = new LineData(lineDataSet);
        mLineChart.setData(linedata);
    }
}
