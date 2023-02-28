package learner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Logger  {
	Connection con;
    private String dbkeyword;
	
	public Logger(String dbLocation) {
		this(dbLocation, "any");
	}
	
	public Logger(String dbLocation, String dbkeyword) {
	    this.dbkeyword = dbkeyword;
		if (dbkeyword == null) {
			System.exit(0);
		}
		
		// Connect
		try {
	        Class.forName("org.sqlite.JDBC");
	        this.con = DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
	        this.con.setAutoCommit(false);
		} 
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public String query(String sequence)
	{
		Statement stmt;
		try {
			stmt = this.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT result FROM queries WHERE sequence='" + sequence + "'");
			if (rs.next()) {
				String cacheResult = rs.getString("result");
				rs.close();
				stmt.close();
				return cacheResult;
			}
			
			rs.close();
			stmt.close();
			return "notfound";
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
			return "sqlerror";
		}
	}
	
	public List<String> selectTracesWithPrefix(String prefix)
    {
	    List<String> traces = new ArrayList<String>();
        Statement stmt;
        try {
            stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT sequence FROM queries WHERE sequence LIKE '" + prefix + "%'");
            while(rs.next()) {
                traces.add(rs.getString("sequence"));
            }
            
            rs.close();
            stmt.close();
            return traces;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
	
	public int deleteEntriesForTracesWithPrefix(String prefix) {
		return delete("sequence", prefix, true, false);
	}
	
	public int deleteEntriesForTracesWithOutput(String output) {
		return delete("result", output, false, false);
	}
	
	
//	KEXINIT KEX30 NEWKEYS SERVICE_REQUEST_AUTH UA_PK_OK CH_OPEN KEXINIT CH_DATA DISCONNECT
	private int delete(String column, String subString, boolean leftClose, boolean rightClose) {
		int rowCount;
		Statement stmt;
		try {
			stmt = this.con.createStatement();
			rowCount = stmt.executeUpdate("DELETE FROM queries WHERE " + column + " LIKE "
					+ (leftClose?"'":"'%") 
					+ subString + (rightClose?"'":"%'"));
			stmt.close();
			if (rowCount > 1000) {
			    System.err.println("Are you sure you want to delete for prefix " + subString + " (no eof/yes neof)");
			    try {
			        int a = new InputStreamReader(System.in).read();
			        if (a == -1) {
			            this.con.rollback();
			            System.exit(0);
			        }
			    }catch(Exception e) {
			        
			    }
			}
			this.con.commit();
			return rowCount;
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
			return -1;
		}
	}
	
	public int updateColumn(String column, String oldValue, String newValue) {
		int rowCount;
		Statement stmt;
		try {
			stmt = this.con.createStatement();
			String cmdStr = "UPDATE queries SET " + column +" = '" + newValue + "'" +
			((oldValue == null || oldValue.length()==0)?"" : " WHERE "+ column +" LIKE '" + oldValue + "'");
			System.out.println(cmdStr);
			rowCount = stmt.executeUpdate(cmdStr);
			stmt.close();
			this.con.commit();
			return rowCount;
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
			return -1;
		}
	}
	
	   public int replaceColumn(String column, String oldValue, String newValue) {
	        int rowCount;
	        Statement stmt;
	        try {
	            stmt = this.con.createStatement();
	            String command = "UPDATE queries SET " + column +" = replace( "+ column + ", '" + oldValue + "','" +
                newValue + "' ) WHERE " + column +" LIKE '%" + oldValue + "%'";
	            System.out.println(command);
	            rowCount = stmt.executeUpdate(command);
	            stmt.close();
	            this.con.commit();
	            return rowCount;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            System.exit(0);
	            return -1;
	        }
	    }
	public int outputVocabulary() {
		// INTERESTING 
		// This function was made to detect nondeterminism runs
		Statement stmt;
		try {
			stmt = this.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT distinct result FROM queries");
			String alloutputs = "";
			while (rs.next()) {
				alloutputs += rs.getString("result");
			}
			
			Set<String> uniques = new HashSet<String>(Arrays.asList(alloutputs.split(" ")));
			
			rs.close();
			stmt.close();
			
			return uniques.size();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
			return -1;
		}
	}

	public void log(String sequence, String result, double duration) {
		Statement stmt;
		try {
			// Insert
			stmt = this.con.createStatement();
			stmt.executeUpdate("INSERT INTO queries (keyword, sequence, result, duration) VALUES('" + this.dbkeyword + "', '" + sequence + "', '" + result + "', '" + duration + "')");
			stmt.close();
			this.con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			this.con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	

	
	public static void main(String args []) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		PrintStream out = System.out;
		Logger logger = null;
		List<String> columns = Arrays.asList("keyword", "sequence", "result");
		// useless opt fun
		boolean exit = false;
		do {
			out.print("Operations Available: \n"
					+ "0. Load DB \n"
					+ "1. Update column \n"
					+ "2. Update (Replace) column \n"
					+ "3. Remove all entries with prefix \n"
					+ "4. Remove all entries with output \n"
					+ "5. Query for trace \n"
					+ "6. Select traces with prefix \n"
					+ "7. Custom (Remove inconsistencies) \n" 
					+ "8. Exit (Default Operation) \n" );
			int read = Integer.valueOf(br.readLine());
			int rowCount;
			String ans;
			switch(read) {
			case 0:
				out.println("Load database");
				out.println("Database name:");
				String dbName = br.readLine();
				logger = new Logger(dbName);
				out.print("Database loaded: " + dbName);
				break;
			case 1:
			    out.println("Selected Column from "  + columns);
			    String column = br.readLine().trim().toLowerCase();
			    if (!columns.contains(column)) {
			        System.err.println("Column " + column + " does not exist");
			        continue;
			    } else {
    				out.println("Selected "+ column + " Update");
    				out.println("Old Value (empty means all entries):");
    				String oldVal = br.readLine();
    				out.println("New Value:");
    				String newVal = br.readLine();
    				rowCount = logger.updateColumn(column, oldVal, newVal);
    				out.println(rowCount + " rows updated");
			    }
				break;
			case 2:
                out.println("Selected Column from "  + columns);
                column = br.readLine().trim().toLowerCase();
                if (!columns.contains(column)) {
                    System.err.println("Column " + column + " does not exist");
                    continue;
                } else {
                    out.println("Selected "+ column + " Update");
                    out.println("Old String occurrence (empty means all entries):");
                    String oldVal = br.readLine();
                    out.println("New Value:");
                    String newVal = br.readLine();
                    rowCount = logger.replaceColumn(column, newVal, oldVal);
                    out.println(rowCount + " rows updated");
                }
			case 3:
				out.println("Selected Entry Removal");
				out.println("Prefix of traces to be removed:");
				String prefix = br.readLine();
				rowCount = logger.deleteEntriesForTracesWithPrefix(prefix);
				out.println(rowCount + " rows removed");
				break;
			case 4:
				out.println("Selected Output Removal");
				out.println("Output for traces to be removed:");
				String output = br.readLine();
				rowCount = logger.deleteEntriesForTracesWithOutput(output);
				out.println(rowCount + " rows removed");
				break;
			case 5:
			    out.println("Query fortrace");
			    out.println("Prefix for traces to be selected:");
                prefix = br.readLine();
                ans = logger.query(prefix);
                out.println(ans);
			    break;
			
			case 6:
			    out.println("Get existing traces with prefix");
			    out.println("Prefix for traces to be selected:");
                prefix = br.readLine();
                List<String> resSel = logger.selectTracesWithPrefix(prefix);
                out.println(resSel);
                out.println("Num Traces: " + resSel.size());
			    break;
			case 7:
			    out.println("Give databases to normalize sep. by ;");
			    String[] dbs = br.readLine().split(";");
			    // to make alphabets consistent again
			    for (String db : dbs) {
			        Logger dbLogger = new Logger(db.trim());
			        int row = 0;
			        row =+dbLogger.replaceColumn("sequence", "UNIMPLEMENTED", "UNIMPL");
			        row =+dbLogger.replaceColumn("sequence", "SERVICE_REQUEST_AUTH", "SR_AUTH");
			        row =+dbLogger.replaceColumn("sequence", "SERVICE_REQUEST_CONN", "SR_CONN");
			        row =+dbLogger.replaceColumn("result", "UNIMPLEMENTED", "UNIMPL");
			        row =+dbLogger.replaceColumn("result", "SERVICE_ACCEPT", "SR_ACCEPT");
			        row =+dbLogger.replaceColumn("result", "+", "_");
			        System.out.println(row + "rows changed in " + db);
			    }
			    break;
			default:
				out.println("Selected Exit");
				logger.close();
				exit = true;
			}
			
		} while(!exit);
	}
	
}
