package lk.ijse.dep8.util;

import java.io.Serializable;

public class Customer implements Serializable {

    private String id;
    private String name;
    private String address;
    private byte[] profilePic;

    public Customer() {
    }

    public Customer(String id, String name, String address, byte[] profilePic) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
        this.setProfilePic(profilePic);
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

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }
    public void printData(){
        System.out.printf("id= %s, name= %s, address= %s\n", getId(), getName(), getAddress());
    }

}
