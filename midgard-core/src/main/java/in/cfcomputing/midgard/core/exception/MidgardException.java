package in.cfcomputing.midgard.core.exception;

import in.cfcomputing.odin.core.exception.OdinException;

/**
 * Created by gibugeorge on 30/03/2017.
 */
public class MidgardException extends OdinException {
    public MidgardException(String message, Exception e) {
        super(message, e);
    }
}
