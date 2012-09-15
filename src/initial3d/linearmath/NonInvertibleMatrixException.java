package initial3d.linearmath;

public class NonInvertibleMatrixException extends MatrixException {

	private static final long serialVersionUID = 1L;

	public NonInvertibleMatrixException() {
		super();
	}

	public NonInvertibleMatrixException(String message) {
		super(message);
	}

	public NonInvertibleMatrixException(String message, Throwable cause) {
		super(message, cause);
	}
}