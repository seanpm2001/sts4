package org.springframework.ide.vscode.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Utility methods to convert exceptions into other types of exceptions, status
 * objects etc.
 *
 * @author Kris De Volder
 */
public class ExceptionUtil {

	public static Throwable getDeepestCause(Throwable e) {
		Throwable cause = e;
		Throwable parent = e.getCause();
		while (parent != null && parent != e) {
			cause = parent;
			parent = cause.getCause();
		}
		return cause;
	}

	public static String getMessage(Throwable e) {
		// The message of nested exception is usually more interesting than the
		// one on top.
		Throwable cause = getDeepestCause(e);
		String msg = cause.getClass().getSimpleName() + ": " + cause.getMessage();
		return msg;
	}

	public static IllegalStateException notImplemented(String string) {
		return new IllegalStateException("Not implemented: " + string);
	}

	public static boolean isCancelation(Throwable e) {
		return (
//				e instanceof OperationCanceledException ||
				e instanceof InterruptedException ||
				e instanceof CancellationException
//				(
//						e instanceof CoreException &&
//						((CoreException)e).getStatus().getSeverity()==IStatus.CANCEL
//				)
		);
	}

	public static RuntimeException unchecked(Exception e) {
		return new RuntimeException(e);
	}

	public static String stacktrace() {
		return stacktrace(new Exception("Stacktrace"));
	}

	public static String stacktrace(Exception exception) {
		ByteArrayOutputStream dump = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(dump);
		try {
			exception.printStackTrace(out);
		} finally {
			out.close();
		}
		return dump.toString();
	}

	/**
	 * Convert throwables into exception try not to wrap if not needing to.
	 */
	public static Exception exception(Throwable cause) {
		if (cause instanceof Exception) {
			return (Exception)cause;
		}
		return new ExecutionException(cause);
	}

	public static Object exception(String message, Throwable error) {
		if (message != null) {
			// Wrap only if there is an additional message
			return new ExecutionException(message, error);
		} else {
			return exception(error);
		}
	}

}
