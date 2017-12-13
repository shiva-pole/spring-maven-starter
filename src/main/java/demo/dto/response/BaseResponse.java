package demo.dto.response;

public class BaseResponse {

	private boolean successFlag = true;
	private String errorCode;
	private String errorMsg;

	public boolean isSuccessFlag() {

		return successFlag;
	}

	public void setSuccessFlag(boolean successFlag) {

		this.successFlag = successFlag;
	}

	public String getErrorCode() {

		return errorCode;
	}

	public void setErrorCode(String errorCode) {

		this.errorCode = errorCode;
	}

	public String getErrorMsg() {

		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {

		this.errorMsg = errorMsg;
	}
}
