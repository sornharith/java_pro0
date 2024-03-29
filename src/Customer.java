// File: Customer
// Description: method of all action that will have in canteen
// Project: 1
//
// ID: 6588067
// Name: Harith Phalangpatanakij
// Section: 2
//
// On my honor, Harith Phalangpatanakij, this project assignment is my own work
// and I have not provided this code to any other students.


import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

public class Customer {
	
	//*********************** DO NOT MODIFY ****************************//
	public static enum CustomerType{DEFAULT, STUDENT, PROFESSOR, ATHLETE, ICTSTUDENT};	//Different types of customers 
	private static int customerRunningNumber = 1;	//static variable for assigning a unique ID to a customer
	private CanteenICT canteen = null;	//reference to the CanteenICT object
	private int customerID = -1;		//this customer's ID
	protected CustomerType customerType = CustomerType.DEFAULT;	//the type of this customer, initialized with a DEFAULT customer.
	protected List<FoodStall.Menu> requiredDishes = new ArrayList<FoodStall.Menu> ();	//List of required dishes
	
	public static enum Payment{DEFAULT, CASH, MOBILE};
	public static final int[] PAYMENT_TIME = {3, 2, 1};
	protected Payment payment = Payment.DEFAULT;
	
	protected int state = 0; // 0 wait-to-enter, 1 wait-to-order, 2 ordering, 
							 // 3 making payment, 4 wait-to-seat, 5 siting, 
							 // 6 eating, 7 done
	//*****************************************************************//
	
	
	/**
	 * Constructor. Initialize canteen reference, default customer type, and default payment method. 
	 * 				Initialize other values as needed
	 * @param _canteen
	 */
	public Customer(CanteenICT _canteen)
	{
		//******************* YOUR CODE HERE **********************
		this.canteen = _canteen;
		this.customerType = CustomerType.DEFAULT;
		this.payment = Payment.DEFAULT;	
		this.customerID = this.customerRunningNumber; // running of customer ID
		this.customerRunningNumber++;
		adddish(); // add all of the menu
		//*****************************************************
	}
	
	/**
	 * Constructor. Initialize canteen reference, default customer type, and specific payment method.
	 * 				Initialize other values as needed 
	 * @param _canteen
	 * @param payment
	 */
			
	public Customer(CanteenICT _canteen, Payment payment)	
	{
		//******************* YOUR CODE HERE **********************
		this.canteen = _canteen;
		this.payment = payment;
		this.customerType = CustomerType.DEFAULT;
		this.customerID = this.customerRunningNumber;// running of customer ID
		this.customerRunningNumber++;
		adddish();// add all of the menu
		//*****************************************************
	}
	
	
	
