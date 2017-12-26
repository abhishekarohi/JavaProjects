import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
 
import com.ibm.as400.access.AS400JDBCConnection;
 

public class GetHostNames {
 
                private String localHosts = "/Users/abhishekarohi/Desktop/hosts.txt";
 
                private ArrayList<String> hosts = new ArrayList<String>();
                
                
                public static void main(String[] args) 
                {
                    new GetHostNames();
                }
 
                private String getJobInfo(AS400JDBCConnection connection) {
                                String wrkStr = "";
                                String jobid = connection.getServerJobIdentifier();
                                wrkStr = jobid.substring(20, 26).trim() + "/"
                                                                + jobid.substring(10, 20).trim() + "/"
                                                                + jobid.substring(0, 10).trim();
 
                                return wrkStr;
                }
 
                public static String padRight(String s, int n) {
                                return String.format("%1$-" + n + "s", s);
                }
 
                public static String padLeft(String s, int n) {
                                return String.format("%1$" + n + "s", s);
                }
 
                
                
                GetHostNames() {
 
                                try {
                                                // make sure driver exists
                                                Class.forName("com.ibm.as400.access.AS400JDBCDriver");
                                } catch (Exception e) {
                                                System.err.println(e.toString());
                                                e.printStackTrace();
                                }
 
                                AS400JDBCConnection localConnection = null;
                                String localHost = "S1016155";
                                /*String localUser = "emruser";*/
                                /*String localPassword = "emruser";*/
 
                                try {
                                             /*   localConnection = (AS400JDBCConnection) DriverManager
                                                                                .getConnection("jdbc:as400://" + localHost
                                                                                                                + ";naming=system", localUser.trim().toUpperCase(),
                                                                                                                localPassword.trim().toLowerCase());*/
                                                localConnection = (AS400JDBCConnection) DriverManager
                                                        .getConnection("jdbc:as400://" + localHost
                                                                                        + ";naming=system");
                                              
                                } catch (SQLException e1) {
                                                e1.printStackTrace();
                                }
                                System.out.println("Job Information: " + getJobInfo(localConnection));
 
                                String sql = " select max(length(trim(hostnme1))) as max1"
                                                                + " ,max(length(trim(hostnme2))) as max2"
                                                                + " ,max(length(trim(hostnme3))) as max3"
                                                                + " ,max(length(trim(hostnme4))) as max4"
                                                                + " ,max(length(trim(internet))) as max5"
                                                                + " FROM QUSRSYS.QATOCHOST";
 
                                int maxLen1 = 0;
                                int maxLen2 = 0;
                                int maxLen3 = 0;
                                int maxLen4 = 0;
                                int maxLen5 = 0;
 
                                ResultSet rs3;
                                try {
                                                rs3 = localConnection.createStatement().executeQuery(sql);
                                                while (rs3.next()) {
                                                                maxLen1 = rs3.getInt("MAX1");
                                                                maxLen2 = rs3.getInt("MAX2");
                                                                maxLen3 = rs3.getInt("MAX3");
                                                                maxLen4 = rs3.getInt("MAX4");
                                                                maxLen5 = rs3.getInt("MAX5");
                                                                // System.out.println(maxLen1 + "\t" + maxLen2 + "\t" + maxLen3
                                                                // + "\t" + maxLen4 + "\t" + maxLen5);
 
                                                }
                                                rs3.close();
                                } catch (SQLException e2) {
                                                e2.printStackTrace();
                                }
 
                                getLocalHostFile();
 
                                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                Date date = new Date();
                                hosts.add("#------------------------------------------------------");
                                hosts.add("# Updated - " + format.format(date));
                                hosts.add("#------------------------------------------------------");
 
                                sql = "SELECT INTERNET,HOSTNME1,HOSTNME2,HOSTNME3,HOSTNME4,TXTDESC "
                                                                + " FROM QUSRSYS.QATOCHOST where (HOSTNME1 <> '' "
                                                                + " or HOSTNME2 <> '' or HOSTNME3 <> '' or HOSTNME4 <> '')"
                                                                // + " and internet like  '44.0.7.%' "
                                                                + " order by internet";
                                ResultSet rs4;
                                try {
                                                rs4 = localConnection.createStatement().executeQuery(sql);
                                                while (rs4.next()) {
                                                                String ipAddress = rs4.getString("INTERNET").trim();
                                                                String hostName1 = rs4.getString("HOSTNME1").trim();
                                                                String hostName2 = rs4.getString("HOSTNME2").trim();
                                                                String hostName3 = rs4.getString("HOSTNME3").trim();
                                                                String hostName4 = rs4.getString("HOSTNME4").trim();
                                                                String description = rs4.getString("TXTDESC").trim();
                                                                // System.out.println(padRight(ipAddress, maxLen5) + "\t"
                                                                // + padRight(hostName1, maxLen1) + "\t"
                                                                // + padRight(hostName2, maxLen2) + "\t"
                                                                // + padRight(hostName3, maxLen3) + "\t"
                                                                // + padRight(hostName4, maxLen4) + "\t# " + description);
                                                                for (int x = 0; x < hosts.size(); x++) {
                                                                                String line = hosts.get(x);
                                                                                String pieces[] = parseHostsLine(line);
                                                                                if (pieces != null) {
                                                                                                for (int p = 0; p < pieces.length; p++) {
                                                                                                                if (hostName1.equalsIgnoreCase(pieces[p])
                                                                                                                                                || hostName2.equalsIgnoreCase(pieces[p])
                                                                                                                // || hostName3.equalsIgnoreCase(pieces[p])
                                                                                                                // || hostName4.equalsIgnoreCase(pieces[p])
                                                                                                                ) {
                                                                                                                                hosts.set(x, "");
                                                                                                                }
                                                                                                }
                                                                                }
                                                                }
                                                                hosts.add(padRight(ipAddress, maxLen5) + "\t"
                                                                                                + padRight(hostName1, maxLen1) + "\t"
                                                                                                + padRight(hostName2, maxLen2) + "\t"
                                                                                                + padRight(hostName3, maxLen3) + "\t"
                                                                                                + padRight(hostName4, maxLen4) + "\t# " + description);
 
                                                }
                                                rs4.close();
                                } catch (SQLException e2) {
                                                e2.printStackTrace();
                                }
 
                                // for (String line : hosts) {
                                // if (!line.trim().isEmpty()) {
                                // System.out.println(line);
                                // }
                                // }
                                createBackupOfLocalHosts();
                                writeLocalHosts();
                }
 
