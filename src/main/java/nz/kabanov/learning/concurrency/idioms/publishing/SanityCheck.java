package nz.kabanov.learning.concurrency.idioms.publishing;

@SuppressWarnings("ALL")
public class SanityCheck {

    private int n;

    public SanityCheck(int n) {
        this.n = n;
    }

    // may fail if the object isn't properly published
    public void assertSanity() {
        if (n != n) throw new AssertionError();
    }
}
