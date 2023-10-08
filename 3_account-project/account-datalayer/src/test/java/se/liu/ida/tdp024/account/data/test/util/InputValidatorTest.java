package se.liu.ida.tdp024.account.data.impl.db.util;

import org.junit.Assert;
import org.junit.Test;
import se.liu.ida.tdp024.account.data.api.exception.UnknownArgumentException;
import se.liu.ida.tdp024.account.data.api.util.InputValidator;
import se.liu.ida.tdp024.account.data.impl.db.util.InputValidatorImpl;

public class InputValidatorTest {
    private InputValidator inputValidator = new InputValidatorImpl();

    @Test(expected = UnknownArgumentException.class)
    public void checkInputNotNullTest1()
            throws UnknownArgumentException {
        inputValidator.checkInputNotNull(null, "7", "SBAB");
    }
    @Test(expected = UnknownArgumentException.class)
    public void checkInputNotNullTest2()
            throws UnknownArgumentException {
        inputValidator.checkInputNotNull("CHECK", null, "SBAB");
    }
    @Test(expected = UnknownArgumentException.class)
    public void checkInputNotNullTest3()
            throws UnknownArgumentException {
        inputValidator.checkInputNotNull("CHECK", "7", null);
    }
    @Test(expected = UnknownArgumentException.class)
    public void checkInputNotNullTest4()
            throws UnknownArgumentException {
        inputValidator.checkInputNotNull(null, null, null);
    }
    @Test
    public void checkInputNotNullTest5()
            throws UnknownArgumentException {
        inputValidator.checkInputNotNull("CHECK", "7", "SBAB");
    }
    @Test
    public void checkInputNotDefault()
            throws UnknownArgumentException {
        inputValidator.checkInputNotDefault("CHECK", "7", "SBAB");
    }
    @Test(expected = UnknownArgumentException.class)
    public void checkInputNotDefault2()
            throws UnknownArgumentException {
        inputValidator.checkInputNotDefault("nullAccount", "7", "SBAB");
    }
    @Test(expected = UnknownArgumentException.class)
    public void checkInputNotDefault3()
            throws UnknownArgumentException {
        inputValidator.checkInputNotDefault("CHECK","nullPerson" , "SBAB");
    }
    @Test(expected = UnknownArgumentException.class)
    public void checkInputNotDefault4()
            throws UnknownArgumentException {
        inputValidator.checkInputNotDefault("CHECK", "7", "nullBank");
    }
    @Test
    public void checkAccountType()
            throws IllegalArgumentException {
        inputValidator.checkAccountType("CHECK");
    }
    @Test
    public void checkAccountType2()
            throws IllegalArgumentException {
        inputValidator.checkAccountType("SAVINGS");
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkAccountType3()
            throws IllegalArgumentException {
        inputValidator.checkAccountType("");
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkAccountType4()
            throws IllegalArgumentException {
        inputValidator.checkAccountType(null);
    }
    @Test(expected = UnknownArgumentException.class)
    public void runAllChecksTest()
            throws UnknownArgumentException, IllegalArgumentException {
        inputValidator.runAllChecks(null, "Bertil", null);
    }
    @Test
    public void checkPersonType()
            throws IllegalArgumentException {
        inputValidator.checkPersonType("67");
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkPersonType2()
            throws IllegalArgumentException {
        inputValidator.checkPersonType("");
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkPersonType3()
            throws IllegalArgumentException {
        inputValidator.checkPersonType(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkPersonType4()
            throws IllegalArgumentException {
        inputValidator.checkPersonType("Hans");
    }

    @Test
    public void runAllTests() {
        inputValidator.runAllChecks("SAVINGS", "2", "SWEATBANK");
    }
}
