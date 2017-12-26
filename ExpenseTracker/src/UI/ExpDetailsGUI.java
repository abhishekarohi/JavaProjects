package UI;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import java.util.ArrayList;
import java.text.*;
import java.util.Calendar;
import javax.swing.table.*;
import Reports.*;

import Data.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.swing.filechooser.FileNameExtensionFilter;

public class ExpDetailsGUI extends JFrame implements ActionListener {
    private ExpTrkExpenseDialog expTrkExpenseDialog;
    private ArrayList tranList = new ArrayList();
    private ArrayList budgetList = new ArrayList();
    private ExpDetailsDAO expDetailsDAO = new ExpDetailsDAO();
    private JScrollPane scrollPane;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTable dataTable;
    private JLabel yearEndprojBal = new JLabel("Year End Projected: $");
    private JLabel ytdCurr = new JLabel("YTD Current: $");
    private JLabel ytdProjBal = new JLabel("YTD Projected: $");


    //Panels for all the tabbed pane
    private JPanel summaryPanel = new JPanel();
    private JPanel reportPanel = new JPanel();
    private JPanel budgetSetupPanel = new JPanel();

    //Menu Items
    private JMenu file = new JMenu("File");
    private JMenuItem open = new JMenuItem("Open");
    private JMenuItem newTable = new JMenuItem("New");
    private JMenuItem savetrans = new JMenuItem("Save");
    private JMenuItem saveAstrans = new JMenuItem("Save As");
    private JMenuItem saveandexit = new JMenuItem("Save & Exit");
    private JMenuItem exit = new JMenuItem("Exit");

    private JMenu transactions = new JMenu("Transactions");
    private JMenuItem Add = new JMenuItem("Add New");
    private JMenuItem addRecurring = new JMenuItem("Add Recurring");

    private JMenuItem sortByDate = new JMenuItem("Sort By Date");
    private JRadioButton sortByDateButton = new JRadioButton("Date");

    private JMenuItem sortByDesc = new JMenuItem("Sort By Desc");
    private JRadioButton sortByDescButton = new JRadioButton("Description");

    private JMenuItem sortByCat = new JMenuItem("Sort By Category");
    private JRadioButton sortByCatButton = new JRadioButton("Category");

    private JMenuItem exportAsCsv = new JMenuItem("Export As CSV");

    //Popup Menu Items
    private JPopupMenu popupMenu;
    private JMenuItem complete = new JMenuItem("Complete");
    private JMenuItem comp_with_diff = new JMenuItem("Complete With Diff");
    private JMenuItem copyToNew = new JMenuItem("Copy to New");
    private JMenuItem modTrans = new JMenuItem("Modify");
    private JMenuItem delTrans = new JMenuItem("Delete");
    private JMenuItem reSeqTransactions = new JMenuItem("Re-Sequence Trans");

    //Drop downs
    private JComboBox detailsMonths = new JComboBox();
    private JComboBox categoryFilter = new JComboBox();
    private JComboBox itemFilter = new JComboBox();
    private JButton resetFilters = new JButton("Reset Filters");
    private JButton showAll = new JButton("Show All");


    //Reports Panel elements
    private final String plannedVsActual = "Planned v/s Actual";
    private final String TrendRpt = "Trend Reports";
    private JLabel reportMthLabel = new JLabel("Report Month: ");
    private JComboBox reportMonths = new JComboBox();
    private JLabel reportTypeLabel = new JLabel("Report Type: ");
    private JRadioButton plannedVsActualreport = new JRadioButton(plannedVsActual);
    private JRadioButton trendReports = new JRadioButton(TrendRpt);

    //Category Setup Variables
    final int MAX_CATEGORIES = 10;
    ArrayList expCategory = new ArrayList();
    JButton addnew;
    JButton saveButton;

    //Work Variables
    private boolean changeExists = false;
    private File f;
    private int tablePaddingX = 0;
    private int tablePaddingY = 0;
    private double projectedAmount = 0.00;
    private DecimalFormat df2 = new DecimalFormat("#.00");

    //Constants
    private final String sortTypeDate = "Date";
    private final String sortTypeDesc = "Desc";
    private final String sortTypeCat = "Cat";

