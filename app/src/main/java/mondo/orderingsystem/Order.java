package mondo.orderingsystem;

/**
 * Created by Naruto on 8/16/2016.
 */
public class Order {

    private int employee_id;
    private int item_id;
    private int quantity;
    private int size_id;
    private String note;
    private Addon[] addons;

    public Order( Addon[] addons, int employee_id, int item_id, int quantity, int size_id, String note){
        this.employee_id =employee_id;
        this.item_id =item_id;
        this.quantity =quantity;
        this.size_id =size_id;
        this.addons =addons;
        this.note =note;
    }
}
