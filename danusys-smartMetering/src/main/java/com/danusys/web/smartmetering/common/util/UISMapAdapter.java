package com.danusys.web.smartmetering.common.util;

import java.util.LinkedHashMap;
import java.util.Map;
/**study
 *
 * <pre>
 *
 *  LinkedHashMap을 구현하려는 클래스들의 Adapter
 *
 * </pre>
 *
 */
@SuppressWarnings("rawtypes")
public class UISMapAdapter extends LinkedHashMap {

    /**
	 *
	 */
	private static final long serialVersionUID = -7363727856222191345L;

	protected String name = null;
    protected boolean isDefaultInit = true;

    public UISMapAdapter() {
        super();
    }

    public UISMapAdapter(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the specified initial capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public UISMapAdapter(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    @SuppressWarnings({ "unchecked" })
	public UISMapAdapter(Map map) {
        super(map);
    }

    public UISMapAdapter(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getName() {
        return name;
    }

    public void setIsDefaultInit(boolean isDefaultInit) {
        this.isDefaultInit = isDefaultInit;
    }

    public boolean getIsDefaultInit() {
        return isDefaultInit;
    }

}