    //Variables to hold the user preference
    private String currSort = sortTypeDate;
    private String currMonth = "All";
    private String currCategory = "All";
    private String currItem = "All";
    private String currReportType = plannedVsActual;


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }

        //Create the GUI for the details
        ExpDetailsGUI expDetailsGUI = new ExpDetailsGUI();
        expDetailsGUI.setVisible(true);

    }

    public ExpDetailsGUI() {
        tranList = expDetailsDAO.returnTransactions();
        ExpDetailsloadGUI();
    }

    //================================================//
    public void setDefaults()
    {
        currSort = sortTypeDate;
        currMonth = getCurrentMonth();
        currCategory = "All";
        currItem = "All";
        currReportType = plannedVsActual;
        yearEndprojBal.setText("Year End Projected: $");
        ytdCurr.setText("YTD Current: $");
        ytdProjBal.setText("YTD Projected: $");
        expCategory.clear();
        summaryPanel.removeAll();
        reportPanel.removeAll();
        budgetSetupPanel.removeAll();
    }

    public void ExpDetailsloadGUI() {

        setLayout(new GridBagLayout());
        ExpTrkScreenDetails screenDetails = new ExpTrkScreenDetails();
        Dimension fSize = new Dimension(screenDetails.getScreenWidth(), screenDetails.getScreenHeight() - 70);
        tablePaddingX = screenDetails.getScreenWidth() - 21;
        tablePaddingY = screenDetails.getScreenHeight() - 275;

        setJMenuBar(createMenuBar());
        popupMenu = createPopUpMenu();

        // Add Summary panel to a Tabbed Pane.
        tabbedPane.addTab("Expense Details", summaryPanel);
        tabbedPane.addTab("Expense Report", reportPanel);
        //tabbedPane.addTab("Budget Summary",budgetSummary);
        tabbedPane.addTab("Budget Setup",budgetSetupPanel);


        //Add months for report selector and details selection
        reportMonths.addItem("All");
        reportMonths.addItem("January");
        reportMonths.addItem("February");
        reportMonths.addItem("March");
        reportMonths.addItem("April");
        reportMonths.addItem("May");
        reportMonths.addItem("June");
        reportMonths.addItem("July");
        reportMonths.addItem("August");
        reportMonths.addItem("September");
        reportMonths.addItem("October");
        reportMonths.addItem("November");
        reportMonths.addItem("December");

        detailsMonths.addItem("All");
        detailsMonths.addItem("January");
        detailsMonths.addItem("February");
        detailsMonths.addItem("March");
        detailsMonths.addItem("April");
        detailsMonths.addItem("May");
        detailsMonths.addItem("June");
        detailsMonths.addItem("July");
        detailsMonths.addItem("August");
        detailsMonths.addItem("September");
        detailsMonths.addItem("October");
        detailsMonths.addItem("November");
        detailsMonths.addItem("December");

        reportMonths.setActionCommand("Month Changed");
        reportMonths.setBackground(Color.WHITE);
        reportMonths.addActionListener(this);

        detailsMonths.setActionCommand("Detail Month Changed");
        detailsMonths.setBackground(Color.WHITE);
        detailsMonths.addActionListener(this);

        categoryFilter.setActionCommand("Category Changed");
        categoryFilter.setBackground(Color.WHITE);

        itemFilter.setActionCommand("Item Changed");
        itemFilter.setBackground(Color.WHITE);

        resetFilters.setActionCommand("Reset Flters");
        resetFilters.addActionListener(this);

        showAll.setActionCommand("Show All");
        showAll.addActionListener(this);

        sortByDescButton.setActionCommand("Sort By Desc");
        sortByDateButton.setActionCommand("Sort By Date");
        sortByCatButton.setActionCommand("Sort By Category");

        categoryFilter.setPreferredSize(new Dimension(200,23));
        detailsMonths.setPreferredSize(new Dimension(120,23));
        itemFilter.setPreferredSize(new Dimension(200,23));
        resetFilters.setPreferredSize(new Dimension(125,23));
        showAll.setPreferredSize(new Dimension(125,23));

        setDefaults();

        setTitle("Expense Tracker - Details");
        setPreferredSize(fSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(screenDetails.getCenterPosition(fSize).width, screenDetails.getCenterPosition(fSize).height);
        setResizable(false);
        pack();

    }

    public void createDetailPanels()
    {
        createSummaryPanel();
        createReportPanel(currMonth,currReportType);
        createBudgetSetupPanel();
        add(tabbedPane);
    }


    public void createBudgetSetupPanel()
    {
        JPanel budgetSetup = new JPanel();
        budgetSetupPanel.removeAll();

        JPanel allCategory = new JPanel();
        allCategory.setLayout(new GridLayout(5,2));

        for (int i = 0; i < expCategory.size(); i++)
        {
            ExpCategory cat = (ExpCategory) expCategory.get(i);
            allCategory.add(cat.getCategoryPanel());
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        //Add Icons to the buttons.
        ImageIcon plus = new ImageIcon(getClass().getResource("/Images/plus.png"));
        addnew = new JButton(plus);
        addnew.setActionCommand("Add New Category");
        addnew.setPreferredSize(new Dimension(30,30));
        addnew.addActionListener(this);

        if (expCategory.size() == MAX_CATEGORIES)
            addnew.setEnabled(false);
        else
            addnew.setEnabled(true);


        ImageIcon save = new ImageIcon(getClass().getResource("/Images/save.png"));
        saveButton = new JButton(save);
        saveButton.setActionCommand("Save Budget");
        saveButton.setPreferredSize(new Dimension(30,30));
        saveButton.addActionListener(this);

        if (expCategory.size() != budgetList.size())
            saveButton.setEnabled(true);
        else
            saveButton.setEnabled(false);

        //Add buttons to the panel.
        buttonPanel.add(addnew);
        buttonPanel.add(saveButton);

        //Add the panels to the budget setup pane
        budgetSetup.setLayout(new BorderLayout());
        budgetSetup.add(buttonPanel,BorderLayout.SOUTH);
        budgetSetup.add(allCategory);

        budgetSetup.setPreferredSize(new Dimension(tablePaddingX, tablePaddingY + 100));
        budgetSetupPanel.add(budgetSetup);
    }

    public void removeCategory(ExpCategory c)
    {
        for (int i = 0; i < expCategory.size(); i++)
        {
            if (expCategory.get(i) != null)
            {
                ExpCategory cat = (ExpCategory) expCategory.get(i);

                if (cat == c) {
                    expCategory.remove(i);
                    createBudgetSetupPanel();
                    revalidate();
                    break;
                }
            }

        }
    }

    public void createSummaryPanel()
    {
        summaryPanel.removeAll();

        detailsMonths.removeActionListener(this);
        categoryFilter.removeActionListener(this);
        itemFilter.removeActionListener(this);
        detailsMonths.setSelectedItem(currMonth);

        categoryFilter.removeAllItems();
        categoryFilter.addItem("All");
        ArrayList catList = getCategoryList();
        for (int i = 0; i < catList.size(); i++)
            categoryFilter.addItem(catList.get(i));
        categoryFilter.setSelectedItem(currCategory);

        itemFilter.removeAllItems();
        itemFilter.addItem("All");
        ArrayList itemList = expDetailsDAO.getItemList();
        for (int i = 0; i < itemList.size(); i++)
            itemFilter.addItem(itemList.get(i));
        itemFilter.setSelectedItem(currItem);

        categoryFilter.addActionListener(this);
        detailsMonths.addActionListener(this);
        itemFilter.addActionListener(this);

        sortByDateButton.removeActionListener(this);
        sortByCatButton.removeActionListener(this);
        sortByDescButton.removeActionListener(this);

        if (currSort.equals(sortTypeDate))
            sortByDateButton.setSelected(true);
        else if (currSort.equals(sortTypeCat))
            sortByCatButton.setSelected(true);
        else if (currSort.equals(sortTypeDesc))
            sortByDescButton.setSelected(true);

        sortByDateButton.addActionListener(this);
        sortByCatButton.addActionListener(this);
        sortByDescButton.addActionListener(this);

        //Create the summary panel
        summaryPanel.setLayout(new GridBagLayout());

        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new GridBagLayout());
        balancePanel.setBorder(BorderFactory.createTitledBorder("Balance Summary"));

        GridBagConstraints s = new GridBagConstraints();
        s.gridx = 0;
        s.gridy = 0;
        s.anchor = GridBagConstraints.FIRST_LINE_START;
        s.ipadx = 50;
        balancePanel.add(yearEndprojBal,s);

        s.gridx = 1;
        s.gridy = 0;
        s.anchor = GridBagConstraints.PAGE_START;
        s.ipadx = 50;
        balancePanel.add(ytdProjBal,s);

        s.gridx = 2;
        s.gridy = 0;
        s.anchor = GridBagConstraints.LINE_END;
        s.ipadx = 50;
        balancePanel.add(ytdCurr,s);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        summaryPanel.add(balancePanel,c);

        JPanel sortPanel = new JPanel();
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort By"));
        ButtonGroup sortButtonGroup = new ButtonGroup();
        sortButtonGroup.add(sortByDateButton);
        sortButtonGroup.add(sortByCatButton);
        sortButtonGroup.add(sortByDescButton);
        sortPanel.add(sortByDateButton);
        sortPanel.add(sortByCatButton);
        sortPanel.add(sortByDescButton);

        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter By"));

        filterPanel.add(new JLabel("Item - "));
        filterPanel.add(itemFilter);

        filterPanel.add(new JLabel(" Month - "));
        filterPanel.add(detailsMonths);

        filterPanel.add(new JLabel(" Category - "));
        filterPanel.add(categoryFilter);

        filterPanel.add(resetFilters);
        filterPanel.add(showAll);

        JPanel sortFilterPanel = new JPanel();
        sortFilterPanel.add(sortPanel);
        sortFilterPanel.add(filterPanel);

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 1;
        c1.anchor = GridBagConstraints.LINE_START;
        summaryPanel.add(sortFilterPanel,c1);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 2;
        summaryPanel.add(scrollPane, c3);
        toggleMenuOptions(true);

    }

    public void createReportPanel(String month,String rptType)
    {
        JPanel chartPanel = new JPanel();
        JPanel reportSelectionPanel = new JPanel();
        JPanel completeReportPanel = new JPanel();
        ButtonGroup reportBG = new ButtonGroup();
        completeReportPanel.setLayout(new GridBagLayout());

        reportBG.add(plannedVsActualreport);
        reportBG.add(trendReports);

        reportPanel.removeAll();
        reportMonths.removeActionListener(this);
        plannedVsActualreport.removeActionListener(this);
        trendReports.removeActionListener(this);

        reportMonths.setSelectedItem(month);

        if (rptType.equals(plannedVsActual))
            plannedVsActualreport.setSelected(true);
        else if (rptType.equals(TrendRpt))
            trendReports.setSelected(true);

        reportMonths.addActionListener(this);

        plannedVsActualreport.addActionListener(this);
        plannedVsActualreport.setActionCommand("PA");

        trendReports.addActionListener(this);
        trendReports.setActionCommand("TR");

        reportSelectionPanel.add(reportMthLabel);
        reportSelectionPanel.add(reportMonths);
        reportSelectionPanel.add(reportTypeLabel);

        reportSelectionPanel.add(plannedVsActualreport);
        reportSelectionPanel.add(trendReports);


        GridBagConstraints r1 = new GridBagConstraints();
        r1.gridx = 0;
        r1.gridy = 0;
        r1.anchor = GridBagConstraints.FIRST_LINE_START;
        completeReportPanel.add(reportSelectionPanel,r1);

        r1.gridx = 0;
        r1.gridy = 1;
        r1.anchor = GridBagConstraints.LINE_START;



        chartPanel.setLayout(new GridBagLayout());
        GridBagConstraints cP = new GridBagConstraints();

        if (rptType.equals(plannedVsActual))
        {
            ExpPieChart plannedPieChart = new ExpPieChart(expDetailsDAO.getCatExpenseSummationPlanned(month),"Planned Expense");
            chartPanel.add(plannedPieChart);

            ExpPieChart actualPieChart = new ExpPieChart(expDetailsDAO.getCatExpenseSummationActual(month),"Actual Expense");
            chartPanel.add(actualPieChart);
        }
        else if (rptType.equals(TrendRpt))
        {
            cP.gridx = 0;
            cP.gridy = 0;
            ExpBarChart barChart = new ExpBarChart((expDetailsDAO.getBalanceComparision()), "Savings Trend");
            chartPanel.add(barChart,cP);

            cP.gridx = 1;
            cP.gridy = 0;
            ExpBarChart barChart1 = new ExpBarChart((expDetailsDAO.getMthEndBalances()),"Month End Balance");
            chartPanel.add(barChart1,cP);

            cP.gridx = 0;
            cP.gridy = 1;
            ExpBudgetBarChart barChart2 = new ExpBudgetBarChart((expDetailsDAO.monthBudgetReport(currMonth)), "Budget Summary");
            chartPanel.add(barChart2,cP);
        }


        completeReportPanel.add(chartPanel,r1);

        reportPanel.add(completeReportPanel);

    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Create the File Menu and its Items
        newTable.addActionListener(this);
        file.add(newTable);

        open.addActionListener(this);
        file.add(open);

        file.addSeparator();

        reSeqTransactions.addActionListener(this);
        file.add(reSeqTransactions);

        savetrans.addActionListener(this);
        file.add(savetrans);

        saveAstrans.addActionListener(this);
        file.add(saveAstrans);

        file.addSeparator();

        saveandexit.addActionListener(this);
        file.add(saveandexit);

        exit.addActionListener(this);
        file.add(exit);

        Add.addActionListener(this);
        transactions.add(Add);

        addRecurring.addActionListener(this);
        transactions.add(addRecurring);

        transactions.addSeparator();

        exportAsCsv.addActionListener(this);
        transactions.add(exportAsCsv);

        transactions.addSeparator();

        sortByDate.addActionListener(this);
        transactions.add(sortByDate);

        sortByDesc.addActionListener(this);
        transactions.add(sortByDesc);

        sortByCat.addActionListener(this);
        transactions.add(sortByCat);

        toggleMenuOptions(false);

        menuBar.add(file);
        menuBar.add(transactions);

        return menuBar;
    }

    public JPopupMenu createPopUpMenu()
    {
        //Create the transactions menu and its items
        JPopupMenu tranPopmenu = new JPopupMenu();
        complete.addActionListener(this);
        tranPopmenu.add(complete);

        comp_with_diff.addActionListener(this);
        tranPopmenu.add(comp_with_diff);

        tranPopmenu.addSeparator();

        copyToNew.addActionListener(this);
        tranPopmenu.add(copyToNew);

        tranPopmenu.addSeparator();

        modTrans.addActionListener(this);
        tranPopmenu.add(modTrans);

        delTrans.addActionListener(this);
        tranPopmenu.add(delTrans);

        return tranPopmenu;
    }

    public void toggleMenuOptions(boolean setOption) {
        newTable.setEnabled(setOption);
        savetrans.setEnabled(setOption);
        saveAstrans.setEnabled(setOption);
        complete.setEnabled(setOption);
        comp_with_diff.setEnabled(setOption);
        copyToNew.setEnabled(setOption);
        modTrans.setEnabled(setOption);
        delTrans.setEnabled(setOption);
        saveandexit.setEnabled(setOption);
        reSeqTransactions.setEnabled(setOption);
        sortByDate.setEnabled(setOption);
        sortByDesc.setEnabled(setOption);
        sortByCat.setEnabled(setOption);
        exportAsCsv.setEnabled(setOption);
    }

    public void setHeader() {
        //Set the header in summary panel
        int loc;
        yearEndprojBal.setText("Year End Projected: $");
        ytdCurr.setText("YTD Current: $");
        ytdProjBal.setText("YTD Projected: $");

        loc = yearEndprojBal.getText().indexOf("$");
        yearEndprojBal.setText(yearEndprojBal.getText().substring(0,loc+1) + df2.format(projectedAmount));

        loc = ytdCurr.getText().indexOf("$");
        ytdCurr.setText(ytdCurr.getText().substring(0,loc+1)+ df2.format(expDetailsDAO.getYtdCurrentBalance()));

        loc = ytdProjBal.getText().indexOf("$");
        ytdProjBal.setText(ytdProjBal.getText().substring(0,loc+1)+ df2.format(expDetailsDAO.getYtdProjectedBalane()));

        if (expDetailsDAO.getYtdCurrentBalance() >= expDetailsDAO.getYtdProjectedBalane())
        {
            ytdCurr.setForeground(Color.BLUE);
            ytdProjBal.setForeground(Color.BLUE);
        }
        else
        {
            ytdCurr.setForeground(Color.RED);
            ytdProjBal.setForeground(Color.RED);
        }


    }

    public String formatDate(String tDate) {
        String finalDate;
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        Calendar currDate = Calendar.getInstance();
        Calendar c = Calendar.getInstance();

        c.set(Integer.parseInt(tDate.substring(0,4)),
                (Integer.parseInt(tDate.substring(5,7)) - 1),
                Integer.parseInt(tDate.substring(8)));

        finalDate = df.format(c.getTime());
        if (c.get(Calendar.MONTH) == currDate.get(Calendar.MONTH) &&
                c.get(Calendar.YEAR) == currDate.get(Calendar.YEAR))
            finalDate = finalDate + "(*)";

        return finalDate;
    }

    //================================================//

    public JScrollPane loadExpenseData() {

        projectedAmount = 0;
        double actualAmount = 0;

        String[] columnNames = {
                "Ref#",
                "Item",
                "Category",
                "Date",
                "Status",
                "Planned Amount ($)",
                "Actual Amount ($)",
                "Projected Balance ($)",
                "Current Balance ($)"
        };

        expDetailsDAO.sortdata(currSort);
        tranList = expDetailsDAO.filterTransactions(currMonth,currCategory,currItem);
        Object[][] data = new Object[tranList.size()][9];

        if (tranList.size() > 0) {

            for (int x = 0; x < tranList.size(); x++)
            {
                ExpSingleTransaction o = (ExpSingleTransaction) tranList.get(x);

                    data[x][0] = o.getTranid();
                    data[x][1] = o.getItem();
                    data[x][2] = o.getCategory();
                    data[x][3] = formatDate(o.getTranDate());
                    data[x][4] = o.getStatus();

                    data[x][5] = Double.toString(o.getpAmount());
                    projectedAmount += o.getpAmount();

                    data[x][6] = Double.toString(o.getaAmount());
                    actualAmount += o.getaAmount();

                    data[x][7] = df2.format(projectedAmount);

                    if (o.getStatus().equals("Completed"))
                        data[x][8] = df2.format(actualAmount);
                    else
                        data[x][8] = df2.format(0.00);
            }

        }

        projectedAmount = expDetailsDAO.getProjectedBalance();

        dataTable = new JTable(data, columnNames) {
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
            Component comp = super.prepareRenderer(renderer, row, col);
            Object value = getModel().getValueAt(row, col);
            comp.setForeground(Color.BLACK);
            comp.setBackground(null);

            if (col == 5 || col == 6 || col == 7 || col == 8)
            {
                if (Double.parseDouble(value.toString()) < 0)
                    comp.setForeground(Color.RED);
                else if (Double.parseDouble(value.toString()) >= 0)
                    comp.setForeground(Color.BLUE);
            }

            if (col == 3 && value.toString().contains("(*)"))
            {
                comp.setBackground(Color.YELLOW);
            }
            if (getSelectedRow() == row)
            {
                comp.setBackground(Color.GRAY);
            }

            return comp;
        }
    };
        formatTable();

        dataTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == e.BUTTON3)
                {
                    int row = dataTable.rowAtPoint(e.getPoint());
                    int column = dataTable.columnAtPoint(e.getPoint());
                    dataTable.changeSelection(row, column, false, false);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }


            } });
        scrollPane = new JScrollPane(dataTable);
        scrollPane.setPreferredSize(new Dimension(tablePaddingX, tablePaddingY));
        dataTable.setFillsViewportHeight(true);
        dataTable.setComponentPopupMenu(popupMenu);
        return scrollPane;
    }

    public void formatTable()
    {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        //Current Balance
        dataTable.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
        dataTable.getColumnModel().getColumn(8).setPreferredWidth(35);//Ref#
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(20);

        //Item
        dataTable.getColumnModel().getColumn(1).setPreferredWidth(150);

        //Category
        dataTable.getColumnModel().getColumn(2).setPreferredWidth(35);

        //Transaction Date
        dataTable.getColumnModel().getColumn(3).setPreferredWidth(10);

        //Transaction Status
        dataTable.getColumnModel().getColumn(4).setPreferredWidth(10);

        //Planned Amount
        dataTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        dataTable.getColumnModel().getColumn(5).setPreferredWidth(35);

        //Actual Amount
        dataTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        dataTable.getColumnModel().getColumn(6).setPreferredWidth(35);

        //Projected Balance
        dataTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        dataTable.getColumnModel().getColumn(7).setPreferredWidth(35);

    }

    public void openFile(File f) {

        if (expDetailsDAO.loadTransactionsDBNew(f))
        {
            setDefaults();
            tranList = expDetailsDAO.returnTransactions();
            budgetList = expDetailsDAO.getBudgetList();

            //Copy Objects from main budget list to the local one
            for (int i = 0; i < budgetList.size(); i++)
            {
                ExpCategory cat = (ExpCategory) budgetList.get(i);
                cat.setParent(this);
                expCategory.add(cat);
            }


            refreshData();
            changeExists = false;
            this.setTitle("Expense Tracker - " + f.getName());
            this.toggleMenuOptions(true);
        }
        else
            JOptionPane.showMessageDialog(this, "Invalid file type. Cannot Open.", "Invalid File",JOptionPane.ERROR_MESSAGE);
    }

    public void setChangeExists()
    {
        changeExists = true;
    }

    public void enableCategorySave()
    {
        saveButton.setEnabled(true);
    }

    public void addNewTransactions(boolean multiAdd)
    {
            if (expTrkExpenseDialog.getUserAction())
            {
                if (!multiAdd)
                    expDetailsDAO.addNewTransaction(expTrkExpenseDialog.getTranData());
                else
                    expDetailsDAO.addMultipleTransaction(expTrkExpenseDialog.getMulTranData());

                changeExists = true;
                refreshData();
            }

    }


    public void modifyTransaction(JsonObject o) {
        expDetailsDAO.modTransaction(o);
        expDetailsDAO.sortdata(currSort);
        refreshData();
        changeExists = true;
    }

    public void saveTransactions(String saveType) {
            if (saveType.equals("Save As") || f == null)
            {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Expense Tracker Files", "exp");
                JFileChooser chooser = new JFileChooser(".");
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    f = chooser.getSelectedFile();
                    String fileName = f.toString();
                    if (!fileName.contains(".exp"))
                    {
                        fileName = f.toString() + ".exp";
                        f = new File(fileName);
                    }

                }

            }

            if (f != null)
            {
                expDetailsDAO.reSeqTrans();
                expDetailsDAO.saveAllTransactionsToDBNew(f);
                changeExists = false;
            }

            if (saveType.equals("Save & Exit") && !changeExists)
                System.exit(0);
            else
                refreshData();
        }

    public boolean saveBudgetSetups()
    {

        //Validate all the category list
        for (int i = 0; i < expCategory.size(); i++)
        {
            ExpCategory cat = (ExpCategory) expCategory.get(i);
            if (cat.isNewCategory())
            {
                if (!cat.isCategorySelected())
                    return false;
            }
        }

        budgetList.clear();

        for (int i = 0; i < expCategory.size(); i++)
        {
            ExpCategory cat = (ExpCategory) expCategory.get(i);
            cat.disableCategory();
            budgetList.add(expCategory.get(i));
        }

        saveButton.setEnabled(false);
        return true;
    }

    public void reSequecneTransactions()
    {
        //Re-Sequence Transactions
        expDetailsDAO.reSeqTrans();
        currSort = sortTypeDate;
        refreshData();
        changeExists = true;
    }

    public void refreshData()
    {
        if (scrollPane != null)
        {
            summaryPanel.removeAll();
            remove(tabbedPane);
        }
        scrollPane = this.loadExpenseData();
        setHeader();
        createDetailPanels();

        repaint();
        revalidate();
    }

    public ArrayList getCategoryList()
    {
        return expDetailsDAO.getCategoryList();
    }

    public String getCurrentMonth() {

        String tDate;
        Calendar c = Calendar.getInstance();
        switch (c.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                tDate = "January";
                break;
            case Calendar.FEBRUARY:
                tDate = "February";
                break;
            case Calendar.MARCH:
                tDate = "March";
                break;
            case Calendar.APRIL:
                tDate = "April";
                break;
            case Calendar.MAY:
                tDate = "May";
                break;
            case Calendar.JUNE:
                tDate = "June";
                break;
            case Calendar.JULY:
                tDate = "July";
                break;
            case Calendar.AUGUST:
                tDate = "August";
                break;
            case Calendar.SEPTEMBER:
                tDate = "September";
                break;
            case Calendar.OCTOBER:
                tDate = "October";
                break;
            case Calendar.NOVEMBER:
                tDate = "November";
                break;
            case Calendar.DECEMBER:
                tDate = "December";
                break;
            default:
                tDate = "Invalid Month";
                break;
        }

        return tDate;
    }

    public void actionPerformed(ActionEvent e)  {

            if (e.getActionCommand().equals(open.getActionCommand()))
            {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Expense Tracker Files", "exp");
                JFileChooser chooser = new JFileChooser(".");
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(filter);

                if ((changeExists && JOptionPane.showConfirmDialog(this,"Unsaved Data Exists. Open New(Y/N)","Open New Confirmation", JOptionPane.YES_NO_OPTION) ==
                        JOptionPane.YES_OPTION) || !changeExists)
                {
                    int returnVal = chooser.showOpenDialog(this);

                    if (returnVal == JFileChooser.APPROVE_OPTION)
                    {
                        f = chooser.getSelectedFile();

                        this.openFile(f);
                    }
                }
            }
            else if (e.getActionCommand().equals(newTable.getActionCommand()))
            {
                if(changeExists)
                {
                    if (JOptionPane.showConfirmDialog(this,"Unsaved Data Exists. Start New(Y/N)","Open New Confirmation", JOptionPane.YES_NO_OPTION) ==
                            JOptionPane.YES_OPTION)
                    {
                            setTitle("Expense Tracker");
                            toggleMenuOptions(false);
                            changeExists = false;
                            remove(tabbedPane);
                            setDefaults();
                            revalidate();

                    }
                }
                else
                {
                    setTitle("Expense Tracker");
                    toggleMenuOptions(false);
                    changeExists = false;
                    remove(tabbedPane);
                    revalidate();
                }
            }
            else if (e.getActionCommand().equals(saveAstrans.getActionCommand()) ||
                     e.getActionCommand().equals(savetrans.getActionCommand()) ||
                     e.getActionCommand().equals(saveandexit.getActionCommand()))
            {
                saveTransactions(e.getActionCommand());
            }
            else if (e.getActionCommand().equals(Add.getActionCommand()) ||
                     e.getActionCommand().equals(addRecurring.getActionCommand()))
            {
                    expTrkExpenseDialog = new ExpTrkExpenseDialog(e.getActionCommand(),this);
                    expTrkExpenseDialog.setVisible(true);

            }
            else if (e.getActionCommand().equals(complete.getActionCommand()) ||
                     e.getActionCommand().equals(comp_with_diff.getActionCommand())||
                     e.getActionCommand().equals(modTrans.getActionCommand()) ||
                     e.getActionCommand().equals(delTrans.getActionCommand()) ||
                     e.getActionCommand().equals(copyToNew.getActionCommand()))
            {
                String actionCommand = e.getActionCommand();
                int tranId;
                int row = dataTable.getSelectedRow();
                if (row == -1)
                    JOptionPane.showMessageDialog(this,"No Transaction Selected");
                else if (dataTable.getModel().getValueAt(row,4).equals("Completed") &&
                        (actionCommand.equals(complete.getActionCommand()) ||
                                actionCommand.equals(comp_with_diff.getActionCommand())))
                {
                    JOptionPane.showMessageDialog(this,"Transaction Already Completed. Please Modify");
                }
                else
                {
                    tranId = (int) dataTable.getModel().getValueAt(row,0);
                    JsonObject o = expDetailsDAO.getSingleTransaction(tranId);

                    if (e.getActionCommand().equals(complete.getActionCommand()))
                    {
                        o.add("AAmount", new JsonPrimitive(o.get("PAmount").toString().replace("\"","")));
                        o.add("Status",new JsonPrimitive("Completed"));
                        modifyTransaction(o);
                        refreshData();
                        setVisible(true);
                    }
                    else if (e.getActionCommand().equals(comp_with_diff.getActionCommand()) ||
                             e.getActionCommand().equals(modTrans.getActionCommand()) ||
                             e.getActionCommand().equals(copyToNew.getActionCommand()))
                    {
                        if (e.getActionCommand().equals(comp_with_diff.getActionCommand()))
                            o.add("Status",new JsonPrimitive("Completed"));

                        expTrkExpenseDialog = new ExpTrkExpenseDialog(e.getActionCommand(), this,o);
                        expTrkExpenseDialog.setVisible(true);
                    }
                    else if (e.getActionCommand().equals(delTrans.getActionCommand()))
                    {
                        if (expDetailsDAO.deleteTransaction(tranId))
                        {
                            toggleMenuOptions(false);
                        }
                        changeExists = true;
                        refreshData();
                        this.setVisible(true);
                    }
                }
            }
            else if (e.getActionCommand().equals(reSeqTransactions.getActionCommand()))
            {
                reSequecneTransactions();
            }
            else if (e.getActionCommand().equals(reportMonths.getActionCommand()))
            {
                currMonth = reportMonths.getSelectedItem().toString();
                refreshData();
            }
            else if (e.getActionCommand().equals(detailsMonths.getActionCommand()))
            {
                currMonth = detailsMonths.getSelectedItem().toString();
                refreshData();
            }
            else if (e.getActionCommand().equals(categoryFilter.getActionCommand()))
            {
                currCategory = categoryFilter.getSelectedItem().toString();
                refreshData();
            }
            else if (e.getActionCommand().equals(itemFilter.getActionCommand()))
            {
                currItem = itemFilter.getSelectedItem().toString();
                refreshData();
            }
            else if (e.getActionCommand().equals(resetFilters.getActionCommand()))
            {
                currItem = "All";
                currCategory = "All";
                currMonth = getCurrentMonth();
                refreshData();
            }
            else if (e.getActionCommand().equals(showAll.getActionCommand()))
            {
                currItem = "All";
                currCategory = "All";
                currMonth = "All";
                refreshData();
            }
            else if (e.getActionCommand().equals(plannedVsActualreport.getActionCommand()) ||
                    e.getActionCommand().equals(trendReports.getActionCommand()))
            {
                if (plannedVsActualreport.isSelected())
                    currReportType = plannedVsActual;
                else if (trendReports.isSelected())
                    currReportType = TrendRpt;

                refreshData();
            }
            else if (e.getActionCommand().equals(sortByDesc.getActionCommand())) {
                currSort = sortTypeDesc;
                refreshData();
            }
            else if (e.getActionCommand().equals(sortByDate.getActionCommand()))
            {
                currSort = sortTypeDate;
                refreshData();
            }
            else if (e.getActionCommand().equals(sortByCat.getActionCommand()))
            {
                currSort = sortTypeCat;
                refreshData();
            }
            else if(e.getActionCommand().equals(exportAsCsv.getActionCommand()))
            {
                String fileName;
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                JFileChooser chooser = new JFileChooser(".");
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    f = chooser.getSelectedFile();
                    if (!f.toString().contains(".csv"))
                    {
                        fileName = f.toString() + ".csv";
                        f = new File(fileName);
                    }

                    if (expDetailsDAO.exportAllTransactionsToCsv(f))
                        JOptionPane.showMessageDialog(this,"Transactions exported");
                    else
                        JOptionPane.showMessageDialog(this,"Unable to export transactions");
                }
            }
            else if (e.getActionCommand().equals(exit.getActionCommand()))
            {
                if (changeExists)
                {
                    if (JOptionPane.showConfirmDialog(this,"Unsaved Data Exists. Exit(Y/N)","Exit Confirmation", JOptionPane.YES_NO_OPTION) ==
                            JOptionPane.YES_OPTION)
                        System.exit(0);
                }
                else
                    System.exit(0);
            }
            else if (e.getActionCommand().equals(addnew.getActionCommand()))
            {
                ExpCategory cat = new ExpCategory(this);
                cat.setCategory(expDetailsDAO.getCategoryList());
                expCategory.add(cat);
                saveButton.setEnabled(true);
                createBudgetSetupPanel();
                changeExists = true;
                revalidate();
            }
            else if (e.getActionCommand().equals(saveButton.getActionCommand()))
            {
                if (saveBudgetSetups())
                {
                    createReportPanel(currMonth,currReportType);
                    revalidate();
                    JOptionPane.showMessageDialog(this,"Budget Categories Saved");
                }
                else
                    JOptionPane.showMessageDialog(this,"Budget Category not Selected");

            }
        }
}
