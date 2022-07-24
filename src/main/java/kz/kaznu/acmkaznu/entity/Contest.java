package kz.kaznu.acmkaznu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contest {

    @Id
    @Column(nullable = false)
    private Long id;

    private String name;

    private String phase;



}
