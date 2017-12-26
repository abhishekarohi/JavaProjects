package UI;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExpTrkExpenseDialog extends JDialog implements ActionListener
{

    //Create the labels for the screen
    private JLabel eDescLabel = new JLabel("Expense Description");
    private JLabel ePlannedAmountLabel = new JLabel("Planned Amount");
    private JLabel eActualAmountLabel = new JLabel("Actual Amount");
    private JLabel eDateLabel = new JLabel("Expense Date");
    private JLabel eStatusLabel = new JLabel("Expense Status");
    private JLabel eCategoryLabel = new JLabel("Category");

    //Create the input fields for the screen
    private JTextField eDescText = new JTextField();
    private JTextField ePlannedAmountText = new JTextField();
    private JTextField eActualAmountText = new JTextField();
    private JTextField eDateText = new JTextField();
    private JComboBox eStatusText = new JComboBox();
    private JDateChooser dateChooser = new JDateChooser();
    private JComboBox eCategory = new JComboBox();
    private JButton eSave = new JButton("Save");
    private JButton eCancel = new JButton("Cancel");
    private ButtonGroup bg = new ButtonGroup();

    private ExpTrkScreenDetails screenDetails = new ExpTrkScreenDetails();
    private Dimension fSize = new Dimension(600,300);

    private JRadioButton daily = new JRadioButton("Daily");
    private JRadioButton weekly = new JRadioButton("Weekly");
    private JRadioButton biWeekly = new JRadioButton("Bi-Weekly");
    private JRadioButton monthly = new JRadioButton("Monthly");
    private JRadioButton yearly = new JRadioButton("Yearly");
    private JTextField freqencyText = new JTextField(5);
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private JCheckBox addMore = new JCheckBox("Add More Transactions");

    private JsonObject tranData;
    private JsonObject oldTransaction;
    private ArrayList multiTrans = new ArrayList();
    private String mode;
    private ExpDetailsGUI parent;
    private boolean userAction = false;
    private String errMSG;

    public ExpTrkExpenseDialog(String m, ExpDetailsGUI p)
    {
        super(p,m,true);
        mode = m;
        parent = p;
        tranData = null;

        //Attach the action listener
        eSave.addActionListener(this);
        eCancel.addActionListener(this);

        createGUI(m);
        setDefaultProperties(m);

    }

    public ExpTrkExpenseDialog(String m, ExpDetailsGUI p, JsonObject o)
    {
        super(p,m,true);
        oldTransaction = o;
        mode = m;
        parent = p;
        //Attach the action listener
        eSave.addActionListener(this);
        eCancel.addActionListener(this);

        createGUI(m);
        loadData(oldTransaction,m);
        setDefaultProperties(m);

    }

    public boolean getAddMore()
    {
        return addMore.isSelected();

    }

    public void createGUI(String action)
    {
        Font font = eDescLabel.getFont();
        Font boldFont = new Font(font.getName(),Font.BOLD,font.getSize());

        eDescLabel.setFont(boldFont);
        eDateLabel.setFont(boldFont);
        ePlannedAmountLabel.setFont(boldFont);
        eActualAmountLabel.setFont(boldFont);
        eStatusLabel.setFont(boldFont);
        eCategoryLabel.setFont(boldFont);

        if (action.equals("Complete"))
        {
            eDescText.setEnabled(false);
            ePlannedAmountText.setEnabled(false);
            eStatusText.setSelectedItem("Complete");
            eStatusText.setEnabled(false);
            eDateText.setEnabled(false);
            eCategory.setEnabled(false);
        }
        eSave.setFont(boldFont);
        eCancel.setFont(boldFont);

        //Set Layout for the frame
        setLayout(new GridBagLayout());


        //Create a panel for putting all the data on screen
        JPanel tranPanel = new JPanel();
        JPanel tranPanel1 = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel freqButtonPanel = new JPanel();
        JPanel addMorePanel = new JPanel();

        tranPanel.setLayout(new GridBagLayout());
        tranPanel1.setLayout(new GridBagLayout());
        buttonPanel.setLayout(new GridBagLayout());
        freqButtonPanel.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints c = new GridBagConstraints();

        //Add the description label and text field
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        tranPanel1.add(eDescLabel,c);

        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 256;
        c.anchor = GridBagConstraints.LINE_START;
        tranPanel1.add(eDescText,c);
        c.ipadx = 0;

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        tranPanel1.add(eCategoryLabel,c);

        c.gridx = 0;
        c.gridy = 3;
        c.ipadx = 200;
        c.anchor = GridBagConstraints.LINE_START;
        eCategory.setEditable(true);

        eCategory.addItem("<Select Category>");
        for (int i = 0; i < parent.getCategoryList().size(); i++)
            eCategory.addItem(parent.getCategoryList().get(i));

        tranPanel1.add(eCategory,c);


        //Add the planned and actual amount label and text field
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 50;
        tranPanel.add(ePlannedAmountLabel,c);

        c.gridx = 1;
        c.gridy = 0;
        c.ipadx = 0;
        tranPanel.add(eActualAmountLabel,c);

        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 100;
        tranPanel.add(ePlannedAmountText,c);

        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 100;
        tranPanel.add(eActualAmountText,c);

        //Add Expense Date and Status
        c.gridx = 0;
        c.gridy = 3;
        c.ipadx = 50;
        tranPanel.add(eDateLabel,c);

        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 0;
        tranPanel.add(eStatusLabel,c);

        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 100;
        if (action.equals("Complete"))
            tranPanel.add(eDateText,c);
        else
            tranPanel.add(dateChooser,c);

        c.gridx = 1;
        c.gridy = 4;
        c.ipadx = 0;
        eStatusText.addItem("Pending");
        eStatusText.addItem("Completed");
        tranPanel.add(eStatusText,c);

        //Add the transaction frequency if recurring
        if (action.equals("Add Recurring"))
        {
            bg.add(daily);
            bg.add(weekly);
            bg.add(biWeekly);
            bg.add(monthly);
            bg.add(yearly);

            freqButtonPanel.add(freqencyText);
            freqButtonPanel.add(daily);
            freqButtonPanel.add(weekly);
            freqButtonPanel.add(biWeekly);
            freqButtonPanel.add(monthly);
            freqButtonPanel.add(yearly);

        }

        //Add the buttons
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        eSave.setForeground(Color.BLUE);
        buttonPanel.add(eSave,c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        eCancel.setForeground(Color.BLUE);
        buttonPanel.add(eCancel,c);

        //Add the add more check box to button panel
        addMorePanel.add(addMore);

        //Set the default properties of the frame and add the panel
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(tranPanel1,c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(tranPanel,c);

        if (action.equals("Add Recurring"))
        {
            c.gridx = 0;
            c.gridy = 5;
            add(freqButtonPanel,c);
        }

        c.gridx = 0;
        c.gridy = 6;
        c.anchor = GridBagConstraints.CENTER;
        add(buttonPanel,c);

        if (action.equals("Add New"))
        {
            c.gridy = 7;
            add(addMorePanel,c);
        }


    }

    public void setDefaultProperties(String action)
    {
        setTitle("Expense Tracker - " + action + " Transaction");
        setPreferredSize(fSize);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocation(screenDetails.getCenterPosition(fSize).width, screenDetails.getCenterPosition(fSize).height);
        setResizable(false);
        pack();
    }

    public void loadData(JsonObject o, String action)
    {
        String tDate = o.get("Date").toString().replace("\"","");

        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(tDate.substring(0,4)),
                (Integer.parseInt(tDate.substring(5,7)) - 1),
                Integer.parseInt(tDate.substring(8)));

        eDescText.setText(o.get("Item").toString().replace("\"",""));
        ePlannedAmountText.setText(o.get("PAmount").toString().replace("\"",""));
        eActualAmountText.setText(o.get("AAmount").toString().replace("\"",""));
        eDateText.setText(o.get("Date").toString().replace("\"",""));
        dateChooser.setDate(c.getTime());
        eCategory.setSelectedItem(o.get("Category").toString().replace("\"",""));

        if (action.equals("Modify"))
            eStatusText.setSelectedItem(o.get("Status").toString().replace("\"",""));
        else if (action.equals("Complete With Diff"))
            eStatusText.setSelectedItem("Completed");

    }

    public void resetAllFields()
    {

        Calendar c = Calendar.getInstance();

        eDescText.setText("");
        ePlannedAmountText.setText("");
        eActualAmountText.setText("");
        eDateText.setText("");
        dateChooser.setDate(c.getTime());
        eCategory.setSelectedIndex(0);
        eStatusText.setSelectedIndex(0);
        addMore.setSelected(false);

    }

    public String formatAmount(String amount)
    {
        if (amount.equals(""))
            amount = "0.00";
        return amount;
    }

    public boolean isEverythinAlright()
    {
        errMSG = "";
        // Validate everything is filled on the screen
        if (eDescText.getText().equals(""))
            errMSG = "Expense Description is Required";
        else if (eCategory.getSelectedItem().equals("<Select Category>") || eCategory.getSelectedItem().equals(""))
            errMSG = "Category is Required";
        else if (ePlannedAmountText.getText().equals("") || !isValidNumber(ePlannedAmountText.getText()))
            errMSG = "Invalid Planned Amount";
        else if (!eActualAmountText.getText().equals("") && !isValidNumber(eActualAmountText.getText()))
            errMSG = "Invalid Actual Amount";
        else if (dateChooser.getDate() == null)
            errMSG = "Transaction Date is Required";
        else if (mode.equals("Add Recurring"))
        {
            if (freqencyText.getText().equals(""))
                errMSG = "# of Transactions Required";
            else if (!daily.isSelected() && !weekly.isSelected() && !biWeekly.isSelected() &&
                     !monthly.isSelected() && !yearly.isSelected())
                errMSG = "Frequency selection is Required";
        }


        if (errMSG.equals(""))
            return true;
        else
            return false;
    }

    public boolean isValidNumber(String s)
    {
        try
        {
            Double.parseDouble(s);
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        return true;
    }
    public void createTransactionData(Date date)
    {

        tranData = new JsonObject();
        tranData.add("Item",new JsonPrimitive(eDescText.getText()));
        tranData.add("Category", new JsonPrimitive(eCategory.getSelectedItem().toString()));
        tranData.add("Date", new JsonPrimitive(format.format(date)));
        tranData.add("AAmount", new JsonPrimitive(formatAmount(eActualAmountText.getText())));
        tranData.add("PAmount", new JsonPrimitive(formatAmount(ePlannedAmountText.getText())));
        tranData.add("Status", new JsonPrimitive(eStatusText.getSelectedItem().toString()));


    }

    public JsonObject getTranData()
    {
        return tranData;
    }

    public ArrayList getMulTranData ()
    {
        return multiTrans;
    }

    public boolean getUserAction()
    {
        return userAction;
    }

    public void performActions()
    {
        setVisible(false);
        if (mode.equals("Add New") || mode.equals("Add Recurring"))
        {
            Date date = dateChooser.getDate();
            userAction = true;
            if (mode.equals("Add New"))
            {
                createTransactionData(date);
                parent.addNewTransactions(false);

                if (addMore.isSelected())
                {
                    resetAllFields();
                    setVisible(true);
                }

            }
            else if (mode.equals("Add Recurring"))
            {
                Calendar c = Calendar.getInstance();
                c.setTime(date);

                for (int x = 1; x <= Integer.parseInt(freqencyText.getText()); x++)
                {
                    createTransactionData(date);
                    multiTrans.add(tranData);

                    if (daily.isSelected())
                    {
                        c.add(Calendar.DAY_OF_MONTH,1);
                    }
                    else if (weekly.isSelected())
                    {
                        c.add(Calendar.DAY_OF_MONTH,7);
                    }
                    else if (biWeekly.isSelected())
                    {
                        c.add(Calendar.DAY_OF_MONTH,14);
                    }
                    else if (monthly.isSelected())
                    {
                        c.add(Calendar.MONTH, 1);
                    }
                    else if (yearly.isSelected())
                    {
                        c.add(Calendar.YEAR,1);
                    }

                    date = c.getTime();
                }

                parent.addNewTransactions(true);
            }

            dispose();
        }
        else if (mode.equals("Modify") || mode.equals("Complete With Diff") || mode.equals("Copy to New"))
        {

            Date date = dateChooser.getDate();
            userAction = true;
            createTransactionData(date);

            if(mode.equals("Modify") || mode.equals("Complete With Diff"))
            {
                String id = oldTransaction.get("Transaction id").toString().replace("\"","");
                tranData.add("Transaction id",new JsonPrimitive(id));
                parent.modifyTransaction(tranData);
            }
            else if (mode.equals("Copy to New"))
                parent.addNewTransactions(false);

            dispose();
        }
    }

    public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("Cancel"))
            {
                userAction = false;
                parent.addNewTransactions(false);
                dispose();
            }
            else if (e.getActionCommand().equals("Save"))
            {
                if (isEverythinAlright())
                    performActions();
                else
                    JOptionPane.showMessageDialog(this,errMSG);

            }

        }
    }
