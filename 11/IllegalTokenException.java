
/**
 * Created by hadas on 28/11/2016.
 */
public class IllegalTokenException extends Exception {
	private static final long serialVersionUID = 1L;
	String tokenString;
    public IllegalTokenException(String token) {
        this.tokenString = token;
    }
    public String getMessage() {
        return "Illegal token in file: " + tokenString;
    }
}