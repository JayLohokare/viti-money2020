package in.skylinelabs.money2020;

/**
 * Created by jaylo on 20-10-2018.
 */


public class LendDetails {

    private String name;
    private String amount;
    private String rating;
    private String photoUrl = "";
    private String endorsedBy;
    private String id;


    public String get_name(){
        return name;
    }
    public String get_endorsement(){
        return endorsedBy;
    }
    public String get_amount(){
        return amount;
    }
    public String get_rating(){
        return rating;
    }
    public String get_photo(){
        return photoUrl;
    }
    public String get_id(){
        return id;
    }


    void set_name(String name){
        this.name = name;
    }
    void set_id(String id){
        this.id = id;
    }
    void set_endorsement(String endorsedBy){
        this.endorsedBy = endorsedBy;
    }
    void set_amount(String amount){
        this.amount = amount;
    }
    void set_rating(String rating){ this.rating = rating; }
    void set_photo(String photoUrl) { this.photoUrl = photoUrl;}


}