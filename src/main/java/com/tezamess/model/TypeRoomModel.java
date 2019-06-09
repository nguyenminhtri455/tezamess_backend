package com.tezamess.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "typeroom")
public class TypeRoomModel implements Serializable {

    @Id
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "id")
    private String id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;

    public TypeRoomModel() {
    }

    public TypeRoomModel(String id) {
        this.id = id;
    }

    public TypeRoomModel(String id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "com.tezamess.model.Typeroom[ id=" + id + " ]";
    }
    
}
