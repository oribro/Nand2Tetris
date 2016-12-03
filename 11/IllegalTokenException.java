
/**
 * Created by hadas on 28/11/2016.
 */
public class IllegalTokenException extends Exception {
    String tokenString;
    public IllegalTokenException(String token) {
        this.tokenString = token;
    }
    public String getMessage() {
        return "Illegal token in file: " + tokenString;
    }
}