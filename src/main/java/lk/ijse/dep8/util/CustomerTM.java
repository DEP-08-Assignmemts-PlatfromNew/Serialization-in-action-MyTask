package lk.ijse.dep8.util;

import java.io.Serializable;

public class CustomerTM implements Serializable {
    private String id;
    private String name;
    private String address;

    public CustomerTM() {
    }

    public CustomerTM(String id, String name, String address) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void printData(){
        System.out.printf("id= %s, name= %s, address= %s\n",id,name,address);
    }
}
