package Data;

import java.nio.file.Files;
import java.util.ArrayList;
import java.io.*;
import java.util.*;

import UI.ExpCategory;
import com.google.gson.*;

/**
 * Created by aarohi on 1/14/16.
 */
public class ExpDetailsDAO
{
    private ArrayList transactionsData = new ArrayList();
    private ArrayList categoryList = new ArrayList();
    private ArrayList itemList = new ArrayList();
    private ArrayList budgetList = new ArrayList();
    private JsonObject catExpenseSummationPlanned;
    private JsonObject catExpenseSummationActual;
    private JsonObject balanceComparision = new JsonObject();
    private JsonObject monthEndBalance;
    private double projectedBalance = 0.0;
    private double ytdProjectedBalane = 0.0;
    private double ytdCurrentBalance = 0.0;
    private int transactionID = 0;

    public boolean loadTransactionsDBNew(File f)
    {
        transactionsData.clear();
        categoryList.clear();
        budgetList.clear();
        JsonParser parser = new JsonParser();
        String line = null;
        projectedBalance = 0.0;
        ytdProjectedBalane = 0.0;
        ytdCurrentBalance = 0.0;

        try (BufferedReader reader = Files.newBufferedReader(f.toPath()))
        {
            if ((line = reader.readLine()) != null)
            {
                JsonObject o = parser.parse(line).getAsJsonObject();
                JsonArray transList = (JsonArray) o.get("Transactions");
                if (transList != null)
                {
                    for(int i = 0; i < transList.size(); i++)
                    {
                        ExpSingleTransaction transaction =
                                new ExpSingleTransaction(transList.get(i).getAsJsonObject());
                        transactionsData.add(transaction);
                        addToCategory(transaction.getCategory(),transaction.getItem());
                        projectedBalance += transaction.getpAmount();

                        if(transaction.isPriorToCurrentDate())
                        {
                            ytdProjectedBalane += transaction.getpAmount();
                            if (transaction.getStatus().equals("Completed"))
                                ytdCurrentBalance += transaction.getaAmount();
                        }
                    }
                }
                else
                    return false;

                try
                {
                    JsonArray budget = (JsonArray) o.get("Budgets");
                    for (int i = 0; i < budget.size(); i++)
                    {
                        ExpCategory cat = new ExpCategory(budget.get(i).getAsJsonObject());
                        budgetList.add(cat);
                    }
                }
                catch (NullPointerException p)
                {
                    //Nothing to do no budgets found
                }

            }

            addCategoryTotals();
            sortdata("Date");

        }
        catch (IOException x)
        {
            System.err.format("IOException: %s%n", x);
        }

        return true;
    }

    public void addNewTransaction (JsonObject o)
    {
        this.transactionID += 1;
        o.add("Transaction id",new JsonPrimitive(Integer.toString(this.transactionID)));
        ExpSingleTransaction transaction =
                new ExpSingleTransaction(o);
        transactionsData.add(transaction);
        addToCategory(transaction.getCategory(),transaction.getItem());
        addCategoryTotals();
        projectedBalance += transaction.getpAmount();

        if(transaction.isPriorToCurrentDate())
        {
            ytdProjectedBalane += transaction.getpAmount();
            if (transaction.getStatus().equals("Completed"))
                ytdCurrentBalance += transaction.getaAmount();
        }
    }

    public void addMultipleTransaction (ArrayList trans)
    {
        for (int i = 0; i < trans.size(); i++)
            addNewTransaction((JsonObject) trans.get(i));
    }

    public boolean deleteTransaction(int i)
    {
        for (int x = 0; x <= this.transactionsData.size();x++)
        {
            ExpSingleTransaction o = (ExpSingleTransaction) transactionsData.get(x);
            if (o.getTranid() == i)
            {
                transactionsData.remove(x);
                projectedBalance -= o.getpAmount();

                if(o.isPriorToCurrentDate())
                {
                    ytdProjectedBalane -= o.getpAmount();
                    if (o.getStatus().equals("Completed"))
                        ytdCurrentBalance -= o.getaAmount();
                }

                break;
            }

        }

        addCategoryTotals();

        if (this.transactionsData.size() == 0)
            return true;
        else
            return false;
    }

