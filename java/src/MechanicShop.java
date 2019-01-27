/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.*;
import java.sql.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
// GUI imports:
import javax.swing.*;
import java.awt.event.*;
import java.awt.Frame;
import java.awt.*;
import java.awt.Color.*;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import java.util.Calendar;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Random;
import javax.swing.SpinnerDateModel;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import java.nio.ByteBuffer; // ByteBuffer for random seed

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
    public static GUI gui;
    public static MechanicShop esql;
    public static boolean vinfound = false;
    public static boolean validcustomer = false;
    public static boolean validrid = false;
    public static boolean validmech_id = false;
	public static boolean validcldate = false;
	public static boolean validowner = false;

    //reference to physical database connection
	private Connection _connection = null;

	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	public static void disconnectFromDB(MechanicShop esql){
		try{
			if(esql != null) {
				System.out.print("Disconnecting from database...");
				esql.cleanup();
				System.out.println("Done\n\nBye !");

			}//end if
		}catch(Exception e){
			// ignored.
		}
	}


	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if

        esql = null;
		// initialize the gui:

		gui = new GUI();

		// try random int

		for (int i = 0; i<10; i++){
			int x = getRandomInt();
			}



		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");

            }catch(Exception e){

			    System.out.println(System.getProperty("java.library.path"));
				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");

			boolean keepon = true;
			/* while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");

				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION

				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			} */




		}catch(Exception e){
			System.err.println (e.getMessage ());
		}

		/*finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");

				}//end if				
			}catch(Exception e){
				// ignored.
			}
		} // end finally */
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void AddCustomer(MechanicShop esql){//1

		try {


		boolean goodint = false;
		boolean goodid = false;
		int newid = -1;
			// loop, picking random numbers, until good id is found:
			while (goodid == false)
			{
				while(!goodint)
				{
					// get random int from a randomizer function
					newid = getRandomInt();
					if (newid < 0)
					{
						goodint = false;
					}
					else{
						goodint = true;
					}
				}
				// check if that int is already used in DB:
				String q = "SELECT * FROM Customer c WHERE c.id =" + newid + ";";
				// get num rows
				int num_rows = esql.executeQueryAndPrintResult(q);
					if (num_rows == 0){
						goodid = true;
					}
					else{
						goodid = false;
					}

				}

			String res2 = "";
		// get the rest of the values fron gui text fields
		String fname_text = gui.getTextFromField(gui.cfname_t);
		String lname_text = gui.getTextFromField(gui.clname_t);
		String phone_text = gui.getTextFromField(gui.cphone_t);
		String address_text = gui.getTextFromField(gui.caddress_t);

		String q2 = "INSERT INTO Customer(id, fname, lname, phone, address) VALUES(" + String.valueOf(newid) + ",'" + fname_text + "','" + lname_text + "','" + phone_text + "','" + address_text + "');";
		System.out.println("TEST: " + q2);
		esql.executeQuery(q2);

		String q3 = "SELECT c.id, c.fname, c.lname, c.phone, c.address FROM Customer c ORDER BY c.id DESC LIMIT 1;";

		List<List<String>> result2 = esql.executeQueryAndReturnResult(q3);

		for (List<String> l:result2){
			res2 += "\n";
			for (String s:l){
				res2 = res2 + s.trim() + " ";
			}
		}

		int rowCount = esql.executeQueryAndPrintResult(q3);

		gui.loadResults(res2);
		gui.loadNumRows(String.valueOf(rowCount));

		} catch(Exception e){
		System.err.println (e.getMessage());
		}

	}

	/** public static void AddSRCustomer(MechanicShop esql){//1

		try {
			//gui.hiddencnum = 0;

			boolean goodint = false;
			boolean goodid = false;
			int newid = -1;
			// loop, picking random numbers, until good id is found:
			while (goodid == false)
			{
				while(!goodint)
				{
					// get random int from a randomizer function
					newid = getRandomInt();
					if (newid < 0)
					{
						goodint = false;
					}
					else{
						goodint = true;
					}
				}
				// check if that int is already used in DB:
				String q = "SELECT * FROM Customer c WHERE c.id =" + newid + ";";
				// get num rows
				int num_rows = esql.executeQueryAndPrintResult(q);
				if (num_rows == 0){
					goodid = true;
				}
				else{
					goodid = false;
				}

			}
			gui.customeridforsr.setText(String.valueOf(newid));

			//gui.hiddencnum = newid;

			String res2 = "";
			// get the rest of the values fron gui text fields
			String fname_text = gui.getTextFromField(gui.sr_cfname_t);
			String lname_text = gui.getTextFromField(gui.sr_clname_t);
			String phone_text = gui.getTextFromField(gui.sr_cphone_t);
			String address_text = gui.getTextFromField(gui.sr_caddress_t);

			String q2 = "INSERT INTO Customer(id, fname, lname, phone, address) VALUES(" + String.valueOf(newid) + ",'" + fname_text + "','" + lname_text + "','" + phone_text + "','" + address_text + "');";
			esql.executeQuery(q2);

		} catch(Exception e){
			System.err.println (e.getMessage());
		}

	}
	*/
	
	public static void AddMechanic(MechanicShop esql){//2

		try {

			boolean goodint = false;
			boolean goodid = false; // assume id is bad, until proven otherwise
			int newid = -1;
			// loop, picking random numbers, until good id is found:
			while (goodid == false)
			{
				while(!goodint)
				{
					// get random int from a randomizer function
					newid = getRandomInt();
					if (newid < 0)
					{
						goodint = false;
					}
					else{
						goodint = true;
					}
				}
				// check if that int is already used in DB:
				String q = "SELECT * FROM Mechanic m WHERE m.id =" + newid + ";";
				// get num rows
				int num_rows = esql.executeQueryAndPrintResult(q);
				if (num_rows == 0){
					goodid = true;
				}
				else{
					goodid = false;
				}

			}

			// get the rest of the values fron gui text fields
			String fname_text = gui.getTextFromField(gui.mfname_t);
			String lname_text = gui.getTextFromField(gui.mlname_t);
			String experience_text = gui.getTextFromField(gui.m_years_t);

			String q2 = "INSERT INTO Mechanic(id, fname, lname, experience) VALUES(" + String.valueOf(newid) + ",'" + fname_text + "','" + lname_text + "'," + experience_text + ");";
			esql.executeQuery(q2);

		} catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void InsertIntoCars(MechanicShop esql){
		try{
			// get the values fron gui text fields
		String vin_text = gui.getTextFromField(gui.vin_t);
		String make_text = gui.getTextFromField(gui.make_t);
		String model_text = gui.getTextFromField(gui.model_t);
		String year_text = gui.getTextFromField(gui.year_t);
		String cust_text = gui.getTextFromField(gui.owner_t);

		String q2 = "INSERT INTO Car(vin, make, model, year) VALUES('" + vin_text.toUpperCase() + "','" + make_text + "','" + model_text + "'," + Integer.valueOf(year_text) + ");";
		//String q3 = "INSERT INTO Owns VALUES (" + newid + ", " + Integer.valueOf(cust_text) +  ", " + vin_text + ");";
		int a = esql.executeQueryAndPrintResult(q2);
				if (a == 0)
				{
					// do nothing - solely for the query to finish
				}

			}
			catch(Exception e)
			{
				System.err.println (e.getMessage());
			}

	}

	public static void InsertIntoOwns(int id, MechanicShop esql){
		try {
			// get the values fron gui text fields
			String vin_text = gui.getTextFromField(gui.vin_t);
			String make_text = gui.getTextFromField(gui.make_t);
			String model_text = gui.getTextFromField(gui.model_t);
			String year_text = gui.getTextFromField(gui.year_t);
			String cust_text = gui.getTextFromField(gui.owner_t);
			int newid = id;

			String q3 = "INSERT INTO Owns VALUES (" + newid + ", " + Integer.valueOf(cust_text) + ", '" + vin_text.toUpperCase() + "');";
			int b = esql.executeQueryAndPrintResult(q3);

			if (b == 0){
				// do nothing
			}

		} catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void AddCar(MechanicShop esql){//3

		try {

			// GENERATE NEW RANDOM ownership id

			boolean goodint = false;
			boolean goodid = false; // assume id is bad, until proven otherwise
			int newid = -1;
			// loop, picking random numbers, until good id is found:
			while (goodid == false)
			{
				while(!goodint)
				{
					// get random int from a randomizer function
					newid = getRandomInt();
					if (newid < 0)
					{
						goodint = false;
					}
					else{
						goodint = true;
					}
				}
				// check if that int is already used in DB:
				String q = "SELECT * FROM Owns o WHERE o.ownership_id =" + newid + ";";
				// get num rows
				int num_rows = esql.executeQueryAndPrintResult(q);
				if (num_rows == 0){
					goodid = true;
				}
				else{
					goodid = false;
				}
			}

			InsertIntoCars(esql);
			InsertIntoOwns(newid, esql);

		} catch(Exception e){
			System.err.println (e.getMessage());
		}
		
	}

	public static void CloseSR(MechanicShop esql){//3

		try {
			// FIND a wid:
			boolean goodint = false;
			boolean goodid = false;
			int newid = -1;
			// loop, picking random numbers, until good id is found:
			while (goodid == false)
			{
				while(!goodint)
				{
					// get random int from a randomizer function
					newid = getRandomInt();
					if (newid < 0)
					{
						goodint = false;
					}
					else{
						goodint = true;
					}
				}
				// check if that int is already used in DB:
				String q = "SELECT * FROM closed_request c WHERE c.wid =" + newid + ";";
				// get num rows
				int num_rows = esql.executeQueryAndPrintResult(q);
				if (num_rows == 0){
					goodid = true;
				}
				else{
					goodid = false;
				}
			}

			// get the values fron gui text fields
			String rid_text = gui.getTextFromField(gui.cl_rid_t);
			String mech_id_text = gui.getTextFromField(gui.clmech_id_t);

			validmech_id = false;
			validrid = false;
			validcldate = false;


			String check_rid = "SELECT r.rid FROM service_request r WHERE r.rid =" + Integer.valueOf(rid_text) + ";";
			int numrids = esql.executeQueryAndPrintResult(check_rid);
			if (numrids == 0){
				validrid = false;
			}
			else if (numrids > 0){
				validrid = true;
			}

			String check_mechid = "SELECT m.id FROM mechanic m WHERE m.id =" + Integer.valueOf(mech_id_text) + ";";

			int nummechid = esql.executeQueryAndPrintResult(check_mechid);
			if (nummechid == 0){
				validmech_id = false;
			}
			else if (nummechid > 0){
				validmech_id = true;
			}

			if (validmech_id && validrid)
			{
				// get stuff from original request
				String q4 = "SELECT r.date, r.complain FROM service_request r WHERE r.rid = " + Integer.valueOf(rid_text) + ";";
				List<List<String>> result3 = esql.executeQueryAndReturnResult(q4);

				String comment_text = result3.get(0).get(1);

				java.util.Date closed_dt = (java.util.Date) gui.spinner2.getValue();
				java.sql.Date cldatesql = new java.sql.Date(closed_dt.getTime());
				String date_opened_str = result3.get(0).get(0); // get date as a string

				SimpleDateFormat u_date_opened = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date udate_op = u_date_opened.parse(date_opened_str);
				java.sql.Date opendatesql = new java.sql.Date(udate_op.getTime());


				if (opendatesql.after(cldatesql)){
					validcldate = false;
					System.out.println("INVALID CLOSE DATE");

				}
				else{
					validcldate = true;
					System.out.println("VALID CLOSE DATE");
				}

				String bill_text = gui.getTextFromField(gui.bill_t);

				// check if dates match

				// check if the service request is already closed - can't close something twice:
				String q6 = "SELECT r.rid FROM service_request r, closed_request cr WHERE r.rid = " + Integer.valueOf(rid_text) + " AND r.rid = cr.rid;";
				int request_exists = esql.executeQueryAndPrintResult(q6);

				if (request_exists > 0)
				{

					Object[] options3 = {"OK"};
					int selectedOption = JOptionPane.showOptionDialog(null,
							"<html> Request with id <font color=\"red\">" + rid_text + "</font> is already closed. <br> You cannot close it again. </html>", "Service Request already closed",
							JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options3, options3[0]);

					if(selectedOption == 0) // Clicking "OK"
					{
						gui.clmech_id_t.setText("");
						gui.cl_rid_t.setText("");
						gui.bill_t.setText("");
						gui.cl_comment_t.setText("");
					}

				}
				else if (request_exists == 0) {
					try {
						// delete from a service_request table
						String q3 = "INSERT INTO closed_request(wid, rid, mid, date, comment, bill) " + "VALUES(" + newid + ", " + Integer.valueOf(rid_text) + ", " + Integer.valueOf(mech_id_text) + ", '" + cldatesql + "', '" + comment_text + "', " + Integer.valueOf(bill_text) + ");";

						int c = esql.executeQueryAndPrintResult(q3);

						// this is how we know the previous query finished:
						if (c == 0) {

							try{
							// insert into a closed_request table
							String q2 = "DELETE FROM service_request WHERE rid =" + Integer.valueOf(rid_text) + ";";
								int d = esql.executeQueryAndPrintResult(q2);
								if (d == 0)
								{
									//System.out.println(" d value: " + d);
								}

							}
							catch(Exception e3)
							{
								System.err.println (e3.getMessage());
							}
						}
					}
					catch(Exception e4){
						System.err.println (e4.getMessage());
					}

				}
				// REQUEST IS IMVALID OR MECH ID IS INVALID
			} else {
				Object[] options = {"OK", "Cancel"};
				int selectedOption = JOptionPane.showOptionDialog(null,
						"<html>Service request with id <font color=\"red\">" + rid_text + "</font> not found. <br> Please click <font color=\"green\"> Ok</font> to return to the previous menu <br> or click <font color=\"green\"> Cancel </font> to repeat search.</html>", "Service Request not found",
						JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, options, options[0]);

				if(selectedOption == 0) // Clicking "OK"
				{
					gui.clmech_id_t.setText("");
					gui.cl_rid_t.setText("");
					gui.bill_t.setText("");
					gui.cl_comment_t.setText("");
					gui.CloseSRpopup.dispose();
				}
				else if(selectedOption == 1) // clicking cancel
				{
					gui.clmech_id_t.setText("");
					gui.cl_rid_t.setText("");
					gui.bill_t.setText("");
					gui.cl_comment_t.setText("");

				}
			}

		} catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void InsertServiceRequest(MechanicShop esql){//4

		try {

			// GENERATE NEW RANDOM RID

			boolean goodint = false;
			boolean goodid = false; // assume id is bad, until proven otherwise
			int newid = -1;
			// loop, picking random numbers, until good id is found:
			while (goodid == false)
			{
				while(!goodint)
				{
					// get random int from a randomizer function
					newid = getRandomInt();
					if (newid < 0)
					{
						goodint = false;
					}
					else{
						goodint = true;
					}
				}
				// check if that int is already used in DB:
				String q = "SELECT * FROM service_request r WHERE r.rid =" + newid + ";";
				// get num rows
				int num_rows = esql.executeQueryAndPrintResult(q);
				if (num_rows == 0){
					goodid = true;
				}
				else{
					goodid = false;
				}
			}

			// get the values fron gui text fields
			String vin_text = gui.getTextFromField(gui.hiddencarvin);
			String odometer_text = gui.getTextFromField(gui.odometer_t);
			String complaint_text = gui.complain_t.getText().trim();
			java.util.Date date = (java.util.Date) gui.spinner.getValue();
			java.sql.Date datesql = new java.sql.Date(date.getTime());

			int custid = (Integer.valueOf(gui.customeridforsr.getText().trim()));

			String q2 = "INSERT INTO service_request(rid, customer_id, car_vin, date, odometer, complain) VALUES(" + newid + "," + custid + ",'" + vin_text + "','" + datesql + "'," + Integer.valueOf(odometer_text) + ",'" + complaint_text + "');";

			esql.executeQuery(q2);

		} catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
		
	} // using CloseSR() instead
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6

		try{
			String q = "SELECT c.fname, c.lname, c.id, r.car_vin, cr.date, cr.comment, cr.bill FROM closed_request cr, service_request r, customer c WHERE cr.rid = r.rid AND r.customer_id = c.id AND cr.bill < 100 ORDER BY c.id;";

			int rowCount = esql.executeQueryAndPrintResult(q);
			List<List<String>> result = esql.executeQueryAndReturnResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("First");
			dm.addColumn("Last");
			dm.addColumn("ID");
			dm.addColumn("VIN");
			dm.addColumn("Date");
			dm.addColumn("Comment");
			dm.addColumn("Bill");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				//System.out.println("t.size: " + t.length);
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
					//System.out.println("t[h]: " + dataRow[h]);
				}
				dm.addRow(dataRow);
			}

			System.out.println ("total row(s): " + rowCount);
			gui.loadNumRows(String.valueOf(rowCount));


		}catch(Exception e){
			System.err.println (e.getMessage());
		}


	}

	public static void ListallcustomersWithIDs(MechanicShop esql){//6

		try{
			String q = "SELECT c.fname, c.lname, c.id FROM customer c ORDER BY c.id;";

			int rowCount = esql.executeQueryAndPrintResult(q);
			List<List<String>> result = esql.executeQueryAndReturnResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("First");
			dm.addColumn("Last");
			dm.addColumn("ID");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				//System.out.println("t.size: " + t.length);
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
					//System.out.println("t[h]: " + dataRow[h]);
				}
				dm.addRow(dataRow);
			}
			System.out.println ("total row(s): " + rowCount);
			gui.loadNumRows(String.valueOf(rowCount));

		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}

	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7

        try{
            String q = "SELECT customer.fname, customer.lname FROM customer where customer.id IN (SELECT customer.id FROM customer, owns WHERE customer.id = owns.customer_id GROUP BY (customer.id) HAVING (COUNT(owns.car_vin)) > 20);";
            //esql.executeQuery(q);

            int rowCount = esql.executeQueryAndPrintResult(q);
            List<List<String>> result = esql.executeQueryAndReturnResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("First");
			dm.addColumn("Last");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
				}
				dm.addRow(dataRow);
			}
				System.out.println("total row(s): " + rowCount);
				gui.loadNumRows(String.valueOf(rowCount));


        }catch(Exception e){
            System.err.println (e.getMessage());
        }
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8

		try{
			String q = "SELECT v.vin, v.make, v.model, v.year, r.odometer FROM car v, service_request r WHERE v.vin = r.car_vin AND v.year < 1995 AND r.odometer < 50000;";

			int rowCount = esql.executeQueryAndPrintResult(q);
			List<List<String>> result = esql.executeQueryAndReturnResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("VIN");
			dm.addColumn("Make");
			dm.addColumn("Model");
			dm.addColumn("Year");
			dm.addColumn("Odometer");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
				}
				dm.addRow(dataRow);
			}

			System.out.println ("total row(s): " + rowCount);
			gui.loadNumRows(String.valueOf(rowCount));


		}catch(Exception e){
			System.err.println (e.getMessage());
		}
		
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9

		try{
			String q = "SELECT make, model, COUNT(rid), vin FROM car JOIN service_request ON car.vin = service_request.car_vin GROUP BY(vin) ORDER BY(COUNT(rid)) DESC LIMIT";
			String k = gui.getKvalue();
			q = q + " ";
			q += k;
			q = q + ";";

			List<List<String>> result = esql.executeQueryAndReturnResult(q);
			int rowCount = esql.executeQueryAndPrintResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("Make");
			dm.addColumn("Model");
			dm.addColumn("Number of Requests");
			dm.addColumn("VIN");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
				}
				dm.addRow(dataRow);
			}

			System.out.println ("total row(s): " + rowCount);
			gui.loadNumRows(String.valueOf(rowCount));

			} catch(Exception e){
			System.err.println (e.getMessage());
			}
	}
	
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//9

	/* String q = "SELECT r.customer_id, COUNT(r.bill)" +
			"FROM Customer c, service_request r, closed_request cr " +
			"WHERE c.id = r.customer_id AND r.rid = cr.rid GROUP BY (r.customer_id) " +
			"ORDER BY (r.bill) DESC"; */

	// ADD real query here

	//String test = "SELECT r.customer_id, SUM(cr.bill) AS total FROM Customer c, service_request r, closed_request cr WHERE c.id = r.customer_id AND r.rid = cr.rid GROUP BY(r.customer_id) ORDER BY total  DESC; ";

		try{
			String q = "SELECT c.fname, c.lname, total_bill " +
					" FROM Customer c," +
					" (SELECT sr.customer_id, SUM(cr.bill) AS total_bill " +
					" FROM closed_request cr, service_request sr " +
					" WHERE cr.rid = sr.rid " +
					" GROUP BY sr.customer_id) AS amount " +
					" WHERE c.id = amount.customer_id " +
					" ORDER BY amount.total_bill DESC;";


			int rowCount = esql.executeQueryAndPrintResult(q);
			List<List<String>> result = esql.executeQueryAndReturnResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("First");
			dm.addColumn("Last");
			dm.addColumn("Total Bill");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
				}
				dm.addRow(dataRow);
			}
			System.out.println("total row(s): " + rowCount);
			gui.loadNumRows(String.valueOf(rowCount));

		} catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void SearchRequestByLastName(MechanicShop esql){
		try{

			// remove old customer data

			Component[] componentList6 = gui.searchresults.getComponents();
			for(Component c6 : componentList6){
				if(c6 instanceof JRadioButton){
					gui.searchresults.remove(c6);
				}
			}
			gui.searchresults.revalidate();
			gui.searchresults.repaint();


			String last_text = gui.getTextFromField(gui.last_sr);
			String q = "SELECT c.lname, c.id FROM Customer c WHERE c.lname = '" + last_text + "' GROUP BY c.id;";
			int rowCount = esql.executeQueryAndPrintResult(q);
			List<List<String>> result = esql.executeQueryAndReturnResult(q);
			gui.search_results_label_line1.setText("");
			gui.addservice_request_module.setVisible(true);
			gui.date_p.setVisible(true);

			if (rowCount == 0)
			{
				Object[] options = {"OK", "Cancel"};
				int selectedOption = JOptionPane.showOptionDialog(null,
						"<html>Customer with last name <font color=\"red\">" + last_text + "</font> not found. <br> Please click <font color=\"green\"> Ok</font>, add a customer, add a car and try again <br> or click <font color=\"green\"> Cancel </font> to repeat search.</html>", "Customer not found",
						JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, options, options[0]);

				if(selectedOption == 0) // Clicking "OK"
				{
					gui.odometer_t.setText("");
					gui.vin_sr.setText("");
					gui.complain_t.setText("");
					gui.last_sr.setText("");
					gui.addSRpopup.dispose();

				}
				else if(selectedOption == 1) // Clicking "Cancel"
				{
					gui.searchresults.setVisible(false);
					gui.addservice_request_module.setVisible(true);
					gui.date_p.setVisible(true);
					//reset fields
					gui.odometer_t.setText("");
					gui.vin_sr.setText("");
					gui.complain_t.setText("");
					gui.last_sr.setText("");
				}

			}
			// customer found:
			else
				{
					gui.searchresults.setVisible(true);
					gui.addservice_request_module.setVisible(false);
					gui.date_p.setVisible(false);
					ButtonGroup group = new ButtonGroup();

					gui.search_results_label_line1.setText("Please select a customer: ");

					for (int i = 0; i < rowCount; i++)
					{
						gui.generic_rb = new JRadioButton(result.get(i).get(0) + " [ID: " + result.get(i).get(1) + " ]");
						group.add(gui.generic_rb);
						gui.generic_rb.setActionCommand(result.get(i).get(1));

							gui.generic_rb.addActionListener(new ActionListener()
							{
							public void actionPerformed(ActionEvent e)
								{
									try {

										//grab customer id from selected radiobutton
										gui.customeridforsr.setText(group.getSelection().getActionCommand());

										// refresh found cars section and repaint:
										Component[] componentList = gui.foundcars.getComponents();
										for(Component c2 : componentList){
											if(c2 instanceof JRadioButton){
												gui.foundcars.remove(c2);
											}
										}
										gui.foundcars.revalidate();
										gui.foundcars.repaint();


										// search for cars
										String car_number = "SELECT o.customer_id, o.car_vin FROM Owns o WHERE o.customer_id = " + Integer.valueOf(gui.customeridforsr.getText()) + ";";
										int carcount = esql.executeQueryAndPrintResult(car_number);
										List<List<String>> result2 = esql.executeQueryAndReturnResult(car_number);
										ButtonGroup cargroup = new ButtonGroup();

										if (carcount == 0) {
											Object[] options1 = {"OK"};
											int selectedOption2 = JOptionPane.showOptionDialog(null,
													"<html>No cars found for this customer. <br> Please add a car and try again </html>", "No cars found",
													JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, options1[0]);
											if(selectedOption2 == 0) // Clicking "OK"
											{

												// refresh searchresults panel
												Component[] componentList4 = gui.searchresults.getComponents();
												for(Component c4 : componentList4){
													if(c4 instanceof JRadioButton){
														gui.searchresults.remove(c4);
													}
												}
												gui.searchresults.revalidate();
												gui.searchresults.repaint();

												// refresh foundcars panel
												Component[] componentList5 = gui.foundcars.getComponents();
												for(Component c5 : componentList5){
													if(c5 instanceof JRadioButton){
														gui.foundcars.remove(c5);
													}
												}
												gui.foundcars.revalidate();
												gui.foundcars.repaint();


											/*	gui.searchresults.setVisible(false);
												gui.foundcars.setVisible(true);
												gui.foundcars.add(gui.addcar_module);
												gui.foundcars.add(gui.addservice_request_module);
												gui.owner_t.setText(gui.customeridforsr.getText()); */


												// dispose of the fields
												gui.odometer_t.setText("");
												gui.vin_sr.setText("");
												gui.complain_t.setText("");
												gui.last_sr.setText("");
												gui.addSRpopup.dispose();

											}

										} else {
											gui.search_results_label_line2.setText("We found the following cars: ");
											gui.foundcars.setVisible(true);
											gui.searchresults.setVisible(false);

											for (int f = 0; f < carcount; f++) {
												gui.generic_car_rb = new JRadioButton(" [ VIN: " + result2.get(f).get(1) + " ]");
												cargroup.add(gui.generic_car_rb);
												gui.generic_car_rb.setActionCommand(result2.get(f).get(1));

												gui.generic_car_rb.addActionListener(new ActionListener() {
													public void actionPerformed(ActionEvent e) {
														gui.hiddencarvin.setText(cargroup.getSelection().getActionCommand());
														gui.vin_sr.setText(gui.hiddencarvin.getText());
														gui.searchresults.setVisible(false);
														gui.addservice_request_module.setVisible(true);
														gui.date_p.setVisible(true);
														gui.foundcars.setVisible(false);
														gui.addsr.setEnabled(true);
														// refresh customers found
														Component[] componentList = gui.searchresults.getComponents();
														for(Component c : componentList){
															if(c instanceof JRadioButton){
																gui.searchresults.remove(c);
															}
														}
														gui.searchresults.revalidate();
														gui.searchresults.repaint();
													}
												});
												gui.foundcars.add(gui.generic_car_rb);
											}
										}
									} // end try
									catch(Exception e2)
									{
										System.err.println (e2.getMessage());
									}
								} // end action listener
							});
						gui.searchresults.add(gui.generic_rb);
					}


						// add stuff for selection
				} // end customer found else
		}
		catch(Exception e)
		{
			System.err.println (e.getMessage());
		}
	}

	public static void ViewAllOpenRequests(MechanicShop esql){

		try{
			String q = "SELECT r.rid, v.vin, v.make, v.model, c.id, r.date, r.odometer, r.complain " +
					"FROM car v, customer c, service_request r " +
					"WHERE v.vin = r.car_vin AND c.id = r.customer_id AND (r.rid NOT IN (SELECT cr.rid FROM closed_request cr));";


			List<List<String>> result = esql.executeQueryAndReturnResult(q);
			int rowCount = esql.executeQueryAndPrintResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("Req ID");
			dm.addColumn("VIN");
			dm.addColumn("Make");
			dm.addColumn("Model");
			dm.addColumn("Customer ID");
			dm.addColumn("Date Open");
			dm.addColumn("Odometer");
			dm.addColumn("Complaint");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
				}
				dm.addRow(dataRow);
			}
			System.out.println ("total row(s): " + rowCount);
			gui.loadNumRows(String.valueOf(rowCount));

		} catch(Exception e){
			System.err.println (e.getMessage());
		}
	}

	public static void ViewAllClosedRequests(MechanicShop esql){

		try{
			String q = "SELECT cr.rid, v.vin, v.make, v.model, c.id, cr.date, cr.mid, cr.comment, cr.bill " +
					"FROM car v, customer c, service_request r, closed_request cr " +
					"WHERE r.rid = cr.rid AND v.vin = r.car_vin AND c.id = r.customer_id;";


			List<List<String>> result = esql.executeQueryAndReturnResult(q);
			int rowCount = esql.executeQueryAndPrintResult(q);

			DefaultTableModel dm = (DefaultTableModel)gui.resultstable.getModel();
			// clear all previous results
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged();
			dm.setColumnCount(0);
			// make headers
			dm.addColumn("Rid");
			dm.addColumn("VIN");
			dm.addColumn("Make");
			dm.addColumn("Model");
			dm.addColumn("Customer ID");
			dm.addColumn("Date Closed");
			dm.addColumn("Mechanic ID");
			dm.addColumn("Comment");
			dm.addColumn("Bill");

			// add data
			for (List<String> l: result)
			{
				String t[] = new String[l.size()];
				l.toArray(t);
				Object[] dataRow = new Object[t.length];
				for (int h = 0; h<dataRow.length; h++){
					dataRow[h] = t[h].trim();
				}
				dm.addRow(dataRow);
			}
			System.out.println ("total row(s): " + rowCount);
			gui.loadNumRows(String.valueOf(rowCount));

		} catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void LookForVin(String s, MechanicShop esql){

		try{

			String q = "SELECT v.vin FROM car v WHERE v.vin = '" + s.toUpperCase() + "';";
			int rowCount = esql.executeQueryAndPrintResult(q);

			if (rowCount == 0){
				vinfound = false;
			}
			else {
				vinfound = true;
			}

		}catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void LookForCustomer(String s, MechanicShop esql){

		try{
			validcustomer = false;

			String q = "SELECT c.id FROM Customer c WHERE c.lname = '" + s + "';";
			int rowCount = esql.executeQueryAndPrintResult(q);

			if (rowCount == 0){
				validcustomer = false;
			}
			else {
				validcustomer = true;
			}

		}catch(Exception e){
			System.err.println (e.getMessage());
		}

	}

	public static void CheckIfValidOwner(String s, MechanicShop esql){
		try{
			validowner = false;

			String q = "SELECT c.id FROM Customer c WHERE c.id = '" + s + "';";
			int rowCount = esql.executeQueryAndPrintResult(q);

			if (rowCount == 0){
				validowner = false;
			}
			else {
				validowner = true;
			}

		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}


	/**
	 * Method to generate a random seed
	 * generates a random array of 20 bytes, iterates through array,
	 * computes a long
	 * @param None
	 * @return long
	 * @throws None
	 */

	public static long MakeRandomSeed(){
		SecureRandom rn = new SecureRandom();
		byte seed[] = rn.generateSeed(20);
		long result = 0;
		for (int i = 0; i < 8; i++) {
			result <<= 8;
			result |= (seed[i] & 0xFF);
			}
		return result;
		}

	/**
	 * Method to generate a random integer
	 * generates a random integer from a random seed,
	 * using standard random function
	 * @param None
	 * @return int
	 * @throws None
	 */

	public static int getRandomInt(){
		Random rn = new Random();
		rn.setSeed(MakeRandomSeed());
		int n = rn.nextInt();
		return n;
	}

    static class GUI extends Frame{

        private final int WINDOW_WIDTH = 1024;
        private final int WINDOW_HEIGHT = 720;
        private JPanel panel, mainpanel, customer_p, serv_req_p, mech_p, cars_p, results_p, addcustomer_module, addmechanic_module, addcar_module;
        private JPanel addservice_request_module, numresults_p, date_p, addsrpanel, searchresults, foundcars, closesrpanel, cl_date_p, main_closedpanel;
        private static JTextField cfname_t, clname_t, cphone_t, caddress_t, sr_cfname_t, sr_clname_t, sr_cphone_t, sr_caddress_t, k_value;
        private JLabel k_label, cfname_label, clname_label, cphone_label, caddress_label, sr_cfname_label, sr_clname_label, sr_cphone_label, sr_caddress_label, numresults_label, date_label;
        private static JLabel c_warning, sr_c_warning, k_valuewarning, m_warning, mfname_label, mlname_label, m_years; // warning on customer module label
		private static JTextField mfname_t, mlname_t, m_years_t, numresults; // mechanic first name textfield
		private static JLabel vin_sr_label, bill_label, last_sr_label, sr_warning, car_warning, year_label, model_label, make_label, vin_label;
		private JLabel odometer_label, complain_label, owner_label, clmech_id_label, cl_rid_label, cl_warning;
		private static JTextArea complain_t;
		private static JTextField owner_t, last_sr, vin_sr, clmech_id_t, cl_rid_t, vin_t, make_t, model_t, year_t, bill_t, odometer_t;
		private static JSpinner spinner, spinner2;
		public static JTextField customeridforsr; //hidden field to pass id of the newly created customer to SR form
		private JButton addmechanic_button; // Add Mechanic button
		private JButton addcustomer_button; // Add customer button
		private JButton addcar_button; // Add Car button
        private JButton listCustomersWithBillLessThan100_button, listKCarsWithTheMostServices_button, listCarsBefore1995With50000Milles_button, listcustomers_button;
        private JButton sr_search, sr_next, addsr;
        private JButton addservicereq_button; // this button just opens a popup
        private JButton viewallopenreq_button, viewallclosereq_button;
        private JButton closesr_button; // this button opens close SR frame
        private JButton close_request; // this button calls a query to close the request
		private JButton listCustomersInDescOrderOfBill_button, allcustomersWithIDs;
		private int numrows = 1;
        private int numcolumns = 1;
        private static JTextArea resultswindow;
        private static JFrame mainwindow, addSRpopup, CloseSRpopup;
        private static JTable resultstable;
        private static JRadioButton generic_rb, generic_car_rb;
        private static JLabel search_results_label_line1, search_results_label_line2, date_closed_label, cl_comment_label;
		private static JTextField cl_comment_t;
		public static JTextField hiddencarvin;

		// GUI constructor
        public GUI(){

            mainwindow = new JFrame("Mechanic Shop");
            mainwindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            mainwindow.setResizable(false);
			mainwindow.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					super.windowClosing(e);
					disconnectFromDB(esql);

				}
			});



            addSRpopup = new JFrame("Add a new Service Request");
			addSRpopup.setSize(500, 400);
			addSRpopup.setResizable(false);
			addSRpopup.setVisible(false);
			addSRpopup.setLocationRelativeTo(mainwindow);
			addSRpopup.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			CloseSRpopup = new JFrame("Close a Service Request");
			CloseSRpopup.setSize(500, 400);
			CloseSRpopup.setResizable(false);
			CloseSRpopup.setVisible(false);
			CloseSRpopup.setLocationRelativeTo(mainwindow);
			CloseSRpopup.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            mainwindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            mainpanel = new JPanel(new GridLayout(2, 1));
            mainpanel.setBackground(Color.white);

            panel = new JPanel(new GridLayout(1,4));

			panel.setBackground(Color.white);

            // nested customer component
            customer_p = new JPanel();
            customer_p.setBackground(Color.white);
            TitledBorder customerborder;
            customerborder = BorderFactory.createTitledBorder("Customer");
            customer_p.setBorder(customerborder);
			BoxLayout layout = new BoxLayout(customer_p, BoxLayout.Y_AXIS);
			customer_p.setLayout(layout);
            //customer_p.setLayout(new FlowLayout());
            addcustomer_module = new JPanel();
            GroupLayout grlayout = new GroupLayout(addcustomer_module);
            // add customer component
            addcustomer_module.setLayout(grlayout);
            addcustomer_module.setBackground(Color.white);

            grlayout.setAutoCreateContainerGaps(true);
            grlayout.setAutoCreateGaps(true);
            c_warning = new JLabel("");
            cfname_label = new JLabel("First:");
            clname_label = new JLabel("Last:");
            cphone_label = new JLabel("Phone:");
            caddress_label = new JLabel("Address:");

            cfname_t = new JTextField(10);
            clname_t = new JTextField(10);
            cphone_t = new JTextField(10);
            caddress_t = new JTextField(10);

            // buttons
            addcustomer_button = new JButton("Add Customer");
            listcustomers_button = new JButton("Customers with > 20 cars");
			listCustomersWithBillLessThan100_button = new JButton("Customers with bill < 100");
            listCarsBefore1995With50000Milles_button = new JButton("Cars before 1995 with < 50K mi");
			listKCarsWithTheMostServices_button = new JButton("K Cars with the most services");
			addcar_button = new JButton("Add Car");
			addcustomer_button.setEnabled(true);
            listcustomers_button.setEnabled(true);
			listCustomersWithBillLessThan100_button.setEnabled(true);
			listCarsBefore1995With50000Milles_button.setEnabled(true);
			listKCarsWithTheMostServices_button.setEnabled(true);
			listcustomers_button.addActionListener(new MoreThan20CarssButtonListener());
			listCustomersWithBillLessThan100_button.addActionListener(new CustomersWithBillLessThan100Listener());
            listCarsBefore1995With50000Milles_button.addActionListener(new CarsBefore1995With50000MillesListener());
			listKCarsWithTheMostServices_button.addActionListener(new KCarsWithTheMostServicesListener());
			listCustomersInDescOrderOfBill_button = new JButton("Customers Desc Order Of Bill");
			listCustomersInDescOrderOfBill_button.addActionListener(new ListCustomersInDescOrderOfBillListener());
			addcustomer_button.addActionListener(new AddCustomerListener());
			allcustomersWithIDs = new JButton("All customers with their IDs");
			allcustomersWithIDs.addActionListener(new allcustomersWithIDsListener());
			addcar_button.setEnabled(true);
			addcar_button.addActionListener(new AddCarListener());

            grlayout.setHorizontalGroup(
                    grlayout.createSequentialGroup()
                    .addGroup(grlayout.createParallelGroup(TRAILING)
							.addComponent(cfname_label)
                            .addComponent(clname_label)
                            .addComponent(cphone_label)
                            .addComponent(caddress_label))
                    .addGroup(grlayout.createParallelGroup()
							.addComponent(c_warning)
                            .addComponent(cfname_t)
                            .addComponent(clname_t)
                            .addComponent(cphone_t)
                            .addComponent(caddress_t)
                            .addComponent(addcustomer_button)
                            )

                    );

            grlayout.setVerticalGroup(
                    grlayout.createSequentialGroup()
							.addComponent(c_warning)
                    .addGroup(grlayout.createParallelGroup(BASELINE)
                            .addComponent(cfname_label)
                            .addComponent(cfname_t))
                    .addGroup(grlayout.createParallelGroup(BASELINE)
                            .addComponent(clname_label)
                            .addComponent(clname_t))
                    .addGroup(grlayout.createParallelGroup(BASELINE)
                            .addComponent(cphone_label)
                            .addComponent(cphone_t))
                    .addGroup(grlayout.createParallelGroup(BASELINE)
                            .addComponent(caddress_label)
                            .addComponent(caddress_t))
                            .addComponent(addcustomer_button)
                    );
			// add buttons to customer panel
            customer_p.add(addcustomer_module);
            listcustomers_button.setAlignmentX(0.75f);
            customer_p.add(listcustomers_button);
            listCustomersWithBillLessThan100_button.setAlignmentX(0.75f);
            customer_p.add(listCustomersWithBillLessThan100_button);
            listCustomersInDescOrderOfBill_button.setAlignmentX(0.75f);
            customer_p.add(listCustomersInDescOrderOfBill_button);
			allcustomersWithIDs.setAlignmentX(0.75f);
            customer_p.add(allcustomersWithIDs);
            customer_p.setVisible(true);

            // nested cars component
            cars_p = new JPanel();
            cars_p.setBackground(Color.white);
			TitledBorder carsborder;
			carsborder = BorderFactory.createTitledBorder("Cars");
			cars_p.setBorder(carsborder);
			cars_p.setLayout(new FlowLayout());
			cars_p.setVisible(true);

			// add elements to cars panel:
			addcar_module = new JPanel();
			GroupLayout grlayout_car = new GroupLayout(addcar_module);
			addcar_module.setLayout(grlayout_car);
			addcar_module.setBackground(Color.white);
			grlayout_car.setAutoCreateContainerGaps(true);
			grlayout_car.setAutoCreateGaps(true);
			// create text fields and labels
			car_warning = new JLabel("");
			vin_label = new JLabel("Vin:");
			make_label = new JLabel("Make:");
			model_label = new JLabel("Model:");
			year_label = new JLabel("Year:");
			owner_label = new JLabel("Owner ID:");

			vin_t = new JTextField(10);
			make_t = new JTextField(10);
			model_t = new JTextField(10);
			year_t = new JTextField(10);
			owner_t = new JTextField(10);

			grlayout_car.setHorizontalGroup(
					grlayout_car.createSequentialGroup()
							.addGroup(grlayout_car.createParallelGroup(TRAILING)
									.addComponent(vin_label)
									.addComponent(make_label)
									.addComponent(model_label)
									.addComponent(year_label)
									.addComponent(owner_label))
							.addGroup(grlayout_car.createParallelGroup()
									.addComponent(car_warning)
									.addComponent(vin_t)
									.addComponent(make_t)
									.addComponent(model_t)
									.addComponent(year_t)
									.addComponent(owner_t)
									.addComponent(addcar_button)
							)

			);

			grlayout_car.setVerticalGroup(
					grlayout_car.createSequentialGroup()
							.addComponent(car_warning)
							.addGroup(grlayout_car.createParallelGroup(BASELINE)
									.addComponent(vin_label)
									.addComponent(vin_t))
							.addGroup(grlayout_car.createParallelGroup(BASELINE)
									.addComponent(make_label)
									.addComponent(make_t))
							.addGroup(grlayout_car.createParallelGroup(BASELINE)
									.addComponent(model_label)
									.addComponent(model_t))
							.addGroup(grlayout_car.createParallelGroup(BASELINE)
									.addComponent(year_label)
									.addComponent(year_t))
							.addGroup(grlayout_car.createParallelGroup(BASELINE)
									.addComponent(owner_label)
									.addComponent(owner_t))
							.addComponent(addcar_button)
			);

			cars_p.add(addcar_module);
			cars_p.add(listCarsBefore1995With50000Milles_button);
			k_value = new JTextField(5);
			k_valuewarning = new JLabel("");
			k_label = new JLabel("K: ");
			cars_p.add(k_valuewarning);
			cars_p.add(k_label);
			cars_p.add(k_value);
			cars_p.add(listKCarsWithTheMostServices_button);

			// nested service request component
            serv_req_p = new JPanel();
            serv_req_p.setBackground(Color.white);
            TitledBorder servicerequestborder;
            servicerequestborder = BorderFactory.createTitledBorder("Service Requests");
            serv_req_p.setBorder(servicerequestborder);
            serv_req_p.setLayout(new FlowLayout());
            sr_search = new JButton("Search by Last");
            sr_search.setEnabled(true);
            sr_search.addActionListener(new SearchRequestByLastName());

            // date picker for a new service request

			date_p = new JPanel();
			date_p.setBackground(Color.white);

			GroupLayout grlayout_date = new GroupLayout(date_p);
			date_p.setLayout(grlayout_date);
			date_p.setBackground(Color.white);
			grlayout_date.setAutoCreateContainerGaps(true);
			grlayout_date.setAutoCreateGaps(true);
			date_label = new JLabel("Date: ");

			Calendar calendar = Calendar.getInstance();
			java.util.Date uDate = new java.util.Date();
			java.sql.Date initDate = new java.sql.Date(uDate.getTime());
			calendar.add(Calendar.YEAR, -100);
			java.sql.Date earliestDate = new java.sql.Date(uDate.getTime());
			calendar.add(Calendar.YEAR, 200);
			java.sql.Date latestDate = new java.sql.Date(uDate.getTime());
			SpinnerModel model = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.YEAR);
			spinner = new JSpinner(model);

			grlayout_date.setHorizontalGroup(
					grlayout_date.createSequentialGroup()
							.addGroup(grlayout_date.createParallelGroup(TRAILING)
									.addComponent(date_label))
							.addGroup(grlayout_date.createParallelGroup()
									.addComponent(spinner)
							)

			);

			grlayout_date.setVerticalGroup(
					grlayout_date.createSequentialGroup()
							.addGroup(grlayout_date.createParallelGroup(BASELINE)
									.addComponent(date_label)
									.addComponent(spinner))
			);

			// add service request module
			addservice_request_module = new JPanel();
			// group layout for add service request module
			GroupLayout grlayout_sr = new GroupLayout(addservice_request_module);
			addservice_request_module.setLayout(grlayout_sr);
			addservice_request_module.setBackground(Color.white);
			grlayout_sr.setAutoCreateContainerGaps(true);
			grlayout_sr.setAutoCreateGaps(true);

			// create fields for group layout
			TitledBorder srborderjtext;
			srborderjtext = BorderFactory.createTitledBorder("");

			odometer_label = new JLabel("Odometer:");
			odometer_t = new JTextField(10);
			complain_label = new JLabel("Complaint:");
			complain_t = new JTextArea(2, 2);
			complain_t.setWrapStyleWord(true);
			complain_t.setLineWrap(true);
			complain_t.setBorder(srborderjtext);
			last_sr_label = new JLabel("Last name:");
			last_sr = new JTextField(10);
			vin_sr_label = new JLabel("VIN:");
			vin_sr = new JTextField(10);
			JScrollPane scrollcompl = new JScrollPane(complain_t);
			sr_warning = new JLabel("");
			addservicereq_button = new JButton("Add New Request");
			addsr = new JButton("Add Request");
			addsr.addActionListener(new AddSRListener());
			addservicereq_button.setEnabled(true);
			addservicereq_button.addActionListener(new OpenSRWindowListener());

			grlayout_sr.setHorizontalGroup(
					grlayout_sr.createSequentialGroup()
							.addGroup(grlayout_sr.createParallelGroup(TRAILING)
									.addComponent(odometer_label)
									.addComponent(complain_label)
									.addComponent(vin_sr_label)
									.addComponent(last_sr_label))
							.addGroup(grlayout_sr.createParallelGroup()
									.addComponent(sr_warning)
									.addComponent(odometer_t)
									.addComponent(complain_t)
									.addComponent(vin_sr)
									.addComponent(last_sr)
									.addComponent(sr_search)
									.addComponent(addsr)
							)

			);

			grlayout_sr.setVerticalGroup(
					grlayout_sr.createSequentialGroup()
							.addComponent(sr_warning)
							.addGroup(grlayout_sr.createParallelGroup(BASELINE)
									.addComponent(odometer_label)
									.addComponent(odometer_t))
							.addGroup(grlayout_sr.createParallelGroup(BASELINE)
									.addComponent(complain_label)
									.addComponent(complain_t))
							.addGroup(grlayout_sr.createParallelGroup(BASELINE)
									.addComponent(vin_sr_label)
									.addComponent(vin_sr))
							.addGroup(grlayout_sr.createParallelGroup(BASELINE)
									.addComponent(last_sr_label)
									.addComponent(last_sr))
							.addComponent(sr_search)
							.addComponent(addsr)

			);

			addservice_request_module.add(scrollcompl);
			serv_req_p.add(addservicereq_button);
			viewallopenreq_button = new JButton("All Open Requests");
			viewallopenreq_button.setEnabled(true);
			viewallopenreq_button.addActionListener(new ViewAllOpenRequestsListener());
			viewallclosereq_button = new JButton("All Closed Requests");
			viewallclosereq_button.setEnabled(true);
			viewallclosereq_button.addActionListener(new ViewAllClosedRequestsListener());
			closesr_button = new JButton("Close a Request");
			serv_req_p.add(closesr_button);
			closesr_button.addActionListener(new ClosedSRWindowListener());
			serv_req_p.add(viewallopenreq_button);
			serv_req_p.add(viewallclosereq_button);
			serv_req_p.setVisible(true);

/************************ CLOSE SERVICE REQUEST MODULE  *****************************/

			closesrpanel = new JPanel(); // main module for new SR
			GroupLayout grlayout_close = new GroupLayout(closesrpanel);
			closesrpanel.setLayout(grlayout_close);
			closesrpanel.setBackground(Color.white);
			closesrpanel.setVisible(true);
			grlayout_close.setAutoCreateContainerGaps(true);
			grlayout_close.setAutoCreateGaps(true);

			cl_date_p = new JPanel();
			cl_date_p.setBackground(Color.white);
			cl_date_p.setVisible(true);

			cl_comment_label = new JLabel("Comment");
			cl_comment_t = new JTextField(10);
			clmech_id_label = new JLabel("Mechanic ID:");
			clmech_id_t = new JTextField(10);
			cl_rid_label = new JLabel("Service Request ID:");
			cl_rid_t = new JTextField(10);
			date_closed_label = new JLabel("Date:");
			cl_warning = new JLabel("");
			bill_label = new JLabel("Amount charged");
			bill_t = new JTextField(10);

			Calendar calendar2 = Calendar.getInstance();
			java.util.Date uDate2 = new java.util.Date();
			java.sql.Date initDate2 = new java.sql.Date(uDate.getTime());
			calendar2.add(Calendar.YEAR, -100);
			java.sql.Date earliestDate2 = new java.sql.Date(uDate.getTime());
			calendar2.add(Calendar.YEAR, 200);
			java.sql.Date latestDate2 = new java.sql.Date(uDate.getTime());
			SpinnerModel model3 = new SpinnerDateModel(initDate2, earliestDate2, latestDate2, Calendar.YEAR);
			spinner2 = new JSpinner(model3);
			close_request = new JButton("Close request");
			close_request.addActionListener(new CloseSRListener());

			GroupLayout grlayout_date_close = new GroupLayout(cl_date_p);
			cl_date_p.setLayout(grlayout_date_close);

			grlayout_date_close.setHorizontalGroup(
					grlayout_date_close.createSequentialGroup()
							.addGroup(grlayout_date_close.createParallelGroup(TRAILING)
									.addComponent(date_closed_label))
							.addGroup(grlayout_date_close.createParallelGroup()
									.addComponent(spinner2)
							)
			);
			grlayout_date_close.setVerticalGroup(
					grlayout_date_close.createSequentialGroup()
							.addGroup(grlayout_date_close.createParallelGroup(BASELINE)
									.addComponent(date_closed_label)
									.addComponent(spinner2))
			);

			main_closedpanel = new JPanel();
			main_closedpanel.setBackground(Color.white);
			main_closedpanel.setLayout(new FlowLayout());
			main_closedpanel.setVisible(true);

			grlayout_close.setHorizontalGroup(
					grlayout_close.createSequentialGroup()
							.addGroup(grlayout_close.createParallelGroup(TRAILING)
									.addComponent(clmech_id_label)
									.addComponent(cl_rid_label)
									.addComponent(bill_label)
									.addComponent(cl_comment_label))
							.addGroup(grlayout_close.createParallelGroup()
									.addComponent(cl_warning)
									.addComponent(clmech_id_t)
									.addComponent(cl_rid_t)
									.addComponent(bill_t)
									.addComponent(cl_comment_t)
									.addComponent(close_request)
							)
			);
			grlayout_close.setVerticalGroup(
					grlayout_close.createSequentialGroup()
							.addComponent(cl_warning)
							.addGroup(grlayout_close.createParallelGroup(BASELINE)
									.addComponent(clmech_id_label)
									.addComponent(clmech_id_t))
							.addGroup(grlayout_close.createParallelGroup(BASELINE)
									.addComponent(cl_rid_label)
									.addComponent(cl_rid_t))
							.addGroup(grlayout_close.createParallelGroup(BASELINE)
									.addComponent(bill_label)
									.addComponent(bill_t))
							.addGroup(grlayout_close.createParallelGroup(BASELINE)
									.addComponent(cl_comment_label)
									.addComponent(cl_comment_t))
							.addComponent(close_request)
			);

			main_closedpanel.add(cl_date_p);
			main_closedpanel.add(closesrpanel);

			main_closedpanel.setVisible(true);
			CloseSRpopup.add(main_closedpanel);


/************************ ADD SERVICE REQUEST MODULE *********************************/
			addsrpanel = new JPanel(); // main module for new SR
			addsrpanel.setLayout(new FlowLayout());
			addsrpanel.setBackground(Color.white);

			date_p.setVisible(true);
			addsrpanel.add(date_p);
			addservice_request_module.setVisible(true);
			addsrpanel.add(addservice_request_module);
			addsrpanel.setVisible(true);
			addSRpopup.add(addsrpanel);

			searchresults = new JPanel(); // panel for existing customers last names
			searchresults.setBackground(Color.white);
			BoxLayout layout_search_res = new BoxLayout(searchresults, BoxLayout.Y_AXIS);
			searchresults.setLayout(layout_search_res);
			searchresults.setVisible(false); //this panel should only be visible on search results

			foundcars = new JPanel(); // panel for found cars
			foundcars.setBackground(Color.white);
			BoxLayout layout_search_cars = new BoxLayout(foundcars, BoxLayout.Y_AXIS);
			foundcars.setLayout(layout_search_cars);
			foundcars.setVisible(false); //this panel should only be visible on search results

			customeridforsr = new JTextField(10); //hidden field to pass customer id to sr form
			hiddencarvin = new JTextField(10); // hidden field to pass car vin to sr form

			search_results_label_line1 = new JLabel("");
			search_results_label_line2 = new JLabel("");

			searchresults.add(search_results_label_line1);
			foundcars.add(search_results_label_line2);

			addsrpanel.add(searchresults);
			addsrpanel.add(foundcars);
			foundcars.setVisible(false);

            // nested mech component
            mech_p = new JPanel();
            mech_p.setBackground(Color.white);
            TitledBorder mechborder;
            mechborder = BorderFactory.createTitledBorder("Mechanic/Personnel");
            mech_p.setBorder(mechborder);
            mech_p.setLayout(new FlowLayout());

			addmechanic_module = new JPanel();
			GroupLayout grlayout_mech = new GroupLayout(addmechanic_module);
			// add mechanic component
			addmechanic_module.setLayout(grlayout_mech);
			addmechanic_module.setBackground(Color.white);
			grlayout_mech.setAutoCreateContainerGaps(true);
			grlayout_mech.setAutoCreateGaps(true);
			m_warning = new JLabel("");
			mfname_label = new JLabel("First:");
			mlname_label = new JLabel("Last:");
			m_years = new JLabel("Experience:");

			mfname_t = new JTextField(10);
			mlname_t = new JTextField(10);
			m_years_t = new JTextField(10);
			addmechanic_button = new JButton("Add Mechanic");
			addmechanic_button.addActionListener(new AddMechanicListener());
			addmechanic_button.setEnabled(true);

			grlayout_mech.setHorizontalGroup(
					grlayout_mech.createSequentialGroup()
							.addGroup(grlayout_mech.createParallelGroup(TRAILING)
									.addComponent(mfname_label)
									.addComponent(mlname_label)
									.addComponent(m_years))
							.addGroup(grlayout_mech.createParallelGroup()
									.addComponent(m_warning)
									.addComponent(mfname_t)
									.addComponent(mlname_t)
									.addComponent(m_years_t)
									.addComponent(addmechanic_button)
							)
			);

			grlayout_mech.setVerticalGroup(
					grlayout_mech.createSequentialGroup()
							.addComponent(m_warning)
							.addGroup(grlayout_mech.createParallelGroup(BASELINE)
									.addComponent(mfname_label)
									.addComponent(mfname_t))
							.addGroup(grlayout_mech.createParallelGroup(BASELINE)
									.addComponent(mlname_label)
									.addComponent(mlname_t))
							.addGroup(grlayout_mech.createParallelGroup(BASELINE)
									.addComponent(m_years)
									.addComponent(m_years_t))
							.addComponent(addmechanic_button)
			);


			mech_p.add(addmechanic_module);
			mech_p.setVisible(true);


            // nested results panel
            results_p = new JPanel();
            results_p.setBackground(Color.white);
            TitledBorder resultsborder;
            resultsborder = BorderFactory.createTitledBorder("Records");

            GridBagConstraints c = new GridBagConstraints();

            results_p.setBorder(resultsborder);
            results_p.setLayout(new GridBagLayout());
            resultstable = new JTable();

            resultswindow = new JTextArea(10, 30);
			//JScrollPane scroll = new JScrollPane (resultswindow);
			JScrollPane scroll = new JScrollPane(resultstable);
			scroll.setBounds(23, 40, 394, 191);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.ipady = 200;      //make this component tall
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.gridwidth = 3;
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            //resultswindow.setBackground(Color.white);
            resultswindow.setEditable(false);
            DefaultTableModel model2 = new DefaultTableModel();
			resultstable.setModel(model2);
			resultstable.setEnabled(false);
            results_p.add(scroll, c);

            numresults_label = new JLabel("Total rows: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.ipady = 10;      //make this component tall
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.PAGE_END;
            c.gridx = 0;
            c.gridy = 1;
            results_p.add(numresults_label, c);
            c.fill = GridBagConstraints.HORIZONTAL;

            numresults = new JTextField(100);
            c.ipady = 10;      //make this component tall
            c.weightx = 1.0;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.PAGE_END;
            c.gridx = 1;
            c.gridy = 1;
            numresults.setEditable(false);
            results_p.add(numresults, c);
            results_p.setVisible(true);

            panel.add(customer_p);
			panel.add(serv_req_p);
			panel.add(cars_p);
            panel.add(mech_p);

			mainpanel.add(panel);
            mainpanel.add(results_p);

            mainwindow.add(mainpanel);
            mainwindow.setVisible(true);
            mainwindow.setLocationRelativeTo(null);

        }

		// action listners for buttons
        private class MoreThan20CarssButtonListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e)
            {
				gui.resetAllWarnings();
                ListCustomersWithMoreThan20Cars(esql);

            }
        }

		private class CarsBefore1995With50000MillesListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				ListCarsBefore1995With50000Milles(esql);

			}
		}

		private class CustomersWithBillLessThan100Listener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				ListCustomersWithBillLessThan100(esql);

			}
		}

		private class KCarsWithTheMostServicesListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				// reset warning - should not be displayed on valid entry
				k_valuewarning.setForeground(Color.black);
				k_valuewarning.setText("");

				// make sure K is ints only (VALIDATION)
				String k = getKvalue();
				boolean valid = false; // always assume false, unless true
				// trim trailing spaces
				k = k.trim();
				try {
					Integer.parseInt(k);
					k_valuewarning.setForeground(Color.black);
					k_valuewarning.setText("");
					valid = true;
					}
					catch (NumberFormatException exc)
					{
						valid = false;
					}

				if (k.equals("")){
					k_valuewarning.setForeground(Color.red);
					k_valuewarning.setText("int required");
				}
				else if (k.charAt(0) == '-'){
					k_valuewarning.setForeground(Color.red);
					k_valuewarning.setText("positive only");
				}
				else if (!valid){
					k_valuewarning.setForeground(Color.red);
					k_valuewarning.setText("ints only");
				}
					//k_value.setText("int required");
				else
				//if (input >=  1800 && input <= 2013)
				{
					ListKCarsWithTheMostServices(esql);
					// reset k_value
					k_value.setText("");
				}



			}
		}

		private class AddCustomerListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				// reset warning for valid entry:
				c_warning.setForeground(Color.black);
				c_warning.setText("");
				// VALIDATION ON ALL Customer fields:
				if(cfname_t.getText().trim().equals("")){
					c_warning.setForeground(Color.red);
					c_warning.setText("First name can't be blank");
				}
				else if (clname_t.getText().trim().equals("")){
					c_warning.setForeground(Color.red);
					c_warning.setText("Last name can't be blank");
				}
				else if (cphone_t.getText().trim().equals("")){
					c_warning.setForeground(Color.red);
					c_warning.setText("Phone can't be blank");
				}
				else if (caddress_t.getText().trim().equals("")){
					c_warning.setForeground(Color.red);
					c_warning.setText("Address can't be blank");
				}
				else if (!(cfname_t.getText().matches("^[A-Za-z]*$|^[A-Za-z][A-Za-z ]*[A-Za-z]$"))){
					c_warning.setForeground(Color.red);
					c_warning.setText("First - chars only");
				}
				else if (cfname_t.getText().length() > 32){
					c_warning.setForeground(Color.red);
					c_warning.setText("First - 32 chars max");
				}
				else if (!(clname_t.getText().matches("^[A-Za-z]*$|^[A-Za-z][A-Za-z ]*[A-Za-z]$"))){
					c_warning.setForeground(Color.red);
					c_warning.setText("Last - chars only");
				}
				else if (clname_t.getText().length() > 32){
					c_warning.setForeground(Color.red);
					c_warning.setText("Last - 32 chars max");
				}
				else if (!(caddress_t.getText().matches("^[^-\\s][a-zA-Z0-9\\s]+[^-\\s]$"))){
					c_warning.setForeground(Color.red);
					c_warning.setText("Address - alphanumeric only");
				}
				else if (caddress_t.getText().length() > 256){
					c_warning.setForeground(Color.red);
					c_warning.setText("Address - 256 chars max");
				}
				// created a regex for phone format: (xxx)xxx-xxxx
				else if (!(cphone_t.getText().matches("^\\((\\d{3})\\)(\\d{3})\\-(\\d{4})$"))){
					c_warning.setForeground(Color.red);
					c_warning.setText("Ph format: (xxx)xxx-xxxx");
				}
				else {
					AddCustomer(esql);
					//reset text fields
					cfname_t.setText("");
					clname_t.setText("");
					cphone_t.setText("");
					caddress_t.setText("");
				}
			}
		}

	/**	private class AddSRCustomerListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				// reset warning for valid entry:
				sr_c_warning.setForeground(Color.black);
				sr_c_warning.setText("");
				// VALIDATION ON ALL Customer fields:
				if(sr_cfname_t.getText().trim().equals("")){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("First name can't be blank");
				}
				else if (sr_clname_t.getText().trim().equals("")){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Last name can't be blank");
				}
				else if (sr_cphone_t.getText().trim().equals("")){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Phone can't be blank");
				}
				else if (sr_caddress_t.getText().trim().equals("")){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Address can't be blank");
				}
				else if (!(sr_cfname_t.getText().trim().matches("^[A-Za-z]*$|^[A-Za-z][A-Za-z ]*[A-Za-z]$"))){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("First - chars only");
				}
				else if (sr_cfname_t.getText().trim().length() > 32){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("First - 32 chars max");
				}
				else if (!(sr_clname_t.getText().trim().matches("^[A-Za-z]*$|^[A-Za-z][A-Za-z ]*[A-Za-z]$"))){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Last - chars only");
				}
				else if (sr_clname_t.getText().trim().length() > 32){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Last - 32 chars max");
				}
				else if (!(sr_caddress_t.getText().trim().matches("^[^-\\s][a-zA-Z0-9\\s]+[^-\\s]$"))){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Address - alphanumeric only");
				}
				else if (sr_caddress_t.getText().trim().length() > 256){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Address - 256 chars max");
				}
				// created a regex for phone format: (xxx)xxx-xxxx
				else if (!(sr_cphone_t.getText().trim().matches("^\\((\\d{3})\\)(\\d{3})\\-(\\d{4})$"))){
					sr_c_warning.setForeground(Color.red);
					sr_c_warning.setText("Ph format: (xxx)xxx-xxxx");
				}
				else {
					AddSRCustomer(esql);
					last_sr.setText(sr_clname_t.getText());
					sr_cfname_t.setText("");
					sr_clname_t.setText("");
					sr_cphone_t.setText("");
					sr_caddress_t.setText("");
					sr_c_warning.setText("");
					searchresults.setVisible(false);
					sr_addcustomer_module.setVisible(false);
					addsrpanel.setVisible(true);
					date_p.setVisible(true);
					addservice_request_module.setVisible(true);
					// test DONT FORGET TO UNCOMMENT THIS
					odometer_t.setText(customeridforsr.getText());




				}
			}
		} */

		private class AddMechanicListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				// reset warning for valid entry:
				m_warning.setForeground(Color.black);
				m_warning.setText("");

				// Validations for Mechanic fields (make sure insert values are not null):
				if(mfname_t.getText().trim().equals("")){
					m_warning.setForeground(Color.red);
					m_warning.setText("First name can't be blank");
				}
				else if (mfname_t.getText().length() > 32){
					m_warning.setForeground(Color.red);
					m_warning.setText("First name - 32 chars max");
				}
				else if (mlname_t.getText().trim().equals("")){
					m_warning.setForeground(Color.red);
					m_warning.setText("Last name can't be blank");
				}
				else if (mlname_t.getText().trim().length() > 32){
					m_warning.setForeground(Color.red);
					m_warning.setText("Last name - 32 chars max");
				}
				else if (m_years_t.getText().trim().equals("")){
					m_warning.setForeground(Color.red);
					m_warning.setText("Experience can't be blank");
				}
				// First and last need to only accept chars
				else if (!(mfname_t.getText().trim().matches("^[A-Za-z]*$|^[A-Za-z][A-Za-z ]*[A-Za-z]$"))){
					m_warning.setForeground(Color.red);
					m_warning.setText("First - chars only");
				}
				else if (!(mlname_t.getText().trim().matches("^[A-Za-z]*$|^[A-Za-z][A-Za-z ]*[A-Za-z]$"))){
					m_warning.setForeground(Color.red);
					m_warning.setText("Last - chars only");
				}
				// experience has to be numeric (up to 2 digits - the rest is unrealistic)
				else if (!(m_years_t.getText().trim().matches("^\\d{1,2}$"))){
					m_warning.setForeground(Color.red);
					m_warning.setText("Experience - ints (0-99)");
				}
				else {
					AddMechanic(esql);
					// reset text fields
					mfname_t.setText("");
					mlname_t.setText("");
					m_years_t.setText("");
				}
			}
		}

		private class AddCarListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				// reset warning for valid entry:
				car_warning.setForeground(Color.black);
				car_warning.setText("");

				// Validations for Car fields (make sure values for insert are not null):
				if(vin_t.getText().trim().equals("")){
					car_warning.setForeground(Color.red);
					car_warning.setText("VIN can't be blank");
				}
				else if (make_t.getText().trim().equals("")){
					car_warning.setForeground(Color.red);
					car_warning.setText("Make can't be blank");
				}
				else if (model_t.getText().trim().equals("")){
					car_warning.setForeground(Color.red);
					car_warning.setText("Model can't be blank");
				}
				else if (year_t.getText().trim().equals("")){
					car_warning.setForeground(Color.red);
					car_warning.setText("Year can't be blank");
				}
				else if (owner_t.getText().trim().equals("")){
					car_warning.setForeground(Color.red);
					car_warning.setText("Owner ID can't be blank");
				}
				// VIN needs to be alpha-numeric (16 digits)
				else if (!(vin_t.getText().trim().matches("^[a-zA-Z0-9]{16}$")))
				{
					car_warning.setForeground(Color.red);
					car_warning.setText("Vin - alphanumeric 16 chars");
				}
				else if (!(owner_t.getText().trim().matches("^[0-9]*$")))
				{
					car_warning.setForeground(Color.red);
					car_warning.setText("Owner ID - numbers only");
				}
				// make can be alphanumeric but 32 char max
				else if (!(make_t.getText().trim().matches("^[a-zA-Z0-9 ]*$"))){
					car_warning.setForeground(Color.red);
					car_warning.setText("Make - alphanumeric only");
				}
				else if (make_t.getText().trim().length() > 32){
					car_warning.setForeground(Color.red);
					car_warning.setText("Make - 32 chars max");
				}
				// model can be alphanumeric but 32 char max
				else if (!(model_t.getText().trim().matches("^[a-zA-Z0-9 ]*$"))){
					car_warning.setForeground(Color.red);
					car_warning.setText("Model - alphanumeric only");
				}
				else if (model_t.getText().trim().length() > 32){
					car_warning.setForeground(Color.red);
					car_warning.setText("Model - 32 chars max");
				}
				// year needs to be a 4-digit int
				else if (!(year_t.getText().trim().matches("^\\d{4}$"))){
					car_warning.setForeground(Color.red);
					car_warning.setText("Year - 4 digit int");
				}
				else {

					CheckIfValidOwner(owner_t.getText().trim(), esql);

					if (!validowner){
						car_warning.setForeground(Color.red);
						car_warning.setText("Such owner does not exist");
					}
					else{
					AddCar(esql);
					// reset text fields
					vin_t.setText("");
					make_t.setText("");
					model_t.setText("");
					year_t.setText("");
					owner_t.setText("");
					}
				}
			}
		}

		private class CloseSRListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				// reset warning for valid entry:
				cl_warning.setForeground(Color.black);
				cl_warning.setText("");

				// Validations for Car fields (make sure values for insert are not null):
				if(clmech_id_t.getText().trim().equals("")){
					cl_warning.setForeground(Color.red);
					cl_warning.setText("Mechanic ID can't be blank");
				}
				else if (cl_rid_t.getText().trim().equals("")){
					cl_warning.setForeground(Color.red);
					cl_warning.setText("Request ID can't be blank");
				}
				else if (bill_t.getText().trim().equals("")){
					cl_warning.setForeground(Color.red);
					cl_warning.setText("Amount can't be blank");
				}
				else if (!(clmech_id_t.getText().trim().matches("^[0-9]*$")))
				{
					cl_warning.setForeground(Color.red);
					cl_warning.setText("Mechanic ID - numbers only");
				}
				else if (!(cl_rid_t.getText().trim().matches("^[0-9]*$"))){
					cl_warning.setForeground(Color.red);
					cl_warning.setText("Request ID - numbers only");
				}
				else if (!(bill_t.getText().trim().matches("^[0-9]*$"))){
					cl_warning.setForeground(Color.red);
					cl_warning.setText("Amount - numbers only");
				}
			/*	else if (validcldate == false){
					cl_warning.setForeground(Color.red);
					cl_warning.setText("Invalid closed date");
				}
				*/
				else {
					// check if sr exists
					CloseSR(esql);
					// reset text fields
					cl_rid_t.setText("");
					clmech_id_t.setText("");
					bill_t.setText("");
					cl_comment_t.setText("");
				}
			}
		}

		private class AddSRListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				// reset warning for valid entry:
				sr_warning.setForeground(Color.black);
				sr_warning.setText("");

				// Validations for sr fields (make sure values for insert are not null, except complaint):
				if(odometer_t.getText().trim().equals("")){
					sr_warning.setForeground(Color.red);
					sr_warning.setText("Odometer can't be blank");
				}
				else if (last_sr.getText().trim().equals("")){
					sr_warning.setForeground(Color.red);
					sr_warning.setText("Last name can't be blank");
				}
				else if (vin_sr.getText().trim().equals("")){
					sr_warning.setForeground(Color.red);
					sr_warning.setText("VIN can't be blank");
				}
				// VIN needs to be alpha-numeric (16 digits)
				else if (!(vin_sr.getText().trim().matches("^[a-zA-Z0-9]{16}$")))
				{
					sr_warning.setForeground(Color.red);
					sr_warning.setText("Vin - alphanumeric 16 chars");
				}
				else if (!(odometer_t.getText().trim().matches("^[0-9]*$")))
				{
					sr_warning.setForeground(Color.red);
					sr_warning.setText("Odometer - ints only");
				}
				else {
					LookForVin(vin_sr.getText(), esql);
					if (!vinfound)
					{
						sr_warning.setForeground(Color.red);
						sr_warning.setText("Invalid VIN");

					} else
						{
						InsertServiceRequest(esql);
							// reset text fields
						vin_sr.setText("");
						last_sr.setText("");
						odometer_t.setText("");
						complain_t.setText("");
						//return to main window:
						addSRpopup.dispose();
						// reset booleans:
						validcustomer = false;
						vinfound = false;
						//reset hidden fields:
						hiddencarvin.setText("");
						customeridforsr.setText("");
						addsr.setEnabled(false);

						// TEST
						//cars_p.add(addcar_module);
					}

				}
			}
		}

		private class OpenSRWindowListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				gui.foundcars.setVisible(false);
				gui.searchresults.setVisible(false);
				gui.addservice_request_module.setVisible(true);
				vin_sr.setText("");
				last_sr.setText("");
				odometer_t.setText("");
				complain_t.setText("");
				addSRpopup.setVisible(true);
				addsr.setEnabled(false);
			}
		}

		private class ClosedSRWindowListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				CloseSRpopup.setVisible(true);
			}
		}

		private class SearchRequestByLastName implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				// reset warning for valid entry:
				sr_warning.setForeground(Color.black);
				sr_warning.setText("");
				// VALIDATION ON ALL Customer fields:
				if(last_sr.getText().trim().equals("")){
					sr_warning.setForeground(Color.red);
					sr_warning.setText("Need last name to search");
				}
				else if (last_sr.getText().trim().length() > 32){
					sr_warning.setForeground(Color.red);
					sr_warning.setText("Last - 32 chars max");
				}
				else if (!(last_sr.getText().trim().matches("^[^-\\s][a-zA-Z\\s]+[^-\\s]$"))){
					sr_warning.setForeground(Color.red);
					sr_warning.setText("Last - chars only");
				}
				else {
				SearchRequestByLastName(esql);
				}

			}
		}

		private class ListCustomersInDescOrderOfBillListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				ListCustomersInDescendingOrderOfTheirTotalBill(esql);
			}
		}
		private class allcustomersWithIDsListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				ListallcustomersWithIDs(esql);
			}
		}

		private class ViewAllOpenRequestsListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				ViewAllOpenRequests(esql);

			}
		}

		private class ViewAllClosedRequestsListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				gui.resetAllWarnings();
				ViewAllClosedRequests(esql);

			}
		}

        // GUI Methods:

        public static void loadResults(String s){
            resultswindow.setText(s);
        }

        public static void loadNumRows(String s){
            numresults.setText(s);
        }

        public static String getKvalue(){
        	String s = k_value.getText();
        	return s;
		}

		public static String getTextFromField(JTextField t){
        	String s = t.getText().trim();
        	return s;
		}

		// results JTable methods

		public static void setTableRow(String[] s){

        	DefaultTableModel dm = (DefaultTableModel)resultstable.getModel();
        	dm.addRow(s);

		}

		public static void getTableModel(){


		}

		public static void setTableColumns(String[] s){
			DefaultTableModel dm = (DefaultTableModel)resultstable.getModel();
			for (int i = 0; i < s.length; i++){
				dm.addColumn(s[i]);
			}

		}

		public static void setTableHeaders(){

		}

		// reset all unrelated warnings on button press
		public static void resetAllWarnings(){
			c_warning.setText("");
			m_warning.setText("");
			car_warning.setText("");
			k_valuewarning.setText("");
		}
    } // end GUI

}

