package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.crypto.provider.RSACipher;

import Instances.Flower;
import database.DBConnection;

/**
 * Servlet implementation class FlowerServlet
 */
@WebServlet("/FlowerServlet")
public class FlowerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FlowerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try
		{
			DBConnection dbConnection = new DBConnection();
			dbConnection.conn = dbConnection.getConnection("FlowerDelivery");
			ArrayList<Flower> flowers = getFlower(dbConnection);
			
			// to pass flowers to jsp
			
			dbConnection.closeDB();
			request.setAttribute("flowersList", flowers);
			request.setAttribute("message", "hellloooo");
			System.out.println("asdfasfdasdf");
			request.getRequestDispatcher("start.jsp").forward(request,response);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try
		{
			DBConnection dbConnection = new DBConnection();
			dbConnection.conn = dbConnection.getConnection("FlowerDelivery");
			ArrayList<Flower> flowers = getFlower(dbConnection);
			
			// to pass flowers to jsp
			
			
			dbConnection.closeDB();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Flower> getFlowers() throws Exception{
		try
		{
			DBConnection dbConnection = new DBConnection();
			dbConnection.conn = dbConnection.getConnection("FlowerDelivery");
			ArrayList<Flower> flowers = getFlower(dbConnection);			
			
			dbConnection.closeDB();
			return flowers;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static ArrayList<Flower> getFlower(DBConnection dbConnection) throws Exception
	{
		ArrayList<Flower> flowers = new ArrayList<Flower>();
		String sql = "select * from flower_house where available = 1";
		dbConnection.rs = dbConnection.query(sql);
		while(dbConnection.rs.next())
		{
			String flowerName = dbConnection.rs.getString(1);
			String description = dbConnection.rs.getString(2);
			String imageLink = dbConnection.rs.getString(3);
			int available = dbConnection.rs.getInt(4);
			Flower flower = new Flower(flowerName,description,imageLink,available);
			flowers.add(flower);
		}
		return flowers;
	}

}
