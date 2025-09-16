package org.trail.scapes.services

import org.springframework.stereotype.Service
import org.trail.scapes.dto.category.CategoryDto
import org.trail.scapes.dto.category.CreateCategoryDto
import org.trail.scapes.mappers.category.CategoryMapper
import org.trail.scapes.repositories.CategoryRepository

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper
) {
    fun getAll(): List<CategoryDto> =
        categoryRepository.findAll()
            .map { categoryMapper.toDto(it) }

    fun getById(id: Long): CategoryDto =
        categoryRepository.findById(id)
            .map { categoryMapper.toDto(it) }
            .orElseThrow { NoSuchElementException("Category with id=$id not found") }

    fun createCategory(dto: CreateCategoryDto): CategoryDto {
        val category = categoryMapper.fromCreateDto(dto)
        val saved = categoryRepository.save(category)
        return categoryMapper.toDto(saved)
    }

    fun updateCategory(id: Long, dto: CreateCategoryDto): CategoryDto {
        val category = categoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Category with id=$id not found") }

        category.name = dto.name
        category.description = dto.description

        val updated = categoryRepository.save(category)
        return categoryMapper.toDto(updated)
    }

    fun deleteById(id: Long) {
        if (!categoryRepository.existsById(id)) {
            throw NoSuchElementException("Category with id=$id not found")
        }
        categoryRepository.deleteById(id)
    }

    fun findByName(name: String): CategoryDto? =
        categoryRepository.findAll()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?.let { categoryMapper.toDto(it) }
}