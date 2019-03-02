package com.mail.error;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

  // 모든 예외 처리
  @ExceptionHandler(Exception.class)
  public void handlerException(HttpServletRequest req, HttpServletResponse response,
                  Exception e) throws Exception{
	    StringWriter error = new StringWriter();
	  e.printStackTrace(new PrintWriter(error));
	  req.getSession().setAttribute("error", "서버에서 에러가 발생했습니다!");
	    if(e.getClass().getName().equals("org.springframework.web.servlet.NoHandlerFoundException"));
	    {
	    	req.getSession().setAttribute("error", "요청하신 페이지를 찾을수 없습니다!");   	 
	    }
    // 컨트롤러가 아니기 때문에 뷰 페이지의 full path를 작성해야 함
	    req.getRequestDispatcher("/resources/error.html").forward(req, response);
  }

  
}