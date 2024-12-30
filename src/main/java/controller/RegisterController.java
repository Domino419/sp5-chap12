package controller;

import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder; // 추가
import org.springframework.web.bind.annotation.*;

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


	/**
	 * method        : handleStep3
	 * 	(실습용) handleStep3에서  Error 타입 파라미터를 제거한 메소드
	 * 	에러코드를 파라미터로 받지 않은 경우에는 검증 실패시에 400 에러 코드가 발생함.
	 * 설명 클라이언트 오류로서 인지된 어떤 문제로 인하여, 서버가 해당 요청을 처리할 수 없거나, 처리하지 않을 것입니다.
	 * (예: 잘못된 요청 문법, 유효하지 않은 요청 메시지 framing, 또는 신뢰할 수 없는 요청 라우팅).
	 * 2024-12-28 22:38:34,719 DEBUG o.s.w.s.m.m.a.ServletInvocableHandlerMethod - Failed to resolve argument 0 of type 'spring.RegisterRequest'
	 * org.springframework.validation.BindException: org.springframework.validation.BeanPropertyBindingResult: 1 errors
	 * Field error in object 'registerRequest' on field 'confirmPassword': rejected value [123456]; codes [nomatch.registerRequest.confirmPassword,nomatch.confirmPassword,nomatch.java.lang.String,nomatch]; arguments []; default message [null]
	 *
	 */

	@PostMapping("/register/step3errors")
	public String step3errors(@Valid RegisterRequest regReq) {
		log.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::step3errors" );
		try {
			memberRegisterService.regist(regReq);  // 회원 등록 서비스 호출: 요청 데이터를 이용해 회원 정보 저장
			return "register/step3";               // 회원 등록 성공 시 step3 페이지 반환
		} catch (DuplicateMemberException ex) {
			return "register/step2";							     // 예외 발생 시 step2 페이지 반환
		}
	}


	/**
	 * method        : initBinder (컨트롤러단의 검증, 로컬 검증 )
	 * date          : 2024-12-26
	 * param         : WebDataBinder binder - 요청 데이터를 바인딩하고 검증하는 WebDataBinder 객체
	 * return        : void
	 * description   : WebDataBinder에 RegisterRequestValidator를 설정하여 요청 데이터의 유효성 검증을 처리
	 * 유효성 검증 로직을 수정하고 싶은 경우에는 setValidator 부분만 수정해서 검증클래스를 변경하거나, 추가하거나 할 수 있음!!
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new RegisterRequestValidator());   // RegisterRequestValidator를 WebDataBinder에 설정하여 유효성 검증 수행
	}




}
