package demo.dto.response;

public class GreetingResponse extends BaseResponse {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
