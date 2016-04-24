package technow.com.vision;

/**
 * Created by Tautvydas on 23/04/2016.
 */
public class estados {

    public static final String HTTP_POST_LOGIN_TOKEN = "http://104.197.94.177/api-token-auth/";
    public static final String HTTP_POST_UP_IMAGE = "http://104.197.94.177/api/v1/env_imagen/";
    public static final String HTTP_POST_GET_DESCRIPTION = "http://104.197.94.177/api/v1/imagenes";
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ACEPTED = 202;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_SERVER_NOT_IMPLEMENTED = 501;
}