                public void writeLocalHosts() {
 
                                File hostsFile = new File(localHosts);
                                try {
                                                BufferedWriter writer = new BufferedWriter(
                                                                                new FileWriter(hostsFile));
                                                // SimpleDateFormat format = new SimpleDateFormat(
                                                // "dd-MM-yyyy HH:mm:ss");
                                                // Date date = new Date();
                                                // writer.write("# Updated - " + format.format(date) + "\n");
                                                for (String line : hosts) {
                                                                if (!line.trim().isEmpty()) {
                                                                                writer.write(line + "\n");
                                                                }
                                                }
                                                writer.close();
 
                                } catch (IOException e) {
                                                e.printStackTrace();
                                }
 
                }
 
                public void createBackupOfLocalHosts() {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
                                File hostsFile = new File(localHosts);
                                Date date = new Date();
                                String newName = "/temp/hosts." + format.format(date);
                                // System.out.println("New Name: [" + newName + "]");
                                File newHostsFile = new File(newName);
                                try {
                                                BufferedReader br = new BufferedReader(new FileReader(hostsFile));
                                                BufferedWriter writer = new BufferedWriter(new FileWriter(
                                                                                newHostsFile));
 
                                                String line = null;
                                                while ((line = br.readLine()) != null) {
                                                                writer.write(line + "\n");
                                                }
                                                br.close();
                                                writer.close();
                                } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                } catch (IOException e) {
                                                e.printStackTrace();
                                }
 
                }
 
                public String[] parseHostsLine(String line) {
 
                                String pieces[] = null;
 
                                if (line.contains("#")) {
                                                int p = line.indexOf("#");
                                                line = line.substring(0, p).trim();
                                }
                                if (!line.isEmpty()) {
                                                pieces = line.split("[\t ]+");
                                                // for (int y = 0; y < pieces.length; y++) {
                                                // System.out.print("[" + pieces[y] + "]");
                                                // }
                                                // System.out.println();
                                }
 
                                return pieces;
 
                }
 
                public void getLocalHostFile() {
 
                                File hostsFile = new File(localHosts);
 
                                if (!hostsFile.exists())
                                {
                                		try {
											hostsFile.createNewFile();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
                                }
                                try {
                                                BufferedReader br = new BufferedReader(new FileReader(hostsFile));
 
                                                String line = null;
                                                while ((line = br.readLine()) != null) {
                                                                hosts.add(line);
                                                }
                                                br.close();
                                } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                } catch (IOException e) {
                                                e.printStackTrace();
                                }
 
                }
 
                /**
                * @param args
                */
 
}