    public void modTransaction(JsonObject o)
    {
        int i = Integer.parseInt(o.get("Transaction id").toString().replace("\"",""));
        for (int x = 0; x < this.transactionsData.size();x++)
        {
            ExpSingleTransaction tran = (ExpSingleTransaction) transactionsData.get(x);
            if (tran.getTranid() == i)
            {
                projectedBalance -= tran.getpAmount();
                if(tran.isPriorToCurrentDate())
                {
                    ytdProjectedBalane -= tran.getpAmount();
                    if (tran.getStatus().equals("Completed"))
                        ytdCurrentBalance -= tran.getaAmount();
                }

                tran.setItem(o.get("Item").toString().replace("\"",""));
                tran.setaAmount(o.get("AAmount").toString().replace("\"",""));
                tran.setpAmount(o.get("PAmount").toString().replace("\"",""));
                tran.setStatus(o.get("Status").toString().replace("\"",""));
                tran.setTranDate(o.get("Date").toString().replace("\"",""));
                tran.setCategory(o.get("Category").toString().replace("\"",""));
                projectedBalance += tran.getpAmount();
                addToCategory(tran.getCategory(),tran.getItem());
                addCategoryTotals();

                if(tran.isPriorToCurrentDate())
                {
                    ytdProjectedBalane += tran.getpAmount();
                    if (tran.getStatus().equals("Completed"))
                        ytdCurrentBalance += tran.getaAmount();
                }
                break;
            }

        }
    }

