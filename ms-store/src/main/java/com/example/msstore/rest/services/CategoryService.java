package com.example.msstore.rest.services;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.repositories.CategoryRepository;
import com.example.msstore.rest.services.exceptions.AlreadyExistException;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import com.example.msstore.rest.dto.CategoryDto;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void createNewCategory(CategoryDto request){
        try {
            categoryRepository.save(new Categories(request));
        }
        catch (DataIntegrityViolationException e){
            throw new AlreadyExistException("This category already exist");
        }
    }

    public List<CategoryDto> findAll(){
        return categoryRepository.findAll().stream()
                .map(CategoryDto::new).collect(Collectors.toList());
    }

    public Categories findById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(()  -> new ObjectNotFoundException("The category is not found"));
    }

    public Categories findByName(String name){
        return categoryRepository.findByName(name)
                .orElseThrow(()  -> new ObjectNotFoundException("The category is not found"));
    }

    public void deleteById(Long id){
        var cat = findById(id);
        categoryRepository.delete(cat);
    }
}
