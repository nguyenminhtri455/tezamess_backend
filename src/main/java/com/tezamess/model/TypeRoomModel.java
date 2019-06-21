package com.tezamess.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "typeroom")
public class TypeRoomModel implements Serializable {

    @Column(name = "name")
    private String name;


    @Id
    @Column(name = "id")
    private String id;
    
    public TypeRoomModel() {
    }


    public TypeRoomModel(String name) {
   
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "com.tezamess.model.Typeroom[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
