package com.example.springtestsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name="can_create")
    private boolean can_create;
    @Column(name="can_update")
    private boolean can_update;
    @Column(name="can_delete")
    private boolean can_delete;

    @OneToMany(mappedBy = "role")
    private List<User> user;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", create=" + can_create +
                ", update=" + can_update +
                ", delete=" + can_delete +
                ", user=" + user +
                '}';
    }
}
