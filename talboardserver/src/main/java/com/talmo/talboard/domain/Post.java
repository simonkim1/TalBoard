package com.talmo.talboard.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Post {
    @Id @GeneratedValue
    @Column(name = "post_no")
    private Long id;

}
