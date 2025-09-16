package org.trail.scapes.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "categories")
open class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false, unique = true)
    open var name: String,

    @Column(length = 500)
    open var description: String? = null
) {
    constructor() : this(null, "category404", null)

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnore
    open var places: MutableList<Place> = mutableListOf()
}