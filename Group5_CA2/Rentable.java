/*
 * Student Name: [W.S.C. WAIDYARATHNA]
 * Student ID: [24IT0541]
 * Group: 5
 */

/** Contract for objects that can be rented and returned. */
public interface Rentable {
    /**
     * Attempts to reserve an available item.
     * @return true when availability changes to rented; false if already rented
     */
    boolean rent();
    /**
     * Marks the item as available again.
     * The agency must validate the active rental before calling this method.
     */
    void giveBack();
}
