/*ackage com.danusys.smartmetering.common.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class UserDeniedHandler implements AccessDeniedHandler{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ade) throws IOException, ServletException {
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		try {
			request.setAttribute("resultCode", "403");
			request.setAttribute("message", "요청 권한이 없습니다.");
			ServletUtil.sendError(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}*/