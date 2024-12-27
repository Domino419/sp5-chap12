package controller;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.DuplicateMemberException;
import spring.MemberRegisterService;
import spring.RegisterRequest;

@Controller
public class RegisterController {

	private static final Log log = LogFactory.getLog(RegisterController.class);

	private MemberRegisterService memberRegisterService;

	public void setMemberRegisterService(
			MemberRegisterService memberRegisterService) {
		this.memberRegisterService = memberRegisterService;
	}

	@RequestMapping("/register/step1")
	public String handleStep1() {
		return "register/step1";
	}

	@PostMapping("/register/step2")
	public String handleStep2(
			@RequestParam(value = "agree", defaultValue = "false") Boolean agree,
			Model model) {
		if (!agree) {
			return "register/step1";
		}
		model.addAttribute("registerRequest", new RegisterRequest());
		return "register/step2";
	}

	@GetMapping("/register/step2")
	public String handleStep2Get() {
		return "redirect:/register/step1";
	}

	/**
	 * method        : handleStep3
	 * date          : 2024-12-26
	 * param         : RegisterRequest regReq - 회원 가입 요청 데이터를 담은 객체
	 * param         : Errors errors - 요청 데이터의 유효성 검증 결과를 담은 객체
	 * return        : String - 처리 결과에 따라 반환할 뷰의 이름
	 * description   : 회원 가입 3단계 처리를 담당. 입력 데이터를 검증하고, 중복 이메일 여부를 확인한 뒤,
	 *                 성공 시 회원 등록을 완료하고 완료 페이지로 이동.
	 *                 - 유효성 검사 실패: step2 페이지 반환
	 *                 - 중복 이메일 예외 발생: step2 페이지 반환, "email" 필드에 "duplicate" 오류 추가
	 *                 - 성공 시: step3 페이지 반환
	 */
	@PostMapping("/register/step3")
	public String handleStep3(@Valid RegisterRequest regReq, Errors errors) {
		log.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::handleStep3" );

		if (errors.hasErrors())        // 유효성 검사 결과에 오류가 있는 경우
			return "register/step2";   // 유효성 검증 실패 시 다시 step2 페이지 반환
		try {
			memberRegisterService.regist(regReq);  // 회원 등록 서비스 호출: 요청 데이터를 이용해 회원 정보 저장
			return "register/step3";               // 회원 등록 성공 시 step3 페이지 반환
		} catch (DuplicateMemberException ex) {
			errors.rejectValue("email", "duplicate");  // 중복 회원 예외 처리: "email" 필드에 "duplicate" 오류 추가
			// errors.reject("SampleCode_NotMatchPassword");  // 중복 회원 예외 처리: 글로벌 오류를 등록처리
			return "register/step2";							     // 예외 발생 시 step2 페이지 반환
		}
	}

}
