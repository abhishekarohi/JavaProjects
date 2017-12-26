package Reports;

import javax.swing.*;
import java.util.*;
import com.google.gson.*;
import UI.*;
import java.awt.*;
import java.util.Map.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Created by aarohi on 1/25/16.
 */
public class ExpPieChart extends JPanel
{

    public ExpPieChart(JsonObject o, String title)
    {
        ExpTrkScreenDetails screenDetails = new ExpTrkScreenDetails();
        add(createChartPanel(o,title));
        //setPreferredSize(new Dimension(screenDetails.getScreenWidth() - 40,400));
    }

    public ChartPanel createChartPanel(JsonObject o, String s)
    {
        ArrayList categories;
        ExpTrkScreenDetails screenDetails = new ExpTrkScreenDetails();
        DefaultPieDataset dataset = new DefaultPieDataset();

        categories = parseJson(o);

        for (int i = 0; i < categories.size(); i++)
        {
            String key = categories.get(i).toString();
            double value = o.get(key).getAsDouble();
            dataset.setValue(key,value);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                s,
                dataset,
                true,
                true,
                false);

        ChartPanel panel = new ChartPanel(chart);

        return panel;
    }

    public ArrayList parseJson(JsonObject o)
    {
        ArrayList keys = new ArrayList();

        for (Entry<String, JsonElement> entry : o.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            keys.add(key);
        }

        return keys;

    }
}
