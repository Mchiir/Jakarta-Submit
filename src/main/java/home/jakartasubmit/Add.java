package home.jakartasubmit;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class Add extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int num1 = Integer.parseInt(request.getParameter("num1"));
        int num2 = Integer.parseInt(request.getParameter("num2"));
        int sum  = num1 + num2;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html style=\"text-align: center;\">");
                out.println("The sum is "+ sum);
                out.println("<div>");
                out.println("<a href=\""+ request.getContextPath() +"\">Back</a>");
                out.println("</div>");
                out.println("</html>");
    }
}
