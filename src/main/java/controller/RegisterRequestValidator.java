package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import spring.RegisterRequest;

/**
 * class         : RegisterRequestValidator
 * date          : 2024-12-26
 * description   : 회원가입 요청 객체(RegisterRequest)에 대한 유효성 검증을 수행하는 Validator 구현 클래스
 */
public class RegisterRequestValidator implements Validator {

    // 이메일 형식 검증을 위한 정규표현식
    private static final String emailRegExp =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // 정규표현식 패턴 객체
    private Pattern pattern;


    /**
     * method        : RegisterRequestValidator
     * date          : 2024-12-26
     * description   : 생성자 - 이메일 형식 검증을 위한 패턴 초기화
     */
    public RegisterRequestValidator() {
        pattern = Pattern.compile(emailRegExp);
        System.out.println("RegisterRequestValidator#new(): " + this);
    }

    /**
     * method        : supports
     * date          : 2024-12-26
     * param         : Class<?> clazz - 검증할 객체의 클래스 타입
     * return        : boolean - 검증 가능한 클래스 타입 여부
     * description   : 이 Validator가 해당 클래스의 인스턴스를 지원하는지 확인
     *  폼 데이터를 처리하기 전, 스프링은 supports 메소드를 호출해 이 Validator가 적용 가능한지 확인하고,
     *  지원하는 클래스(RegisterRequest)가 아니라면, 검증 과정에서 해당 Validator는 무시한다.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.isAssignableFrom(clazz);
    }

    /**
     * method        : validate
     * date          : 2024-12-26
     * param         : Object target - 검증할 객체
     *                Errors errors - 검증 오류를 저장할 객체
     * return        : void
     * description   : 주어진 객체에 대해 유효성 검증 수행
     */
    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("RegisterRequestValidator#validate(): " + this);
        // 검증 대상 객체를 RegisterRequest로 캐스팅
        RegisterRequest regReq = (RegisterRequest) target;

        // 이메일 검증
        if (regReq.getEmail() == null || regReq.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "required");   // 이메일이 비어있으면 오류 등록
        } else {
            Matcher matcher = pattern.matcher(regReq.getEmail());
            if (!matcher.matches()) {
                errors.rejectValue("email", "bad"); // 이메일 형식이 잘못되면 오류 등록
            }
        }
        // 이름 필수 값 검증
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required");
        // 비밀번호 필수 값 검증
        ValidationUtils.rejectIfEmpty(errors, "password", "required");
        // 비밀번호 확인 필드 필수 값 검증
        ValidationUtils.rejectIfEmpty(errors, "confirmPassword", "required");
        // 비밀번호와 확인 비밀번호 일치 여부 검증
        if (!regReq.getPassword().isEmpty()) {
            if (!regReq.isPasswordEqualToConfirmPassword()) {
                errors.rejectValue("confirmPassword", "nomatch");  // 비밀번호가 일치하지 않으면 오류 등록
            }
        }
    }

}
