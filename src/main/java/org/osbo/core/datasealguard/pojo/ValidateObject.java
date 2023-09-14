package org.osbo.core.datasealguard.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author programmercito
 */
public class ValidateObject<T,U> {

    private U data;
    private T user;

    /**
     * @return the data
     */
    public U getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(U data) {
        this.data = data;
    }

    /**
     * @return the user
     */
    public T getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(T user) {
        this.user = user;
    }

}
