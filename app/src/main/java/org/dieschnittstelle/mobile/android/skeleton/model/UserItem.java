package org.dieschnittstelle.mobile.android.skeleton.model;


import java.io.Serializable;
import java.util.Objects;


public class UserItem implements Serializable {
    private static final long serialVersionUID = -7306724305413428761L;
    private String eMail;
    private String password;

    public UserItem(String eMail, String password) {
        this.eMail = eMail;
        this.password = password;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserItem userItem = (UserItem) o;
        return Objects.equals(eMail, userItem.eMail) && Objects.equals(password, userItem.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eMail, password);
    }
}
