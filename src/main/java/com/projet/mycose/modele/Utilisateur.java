package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String numeroDeTelephone;

    @Embedded
    private Credentials credentials;

    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OffreStage> offreStagesUploaded = new ArrayList<>();


    public Utilisateur(Long id, String prenom, String nom, String numeroDeTelephone, Credentials credentials) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.numeroDeTelephone = numeroDeTelephone;
        this.credentials = credentials;
    }

    public Utilisateur(String prenom, String nom, String numeroDeTelephone, Credentials credentials) {
        this.prenom = prenom;
        this.nom = nom;
        this.numeroDeTelephone = numeroDeTelephone;
        this.credentials = credentials;
    }

    public String getCourriel(){
        return credentials.getEmail();
    }

    public String getMotDePasse(){
        return credentials.getPassword();
    }

    public Role getRole(){
        return credentials.getRole();
    }

    public Collection<? extends GrantedAuthority> getAuthorities(){
        return credentials.getAuthorities();
    }

    @Override
    public String toString(){
        return "Utilisateur{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", prenom='" + prenom + '\'' +
                ", nom='" + nom + '\'' +
                ", numeroDeTelephone='" + numeroDeTelephone + '\'' +
                ", credentials=" + credentials +
                '}';
    }
}