    public void saveAllTransactionsToDBNew(File f)
    {
        JsonObject completeTrans = new JsonObject();

        JsonArray tranList = new JsonArray();
        JsonArray budget = new JsonArray();

        for (int x = 0; x < transactionsData.size();x++)
        {
            JsonObject o = new JsonObject();
            ExpSingleTransaction tran = (ExpSingleTransaction) transactionsData.get(x);
            o.add("Transaction id", new JsonPrimitive(Integer.toString(tran.getTranid())));
            o.add("Category", new JsonPrimitive(tran.getCategory()));
            o.add("Item", new JsonPrimitive(tran.getItem()));
            o.add("Date", new JsonPrimitive(tran.getTranDate()));
            o.add("PAmount", new JsonPrimitive(Double.toString(tran.getpAmount())));
            o.add("AAmount", new JsonPrimitive(Double.toString(tran.getaAmount())));
            o.add("Status", new JsonPrimitive(tran.getStatus()));

            tranList.add(o);
        }


        for (int i = 0; i < budgetList.size(); i++)
        {
            JsonObject o = new JsonObject();
            ExpCategory cat = (ExpCategory) budgetList.get(i);
            o.add("Category",new JsonPrimitive(cat.getCategory()));
            o.add("Amount",new JsonPrimitive(cat.getSliderAmount()));

            budget.add(o);
        }

        completeTrans.add("Transactions",tranList);
        completeTrans.add("Budgets",budget);


        try (BufferedWriter writer = Files.newBufferedWriter(f.toPath()))
        {
            writer.write(completeTrans.toString(), 0, completeTrans.toString().length());

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    public boolean exportAllTransactionsToCsv(File f)
    {
        String o;
        try (BufferedWriter writer = Files.newBufferedWriter(f.toPath()))
        {
            o = "\"" + "Item" + "\"," +
                    "\"" + "Category" + "\"," +
                    "\"" + "Date" + "\"," +
                    "\"" + "Status" + "\"," +
                    "\"" + "Planned Amount ($)" + "\"," +
                    "\"" + "Actual Amount ($)" + "\"";

            o = o.concat("\n");
            writer.write(o, 0, o.length());

            for (int x = 0; x < this.transactionsData.size();x++)
            {
                ExpSingleTransaction tran = (ExpSingleTransaction) transactionsData.get(x);

                o = "\"" + tran.getItem() + "\"," +
                        "\"" + tran.getCategory() + "\"," +
                        "\"" + tran.getTranDate() + "\"," +
                        "\"" + tran.getStatus() + "\"," +
                        "\"" + tran.getpAmount() + "\"," +
                        "\"" + tran.getaAmount() + "\"";
                o = o.concat("\n");
                writer.write(o, 0, o.length());
            }

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            return false;
        }

        return true;
    }

    public JsonObject getSingleTransaction(int tid)
    {
        ExpSingleTransaction tran;
        JsonObject o = new JsonObject();
        for (int x = 0; x < this.transactionsData.size();x++)
        {
            tran = (ExpSingleTransaction) transactionsData.get(x);
            if (tran.getTranid() == tid)
            {
                o.add("Transaction id", new JsonPrimitive(Integer.toString(tran.getTranid())));
                o.add("Item", new JsonPrimitive(tran.getItem()));
                o.add("Category", new JsonPrimitive(tran.getCategory()));
                o.add("Date", new JsonPrimitive(tran.getTranDate()));
                o.add("PAmount", new JsonPrimitive(Double.toString(tran.getpAmount())));
                o.add("AAmount", new JsonPrimitive(Double.toString(tran.getaAmount())));
                o.add("Status", new JsonPrimitive(tran.getStatus()));
                break;
            }
        }

        return o;
    }

    public ArrayList returnTransactions() {
        return transactionsData;
    }

    public ArrayList filterTransactions(String month,String category,String item)
    {
        ArrayList monthTransactions = new ArrayList();
        if (month.equals("All") && category.equals("All") && item.equals("All"))
            monthTransactions = transactionsData;
        else
        {
            for (int i = 0; i < transactionsData.size(); i++)
            {
                ExpSingleTransaction o = (ExpSingleTransaction) transactionsData.get(i);
                if ((month.equals("All") || o.getTransactionMonth().equals(month)) &&
                    (category.equals("All") || o.getCategory().equals(category)) &&
                        (item.equals("All") || o.getItem().equals(item)))
                    monthTransactions.add(o);
            }
        }

        return monthTransactions;
    }

    public ArrayList getCategoryList()
    {
        return categoryList;
    }

    public ArrayList getItemList() {return itemList;}

    public void addToCategory(String cat, String item)
    {
        if(!categoryList.contains(cat))
        {
            categoryList.add(cat);
            Collections.sort(categoryList);
        }

        if (!itemList.contains(item))
        {
            itemList.add(item);
            Collections.sort(itemList);
        }
    }

    public void addCategoryTotals()
    {
        catExpenseSummationPlanned = new JsonObject();
        catExpenseSummationActual = new JsonObject();
        Double [] plannedBalance = new Double[12];
        Double [] actualBalance = new Double[12];

        Arrays.fill(plannedBalance,0.0);
        Arrays.fill(actualBalance,0.0);
        balanceComparision = new JsonObject();

        for (int i = 0; i < transactionsData.size(); i++)
        {
            ExpSingleTransaction o = (ExpSingleTransaction) transactionsData.get(i);

            //Create Category wise planned expense JSON
            if (o.getpAmount() < 0)
            {
                if (catExpenseSummationPlanned.has(o.getCategory()))
                {
                    Double value = catExpenseSummationPlanned.get(o.getCategory()).getAsDouble() + (o.getpAmount() * -1);
                    catExpenseSummationPlanned.add(o.getCategory(), new JsonPrimitive(value));
                }
                else
                {
                    catExpenseSummationPlanned.add(o.getCategory(), new JsonPrimitive(o.getpAmount() * -1));
                }
            }

            //Create Category wise actual expense JSON
            if (o.getaAmount() < 0)
            {
                if (catExpenseSummationActual.has(o.getCategory()))
                {
                    Double value = catExpenseSummationActual.get(o.getCategory()).getAsDouble() + (o.getaAmount() * -1);
                    catExpenseSummationActual.add(o.getCategory(), new JsonPrimitive(value));
                }
                else
                {
                    catExpenseSummationActual.add(o.getCategory(), new JsonPrimitive(o.getaAmount() * -1));
                }
            }

            //Add the monthly total comparison
            createMonthlyPlannedvsActualBalance(o);

            //Add the month balances
            plannedBalance[o.getTransactionMonthNumeral()] += o.getpAmount();
            actualBalance[o.getTransactionMonthNumeral()] += o.getaAmount();
        }

        createMonthEndBalances(plannedBalance,actualBalance);
    }

    public JsonObject monthBudgetReport(String month)
    {
        Double value;
        JsonObject budgetObject = new JsonObject();

        //Enter all the budgeted amount in a JSON
        for (int i = 0; i < budgetList.size(); i++)
        {
            JsonObject categoryValues = new JsonObject();
            ExpCategory budgetCategory = (ExpCategory) budgetList.get(i);
            categoryValues.add("Budgeted Amount",new JsonPrimitive(budgetCategory.getSliderAmount()));
            categoryValues.add("Expense Amount",new JsonPrimitive(0.0));
            budgetObject.add(budgetCategory.getCategory(),categoryValues);
        }

        //Add an un-budgeted category
        {
            JsonObject categoryValues = new JsonObject();
            categoryValues.add("Budgeted Amount", new JsonPrimitive(0.0));
            categoryValues.add("Expense Amount", new JsonPrimitive(0.0));
            budgetObject.add("Un-Budgeted", categoryValues);
        }


        //Read all transactions and accumulate as per category and budgets
        for (int i = 0; i < transactionsData.size(); i++)
        {
            ExpSingleTransaction o = (ExpSingleTransaction) transactionsData.get(i);

            if (month.equals(o.getTransactionMonth()))
            {
                value = o.getaAmount();

                //Create Category wise actual expense v/s budget JSON
                if (value < 0)
                {
                    if (budgetObject.has(o.getCategory()))
                    {
                        JsonObject categoryValues;
                        categoryValues = budgetObject.get(o.getCategory()).getAsJsonObject();
                        Double finalValue = categoryValues.get("Expense Amount").getAsDouble() + (value * -1);
                        categoryValues.add("Expense Amount", new JsonPrimitive(finalValue));
                        budgetObject.add(o.getCategory(),categoryValues);

                    }
                    else
                    {
                        JsonObject categoryValues;
                        categoryValues = budgetObject.get("Un-Budgeted").getAsJsonObject();
                        Double finalValue = categoryValues.get("Expense Amount").getAsDouble() + (value * -1);
                        categoryValues.add("Expense Amount", new JsonPrimitive(finalValue));
                        budgetObject.add("Un-Budgeted",categoryValues);
                    }
                }
            }
        }

        return budgetObject;
    }

    public void createMonthlyPlannedvsActualBalance(ExpSingleTransaction o)
    {
        JsonObject monthlyValues = new JsonObject();

        //Create Month wise projected and actual balance comparison
        if (!balanceComparision.has(o.getTransactionMonth()))
        {
            monthlyValues.add("Planned Savings",new JsonPrimitive(o.getpAmount()));
            monthlyValues.add("Actual Savings",new JsonPrimitive(o.getaAmount()));

        }
        else
        {
            Double newValue = 0.0;
            monthlyValues = balanceComparision.get(o.getTransactionMonth()).getAsJsonObject();

            newValue = (o.getpAmount() + monthlyValues.get("Planned Savings").getAsDouble());
            monthlyValues.add("Planned Savings",new JsonPrimitive(newValue));

            newValue = (o.getaAmount() + monthlyValues.get("Actual Savings").getAsDouble());
            monthlyValues.add("Actual Savings",new JsonPrimitive(newValue));
        }

        balanceComparision.add(o.getTransactionMonth(),monthlyValues);
    }

    public void createMonthEndBalances(Double [] plannedBalance, Double [] actualBalance)
    {
        monthEndBalance = new JsonObject();
        Double plannedPrevValue = 0.0;
        Double actualPrevValue = 0.0;
        String month;

        //Create Month End balances JSON object
        for (int i = 0; i <= 11; i++)
        {
            if (plannedBalance[i] != 0.0)
                plannedPrevValue += plannedBalance[i];
            else
                plannedPrevValue = 0.0;

            if (actualBalance[i] != 0.0)
                actualPrevValue += actualBalance[i];
            else
                actualPrevValue = 0.0;

            JsonObject monthlyValues = new JsonObject();

            switch(i)
            {
                case Calendar.JANUARY:
                    month = "January";
                    break;
                case Calendar.FEBRUARY:
                    month = "February";
                    break;
                case Calendar.MARCH:
                    month = "March";
                    break;
                case Calendar.APRIL:
                    month = "April";
                    break;
                case Calendar.MAY:
                    month = "May";
                    break;
                case Calendar.JUNE:
                    month = "June";
                    break;
                case Calendar.JULY:
                    month = "July";
                    break;
                case Calendar.AUGUST:
                    month = "August";
                    break;
                case Calendar.SEPTEMBER:
                    month = "September";
                    break;
                case Calendar.OCTOBER:
                    month = "October";
                    break;
                case Calendar.NOVEMBER:
                    month = "November";
                    break;
                case Calendar.DECEMBER:
                    month = "December";
                    break;
                default:
                    month = "Invalid Month";
                    break;
            }

            monthlyValues.add("Planned Balance",new JsonPrimitive(plannedPrevValue));
            monthlyValues.add("Actual Balance",new JsonPrimitive(actualPrevValue));

            monthEndBalance.add(month,monthlyValues);
        }
    }

    public JsonObject monthReport(String month, String type)
    {
        Double value;
        JsonObject reportObject = new JsonObject();
        for (int i = 0; i < transactionsData.size(); i++)
        {
            ExpSingleTransaction o = (ExpSingleTransaction) transactionsData.get(i);

            if (month.equals(o.getTransactionMonth()))
            {
                if (type.equals("Planned"))
                    value = o.getpAmount();
                else if (type.equals("Actual"))
                    value = o.getaAmount();
                else
                    return null;

                //Create Category wise planned expense JSON
                if (value < 0) {
                    if (reportObject.has(o.getCategory())) {
                        Double finalValue = reportObject.get(o.getCategory()).getAsDouble() + (value * -1);
                        reportObject.add(o.getCategory(), new JsonPrimitive(finalValue));
                    } else {
                        reportObject.add(o.getCategory(), new JsonPrimitive(value * -1));
                    }
                }
            }
        }

        return reportObject;
    }

    public void sortdata(String sortType)
    {
        ExpSingleTransaction [] trans = new ExpSingleTransaction[transactionsData.size()];
        boolean posFound = false;
        int position = 0;

        for (int x = 0; x < this.transactionsData.size();x++)
        {
            ExpSingleTransaction o = (ExpSingleTransaction) transactionsData.get(x);

            if (x == 0)
                trans[x] = o;
            else
            {
                //Put logic to compare objects and replace spaces.
                for (int y = 0; y < x; y++)
                {
                    if (!o.isGreaterThan(trans[y], sortType))
                    {
                        for(int z = x ; z >= y ;z--)
                        {
                            if (z > 0)
                                trans[z] = trans[z-1];
                        }

                        trans[y] = o;
                        posFound = true;
                        break;
                    }
                    position = y + 1;

                }

                if (!posFound)
                    trans[position] = o;

                posFound = false;

            }

        }
        transactionsData.clear();
        transactionID = 0;
        for (int i = 0; i < trans.length; i++)
        {
            transactionsData.add(trans[i]);
            if (transactionID < trans[i].getTranid())
                transactionID = trans[i].getTranid();
        }
    }

    public void reSeqTrans()
    {
        sortdata("Date");
        for (int i = 0; i < transactionsData.size(); i++)
        {
            ExpSingleTransaction o = (ExpSingleTransaction) transactionsData.get(i);
            o.setTranid(i+1);
        }

    }

    public JsonObject getCatExpenseSummationPlanned(String month)
    {

        if (month.equals("All"))
            return catExpenseSummationPlanned;
        else
            return monthReport(month,"Planned");
    }
    public JsonObject getCatExpenseSummationActual(String month)
    {

        if (month.equals("All"))
            return catExpenseSummationActual;
        else
            return monthReport(month,"Actual");
    }

    public JsonObject getBalanceComparision()
    {
        return balanceComparision;
    }

    public JsonObject getMthEndBalances()
    {
        return monthEndBalance;
    }

    public double getProjectedBalance()
    {
        return projectedBalance;
    }

    public double getYtdProjectedBalane()
    {
        return ytdProjectedBalane;
    }

    public double getYtdCurrentBalance()
    {
        return ytdCurrentBalance;
    }

    public ArrayList getBudgetList()
    {
        return budgetList;
    }


}
