package initial3d.linearmath;

public class NonSquareMatrixException extends
		MatrixException {

	private static final long serialVersionUID = 1L;

	public NonSquareMatrixException() {
		super();
	}

	public NonSquareMatrixException(String message) {
		super(message);
	}

	public NonSquareMatrixException(String message, Throwable cause) {
		super(message, cause);
	}
}