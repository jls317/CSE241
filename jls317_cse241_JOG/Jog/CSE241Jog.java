/*
Jeffrey Stewart
jls317
CSE241
04/19/2016

Project Jog
 */
package cse241.jog;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Scanner;
import static jdk.nashorn.internal.runtime.JSType.isNumber;

public class CSE241Jog {

    static int month, day, year, hour, minute;
    static String pass;
    static Connection con = null;
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            //get input from user
            /*Console console = System.console();
           if (console == null) {
        System.out.println("Couldn't get Console instance");
        System.exit(0);
    }*/
            String user = " ";
            String pass = " ";
            boolean x, y;
            x = true;
            y = true;
            String option = " ";

            while (x) {
                try {
                    System.out.println("Enter your username: ");
                    System.out.print(">>");
                    user = input.next();
                    if (user.equals("exit")) {
                        return;
                    }
                    System.out.println("Enter your password: ");
                    System.out.print(">>");
                    //char[] passString = console.readPassword();
                    pass = input.next();
                    if (pass.equals("exit")) {
                        return;
                    }
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    con = DriverManager.getConnection(
                            "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", user,
                            pass);
                } catch (SQLException se) {
                    if (pass.equals("esc") || user.equals("esc")) {
                        System.out.println("Exiting Program.");
                        return;
                    } else {
                        System.out.println("Invalid Username/Password.");
                        System.out.println("Try Again. Use \"esc\" as username or password to exit.");
                        continue;
                    }
                }
                x = false;
            }
            System.out.println("Connected.");
            System.out.println();
            while (y) {
                System.out.println("Please Select One of the Following Options:");
                System.out.println("1. Store List");
                System.out.println("2. Inventory Management");
                System.out.println("3. Billing Report");
                System.out.println("4. Exit");
                System.out.print(">>");
                option = input.next();
                if (option.equals("1")) {
                    stores();
                } else if (option.equals("2")) {
                    System.out.println("Inventory Management");
                    inventory();
                } else if (option.equals("3")) {
                    report();
                } else if (option.equals("4")) {
                    System.out.println("Exiting.");
                    y = false;
                } else if (option.equals("*")) {
                    System.out.println("Exiting.");
                    y = false;
                } else {
                    System.out.println("Invalid Option.");
                }
                System.out.println("");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void inventory() {
        try {
            String store = " ";
            String itemNum = " ";
            String query1 = " ";
            String query2 = " ";
            String query3 = " ";
            String stock;
            String capacity;
            int stockNum = 0;
            int capNum = 0;
            int orderNum = 0;
            int newStock = 0;
            boolean storeFound = true;
            boolean itemFound = true;
            boolean itemOrder = true;
            ResultSet result1, result2, result3;
            Statement storeItems = con.createStatement();

            while (storeFound) {
                System.out.println("Store Number: ");
                System.out.print(">>");
                store = input.next();
                if (store.equals("*")) {
                    return;
                } else if (!isNumber(store)) {
                    System.out.println("Store Not Found.");
                    System.out.println();
                } else {
                    System.out.println();

                    query1 = "SELECT PRODUCT_ID, DESCRIPTION FROM PRODUCTS WHERE STORE_ID = " + store + "";
                    result1 = storeItems.executeQuery(query1);

                    if (!result1.next()) {
                        System.out.println("Store Not Found.");
                    } else {
                        storeFound = false;
                        System.out.println("Inventory of Store Number: " + store + " ");
                        System.out.println("--------------------------------");
                        do {
                            System.out.printf("%-8s %-20s \n", result1.getString("PRODUCT_ID"), result1.getString("DESCRIPTION"));
                        } while (result1.next());
                    }
                    System.out.println();
                }
            }
            while (true) {
                while (itemFound) {
                    System.out.println("Product ID to Order: ");
                    System.out.print(">>");
                    itemNum = input.next();
                    if (itemNum.equals("*")) {
                        return;
                    }
                    query2 = "SELECT STOCK, CAPACITY FROM PRODUCTS WHERE STORE_ID = " + store + "AND PRODUCT_ID = " + itemNum + "";
                    result2 = storeItems.executeQuery(query2);

                    if (!result2.next()) {
                        System.out.println("Item Not Found.");

                    } else {
                        itemFound = false;
                        stock = result2.getString("Stock");
                        capacity = result2.getString("Capacity");
                        stockNum = Integer.parseInt(stock);
                        capNum = Integer.parseInt(capacity);
                        System.out.println();
                        System.out.printf("%-8s %-8s \n", "Stock", "Capacity");
                        System.out.printf("%-8s %-8s \n", stock, capacity);
                    }
                }
                while (itemOrder) {
                    System.out.println("Net Change (+Order/-Sales): ");
                    System.out.print(">>");
                    if (input.hasNextInt()) {
                        orderNum = input.nextInt();
                        newStock = orderNum + stockNum;
                        if (newStock > capNum) {
                            System.out.println("Exceeds Capacity.");
                        } else if (newStock < 0) {
                            System.out.println("Neg. Stock.");
                        } else {
                            query3 = "UPDATE PRODUCTS SET STOCK = " + newStock + "WHERE STORE_ID= " + store + " AND PRODUCT_ID= " + itemNum + " ";
                            result3 = storeItems.executeQuery(query3);
                            itemOrder = false;
                        }
                    } else {
                        if ((input.next()).equals("*")) {
                            return;
                        }
                        System.out.println("Enter Integer.");
                    }
                }
                System.out.println("Item: " + itemNum);
                System.out.printf("%-8s %-8s \n", "Stock", "Capacity");
                System.out.printf("%-8s %-8s \n", newStock, capNum);
                System.out.println();
                itemOrder = true;
                itemFound = true;
            }
        } catch (SQLException e) {
            System.out.println("Process Failed.");
        }

    }

    public static void stores() {
        try {
            String query1 = "SELECT * FROM STORE";
            ResultSet result1;
            Statement stores = con.createStatement();
            result1 = stores.executeQuery(query1);
            if (!result1.next()) {
                System.out.println("No Stores.");
            } else {
                System.out.printf("%-15s %-20s \n", "Store Number", "Location");
                do {
                    System.out.printf("%-15s %-20s \n", result1.getString("STORE_ID"), result1.getString("LOCATION"));
                } while (result1.next());
            }
        } catch (SQLException S) {
            System.out.println("Process failed.");
        }
    }

    public static void report() {
        String billOption;
        boolean z = true;
        while (z) {
            System.out.println("Billing Report");
            System.out.println("Please select one of the following options:");
            System.out.println("1. Current Customers");
            System.out.println("2. Generate Report");
            System.out.println("3. Return to Main Menu");
            System.out.print(">>");
            billOption = input.next();
            if (billOption.equals("1")) {
                currentCustomers();
            } else if (billOption.equals("2")) {
                genReport();
            } else if (billOption.equals("3")) {
                return;
            } else if (billOption.equals("*")) {
                return;
            } else {
                System.out.println("Invalid Option");
                System.out.println();
            }

        }

    }

    public static void genReport() {
        int i = 0;
        int count = 0;
        String acctNum = null, repYear, repMonth;
        int repYear1 = 0;
        int repMonth1 = 0;
        String[] accounts = null;
        boolean acctCheck = true;
        boolean monthCheck = true;
        Statement customers = null;

        //check for valid number
        //initialize string[] accounts
        //get count of accounts (string size)
        try {
            customers = con.createStatement();
            String q2 = "SELECT COUNT(ACCTID) FROM PROVIDES WHERE END_DATE IS NULL";
            ResultSet r2 = customers.executeQuery(q2);
            if (!r2.next()) {
            } else {
                do {
                    count = Integer.parseInt(r2.getString("COUNT(ACCTID)"));
                } while (r2.next());
            }
            accounts = new String[count];
        } catch (SQLException S) {
            System.out.println("S Fail");
        }
        //get input of account number  
        while (acctCheck) {
            System.out.println("Enter a Current Account Number: ");
            System.out.print(">>");
            acctNum = input.next();

            if (acctNum.equals("*")) {
                return;
            }
            try {
                Statement customers2 = con.createStatement();
                String q1 = "SELECT ACCTID FROM PROVIDES WHERE END_DATE IS NULL";
                ResultSet r1 = customers2.executeQuery(q1);

                if (!r1.next()) {
                    System.out.println("No Accounts.");
                } else {
                    do {
                        accounts[i] = r1.getString("ACCTID");
                        i++;
                    } while (r1.next());
                }
            } catch (SQLException V) {
                System.out.println("Failed to Verify Account.");
            }
            //compare input against all values
            for (int j = 0; j < count; j++) {
                if (accounts[j].equals(acctNum)) {
                    acctCheck = false;
                }
            }
            if (!acctCheck) {
                break;
            }
            System.out.println("Not a Current Account Number!");
            System.out.println();
            i = 0;
        }

        //get month and year
        while (monthCheck) {
            System.out.println("Enter the desired year (YYYY):");
            System.out.print(">>");
            repYear = input.next();
            if (repYear.equals("*")) {
                return;
            }
            System.out.println("Enter the desired month (MM or \"month\")");
            System.out.print(">>");
            repMonth = input.next();
            if (repMonth.equals("*")) {
                return;
            }
            try {
                repYear1 = Integer.parseInt(repYear);
            } catch (NumberFormatException N) {

            }
            repMonth1 = monthInt(repMonth);
            if (repYear1 > 2016 || repYear1 < 1900 || repMonth1 == 0) {
                System.out.println("Invalid date.");
                System.out.println();
            } else {
                monthCheck = false;
            }
        }
        if (!monthCheck) {
            System.out.println("Generating Report.");
            generateReport(acctNum, repYear1, repMonth1);
        }
    }

    public static void generateReport(String acct, int inYear, int inMonth) {
        int[][] ttime = null;
        double[] tsize = null;
        int[][] dtime = null;
        double[] dsize = null;
        int[][] ptime = null;
        int[] psize = null;
        int phoneday1, phonehour1, phonemin1, phoneday2, phonehour2, phonemin2, duration;
        double totalT = 0, textcost = 0;
        double totalD = 0, datacost = 0;
        double totalP = 0, phonecost = 0;
        double othercost = 0;
        double totalcost;
        String rawTime, rawTime2, address1 = null, address2 = null, name = null;
        Statement report = null;

        int counttext = 0;
        int countdata = 0;
        int countphone = 0;
        int i = 0;
        //plan info
        double fixmin = 0, fixtext = 0, fixdata = 0, uuf = 0, uuu = 0, fuu = 0;
        //Personal Info
        try {
            //Personal Info
            report = con.createStatement();
            String persons = "SELECT FIRST_NAME, LAST_NAME, STREET_ADDRESS, CITY, STATE, ZIP FROM CUSTOMER NATURAL JOIN ACCOUNT WHERE ACCTID = " + acct + "";
            ResultSet p = report.executeQuery(persons);
            if (!p.next()) {
            } else {
                do {
                    name = p.getString("LAST_NAME") + ", " + p.getString("FIRST_NAME");
                    address1 = p.getString("STREET_ADDRESS");
                    address2 = p.getString("CITY") + " " + p.getString("STATE") + " " + p.getString("ZIP");
                } while (p.next());
            }

            //Text Arrays Initialize
            String q1 = "SELECT COUNT(TIME) FROM SERVICES WHERE ACCTID = " + acct + " AND TEXT_SIZE IS NOT NULL";
            ResultSet r1 = report.executeQuery(q1);
            if (!r1.next()) {
            } else {
                do {
                    counttext = Integer.parseInt(r1.getString("COUNT(TIME)"));
                } while (r1.next());
            }
            if (counttext != 0) {
                tsize = new double[counttext];
                ttime = new int[counttext][2];
                //Text Time and Size
                String text = "SELECT TIME, TEXT_SIZE FROM SERVICES WHERE ACCTID = " + acct + " AND TEXT_SIZE IS NOT NULL";
                ResultSet rtext = report.executeQuery(text);
                if (!rtext.next()) {
                } else {
                    i = 0;
                    do {
                        rawTime = rtext.getString("TIME");
                        extractTime(rawTime);
                        ttime[i][0] = month;
                        ttime[i][1] = year;
                        tsize[i] = Double.parseDouble(rtext.getString("TEXT_SIZE"));
                        i++;
                    } while (rtext.next());
                }
            }

            //Data Arrays Initialize
            String dataq = "SELECT COUNT(TIME) FROM SERVICES WHERE ACCTID = " + acct + " AND DATA_BYTES IS NOT NULL";
            ResultSet rd1 = report.executeQuery(dataq);
            if (!rd1.next()) {
            } else {
                do {
                    countdata = Integer.parseInt(rd1.getString("COUNT(TIME)"));
                } while (rd1.next());
            }
            if (countdata != 0) {
                dsize = new double[countdata];
                dtime = new int[countdata][2];
                //Data Time and Size
                String data = "SELECT TIME, DATA_BYTES FROM SERVICES WHERE ACCTID = " + acct + " AND DATA_BYTES IS NOT NULL";
                ResultSet rdata = report.executeQuery(data);
                if (!rdata.next()) {
                } else {
                    i = 0;
                    do {
                        rawTime = rdata.getString("TIME");
                        extractTime(rawTime);
                        dtime[i][0] = month;
                        dtime[i][1] = year;

                        dsize[i] = Double.parseDouble(rdata.getString("DATA_BYTES"));
                        i++;
                    } while (rdata.next());
                }
            }

            //Phone Arrays Initialize
            String phoneq = "SELECT COUNT(CALL_START_TIME) FROM SERVICES WHERE ACCTID = " + acct + " AND TIME IS NULL";
            ResultSet p1 = report.executeQuery(phoneq);
            if (!p1.next()) {
                countphone = 0;
            } else {
                do {
                    countphone = Integer.parseInt(p1.getString("COUNT(CALL_START_TIME)"));

                } while (p1.next());
            }
            if (countphone != 0) {
                psize = new int[countphone];
                ptime = new int[countphone][2];
                //Phone Time and Size
                String phoneusage = "SELECT (CALL_START_TIME),(CALL_END_TIME) FROM SERVICES WHERE ACCTID = " + acct + " AND TIME IS NULL";
                ResultSet phoner = report.executeQuery(phoneusage);
                if (!phoner.next()) {
                } else {
                    i = 0;
                    do {
                        rawTime = phoner.getString("CALL_START_TIME");
                        extractTime(rawTime);
                        ptime[i][0] = month;
                        ptime[i][1] = year;
                        phoneday1 = day;
                        phonehour1 = hour;
                        phonemin1 = minute;
                        rawTime2 = phoner.getString("CALL_END_TIME");
                        extractTime(rawTime2);
                        phoneday2 = day;
                        phonehour2 = hour;
                        phonemin2 = minute;

                        duration = calcDuration(phoneday1, phoneday2, phonehour1, phonehour2, phonemin1, phonemin2);

                        psize[i] = duration;
                        i++;
                    } while (phoner.next());
                }
            }

            //plan info
            String plan = "SELECT FIXED_MIN, FIXED_TEXT, FIXED_DATA, UUF_MONTH, UUU_MONTH, FUU_MONTH FROM PLAN WHERE ACCTID =" + acct + "";
            ResultSet planresult = report.executeQuery(plan);
            if (!planresult.next()) {
            } else {
                do {
                    fixmin = Double.parseDouble(planresult.getString("FIXED_MIN"));
                    fixtext = Double.parseDouble(planresult.getString("FIXED_TEXT"));
                    fixdata = Double.parseDouble(planresult.getString("FIXED_DATA"));
                    uuf = Double.parseDouble(planresult.getString("UUF_MONTH"));
                    uuu = Double.parseDouble(planresult.getString("UUU_MONTH"));
                    fuu = Double.parseDouble(planresult.getString("FUU_MONTH"));

                } while (planresult.next());
            }

        } catch (SQLException S) {
            System.out.println("S Fail");
        }

        //text
        for (int k = 0; k < counttext; k++) {
            if ((ttime[k][0] == inMonth) && (ttime[k][1] == inYear)) {
                totalT += tsize[k];
            }
        }
        //data

        for (int k = 0; k < countdata; k++) {
            if ((dtime[k][0] == inMonth) && (dtime[k][1] == inYear)) {
                totalD += dsize[k];
            }
        }

        //phone
        for (int k = 0; k < countphone; k++) {
            if ((ptime[k][0] == inMonth) && (ptime[k][1] == inYear)) {
                totalP += psize[k];

            }
        }
        if (uuf != 0) {     //unlimited call/text, fixed data
            othercost = uuf;
            datacost = totalD * fixdata;
            totalcost = othercost + datacost;
        } else if (uuu != 0) {
            othercost = uuu;
            totalcost = othercost;
        } else if (fuu != 0) {
            othercost = fuu;
            phonecost = totalP * fixmin;
            totalcost = othercost + phonecost;
        } else {
            phonecost = totalP * fixmin;
            datacost = totalD * fixdata;
            textcost = totalT * fixtext;
            totalcost = phonecost + datacost + textcost;
        }
        System.out.println("Printing Report...");
        constructReport(acct, name, address1, address2, inMonth, inYear, fixmin, fixtext, fixdata, uuf, uuu, fuu, textcost, datacost, phonecost, othercost, totalcost, totalD, totalP, totalT);
        System.out.println("Process Complete.");
        System.out.println();
    }

    public static void currentCustomers() {
        try {
            Statement customers = con.createStatement();
            String q1 = "SELECT ACCTID FROM PROVIDES WHERE END_DATE IS NULL";
            ResultSet r1 = customers.executeQuery(q1);

            if (!r1.next()) {
                System.out.println("No Accounts.");
            } else {
                System.out.printf("%-7s \n", "Customer ID");
                do {
                    System.out.printf("%-17s \n", r1.getString("ACCTID"));
                } while (r1.next());
            }
            System.out.println("");
        } catch (SQLException S) {
            System.out.println("Process failed.");
        }
    }

    public static void extractTime(String time) {
        String sMonth, sDay, sYear, sHour, sMinute;
        sMonth = time.substring(0, 2);
        month = Integer.parseInt(sMonth);
        sDay = time.substring(3, 5);
        day = Integer.parseInt(sDay);
        sYear = time.substring(6, 10);
        year = Integer.parseInt(sYear);
        sHour = time.substring(11, 13);
        hour = Integer.parseInt(sHour);
        sMinute = time.substring(14);
        minute = Integer.parseInt(sMinute);
    }

    public static int monthInt(String month) {
        int monthInt = 0;
        switch (month) {
            case "01":
            case "January":
            case "january":
                monthInt = 1;
                break;
            case "02":
            case "February":
            case "february":
                monthInt = 2;
                break;
            case "03":
            case "March":
            case "march":
                monthInt = 3;
                break;
            case "04":
            case "April":
            case "april":
                monthInt = 4;
                break;
            case "05":
            case "May":
            case "may":
                monthInt = 5;
                break;
            case "06":
            case "June":
            case "june":
                monthInt = 6;
                break;
            case "07":
            case "July":
            case "july":
                monthInt = 7;
                break;
            case "08":
            case "August":
            case "august":
                monthInt = 8;
                break;
            case "09":
            case "September":
            case "september":
                monthInt = 9;
                break;
            case "10":
            case "October":
            case "october":
                monthInt = 10;
                break;
            case "11":
            case "November":
            case "november":
                monthInt = 11;
                break;
            case "12":
            case "December":
            case "december":
                monthInt = 12;
                break;
        }
        return monthInt;
    }

    public static int calcDuration(int day1, int day2, int hour1, int hour2, int min1, int min2) {

        int daydur, hourdur, mindur, duration;
        daydur = (day2 - day1) * 24 * 60;
        hourdur = (hour2 - hour1) * 60;
        mindur = (min2 - min1);
        duration = daydur + hourdur + mindur;

        return duration;
    }

    public static void constructReport(String acct, String name, String address1, String address2, int inMonth, int inYear, double fixmin, double fixtext, double fixdata, double uuf, double uuu, double fuu, double textcost, double datacost, double phonecost, double othercost, double totalcost, double totalD, double totalP, double totalT) {
        Writer writer = null;
        java.util.Date date = new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));
        DecimalFormat df = new DecimalFormat("#.##");
        totalT = Double.valueOf(df.format(totalT));
        totalP = Double.valueOf(df.format(totalP));
        totalD = Double.valueOf(df.format(totalD));
        textcost = Double.valueOf(df.format(textcost));
        phonecost = Double.valueOf(df.format(phonecost));
        datacost = Double.valueOf(df.format(datacost));
        totalcost = Double.valueOf(df.format(totalcost));
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("BillingReports/" + acct + "_" + inMonth + "_" + inYear + ".txt"), "utf-8"));
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Jog Wireless                                      " + new Timestamp(date.getTime()) + "");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Billing Report for Account: " + acct + "                         " + inMonth + "/" + inYear + "");
            writer.write("\r\n");
            writer.write(name);
            writer.write("\r\n");
            writer.write(address1);
            writer.write("\r\n");
            writer.write(address2);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("-----------------------------------------------------------------------------------");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Current Plan Information:");
            writer.write("\r\n");
            if (uuf != 0) {
                writer.write("Unlimited Calls and Texts: " + uuf);
                writer.write("\r\n");
                writer.write("Data Charge per Byte:      " + fixdata);
                writer.write("\r\n");
            } else if (uuu != 0) {
                writer.write("Unlimited Calls, Texts, and Data: " + uuu);
                writer.write("\r\n");
            } else if (fuu != 0) {
                writer.write("Unlimited Texts and Data: " + fuu);
                writer.write("\r\n");
                writer.write("Call Charge per Minute:   " + fixmin);
                writer.write("\r\n");
            } else {
                writer.write("Call Charge per Minute: " + fixmin);
                writer.write("\r\n");
                writer.write("Text Charge per Byte:   " + fixtext);
                writer.write("\r\n");
                writer.write("Data Charge per Byte:   " + fixdata);
                writer.write("\r\n");
            }
            writer.write("\r\n");
            writer.write("-----------------------------------------------------------------------------------");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Bill Breakdown: ");
            writer.write("\r\n");
            if (uuf != 0) {
                writer.write("Unlimited Calls and Texts: " + uuf);
                writer.write("\r\n");
                writer.write("Call Usage: " + totalP + " minutes = FREE");
                writer.write("\r\n");
                writer.write("Text Usage: " + totalT + " Bytes   = FREE");
                writer.write("\r\n");
                writer.write("Data Usage: " + totalD + " Bytes   = $" + datacost);
                writer.write("\r\n");
            } else if (uuu != 0) {
                writer.write("Unlimited Calls, Texts, and Data: " + uuu);
                writer.write("\r\n");
                writer.write("Call Usage: " + totalP + " minutes = FREE");
                writer.write("\r\n");
                writer.write("Text Usage: " + totalT + " Bytes   = FREE");
                writer.write("\r\n");
                writer.write("Data Usage: " + totalD + " Bytes   = FREE");
                writer.write("\r\n");
            } else if (fuu != 0) {
                writer.write("Unlimited Texts and Data: " + fuu);
                writer.write("\r\n");
                writer.write("Call Usage: " + totalP + " minutes = $" + phonecost);
                writer.write("\r\n");
                writer.write("Text Usage: " + totalT + " Bytes   = FREE");
                writer.write("\r\n");
                writer.write("Data Usage: " + totalD + " Bytes   = FREE");
                writer.write("\r\n");
            } else {
                writer.write("Call Usage: " + totalP + " minutes = $" + phonecost);
                writer.write("\r\n");
                writer.write("Text Usage: " + totalT + " Bytes   = $" + textcost);
                writer.write("\r\n");
                writer.write("Data Usage: " + totalD + " Bytes   = $" + datacost);
                writer.write("\r\n");
            }
            writer.write("Total= $" + totalcost);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Due by the end of " + (inMonth + 1) + "/" + inYear + ". ");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Send Payment To: Hank Korth");
            writer.write("\r\n");
            writer.write("                 Packard Lab 414");
            writer.write("\r\n");
            writer.write("                 19 Memorial Drive West");
            writer.write("\r\n");
            writer.write("                 Bethlehem, PA 18015");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("Thank you for choosing Jog Wireless!");

        } catch (IOException ex) {
            System.out.println("IOException in Print");
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }
    }
}
