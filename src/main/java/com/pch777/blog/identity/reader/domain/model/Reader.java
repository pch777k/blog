package com.pch777.blog.identity.reader.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pch777.blog.subscription.domain.model.Subscription;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "readers")
@Entity
public class Reader extends User {

    @OneToMany(mappedBy = "reader", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Subscription> subscriptions = new ArrayList<>();

    @Formula("(SELECT COUNT(*) FROM subscriptions s WHERE s.reader_id = id)")
    private int subscriptionCount;

    public Reader(String firstName, String lastName, String username, String password, String email, Role role) {
        super(firstName, lastName, username, password, email, role);
    }

}
