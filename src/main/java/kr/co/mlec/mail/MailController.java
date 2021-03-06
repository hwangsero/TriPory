package kr.co.mlec.mail;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.mlec.login.LoginService;
import kr.co.mlec.vo.MemberVO;

@Controller
public class MailController {

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private MailService mailService;

	@GetMapping("/emailConfirm")
	public String emailConfirm(HttpServletRequest request, HttpSession session, Model model) {
		MemberVO member = (MemberVO)session.getAttribute("userVO"); // 로그인 정보
		String email = (String) request.getParameter("email"); // 이메일 파라미터
		String key = (String) request.getParameter("key"); // 키 파라미터
		
		if( email == null && key == null && member != null ) { // 회원가입을 통해 접속
			return "email/authRequest";
		} else if( email != null && key != null ){ // 이메일 링크를 통해 접속
			MemberVO keyConfirm = loginService.keyConfirm(email, key); // 회원 조회
			
			if( keyConfirm != null ) { // 이메일과 key값을 동일하게 가지고 있는 회원이 있는경우
				// 이미 인증한경우
				if( keyConfirm.getIs_auth().equals("1") ) { 
					model.addAttribute("code", "-1");
				} else { 	// 인증하지 않았고 인증된경우
					loginService.AuthUpdate(email);
					member.setIs_auth("1");
					session.setAttribute("userVO", member);
					model.addAttribute("code", "1");
				}
			} else { // 인증 실패
				model.addAttribute("code", "0");
			}
			return "email/keyConfirm";
		} else{
			return "redirect:/";
		}
	}
	
	@ResponseBody
	@PostMapping("/resendEmail")
	public String resendEmail(HttpSession session) throws Exception {
		MemberVO member = (MemberVO)session.getAttribute("userVO"); // 로그인 정보
		mailService.sendEmail(member);
		return "이메일 재전송";
	}
	
	@PostMapping("/find/resendEmail")
	public String findResendEmail(MemberVO vo) throws Exception {
		MemberVO member = new MemberVO();
		member.setEmail(vo.getEmail() + "@" +vo.getEmail_domain());
		System.out.println(member.getEmail());
		mailService.findSendEmail(member);
		System.out.println("여기를 못넘어오지?");
		return "findPassword/findAuthRequest";
	}
	
}