	/**
	 * Depends on the current state of the customer, different action will be taken
	 * @return true if the customer has to move to the next queue, otherwise return false
	 */
	public boolean takeAction()
	{
		//************************** YOUR CODE HERE **********************//
		if(this.state == 0) { // for adding the customer queue to store
			if (this.customerID == this.canteen.getWaitToEnter().get(0).getCustomerID()) {
				
				if (this.canteen.getfoodStalls().get(shortestqueue()).getCustomerQueue().size() < 5) { // in each store need to have customer least than 5 people
					this.canteen.getfoodStalls().get(shortestqueue()).getCustomerQueue().add(this.canteen.getWaitToEnter().get(0));
					this.canteen.getwStore().add(this.canteen.getWaitToEnter().get(0)); 
					this.state++;
					jot("@"+getCode()+"-"+getstate()+" queues up at "+this.canteen.getfoodStalls().get(shortestqueue()).getName()+ ", and waiting to order.");
					return true;
				}
			}
		} else if (state == 1) { // for order the food in the menu
			for (int i = 0 ; i < this.canteen.getfoodStalls().size() ; i++){
				if (this.canteen.getfoodStalls().get(i).getCustomerQueue().size() != 0) {
					if(this.customerID == this.canteen.getfoodStalls().get(i).getCustomerQueue().get(0).getCustomerID() ) {
						if(this.canteen.getfoodStalls().get(i).isWaitingForOrder() && this.canteen.getfoodStalls().size() != 0){
							this.canteen.getfoodStalls().get(i).takeOrder(this.requiredDishes);
							this.state++;
							setsTime(); // seting of the order that taken
							jot("@"+getCode()+"-"+getstate()+" orders from "+this.canteen.getfoodStalls().get(i).getName()+", and will need to wait for 9 periods to cook.");
							return true;
						}
					}
				}
			}

		} else if (state == 2){ // for payment
			for (int i = 0 ; i < this.canteen.getfoodStalls().size() ; i++ ){
				if (this.canteen.getfoodStalls().get(i).getCustomerQueue().size() < 1){
					continue;
				}
				if (this.customerID == this.canteen.getfoodStalls().get(i).getCustomerQueue().get(0).getCustomerID()){
					if (this.canteen.getfoodStalls().get(i).isCooking()){
						break;
					} else {
						if (this.canteen.getfoodStalls().get(i).isReadyToServe() && this.sTIme+10 == this.canteen.getCurrentTime()){ // check that ordre that ready and use 10 second for waiting
							this.canteen.getfoodStalls().get(i).takePayment(payment);
							setpTime();
							this.state++;
							jot("@"+getCode()+"-"+getstate()+" at "+this.canteen.getfoodStalls().get(i).getName()+" using DEFAULT payment which requires 3 period(s) to process payment.");
							return true;
						}
					}
					}
				}
		} else if (state == 3){ // for this add customer from foodstore to the waiting table
			for (int i = 0 ; i < this.canteen.getfoodStalls().size() ; i++){
				if (this.canteen.getfoodStalls().get(i).getCustomerQueue().size() == 0){ // skip when have no customer in food order
					continue;
				}
				if (this.customerID == this.canteen.getfoodStalls().get(i).getCustomerQueue().get(0).getCustomerID()){
					if (this.canteen.getfoodStalls().get(i).isPaid()){
						this.canteen.getfoodStalls().get(i).serve();
						this.canteen.getWaitToSeat().add(this.canteen.getfoodStalls().get(i).getCustomerQueue().get(0));
						this.state++;
						jot("@"+getCode()+"-"+getstate()+" retrieves food from "+this.canteen.getfoodStalls().get(i).getName()+", and goes to Waiting-to-Seat Queue.");
						return true;
					}
				}
			}
		} else if (state == 4) { // for adding customer from waiting table to the table
			int er = 0;
			if (this.canteen.getWaitToSeat().size() > 0) {
				for (int i = 0 ; i < this.canteen.getTable().size() ; i++) {
					for (Table l : this.canteen.getTable()) {
						if (er < 1){
						if (!l.isFull() && Ttable == false) { // check that table is full and didn't add customer 2 time in one second
							l.getSeatedCustomers().add(this.canteen.getWaitToSeat().get(0));
							er++;
							Ttable = true;
							this.canteen.getWaitToSeat().remove(0);	
							this.state++;
							jot("@"+getCode()+"-"+getstate()+" sits at Table "+(i+1)+".");
							return true;
							}
						} else if (er > 1){ // if it alerdy add customer to table it will break.
							break;
						}	
					}
				}
			}
		} else if (state == 5){ // for set time that customer start eating
			for (int i = 0 ; i < this.canteen.getTable().size() ; i++){
				for (int j = 0 ; j < this.canteen.getTable().get(i).getSeatedCustomers().size() ; j++){
					if (this.itTIme == 0){
						setTime();
						this.state++;
						jot("@"+getCode()+"-"+getstate()+" eats at the table, and will need 28 periods to eat his/her meal.");
						return false;
					}
				}
			}
		} else if (state == 6) { // check that customer done eat
			for (int i = 0 ; i < this.canteen.getTable().size() ; i++){
				for (int j = 0 ; j < this.canteen.getTable().get(i).getSeatedCustomers().size() ; j++){
					if (this.itTIme+28 == this.canteen.getCurrentTime()){
						this.canteen.getDone().add(this.canteen.getTable().get(i).getSeatedCustomers().get(j));
						this.canteen.getwTable().add(this.canteen.getTable().get(i).getSeatedCustomers().get(j));
						this.state++;
						jot("@"+getCode()+"-"+getstate()+"is done eating.");
						return true;
					}
				}
			}
		}
		return false;
		//**************************************************************//
		
	}
	
	
	//******************************************** YOUR ADDITIONAL CODE HERE (IF ANY) *******************************//
	public int getstate(){
		return this.state;
	}
	public int shortestqueue() { // method to find which store have shortest queue to go
		int shortqu = 0;
		int nnum = 0;
		if (this.canteen.getfoodStalls().size() > 1){ // have customer come in to store or not
			for (int i = 0 ; i < this.canteen.getfoodStalls().size() ;i++) {
				if (i == 0 && this.canteen.getfoodStalls().get(i).getMenu().size() == 5) { // first customer 
					nnum = this.canteen.getfoodStalls().get(i).getCustomerQueue().size();
					shortqu = i;
				}
				else {
					if (this.canteen.getfoodStalls().get(i).getCustomerQueue().size() < nnum && this.canteen.getfoodStalls().get(i).getMenu().size() == 5) {
						nnum = this.canteen.getfoodStalls().get(i).getCustomerQueue().size();
						shortqu = i;
					}
				}
			}
		}
		return shortqu;
	}

