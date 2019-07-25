package com.tezamess.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "typemessage")
public class TypeMessageModel implements Serializable {

    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "name")
    private String name;
    
    public TypeMessageModel() {
    }


    public TypeMessageModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "com.tezamess.model.Typemessage[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
