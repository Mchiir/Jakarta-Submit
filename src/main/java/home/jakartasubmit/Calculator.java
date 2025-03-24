package home.jakartasubmit;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class Calculator extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action.equalsIgnoreCase("add")){
            addNumbers(request, response);
        }else{
            response.sendRedirect(request.getContextPath()+"/error.jsp");
        }
    }

    private void addNumbers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try{
            int num1 = Integer.parseInt(request.getParameter("num1"));
            int num2 = Integer.parseInt(request.getParameter("num2"));
            int sum  = num1 + num2;
            request.setAttribute("sum", sum);
            request.getRequestDispatcher("home.jsp").forward(request, response);
        } catch (Exception e) {
            out.println("Error occured while adding numbers: "+ e.getMessage());
            out.println("<div><a href=\" "+request.getContextPath()+" \">Back</a></div>");
        }
    }
}
