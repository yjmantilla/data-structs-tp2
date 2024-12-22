/** A Union-Find structure for maintaining disjoint sets. */
public class Partition<E> {

    // -------------- nested Locator class --------------
    private class Locator<E> implements Position<E> {
        public E element;
        public int size;
        public Locator<E> parent;

        public Locator(E elem) {
            element = elem;
            size = 1;
            parent = this; // convention for a cluster leader
        }

        public E getElement() {
            return element;
        }
    }
    // --------- end of nested Locator class --------

    /** Makes a new cluster containing element e and returns its position. */
    public Position<E> makeCluster(E e) {
        return new Locator<>(e);
    }

    /**
     * Finds the cluster containing the element identified by Position p
     * and returns the Position of the cluster's leader.
     */
    public Position<E> find(Position<E> p) {
        Locator<E> loc = validate(p);
        if (loc.parent != loc) {
            loc.parent = (Locator<E>) find(loc.parent); // overwrite parent after recursion
        }
        return loc.parent;
    }

    /** Merges the clusters containing elements with positions p and q (if distinct). */
    public void union(Position<E> p, Position<E> q) {
        Locator<E> a = (Locator<E>) find(p);
        Locator<E> b = (Locator<E>) find(q);

        if (a != b) {
            if (a.size > b.size) {
                b.parent = a;
                a.size += b.size;
            } else {
                a.parent = b;
                b.size += a.size;
            }
        }
    }

    // Helper method to validate positions
    private Locator<E> validate(Position<E> p) {
        if (!(p instanceof Locator)) throw new IllegalArgumentException("Invalid position");
        return (Locator<E>) p;
    }
}
