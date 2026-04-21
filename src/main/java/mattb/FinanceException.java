package mattb;

public class FinanceException extends RuntimeException {
    public FinanceException(FinanceError error) {
        super(error.getMessage());
    }
}
