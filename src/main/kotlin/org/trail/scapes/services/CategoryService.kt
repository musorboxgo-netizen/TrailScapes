package org.trail.scapes.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.trail.scapes.domain.Category
import org.trail.scapes.dto.category.CategoryDto
import org.trail.scapes.dto.category.CreateCategoryDto
import org.trail.scapes.mappers.category.CategoryMapper
import org.trail.scapes.repositories.CategoryRepository
import org.trail.scapes.repositories.PlaceRepository

@Service
open class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val placeRepository: PlaceRepository,
    private val categoryMapper: CategoryMapper
) {
    // Получение всех категорий
    fun getAllCategories(): List<CategoryDto> {
        val categories = categoryRepository.findAll()
        return categories.map { categoryMapper.toDto(it) }
    }

    // Получение категории по ID
    fun getCategoryById(id: Long): CategoryDto {
        val category = categoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Category with id=$id not found") }
        return categoryMapper.toDto(category)
    }

    // Получение категории по имени (без учета регистра)
    fun getCategoryByName(name: String): CategoryDto? {
        val categoryOpt = categoryRepository.findByNameIgnoreCase(name)
        return categoryOpt.map { categoryMapper.toDto(it) }.orElse(null)
    }

    // Создание новой категории отдельно
    fun createCategory(dto: CreateCategoryDto): CategoryDto {
        // Проверка дубликата по имени (игнорируя регистр)
        if (categoryRepository.existsByNameIgnoreCase(dto.name)) {
            throw IllegalArgumentException("Категория с именем '${dto.name}' уже существует")
        }
        val category = categoryMapper.fromCreateDto(dto)
        val saved = categoryRepository.save(category)
        return categoryMapper.toDto(saved)
    }

    // Создание (или получение) списка категорий по именам – для использования при создании плейса
    fun getOrCreateCategoriesByNames(names: List<String>): List<Category> {
        val categories: MutableList<Category> = mutableListOf()
        for (name in names) {
            // Ищем существующую категорию (в репозитории метод должен игнорировать регистр)
            val existing = categoryRepository.findByNameIgnoreCase(name).orElse(null)
            if (existing != null) {
                categories.add(existing)
            } else {
                // Создаем новую категорию, сразу сохраняем и добавляем в список
                val newCat = Category(name = name, description = null)
                categoryRepository.save(newCat)
                categories.add(newCat)
            }
        }
        return categories
    }

    // Обновление категории
    fun updateCategory(id: Long, dto: CreateCategoryDto): CategoryDto {
        val category = categoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Category with id=$id not found") }
        // Если имя изменилось, проверим уникальность
        if (!category.name.equals(dto.name, ignoreCase = true)) {
            if (categoryRepository.existsByNameIgnoreCase(dto.name)) {
                throw IllegalArgumentException("Категория с именем '${dto.name}' уже существует")
            }
        }
        category.name = dto.name
        category.description = dto.description
        val updated = categoryRepository.save(category)
        return categoryMapper.toDto(updated)
    }

    // Удаление категории
    @Transactional
    open fun deleteCategory(id: Long) {
        val category = categoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Category with id=$id not found") }
        // Проверяем, есть ли связанные плейсы
        val count = categoryRepository.countPlacesByCategoryId(id)
        if (count > 0) {
            // Удаляем категорию из каждого связанного плейса
            category.places.forEach { place ->
                place.categories.remove(category)
                placeRepository.save(place)
            }
        }
        // Теперь можно удалить категорию
        categoryRepository.delete(category)
    }
}