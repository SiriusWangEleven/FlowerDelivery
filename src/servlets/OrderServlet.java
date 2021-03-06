package servlets;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Instances.Order;
import database.DBConnection;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderServlet()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// handle 3 different kinds of request: placeOrder, userCheckOrder, adminCheckOrder. 
		// Distinguished by URL
		try
		{
			DBConnection dbConnection = new DBConnection();
			dbConnection.conn = dbConnection.getConnection("FlowerDelivery");

			String url = request.getServletPath();
			System.out.println(url);
			
			if (url.contains("userCheckOrder"))
			{
				String userPhoneNum = request.getParameter("userPhoneNum");
				String password = request.getParameter("password");
				int ok = checkUser(dbConnection, userPhoneNum, password);
				if (ok==1)
				{
					ArrayList<Order> orders = userCheckOrder(dbConnection, userPhoneNum);
					// to pass orders to jsp
				}
			} else if (url.contains("placeOrder"))
			{
				
				String userPhoneNum = request.getParameter("userPhoneNum");
				String password = request.getParameter("password");
				int ok = checkUser(dbConnection, userPhoneNum, password);
				if (ok==0)
				{
					// new user, create user account and order
					dbConnection.update("insert into users values('" + userPhoneNum + "','" + password + "')");
					Order order = createOrder(request);
					placeOrder(dbConnection, order);
				}
				else if(ok==1)
				{
					// user account already exists
					Order order = createOrder(request);
					placeOrder(dbConnection, order);
				}
				
				
			} else if (url.contains("adminCheckOrder"))
			{
				ArrayList<Order> orders = adminCheckOrder(dbConnection);
				// to pass orders to jsp
			}

			dbConnection.closeDB();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private ArrayList<Order> adminCheckOrder(DBConnection dbConnection) throws Exception
	{
		ArrayList<Order> orders = new ArrayList<>();
		String sql = "select * from orders";
		dbConnection.rs = dbConnection.query(sql);
		while (dbConnection.rs.next())
		{
			Order order = createOrder(dbConnection.rs);
			orders.add(order);
		}
		return orders;
		
	}
	private void placeOrder(DBConnection dbConnection, Order order)
	{
		dbConnection.update("insert into users orders(" + order.getOrderID() + ",'" + order.getNextDeliveryDay() + 
				"'," + order.getReceivePeriod() +"," + order.getTimesLeft() +",'" + order.getFlowers() +
				"','" + order.getUserPhoneNum() +"','" + order.getReceiverName() +"','" + order.getReceiverAddr() +
				"','" + order.getReceiverPhone() +"')");
	}

	private ArrayList<Order> userCheckOrder(DBConnection dbConnection, String userPhoneNum) throws Exception
	{
		ArrayList<Order> orders = new ArrayList<>();
		String sql = "select * from orders where userPhoneNum=" + userPhoneNum;
		dbConnection.rs = dbConnection.query(sql);
		while (dbConnection.rs.next())
		{
			Order order = createOrder(dbConnection.rs);
			orders.add(order);
		}
		return orders;
	}

	private int checkUser(DBConnection dbConnection, String userPhoneNum, String password) throws Exception
	{
		String sql = "select * from users where userPhoneNum=" + userPhoneNum;
		dbConnection.rs = dbConnection.query(sql);
		int ok;
		// ok = 1: this user information is correct;
		// ok = 0: new user;
		// ok = -1: this user information is incorrect
		if (dbConnection.rs.wasNull())
			ok = 0;
		else if (dbConnection.rs.getString(2).equals(password))
			ok = 1;
		else
			ok = -1;
		return ok;
	}

	private Order createOrder(HttpServletRequest request)
	{
		String userPhoneNum = request.getParameter("userPhoneNum");
		String password = request.getParameter("password");
		String receiverName = request.getParameter("receiverName");
		String receiverAddr = request.getParameter("receiverAddr");
		String receiverPhone = request.getParameter("receiverPhone");
		String flowers = request.getParameter("flowers");
		String date = request.getParameter("date");
		int receivePeriod = Integer.parseInt(request.getParameter("receivePeriod"));
		int timesLeft = Integer.parseInt(request.getParameter("timesLeft"));
		Order order = new Order(userPhoneNum, password, receiverName, receiverAddr, receiverPhone, flowers, date,
				receivePeriod, timesLeft);
		return order;
	}

	private Order createOrder(ResultSet rs) throws Exception
	{

		int orderID = rs.getInt(1);
		String nextDeliveryDay = rs.getString(2);
		int receivePeriod = rs.getInt(3);
		int timesLeft = rs.getInt(4);
		String flowers = rs.getString(5);
		String userPhoneNum = rs.getString(6);
		String receiverName = rs.getString(7);
		String receiverAddr = rs.getString(8);
		String receiverPhone = rs.getString(9);

		Order order = new Order();
		order.setOrderID(orderID);
		order.setNextDeliveryDay(nextDeliveryDay);
		order.setReceivePeriod(receivePeriod);
		order.setTimesLeft(timesLeft);
		order.setFlowers(flowers);
		order.setUserPhoneNum(userPhoneNum);
		order.setReceiverName(receiverName);
		order.setReceiverAddr(receiverAddr);
		order.setReceiverPhone(receiverPhone);

		return order;
	}

}
