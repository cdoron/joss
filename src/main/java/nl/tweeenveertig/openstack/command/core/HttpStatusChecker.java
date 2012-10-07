package nl.tweeenveertig.openstack.command.core;

import nl.tweeenveertig.openstack.exception.CommandException;
import nl.tweeenveertig.openstack.exception.HttpStatusToExceptionMapper;
import org.apache.http.HttpStatus;

import java.lang.reflect.Constructor;

public class HttpStatusChecker {

    private HttpStatusMatcher matcher;

    private CommandExceptionError error;

    private Class exceptionToThrow;

    private static final HttpStatusChecker authorizationMatcher =
            new HttpStatusChecker(new HttpStatusMatch(HttpStatus.SC_UNAUTHORIZED), CommandExceptionError.UNAUTHORIZED);
    private static final HttpStatusChecker forbiddenMatcher =
            new HttpStatusChecker(new HttpStatusMatch(HttpStatus.SC_FORBIDDEN), CommandExceptionError.ACCESS_FORBIDDEN);

    public HttpStatusChecker(final HttpStatusMatcher matcher, final CommandExceptionError error) {
        this.matcher = matcher;
        this.error = error;
    }

    public HttpStatusChecker(final HttpStatusMatcher matcher, final CommandExceptionError error, final Class exceptionToThrow) {
        this(matcher, error);
        this.exceptionToThrow = exceptionToThrow;
    }

    public boolean isOk(int httpStatusCode) {
        if (matcher.matches(httpStatusCode)) {
            if (error == null) {
                return true; // The OK signal
            } else {
                HttpStatusToExceptionMapper.throwException(httpStatusCode);
//                if (this.exceptionToThrow == null) {
//                    throw new CommandException(httpStatusCode, error);
//                } else {
//                    try {
//                        Constructor constructor = this.exceptionToThrow.getDeclaredConstructor(new Class[]{Integer.class, CommandExceptionError.class});
//                        Object[] arguments = new Object[] { httpStatusCode, error };
//                        throw (CommandException)constructor.newInstance(arguments);
//                    } catch (Exception err) {
//                        throw err instanceof CommandException ?
//                                (CommandException)err :
//                                new CommandException("Programming error - unable to throw exception for "+httpStatusCode+"/"+error.toString(), err);
//                    }
//                }
            }
        }
        return false;
    }

    public static void verifyCode(HttpStatusChecker[] checkers, int httpStatusCode) {
        authorizationMatcher.isOk(httpStatusCode);
        forbiddenMatcher.isOk(httpStatusCode);
        for (HttpStatusChecker checker : checkers) {
            if (checker.isOk(httpStatusCode)) {
                return;
            }
        }
        throw new CommandException(httpStatusCode, CommandExceptionError.UNKNOWN);
    }
}
