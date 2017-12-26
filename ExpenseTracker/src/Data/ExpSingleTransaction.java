package Data;

import com.google.gson.*;

import java.util.Calendar;

/**
 * Created by aarohi on 1/12/16.
 */
public class ExpSingleTransaction
{
    private int transactionId;
    private String item;
    private String tranDate;
    private String pAmount;
    private String aAmount;
    private String status;
    private String category;


    public ExpSingleTransaction(JsonObject s)
    {
        transactionId = s.get("Transaction id").getAsInt();
        item = s.get("Item").toString().replace("\"","");
        tranDate = s.get("Date").toString().replace("\"","");
        pAmount = s.get("PAmount").toString().replace("\"","");
        aAmount = s.get("AAmount").toString().replace("\"","");
        status = s.get("Status").toString().replace("\"","");
        try
        {
            category = s.get("Category").toString().replace("\"","");
        }catch(NullPointerException e)
        {
            category = "";
        }

    }


    public int getTranid()
    {
        return transactionId;
    }

    public void setTranid(int tId){
        transactionId = tId;
    }

    public void setItem (String s)
    {
        item = s;
    }

    public String getItem ()
    {
        return item;
    }

    public void setTranDate(String s)
    {
        tranDate = s;
    }

    public String getTranDate()
    {
        return tranDate;
    }

    public void setpAmount(String s)
    {
        pAmount = s;
    }

    public double getpAmount()
    {
        return Double.parseDouble(pAmount);
    }

    public void setaAmount(String s)
    {
        aAmount = s;
    }

    public double getaAmount()
    {
        return Double.parseDouble(aAmount);
    }

    public void setStatus(String s)
    {
        status = s;
    }

    public String getStatus()
    {
        return status;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String c){
        category = c;
    }

    public boolean isGreaterThan(ExpSingleTransaction o, String s)
    {
        String string1 = "";
        String string2 = "";

        if (s.equals("Date"))
        {
            string1 = this.getTranDate().replace("\"","");
            string2 = o.getTranDate().replace("\"","");
        }
        else if (s.equals("Desc"))
        {
            string1 = this.getItem().replace("\"","");
            string2 = o.getItem().replace("\"","");
        }
        else if (s.equals("Cat"))
        {
            string1 = this.getCategory().replace("\"","");
            string2 = o.getCategory().replace("\"","");
        }

        if (string1.compareTo(string2) < 0)
            return false;
        else if (string1.compareTo(string2) >= 0)
            return  true;
        else
            return false;

    }

    public String getTransactionMonth()
    {
        String tDate = getTranDate();
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(tDate.substring(0,4)),
                (Integer.parseInt(tDate.substring(5,7)) - 1),
                Integer.parseInt(tDate.substring(8)));


        switch(c.get(Calendar.MONTH))
        {
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

    public int getTransactionMonthNumeral()
    {
        String tDate = getTranDate();
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(tDate.substring(0,4)),
                (Integer.parseInt(tDate.substring(5,7)) - 1),
                Integer.parseInt(tDate.substring(8)));

        return c.get(Calendar.MONTH);
    }

    public boolean isPriorToCurrentDate()
    {
        Calendar c = Calendar.getInstance();
        String month;
        String day;
        if (c.get(Calendar.MONTH) + 1 < 10)
            month = "0" + (c.get(Calendar.MONTH) + 1);
        else
            month = Integer.toString(c.get(Calendar.MONTH) + 1);

        if (c.get(Calendar.DATE) < 10)
            day = "0" + (c.get(Calendar.DATE));
        else
            day = Integer.toString(c.get(Calendar.DATE));

        String tDate = getTranDate().replace("\"","");
        String currDate = c.get(Calendar.YEAR) + "-" + month + "-" + day;

        if (currDate.compareTo(tDate) < 0)
            return false;
        else
            return  true;
    }
}
