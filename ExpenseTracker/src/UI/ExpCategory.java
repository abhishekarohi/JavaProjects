package UI;

import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by aarohi on 2/16/16.
 */
public class ExpCategory implements ChangeListener, ActionListener{
    private JSlider slider;
    private JComboBox category;
    private JLabel budgetCategory;
    private JButton delButton;
    private JLabel bAmount;
    private ExpDetailsGUI parent;
    private boolean isNew;

    public ExpCategory(ExpDetailsGUI p) {

        parent = p;

        budgetCategory = null;

        ImageIcon icon = new ImageIcon(getClass().getResource("/Images/remove.png"));
        delButton = new JButton(icon);
        delButton.setActionCommand("Delete Budget");
        delButton.setPreferredSize(new Dimension(20, 20));
        delButton.addActionListener(this);

        slider = new JSlider(JSlider.HORIZONTAL,50,1500,50);
        slider.addChangeListener(this);
        slider.setPreferredSize(new Dimension(200, 50));

        category = new JComboBox();
        category.setPreferredSize(new Dimension(150,20));

        bAmount = new JLabel("$50");
        bAmount.setPreferredSize(new Dimension(50, 20));
        isNew = true;

    }

    public ExpCategory(JsonObject o) {

        parent = null;
        category = null;

        ImageIcon icon = new ImageIcon(getClass().getResource("/Images/remove.png"));
        delButton = new JButton(icon);
        delButton.setActionCommand("Delete Budget");
        delButton.setPreferredSize(new Dimension(20, 20));
        delButton.addActionListener(this);

        slider = new JSlider(JSlider.HORIZONTAL,50,1500,o.get("Amount").getAsInt());
        slider.addChangeListener(this);
        slider.setPreferredSize(new Dimension(200, 50));

        budgetCategory = new JLabel();
        budgetCategory.setPreferredSize(new Dimension(150,20));
        budgetCategory.setText(o.get("Category").toString().replace("\"",""));

        bAmount = new JLabel("$" + o.get("Amount").toString());
        bAmount.setPreferredSize(new Dimension(50, 20));

        isNew = false;

    }

    public int getSliderAmount()
    {
        return slider.getValue();
    }

    public void setParent(ExpDetailsGUI g)
    {
        parent = g;
    }

    public JPanel getCategoryPanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());

        if(budgetCategory != null)
            panel.add(budgetCategory);
        else
            panel.add(category);

        panel.add(slider);
        panel.add(bAmount);
        panel.add(delButton);

        panel.setPreferredSize(new Dimension(200,100));
        return panel;
    }

    public void setCategory(ArrayList c)
    {
        category.addItem("");
        for (int i = 0; i < c.size(); i++)
        {
            category.addItem(c.get(i).toString());
        }

    }

    public boolean isNewCategory()
    {
        return isNew;
    }

    public boolean isCategorySelected()
    {
        if (isNewCategory() && category.getSelectedItem().equals(""))
            return false;
        else
            return true;
    }

    public void disableCategory()
    {
        if (category != null)
            category.setEnabled(false);
    }


    public String getCategory()
    {
        if (category != null)
            return category.getSelectedItem().toString();
        else
            return budgetCategory.getText();
    }

    public void actionPerformed (ActionEvent e)
    {
        if (e.getActionCommand().equals(delButton.getActionCommand()))
        {
            parent.removeCategory(this);
        }
    }
    public void stateChanged(ChangeEvent e)
    {
        JSlider s = (JSlider) e.getSource();
        int value = s.getValue();
        bAmount.setText("$" + value);
        parent.setChangeExists();
        parent.enableCategorySave();
    }
}
