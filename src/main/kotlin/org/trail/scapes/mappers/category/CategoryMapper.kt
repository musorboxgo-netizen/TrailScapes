package org.trail.scapes.mappers.category

import org.springframework.stereotype.Service
import org.trail.scapes.domain.Category
import org.trail.scapes.dto.category.CategoryDto
import org.trail.scapes.dto.category.CreateCategoryDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import org.trail.scapes.repositories.CategoryRepository

@Service
class CategoryMapper(
    private val categoryRepository: CategoryRepository
): ExtendedEntityMapper<Category, CategoryDto, CreateCategoryDto> {
    override fun toDto(entity: Category): CategoryDto {
        val count: Long? = entity.id?.let { categoryRepository.countPlacesByCategoryId(it) }
        return CategoryDto(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            placesAssigned = count?.toInt()
        )
    }

    override fun toEntity(dto: CategoryDto): Category =
        Category(id = dto.id, name = dto.name, description = dto.description)

    override fun fromCreateDto(dto: CreateCategoryDto): Category =
        Category(id = null, name = dto.name, description = dto.description)
}