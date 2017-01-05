README
Jeffrey Stewart
JLS317
CSE241 - Project Jog

tables.txt:
*code for implementing all tables into Oracle SQL Developer Database

data.txt:
*code for adding all data into the tables


Program:
*Throughout the program use "*" to return to the nearest menu (Main Menu or Report Menu)
*Using "*" on main menu exits the program.

*Option 1 - Store List
	Gives reference of all numbers and location of all Jog Wireless stores.
	Returns to main menu


*Option 2 - Inventory Management
	Manage inventory changes from orders(+) and sales(-). 
	Input relevant store number (STORE_ID)
		-Displays list of all numbers and descriptions of each item sold by the store.
	Input the item number to change
		-Displays the current stock and capacity of the item in the store.
	Input net change
		-Restocking Orders (positive number) must not exceed the store's capacity.
		-Sales (negative number) must not give negative stock
		-Valid changes will update the database
		-Displays item number, its new stock value, and the store's capacity
	Repeats, input item number to change


*Option 3 - Billing Report
	Option 1 - Current Customers
		Displays list of all current customer AcctIDs
		Jog Wireless can only generate reports of current customer
			-Reports of previous customers should have been archived
			-Reduces error in typing AcctID
	Option 2 - Generate Report
		Enter a Current Account Number
			-AcctID 54387 demonstrates a plan with no unlimited options
			-AcctID 30123 demonstrates a plan with unlimited texts and data
			-AcctID 39905 demonstrates a plan with unlimited everything
		Enter the desired year
			-Only 2016 has data, but must be between 1900 and 2016
		Enter the desired month
			-Only 'march' or '03' and 'april' or '04' has data
			-Can be run for any month
		Process Complete.
			-Text File is saved in ./BillingReports/ from working directory
			-File Name is formatted: 'Acct_ID'_'MM'_'YYYY'.txt
	Option 3 - Return to Main Menu
			

*Option 4 - Exits the Program
