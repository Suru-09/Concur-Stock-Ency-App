package entity;

public class Client extends BaseEntity<Long>{
    private String name;
    private String address;
    private Long budget;

    public Client(String name, String address, Long budget) {
        this.name = name;
        this.address = address;
        this.budget = budget;
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

    public Long getBudget() {
        return budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", budget=" + budget +
                '}';
    }
}