	 public int shortestT (){ // method to find the table with least customer
	 	int avaT =  -1;
		for (int i = 0 ; i < this.canteen.getWaitToSeat().size() ; i++){
			if (this.canteen.getTable().size() == 1 && this.canteen.getTable().get(0).isFull() == false){
				avaT = 0;
			} 
			if (this.canteen.getTable().size() > 1){
				if (this.canteen.getTable().get(i).isFull() == false){
					avaT = i;
					break;
				}
			}
		}
	 	return avaT;
	 }

	public void adddish (){ // add all dish
		this.requiredDishes.add(FoodStall.Menu.NOODLES);
		this.requiredDishes.add(FoodStall.Menu.DESSERT);
		this.requiredDishes.add(FoodStall.Menu.MEAT);
		this.requiredDishes.add(FoodStall.Menu.SALAD);
		this.requiredDishes.add(FoodStall.Menu.BEVERAGE);	
	}
	public int sumTime(){ // findind time that customer need to eat
		int sum = 0;
		List<FoodStall.Menu> m = new ArrayList<FoodStall.Menu>();
		for (int i = 0 ; i < this.canteen.getfoodStalls().size() ; i++){
			for (int j = 0 ; j < this.canteen.getfoodStalls().get(i).getMenu().size() ; j++){
				m.add(this.canteen.getfoodStalls().get(i).getMenu().get(j));
				switch (m.get(j).toString()){
					case "NOODLES" : sum += 6; break;
					case "DESSERT" : sum += 5; break;
					case "MEAT"	   : sum += 10; break;
					case "SALAD"   : sum += 5; break;
					case "BEVERAGE": sum += 2; break;
				}
			}
		}
		return sum;
	}
	private int itTIme = 0; // time that customer start eating
	public void setTime(){
		this.itTIme = this.canteen.getCurrentTime();
	}
	private int sTIme = 0; // time that customer order
	
	public void setsTime(){
		this.sTIme = this.canteen.getCurrentTime();
	}
	private int pTIme = 0; //time that customer get order
	public void setpTime(){
		this.pTIme = this.canteen.getCurrentTime();
	}
	public static boolean Ttable = false;
	
	//****************************************************************************************************//
				
	

	//***************For hashing, equality checking, and general purposes. DO NOT MODIFY **************************//	
	
	public CustomerType getCustomerType()
	{
		return this.customerType;
	}
	
	public int getCustomerID()
	{
		return this.customerID;
	}
	
	public Payment getPayment()
	{
		return this.payment;
	}
	
	public List<FoodStall.Menu> getRequiredFood()
	{
		return this.requiredDishes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + customerID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (customerID != other.customerID)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Customer [customerID=" + customerID + ", customerType=" + customerType +", payment="+payment.name()+"]";
	}

	public String getCode()
	{
		return this.customerType.toString().charAt(0)+""+this.customerID;
	}
	
	/**
	 * print something out if VERBOSE is true 
	 * @param str
	 */
	public void jot(String str)
	{
		if(CanteenICT.VERBOSE) System.out.println(str);
		
		if(CanteenICT.WRITELOG) CanteenICT.append(str, canteen.name+"_state.log");
	}


	//*************************************************************************************************//
	
}
