package Reports;

import UI.ExpTrkScreenDetails;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by aarohi on 1/25/16.
 */
public class ExpBudgetBarChart extends JPanel
{

    JFreeChart barChart;

    public ExpBudgetBarChart(JsonObject o, String title)
    {
        add(createChartPanel(o,title));
    }

    public ChartPanel createChartPanel(JsonObject o, String s)
    {
        ArrayList months;
        ArrayList categories;
        ExpTrkScreenDetails screenDetails = new ExpTrkScreenDetails();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        months = parseJson(o);

        for (int i = 0; i < months.size(); i++)
        {
            String key = months.get(i).toString();
            JsonObject mthValue = o.get(key).getAsJsonObject();

            categories = parseJson(mthValue);

            for (int j = 0; j < categories.size(); j++)
            {
                String str1 = categories.get(j).toString();
                Double value = mthValue.get(str1).getAsDouble();
                dataset.addValue(value,str1,key);
            }
        }

        barChart = ChartFactory.createBarChart(
                s,
                "Categories", "Amount",
                dataset,PlotOrientation.HORIZONTAL,
                true, true, true);


        ChartPanel panel = new ChartPanel(barChart);

        return panel;
    }

    public ArrayList parseJson(JsonObject o)
    {
        ArrayList keys = new ArrayList();

        for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            keys.add(key);
        }

        return keys;

    }
}